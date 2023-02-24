package org.newcih.service.agent.frame.mybatis;

import org.apache.ibatis.binding.MapperProxyFactory;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.newcih.utils.GaloisLog;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.*;

/**
 * MyBatis的Mapper重新加载服务类
 */
public class MyBatisBeanReloader {

    private static final GaloisLog LOGGER = GaloisLog.getLogger(MyBatisBeanReloader.class);
    private static final MyBatisBeanReloader MYBATIS_BEAN_RELOADER = new MyBatisBeanReloader();
    private Configuration configuration;

    private MyBatisBeanReloader() {
    }

    /**
     * 通过Transform方式注入对象
     *
     * @param configuration
     */
    public void registerConfiguration(Configuration configuration) {
        getInstance().configuration = configuration;
    }

    /**
     * 获取单例实例
     *
     * @return
     */
    public static MyBatisBeanReloader getInstance() {
        return MYBATIS_BEAN_RELOADER;
    }

    public void addBean(File newXMLFile) {
        try (FileInputStream fis = new FileInputStream(newXMLFile)) {
            XPathParser parser = new XPathParser(fis, true, configuration.getVariables(), new XMLMapperEntityResolver());
            XNode context = parser.evalNode("/mapper");
            String namespace = context.getStringAttribute("namespace");
            // 清除缓存
            clearMapperRegistry(namespace);
            clearLoadedResources(newXMLFile.getName());
            clearCachedNames(namespace);
            clearParameterMap(context.evalNodes("/mapper/parameterMap"), namespace);
            clearResultMap(context.evalNodes("/mapper/resultMap"), namespace);
            clearKeyGenerators(context.evalNodes("insert|update|select|delete"), namespace);
            clearSqlElement(context.evalNodes("/mapper/sql"), namespace);
            // 重新加载
            reloadXML(newXMLFile);
        } catch (Exception e) {
            LOGGER.error("重新加载MyBatis的XML文件发生异常", e);
        }
    }

    private void reloadXML(File newXMLFile) throws IOException {
        XMLMapperBuilder builder = new XMLMapperBuilder(Files.newInputStream(newXMLFile.toPath()), configuration,
                newXMLFile.getName(), configuration.getSqlFragments());
        builder.parse();
    }

    @SuppressWarnings("unchecked")
    private void clearMapperRegistry(String namespace) throws NoSuchFieldException, IllegalAccessException {
        Field field = MapperRegistry.class.getDeclaredField("knownMappers");
        field.setAccessible(true);
        Map<Class<?>, MapperProxyFactory<?>> mapConfig = (Map<Class<?>, MapperProxyFactory<?>>) field.get(configuration.getMapperRegistry());
        Class<?> refreshKey = null;

        for (Map.Entry<Class<?>, MapperProxyFactory<?>> item : mapConfig.entrySet()) {
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
                    configuration.getResultMapNames().remove(child.getStringAttribute("id", child.getValueBasedIdentifier()));
                    configuration.getResultMapNames().remove(namespace + "." + child.getStringAttribute("id", child.getValueBasedIdentifier()));

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

}
