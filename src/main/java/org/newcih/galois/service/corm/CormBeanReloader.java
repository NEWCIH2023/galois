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

package org.newcih.galois.service.corm;

import static org.newcih.galois.constants.Constant.ID;
import static org.newcih.galois.constants.Constant.NAMESPACE;

import com.comtop.corm.builder.xml.XMLMapperBuilder;
import com.comtop.corm.builder.xml.XMLMapperEntityResolver;
import com.comtop.corm.parsing.XNode;
import com.comtop.corm.parsing.XPathParser;
import com.comtop.corm.resource.core.io.FileSystemResource;
import com.comtop.corm.session.Configuration;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import java.util.Objects;
import java.util.Properties;
import java.util.Set;
import org.newcih.galois.service.BeanReloader;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Corm Bean Reloader Service
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public class CormBeanReloader implements BeanReloader<File> {

  /**
   * The constant mybatisBeanReloder.
   */
  public static final CormBeanReloader mybatisBeanReloder = new CormBeanReloader();
  private static final Logger logger = LoggerFactory.getLogger(CormBeanReloader.class);
  /**
   * The Configuration.
   */
  protected Configuration configuration;

  private CormBeanReloader() {
  }

  /**
   * get instance
   *
   * @return {@link CormBeanReloader}
   * @see CormBeanReloader
   */
  public static CormBeanReloader getInstance() {
    return mybatisBeanReloder;
  }

  /**
   * 更新bean实例
   *
   * @param mapperFile mapper xml file
   */
  @Override
  public void updateBean(File mapperFile) {
    if (configuration == null) {
      logger.error("CormBeanRealoder not prepare ready. Configuration object is null.");
      return;
    }

    try (FileInputStream fis = new FileInputStream(mapperFile)) {
      Properties variables = configuration.getVariables();
      XPathParser parser = new XPathParser(fis, true, variables, new XMLMapperEntityResolver());
      XNode context = parser.evalNode("/mapper");
      String namespace = context.getStringAttribute(NAMESPACE);

      // clear mapper cache
      clearLoadedResources(mapperFile.getName());
      clearCachedNames(namespace);
      clearParameterMap(context.evalNodes("/mapper/parameterMap"), namespace);
      clearResultMap(context.evalNodes("/mapper/resultMap"), namespace);
      clearSqlElement(context.evalNodes("/mapper/sql"), namespace);
      // reload mapper by xml file
      reloadXML(mapperFile);
    } catch (Exception e) {
      logger.error("Reload mybatis mapper by xml file fail.", e);
    }

    logger.info("Reload mybatis mapper by xml file {} success.", mapperFile.getName());
  }

  @Override
  public boolean isUseful(File file) {
    return true;
  }

  /**
   * reload xml file
   *
   * @param mapperFile mapperFile
   */
  private void reloadXML(File mapperFile) throws IOException {
    InputStream is = Files.newInputStream(mapperFile.toPath());
    XMLMapperBuilder builder = new XMLMapperBuilder(is, configuration, mapperFile.getName(),
        configuration.getSqlFragments(), new FileSystemResource(mapperFile));
    builder.parse();
  }

  /**
   * clear loaded resources
   *
   * @param fileName fileName
   */
  @SuppressWarnings("rawtypes")
  private void clearLoadedResources(String fileName)
      throws NoSuchFieldException, IllegalAccessException {
    Field loadedResourcesField = configuration.getClass().getDeclaredField("loadedResources");
    loadedResourcesField.setAccessible(true);
    Set loadedResourcesSet = (Set) loadedResourcesField.get(configuration);
    loadedResourcesSet.remove(fileName);
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
   * clear parameter map
   *
   * @param list      list
   * @param namespace namespace
   */
  private void clearParameterMap(List<XNode> list, String namespace) {
    for (XNode xNode : list) {
      String id = xNode.getStringAttribute(ID);
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
      String id = xNode.getStringAttribute(ID, xNode.getValueBasedIdentifier());
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
      if (Objects.equals("association", child.getName()) || Objects.equals("collection",
          child.getName()) || Objects.equals("case", child.getName())) {
        if (child.getStringAttribute("select") == null) {
          configuration.getResultMapNames().remove(child.getStringAttribute(ID,
              child.getValueBasedIdentifier()));
          configuration.getResultMapNames()
              .remove(namespace + "." + child.getStringAttribute(ID,
                  child.getValueBasedIdentifier()));

          if (child.getChildren() != null && !child.getChildren().isEmpty()) {
            clearResultMap(child, namespace);
          }
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
    for (XNode xNode : list) {
      String id = xNode.getStringAttribute(ID);
      configuration.getSqlFragments().remove(id);
      configuration.getSqlFragments().remove(namespace + "." + id);
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
   * Sets configuration.
   *
   * @param configuration the configuration
   */
  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }
}
