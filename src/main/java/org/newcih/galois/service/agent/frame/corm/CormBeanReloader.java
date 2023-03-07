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

import com.comtop.corm.builder.xml.XMLMapperEntityResolver;
import com.comtop.corm.extend.xml.XMLMapperReload;
import com.comtop.corm.parsing.XNode;
import com.comtop.corm.parsing.XPathParser;
import com.comtop.corm.session.Configuration;
import org.newcih.galois.service.agent.BeanReloader;
import org.newcih.galois.utils.GaloisLog;

import java.io.File;
import java.io.FileInputStream;
import java.util.Properties;

import static org.newcih.galois.constants.Constant.NAMESPACE;

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
            Properties variables = configuration.getVariables();
            XPathParser parser = new XPathParser(fis, true, variables, new XMLMapperEntityResolver());
            XNode context = parser.evalNode("/mapper");
            String namespace = context.getStringAttribute(NAMESPACE);
            // clear cache
            XMLMapperReload.parseSqlXml(namespace);
        } catch (Exception e) {
            logger.error("reload mybatis xml throw exception", e);
        }

        if (logger.isDebugEnabled()) {
            logger.debug("reload mybatis xml file {} success", newXMLFile.getName());
        }
    }

    @Override
    public boolean isUseful(File file) {
        return true;
    }

    public Configuration getConfiguration() {
        return configuration;
    }

    public void setConfiguration(Configuration configuration) {
        this.configuration = configuration;
    }
}
