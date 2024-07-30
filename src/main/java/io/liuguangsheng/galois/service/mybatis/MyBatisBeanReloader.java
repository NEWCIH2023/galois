/*
 * MIT License
 *
 * Copyright (c) [2024] [$user]
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

import io.liuguangsheng.galois.service.BeanReloader;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.mybatis.visitors.MyBatisConfigurationVisitor;
import io.liuguangsheng.galois.utils.GaloisLog;
import org.apache.ibatis.binding.MapperRegistry;
import org.apache.ibatis.builder.BuilderException;
import org.apache.ibatis.builder.xml.XMLMapperBuilder;
import org.apache.ibatis.builder.xml.XMLMapperEntityResolver;
import org.apache.ibatis.executor.keygen.SelectKeyGenerator;
import org.apache.ibatis.mapping.MappedStatement;
import org.apache.ibatis.parsing.XNode;
import org.apache.ibatis.parsing.XPathParser;
import org.apache.ibatis.session.Configuration;
import org.slf4j.Logger;
import org.springframework.core.io.PathResource;
import org.springframework.core.io.Resource;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;
import java.util.stream.Collectors;

import static io.liuguangsheng.galois.constants.Constant.*;

/**
 * The type My batis bean reloader.
 */
@SuppressWarnings("unchecked")
@LazyBean(value = "MyBatisBeanReloader", manager = MyBatisAgentService.class)
public class MyBatisBeanReloader implements BeanReloader<File>, MyBatisConfigurationVisitor.NecessaryMethods {
    private static final Logger logger = new GaloisLog(MyBatisBeanReloader.class);
    private static final String NODE_MAPPER = "/mapper";
    protected Configuration configuration;

    private MyBatisBeanReloader() {
    }

    public static MyBatisBeanReloader getInstance() {
        return MyBatisBeanReloaderHolder.instance;
    }

    public static String applyCurrentNamespace(String base, boolean isReference, String namespace) {
        if (base == null) {
            return null;
        }

        if (isReference) {
            if (base.contains(DOT)) {
                return base;
            }
        } else {
            if (base.startsWith(namespace + DOT)) {
                return base;
            }
            if (base.contains(DOT)) {
                throw new BuilderException("Dots are not allowed in element names, please remove it from " + base);
            }
        }

        return namespace + DOT + base;
    }

    @Override
    public void updateBean(File mapperFile) {
        try {
            Resource mapperLocation = new PathResource(mapperFile.toPath());
            XPathParser parser = new XPathParser(mapperLocation.getInputStream(), true, configuration.getVariables(),
                    new XMLMapperEntityResolver());
            XNode context = parser.evalNode(NODE_MAPPER);
            String namespace = context.getStringAttribute(NAMESPACE);

            updateSingleBean(mapperLocation, namespace);
            Set<Resource> files = getAllNamespaceFile(namespace);
            Set<Resource> mappers = files.stream()
                    .filter(resource -> !resource.toString()
                            .contains(Objects.requireNonNull(mapperLocation.getFilename())))
                    .collect(Collectors.toSet());

            for (Resource resource : mappers) {
                updateSingleBean(resource, namespace);
            }

            logger.info("Reload mybatis mapper by namespace {} success.", namespace);
        } catch (Throwable e) {
            logger.error("Update MyBatis bean fail.", e);
        }
    }

    private void updateSingleBean(Resource mapperLocation, String namespace) {
        if (mapperLocation == null || namespace == null || namespace.trim().isEmpty()) {
            logger.error("Invalid input parameters for updating single bean.");
            return;
        }

        try (InputStream in = mapperLocation.getInputStream()) {
            XPathParser parser = new XPathParser(in, true, configuration.getVariables(), new XMLMapperEntityResolver());
            XNode context = parser.evalNode("/mapper");

            clearLoadedResources(mapperLocation);
            clearMapperRegistry(namespace);
            clearCachedNames(namespace);
            clearBuildStatementFromContext(context.evalNodes("insert|update|select|delete"), namespace);
            clearSqlElement(context.evalNodes("/mapper/sql"), namespace);
            clearResultMapElements(context.evalNodes("/mapper/resultMap"), namespace);
            clearParameterMapElement(context.evalNodes("/mapper/parameterMap"), namespace);
            clearCacheElement(context.evalNode("cache"));
            clearCacheRefElement(namespace);
            reloadXML(mapperLocation);
        } catch (Throwable e) {
            logger.error("Reload mybatis mapper by xml file fail.", e);
        }
    }

    @Override
    public boolean isSuitable(File file) {
        return true;
    }

    /**
     * 检查当前实例是否已准备好可以使用。
     * 此方法用于确定MybatisBeanReloader是否已经成功初始化并配置了必要的设置。
     *
     * @return 如果已准备好，则返回true；如果未准备好，例如配置对象为null，则返回false。
     */
    @Override
    public boolean isReady() {
        // 检查配置对象是否为null，如果是，则记录错误并返回false
        if (configuration == null) {
            logger.error("MybatisBeanReloader not prepare ready. Configuration object is null.");
            return false;
        }

        // 如果配置对象不为null，说明已经准备好，返回true
        return true;
    }


