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

package org.newcih.galois.service.agent.frame.corm;

import com.comtop.corm.builder.xml.XMLMapperBuilder;
import com.comtop.corm.builder.xml.XMLMapperEntityResolver;
import com.comtop.corm.executor.keygen.SelectKeyGenerator;
import com.comtop.corm.mapping.MappedStatement;
import com.comtop.corm.parsing.XNode;
import com.comtop.corm.parsing.XPathParser;
import com.comtop.corm.session.Configuration;
import org.newcih.galois.service.agent.BeanReloader;
import org.newcih.galois.utils.GaloisLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

/**
 * MyBatis的Mapper重新加载服务类
 */
public class CormBeanReloader implements BeanReloader<File> {

    public static final CormBeanReloader mybatisBeanReloder = new CormBeanReloader();
    private static final GaloisLog logger = GaloisLog.getLogger(CormBeanReloader.class);
    protected Configuration configuration;

    private CormBeanReloader() {
    }

    /**
     * 获取单例实例
     *
     * @return
     */
    public static CormBeanReloader getInstance() {
        return mybatisBeanReloder;
    }

    /**
     * 更新bean实例
     *
     * @param newXMLFile
     */
    @Override
    public void updateBean(File newXMLFile) {

        try (FileInputStream fis = new FileInputStream(newXMLFile)) {

            XPathParser parser = new XPathParser(fis, true, configuration.getVariables(),
                    new XMLMapperEntityResolver());
            XNode context = parser.evalNode("/mapper");
            String namespace = context.getStringAttribute("namespace");
            // clear cache
            clearMapperRegistry(namespace);
            clearLoadedResources(newXMLFile.getName());
            clearCachedNames(namespace);
            clearParameterMap(context.evalNodes("/mapper/parameterMap"), namespace);
            clearResultMap(context.evalNodes("/mapper/resultMap"), namespace);
            clearKeyGenerators(context.evalNodes("insert|update|select|delete"), namespace);
            clearSqlElement(context.evalNodes("/mapper/sql"), namespace);
            // reparse mybatis mapper xml file
            reloadXML(newXMLFile);
        } catch (Exception e) {
            logger.error("reload mybatis xml throw exception", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reload mybatis xml file {} success", newXMLFile.getName());
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

    private void reloadXML(File newXMLFile) throws IOException {
        XMLMapperBuilder builder = new XMLMapperBuilder(Files.newInputStream(newXMLFile.toPath()), configuration, newXMLFile.getName(), configuration.getSqlFragments());
        builder.parse();
    }

    @SuppressWarnings("unchecked")
    private void clearMapperRegistry(String namespace) throws NoSuchFieldException, IllegalAccessException {
//        Field field = MapperRegistry.class.getDeclaredField("knownMappers");
//        field.setAccessible(true);
//        Map<Class<?>, MapperProxyFactory<?>> mapConfig = (Map<Class<?>, MapperProxyFactory<?>>) field.get
//        (configuration.getMapperRegistry());
//        Class<?> refreshKey = null;
//
//        for (Map.Entry<Class<?>, MapperProxyFactory<?>> item : mapConfig.entrySet()) {
//            if (item.getKey().getName().contains(namespace)) {
//                refreshKey = item.getKey();
//                break;
//            }
//        }
//
//        if (refreshKey != null) {
//            mapConfig.remove(refreshKey);
//        }
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
            String id = xNode.getStringAttribute("id");
            configuration.getResultMapNames().remove(namespace + "." + id);
        }
    }

    private void clearResultMap(List<XNode> list, String namespace) {
        for (XNode xNode : list) {
            String id = xNode.getStringAttribute("id", xNode.getValueBasedIdentifier());
            configuration.getResultMapNames().remove(id);
            configuration.getResultMapNames().remove(namespace + "." + id);
            clearResultMap(xNode, namespace);
        }
    }

    private void clearResultMap(XNode xNode, String namespace) {
        for (XNode child : xNode.getChildren()) {
            if (Objects.equals("association", child.getName()) || Objects.equals("collection", child.getName()) || Objects.equals("case", child.getName())) {

                if (child.getStringAttribute("select") == null) {
                    configuration.getResultMapNames().remove(child.getStringAttribute("id",
                            child.getValueBasedIdentifier()));
                    configuration.getResultMapNames().remove(namespace + "." + child.getStringAttribute("id",
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
            String id = xNode.getStringAttribute("id");
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
            String id = xNode.getStringAttribute("id");
            configuration.getSqlFragments().remove(id);
            configuration.getSqlFragments().remove(namespace + "." + id);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
