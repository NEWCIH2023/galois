/*
 * MIT License
 *
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
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static io.liuguangsheng.galois.constants.Constant.DOT;
import static io.liuguangsheng.galois.constants.Constant.ID;
import static io.liuguangsheng.galois.constants.Constant.NAMESPACE;

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
	protected Configuration configuration;
	public static final String PARAMETER_MAPS = "parameterMaps";
	public static final String KNOWN_MAPPERS = "knownMappers";
	
	private MyBatisBeanReloader() {
	}
	
	/**
	 * Gets instance.
	 *
	 * @return the instance
	 */
	public static MyBatisBeanReloader getInstance() {
		return mybatisBeanReloder;
	}
	
	/**
	 * update bean
	 *
	 * @param mapperFile xmlFile
	 */
	@Override
	public void updateBean(File mapperFile) {
		
		try (FileInputStream fis = new FileInputStream(mapperFile)) {
			XPathParser parser = new XPathParser(fis, true, configuration.getVariables(), new XMLMapperEntityResolver());
			XNode context = parser.evalNode("/mapper");
			
			String namespace = context.getStringAttribute(NAMESPACE);
			if (namespace == null || namespace.isEmpty()) {
				throw new BuilderException("Mapper's namespace cannot be empty.");
			}
			
			clearMapperRegistry(namespace);
			clearCachedNames(namespace);
			clearBuildStatementFromContext(context.evalNodes("insert|update|select|delete"), namespace);
			clearSqlElement(context.evalNodes("/mapper/sql"), namespace);
			clearResultMapElements(context.evalNodes("/mapper/resultMap"), namespace);
			clearParameterMapElement(context.evalNodes("/mapper/parameterMap"), namespace);
			clearCacheElement(context.evalNode("cache"));
			clearCacheRefElement(context.evalNode("cache-ref"));
			reloadXML(mapperFile);
			
		} catch (Throwable e) {
			logger.error("Reload mybatis mapper by xml file fail.", e);
			return;
		}
		
		logger.info("Reload mybatis mapper by xml file {} success.", mapperFile.getName());
	}
	
	/**
	 * is useful
	 *
	 * @param file file
	 * @return {@link boolean}
	 */
	@Override
	public boolean isUseful(File file) {
		return true;
	}
	
	/**
	 * is prepared
	 *
	 * @return {@link boolean}
	 */
	@Override
	public boolean isPrepared() {
		if (configuration == null) {
			logger.error("MybatisBeanReloader not prepare ready. Configuration object is null.");
			return false;
		}
		
		return true;
	}
	
	/**
	 * reload mabatis mapper xml
	 *
	 * @param mapperFile xmlFile
	 * @throws IOException java.io. i o exception
	 */
	private void reloadXML(File mapperFile) throws IOException {
		InputStream is = Files.newInputStream(mapperFile.toPath());
		XMLMapperBuilder builder = new XMLMapperBuilder(is, configuration, mapperFile.getName(), configuration.getSqlFragments());
		builder.parse();
	}
	
	/**
	 * clear cache ref element
	 *
	 * @param cacheRef cacheRef
	 */
	private void clearCacheRefElement(XNode cacheRef) {
		// nothing to do
	}
	
	/**
	 * clear cache element
	 *
	 * @param cache cache
	 */
	private void clearCacheElement(XNode cache) {
		// nothing to do
	}
	
	/**
	 * clear mapper registry
	 *
	 * @param namespace namespace
	 * @throws NoSuchFieldException   java.lang. no such field exception
	 * @throws IllegalAccessException java.lang. illegal access exception
	 */
	@SuppressWarnings("unchecked")
	private void clearMapperRegistry(String namespace) throws NoSuchFieldException, IllegalAccessException {
		Field field = MapperRegistry.class.getDeclaredField(KNOWN_MAPPERS);
		field.setAccessible(true);
		
		Map<Class<?>, Object> mapConfig = (Map<Class<?>, Object>) field.get(configuration.getMapperRegistry());
		mapConfig.entrySet().removeIf(entry -> entry.getKey().getName().contains(namespace));
	}
	
	/**
	 * clear cached names
	 *
	 * @param namespace namespace
	 */
	private void clearCachedNames(String namespace) {
		configuration.getCacheNames().remove(namespace);
	}
	
	/**
	 * clear parameter map element
	 *
	 * @param list      list
	 * @param namespace namespace
	 */
	@SuppressWarnings({"unchecked"})
	private void clearParameterMapElement(List<XNode> list, String namespace) throws IllegalAccessException, NoSuchFieldException {
		
		for (XNode parameterMapNode : list) {
			String id = parameterMapNode.getStringAttribute(ID);
			id = applyCurrentNamespace(id, false, namespace);
			
			Field parameterMaps = Configuration.class.getField(PARAMETER_MAPS);
			parameterMaps.setAccessible(true);
			
			((Map<String, Object>) parameterMaps.get(configuration)).remove(id);
		}
		
	}
	
	/**
	 * clear result map elements
	 *
	 * @param list      list
	 * @param namespace namespace
	 */
	private void clearResultMapElements(List<XNode> list, String namespace) {
		for (XNode resultMapNode : list) {
			String id = resultMapNode.getStringAttribute(ID, resultMapNode.getValueBasedIdentifier());
			configuration.getResultMapNames().remove(id);
			
			id = applyCurrentNamespace(id, false, namespace);
			configuration.getResultMapNames().remove(id);
		}
	}
	
	/**
	 * clear build statement from context
	 *
	 * @param list      list
	 * @param namespace namespace
	 */
	private void clearBuildStatementFromContext(List<XNode> list, String namespace) {
		String id, keyStatementId;
		
		for (XNode context : list) {
			id = applyCurrentNamespace(context.getStringAttribute(ID), false, namespace);
			keyStatementId = applyCurrentNamespace(context.getStringAttribute(ID) + SelectKeyGenerator.SELECT_KEY_SUFFIX, true, namespace);
			configuration.getKeyGeneratorNames().remove(keyStatementId);
			
			Iterator<MappedStatement> mappedStatementIterator = configuration.getMappedStatements().iterator();
			MappedStatement tmp;
			while (mappedStatementIterator.hasNext()) {
				tmp = mappedStatementIterator.next();
				if (tmp != null && tmp.getId().equals(id)) {
					mappedStatementIterator.remove();
				}
			}
			
		}
	}
	
	/**
	 * clear sql element
	 *
	 * @param list      list
	 * @param namespace namespace
	 */
	private void clearSqlElement(List<XNode> list, String namespace) {
		for (XNode context : list) {
			String id = context.getStringAttribute(ID);
			configuration.getSqlFragments().remove(id);
			
			id = applyCurrentNamespace(id, false, namespace);
			configuration.getSqlFragments().remove(id);
		}
	}
	
	/**
	 * Gets configuration.
	 *
	 * @return the configuration
	 */
	public Configuration getConfiguration() {
		return configuration;
	}
	
	/**
	 * set configuration
	 *
	 * @param configuration configuration
	 */
	@Override
	public void setConfiguration(Configuration configuration) {
		this.configuration = configuration;
	}
	
	/**
	 * apply current namespace
	 *
	 * @param base        base
	 * @param isReference isReference
	 * @param namespace   namespace
	 * @return {@link String}
	 * @see String
	 */
	public String applyCurrentNamespace(String base, boolean isReference, String namespace) {
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
}
