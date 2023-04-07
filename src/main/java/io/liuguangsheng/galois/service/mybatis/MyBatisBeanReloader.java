/*
 * MIT License
 *
 * Copyright (c) [2023] [$user]
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

package io.liuguangsheng.galois.service.mybatis;

import io.liuguangsheng.galois.constants.Constant;
import io.liuguangsheng.galois.service.BeanReloader;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.mybatis.visitors.MyBatisConfigurationVisitor;
import io.liuguangsheng.galois.utils.GaloisLog;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Properties;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;

/**
 * MyBatis的Mapper重新加载服务类，适用于3.2.0或以上版本
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
@LazyBean(value = "MyBatisBeanReloader", manager = MyBatisAgentService.class)
public class MyBatisBeanReloader implements BeanReloader<File>, MyBatisConfigurationVisitor.NecessaryMethods {

    private static final MyBatisBeanReloader mybatisBeanReloder = new MyBatisBeanReloader();
    private static final Logger logger = new GaloisLog(MyBatisBeanReloader.class);
    private static final List<String> CHILD_NAMES = Arrays.asList("association", "collection", "case");
    /**
     * The Configuration.
     */
    protected Configuration configuration;

    private MyBatisBeanReloader() {
    }

    /**
     * 获取单例实例
     *
     * @return the instance
     */
    public static MyBatisBeanReloader getInstance() {
        return mybatisBeanReloder;
    }

    /**
     * 更新bean实例
     */
    @Override
    public void updateBean(File xmlFile) {
        if (configuration == null) {
            logger.error("MybatisBeanReloader not prepare ready. Configuration object is null.");
            return;
        }

        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            Properties properties = configuration.getVariables();
            XPathParser parser = new XPathParser(fis, true, properties, new XMLMapperEntityResolver());
            XNode context = parser.evalNode("/mapper");
            String namespace = context.getStringAttribute(Constant.NAMESPACE);
            // clear cache
            clearMapperRegistry(namespace);
            clearCachedNames(namespace);
            clearParameterMap(context.evalNodes("/mapper/parameterMap"), namespace);
            clearResultMap(context.evalNodes("/mapper/resultMap"), namespace);
            clearKeyGenerators(context.evalNodes("insert|update|select|delete"), namespace);
            clearSqlElement(context.evalNodes("/mapper/sql"), namespace);
            // reparse mybatis mapper xml file
            reloadXML(xmlFile);
        } catch (Throwable e) {
            logger.error("Reload mybatis mapper by xml file fail.", e);
            return;
        }

        logger.info("Reload mybatis mapper by xml file {} success.", xmlFile.getName());
    }

    @Override
    public boolean isUseful(File file) {
        return true;
    }

    /**
     * reload xml
     *
     * @param xmlFile xmlFile
     */
    private void reloadXML(File xmlFile) throws IOException {
        InputStream is = Files.newInputStream(xmlFile.toPath());
        XMLMapperBuilder builder = new XMLMapperBuilder(is, configuration, xmlFile.getName(),
                configuration.getSqlFragments());
        builder.parse();
    }

    /**
     * clear mapper registry
     *
     * @param namespace namespace
     */
    @SuppressWarnings("unchecked")
    private void clearMapperRegistry(String namespace)
            throws NoSuchFieldException, IllegalAccessException {
        Field field = MapperRegistry.class.getDeclaredField("knownMappers");
        field.setAccessible(true);
        Map<Class<?>, Object> mapConfig = (Map<Class<?>, Object>) field.get(configuration.getMapperRegistry());
        Class<?> refreshKey = null;

        for (Map.Entry<Class<?>, Object> item : mapConfig.entrySet()) {
            if (item.getKey().getName().contains(namespace)) {
                refreshKey = item.getKey();
                break;
            }
        }

        if (refreshKey != null) {
            mapConfig.remove(refreshKey);
        }
    }

    /**
     * clear cached names
     *
     * @param namespace namespace
     */
    @Deprecated
    private void clearCachedNames(String namespace) {
        configuration.getCacheNames().remove(namespace);
    }

    /**
     * clear parameter map
     *
     * @param list      list
     * @param namespace namespace
     */
    private void clearParameterMap(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute(Constant.ID);
            configuration.getResultMapNames().remove(namespace + "." + id);
        }
    }

    /**
     * clear result map
     *
     * @param list      list
     * @param namespace namespace
     */
    private void clearResultMap(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute(Constant.ID, xNode.getValueBasedIdentifier());
            configuration.getResultMapNames().remove(id);
            configuration.getResultMapNames().remove(namespace + "." + id);
            clearResultMap(xNode, namespace);
        }
    }

    /**
     * clear result map
     *
     * @param xNode     xNode
     * @param namespace namespace
     */
    private void clearResultMap(XNode xNode, String namespace) {
        for (XNode child : xNode.getChildren()) {
            if (CHILD_NAMES.contains(child.getName())) {
                if (child.getStringAttribute("select") == null) {
                    configuration.getResultMapNames().remove(child.getStringAttribute(Constant.ID,
                            child.getValueBasedIdentifier()));
                    configuration.getResultMapNames()
                            .remove(namespace + "." + child.getStringAttribute(Constant.ID,
                                    child.getValueBasedIdentifier()));

                    if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                        clearResultMap(child, namespace);
                    }
                }
            }
        }
    }

    /**
     * clear key generators
     *
     * @param list      list
     * @param namespace namespace
     */
    private void clearKeyGenerators(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute(Constant.ID);
            configuration.getKeyGeneratorNames().remove(id + SelectKeyGenerator.SELECT_KEY_SUFFIX);
            configuration.getKeyGeneratorNames()
                    .remove(namespace + "." + id + SelectKeyGenerator.SELECT_KEY_SUFFIX);

            Collection<MappedStatement> mappedStatements = configuration.getMappedStatements();
            List<MappedStatement> tempStatements = new ArrayList<>(64);

            for (MappedStatement statement : mappedStatements) {
                if (statement != null) {
                    if (Objects.equals(statement.getId(), namespace + "." + id)) {
                        tempStatements.add(statement);
                    }
                }
            }

            mappedStatements.removeAll(tempStatements);
        }
    }

    /**
     * clear sql element
     *
     * @param list      list
     * @param namespace namespace
     */
    private void clearSqlElement(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute(Constant.ID);
            configuration.getSqlFragments().remove(id);
            configuration.getSqlFragments().remove(namespace + "." + id);
        }
    }

    /**
     * get configuration
     *
     * @return {@link Configuration}
     * @see Configuration
     */
    public Configuration getConfiguration() {
        return configuration;
    }

    /**
     * Sets configuration.
     *
     * @param configuration the configuration
     */
    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
