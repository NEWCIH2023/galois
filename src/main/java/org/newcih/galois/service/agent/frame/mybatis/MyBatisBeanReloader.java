/*
 * MIT License
 * Copyright (c) [2023] [liuguangsheng]
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

package org.newcih.galois.service.agent.frame.mybatis;

import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.mybatis.spring.SqlSessionFactoryBean;
import org.newcih.galois.service.agent.BeanReloader;
import org.newcih.galois.utils.GaloisLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

import static org.newcih.galois.constants.Constant.ID;
import static org.newcih.galois.constants.Constant.NAMESPACE;

/**
 * MyBatis的Mapper重新加载服务类，适用于 >= 3.2.0版本
 */
public class MyBatisBeanReloader implements BeanReloader<File> {

    public static final MyBatisBeanReloader mybatisBeanReloder = new MyBatisBeanReloader();
    private static final GaloisLog logger = GaloisLog.getLogger(MyBatisBeanReloader.class);
    protected SqlSessionFactoryBean sqlSessionFactoryBean;
    protected Configuration configuration;

    private MyBatisBeanReloader() {
    }

    /**
     * 获取单例实例
     *
     * @return
     */
    public static MyBatisBeanReloader getInstance() {
        return mybatisBeanReloder;
    }

    /**
     * 更新bean实例
     *
     * @param xmlFile
     */
    @Override
    public void updateBean(File xmlFile) {
        try (FileInputStream fis = new FileInputStream(xmlFile)) {
            XPathParser parser = new XPathParser(fis, true, configuration.getVariables(),
                    new XMLMapperEntityResolver());
            XNode context = parser.evalNode("/mapper");
            String namespace = context.getStringAttribute(NAMESPACE);
            // clear cache
            clearMapperRegistry(namespace);
            clearLoadedResources(xmlFile.getName());
            clearCachedNames(namespace);
            clearParameterMap(context.evalNodes("/mapper/parameterMap"), namespace);
            clearResultMap(context.evalNodes("/mapper/resultMap"), namespace);
            clearKeyGenerators(context.evalNodes("insert|update|select|delete"), namespace);
            clearSqlElement(context.evalNodes("/mapper/sql"), namespace);
            // reparse mybatis mapper xml file
            reloadXML(xmlFile);
        } catch (Exception e) {
            logger.error("reload mybatis xml throw exception", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reload mybatis xml file {} success", xmlFile.getName());
        }
    }

    @Override
    public boolean validBean(File file) {
        return false;
    }

    /**
     * @return
     */
    @Override
    public boolean validVersion() {
        return false;
    }

    private void reloadXML(File xmlFile) throws IOException {
        InputStream is = Files.newInputStream(xmlFile.toPath());
        XMLMapperBuilder builder = new XMLMapperBuilder(is, configuration, xmlFile.getName(),
                configuration.getSqlFragments());
        builder.parse();
    }

    @SuppressWarnings("unchecked")
    private void clearMapperRegistry(String namespace) throws NoSuchFieldException, IllegalAccessException {
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

    @SuppressWarnings("rawtypes")
    @Deprecated
    private void clearLoadedResources(String fileName) throws NoSuchFieldException, IllegalAccessException {
        Field loadedResourcesField = configuration.getClass().getDeclaredField("loadedResources");
        loadedResourcesField.setAccessible(true);
        Set loadedResourcesSet = (Set) loadedResourcesField.get(configuration);
        loadedResourcesSet.remove(fileName);
    }

    private void clearCachedNames(String namespace) {
        configuration.getCacheNames().remove(namespace);
    }

    private void clearParameterMap(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute(ID);
            configuration.getResultMapNames().remove(namespace + "." + id);
        }
    }

    private void clearResultMap(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute(ID, xNode.getValueBasedIdentifier());
            configuration.getResultMapNames().remove(id);
            configuration.getResultMapNames().remove(namespace + "." + id);
            clearResultMap(xNode, namespace);
        }
    }

    private void clearResultMap(XNode xNode, String namespace) {
        for (XNode child : xNode.getChildren()) {
            if (Objects.equals("association", child.getName()) || Objects.equals("collection", child.getName()) || Objects.equals("case", child.getName())) {
                if (child.getStringAttribute("select") == null) {
                    configuration.getResultMapNames().remove(child.getStringAttribute(ID,
                            child.getValueBasedIdentifier()));
                    configuration.getResultMapNames().remove(namespace + "." + child.getStringAttribute(ID,
                            child.getValueBasedIdentifier()));

                    if (child.getChildren() != null && !child.getChildren().isEmpty()) {
                        clearResultMap(child, namespace);
                    }
                }

            }
        }
    }

    private void clearKeyGenerators(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute(ID);
            configuration.getKeyGeneratorNames().remove(id + SelectKeyGenerator.SELECT_KEY_SUFFIX);
            configuration.getKeyGeneratorNames().remove(namespace + "." + id + SelectKeyGenerator.SELECT_KEY_SUFFIX);

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

    private void clearSqlElement(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute(ID);
            configuration.getSqlFragments().remove(id);
            configuration.getSqlFragments().remove(namespace + "." + id);
        }
    }

    public SqlSessionFactoryBean getSqlSessionFactoryBean() {
        return sqlSessionFactoryBean;
    }

    public void setSqlSessionFactoryBean(SqlSessionFactoryBean sqlSessionFactoryBean) {
        this.sqlSessionFactoryBean = sqlSessionFactoryBean;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
