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

package org.newcih.galois.service.agent.corm;

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
import org.newcih.galois.service.agent.BeanReloader;
import org.newcih.galois.utils.GaloisLog;

/**
 * MyBatis的Mapper重新加载服务类
 *
 * @author liuguangsheng
 * @since 1.0.0
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
   * @param mapperFile mapper xml file
   */
  @Override
  public void updateBean(File mapperFile) {
    if (configuration == null) {
      logger.error("corm had not ready. configuration is null.");
      return;
    }

    try (FileInputStream fis = new FileInputStream(mapperFile)) {
      Properties variables = configuration.getVariables();
      XPathParser parser = new XPathParser(fis, true, variables, new XMLMapperEntityResolver());
      XNode context = parser.evalNode("/mapper");
      String namespace = context.getStringAttribute(NAMESPACE);
      // 清空Mybatis缓存
      clearMapperRegistry(namespace);
      clearLoadedResources(mapperFile.getName());
      clearCachedNames(namespace);
      clearParameterMap(context.evalNodes("/mapper/parameterMap"), namespace);
      clearResultMap(context.evalNodes("/mapper/resultMap"), namespace);
      clearSqlElement(context.evalNodes("/mapper/sql"), namespace);
      // 使MyBatis重新加载xml配置的mapper对象
      reloadXML(mapperFile);
    } catch (Exception e) {
      logger.error("reload mybatis xml throw exception", e);
    }

    if (logger.isDebugEnabled()) {
      logger.debug("reload mybatis xml file {} success", mapperFile.getName());
    }
  }

  @Override
  public boolean isUseful(File file) {
    return true;
  }

  private void reloadXML(File mapperFile) throws IOException {
    InputStream is = Files.newInputStream(mapperFile.toPath());
    XMLMapperBuilder builder = new XMLMapperBuilder(is, getConfiguration(), mapperFile.getName(),
        getConfiguration().getSqlFragments(), new FileSystemResource(mapperFile));
    builder.parse();
  }

  @SuppressWarnings("unchecked")
  private void clearMapperRegistry(String namespace)
      throws NoSuchFieldException, IllegalAccessException {
//        Field field = MapperRegistry.class.getDeclaredField("knownMappers");
//        field.setAccessible(true);
//        Map<Class<?>, Object> mapConfig = (Map<Class<?>, Object>) field.get(getConfiguration().getmapp());
//        Class<?> refreshKey = null;
//
//        for (Map.Entry<Class<?>, Object> item : mapConfig.entrySet()) {
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
  private void clearLoadedResources(String fileName)
      throws NoSuchFieldException, IllegalAccessException {
    Field loadedResourcesField = getConfiguration().getClass().getDeclaredField("loadedResources");
    loadedResourcesField.setAccessible(true);
    Set loadedResourcesSet = (Set) loadedResourcesField.get(getConfiguration());
    loadedResourcesSet.remove(fileName);
  }

  private void clearCachedNames(String namespace) {
    getConfiguration().getCacheNames().remove(namespace);
  }

  private void clearParameterMap(List<XNode> list, String namespace) {
    for (XNode xNode : list) {
      String id = xNode.getStringAttribute(ID);
      getConfiguration().getResultMapNames().remove(namespace + "." + id);
    }
  }

  private void clearResultMap(List<XNode> list, String namespace) {
    for (XNode xNode : list) {
      String id = xNode.getStringAttribute(ID, xNode.getValueBasedIdentifier());
      getConfiguration().getResultMapNames().remove(id);
      getConfiguration().getResultMapNames().remove(namespace + "." + id);
      clearResultMap(xNode, namespace);
    }
  }

  private void clearResultMap(XNode xNode, String namespace) {
    for (XNode child : xNode.getChildren()) {
      if (Objects.equals("association", child.getName()) || Objects.equals("collection",
          child.getName()) || Objects.equals("case", child.getName())) {
        if (child.getStringAttribute("select") == null) {
          getConfiguration().getResultMapNames().remove(child.getStringAttribute(ID,
              child.getValueBasedIdentifier()));
          getConfiguration().getResultMapNames()
              .remove(namespace + "." + child.getStringAttribute(ID,
                  child.getValueBasedIdentifier()));

          if (child.getChildren() != null && !child.getChildren().isEmpty()) {
            clearResultMap(child, namespace);
          }
        }

      }
    }
  }

  private void clearSqlElement(List<XNode> list, String namespace) {
    for (XNode xNode : list) {
      String id = xNode.getStringAttribute(ID);
      getConfiguration().getSqlFragments().remove(id);
      getConfiguration().getSqlFragments().remove(namespace + "." + id);
    }
  }

  public Configuration getConfiguration() {
    return configuration;
  }

  public void setConfiguration(Configuration configuration) {
    this.configuration = configuration;
  }
}