    private Set<Resource> getAllNamespaceFile(String namespace) {
        try {
            Field mappedStatementsField = configuration.getClass().getDeclaredField(MAPPED_STATEMENTS);
            mappedStatementsField.setAccessible(true);
            Map<String, Object> mappedStatements = (Map<String, Object>) mappedStatementsField.get(configuration);

            return mappedStatements.values()
                    .stream()
                    .filter(statement -> statement.getClass().equals(MappedStatement.class))
                    .map(statement -> (MappedStatement) statement)
                    .filter(statement -> statement.getId().contains(namespace))
                    .map(statement -> {
                        String tmpPath = statement.getResource();
                        if (tmpPath.contains("[")) {
                            tmpPath = tmpPath.substring(tmpPath.indexOf('[') + 1, tmpPath.lastIndexOf(']'));
                        }
                        return new PathResource(tmpPath);
                    })
                    .collect(Collectors.toSet());
        } catch (Throwable e) {
            logger.error("Get all mapper in namespace {} fail.", namespace, e);
        }

        return new HashSet<>();
    }

    private void reloadXML(Resource mapperLocation) throws IOException {
        XMLMapperBuilder builder = new XMLMapperBuilder(mapperLocation.getInputStream(), configuration,
                mapperLocation.toString(), configuration.getSqlFragments());
        builder.parse();
    }

    @SuppressWarnings("unchecked")
    private void clearLoadedResources(Resource mapperLocation) {
        try {
            Field loadedResourcesField = configuration.getClass().getDeclaredField(LOADED_RESOURCES);
            loadedResourcesField.setAccessible(true);
            Set<String> loadedResources = (Set<String>) loadedResourcesField.get(configuration);
            loadedResources.remove(mapperLocation.toString());
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    @SuppressWarnings("unchecked")
    private void clearCacheRefElement(String namespace) {
        try {
            Field cacheRefMapField = configuration.getClass().getDeclaredField(CACHE_REF_MAP);
            cacheRefMapField.setAccessible(true);
            Map<String, String> cacheRefMap = (Map<String, String>) cacheRefMapField.get(configuration);
            cacheRefMap.remove(namespace);
        } catch (NoSuchFieldException | IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

    private void clearCacheElement(XNode cache) {
        // nothing to do
    }

    @SuppressWarnings("unchecked")
    private void clearMapperRegistry(String namespace) throws NoSuchFieldException, IllegalAccessException {
        Field field = MapperRegistry.class.getDeclaredField(KNOWN_MAPPERS);
        field.setAccessible(true);
        Map<Class<?>, Object> mapConfig = (Map<Class<?>, Object>) field.get(configuration.getMapperRegistry());
        mapConfig.entrySet().removeIf(entry -> entry.getKey().getName().contains(namespace));
    }

    private void clearCachedNames(String namespace) {
        configuration.getCacheNames().remove(namespace);
    }

    @SuppressWarnings({"unchecked"})
    private void clearParameterMapElement(List<XNode> list, String namespace) throws IllegalAccessException,
            NoSuchFieldException {

        String baseId, namespaceId;
        for (XNode parameterMapNode : list) {
            baseId = parameterMapNode.getStringAttribute(ID);
            namespaceId = applyCurrentNamespace(baseId, false, namespace);
            Field parameterMaps = Configuration.class.getField(PARAMETER_MAPS);
            parameterMaps.setAccessible(true);
            ((Map<String, Object>) parameterMaps.get(configuration)).remove(namespaceId);
        }
    }

    private void clearResultMapElements(List<XNode> list, String namespace) {
        String baseId, namespaceId;
        for (XNode resultMapNode : list) {
            baseId = resultMapNode.getStringAttribute(ID, resultMapNode.getValueBasedIdentifier());
            namespaceId = applyCurrentNamespace(baseId, false, namespace);
            configuration.getResultMapNames().remove(baseId);
            configuration.getResultMapNames().remove(namespaceId);
        }
    }

    private void clearBuildStatementFromContext(List<XNode> list, String namespace) {
        try {
            String baseId, namespaceId, keyStatementId;
            Field mappedStatementsField = configuration.getClass().getDeclaredField(MAPPED_STATEMENTS);
            mappedStatementsField.setAccessible(true);
            Map<String, Object> mappedStatements = (Map<String, Object>) mappedStatementsField.get(configuration);

            for (XNode context : list) {
                baseId = context.getStringAttribute(ID);
                namespaceId = applyCurrentNamespace(baseId, false, namespace);
                keyStatementId = applyCurrentNamespace(baseId + SelectKeyGenerator.SELECT_KEY_SUFFIX, true, namespace);
                configuration.getKeyGeneratorNames().remove(keyStatementId);
                mappedStatements.remove(namespaceId);
            }
        } catch (Throwable e) {
            logger.error("Clear BuildStatement fail.", e);
        }
    }

    private void clearSqlElement(List<XNode> list, String namespace) {
        String baseId, namespaceId;
        for (XNode context : list) {
            baseId = context.getStringAttribute(ID);
            namespaceId = applyCurrentNamespace(baseId, false, namespace);
            configuration.getSqlFragments().remove(baseId);
            configuration.getSqlFragments().remove(namespaceId);
        }
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    @Override
    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }

    private static class MyBatisBeanReloaderHolder {
        private static final MyBatisBeanReloader instance = new MyBatisBeanReloader();
    }
}
