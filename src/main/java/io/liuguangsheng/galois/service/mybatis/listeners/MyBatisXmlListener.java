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

package io.liuguangsheng.galois.service.mybatis.listeners;

import io.liuguangsheng.galois.constants.FileType;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.monitor.FileChangedListener;
import io.liuguangsheng.galois.service.mybatis.MyBatisAgentService;
import io.liuguangsheng.galois.service.mybatis.MyBatisBeanReloader;
import io.liuguangsheng.galois.utils.FileUtil;
import io.liuguangsheng.galois.utils.GaloisLog;
import org.slf4j.Logger;
import org.w3c.dom.Document;
import org.w3c.dom.DocumentType;
import org.xml.sax.InputSource;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.ByteArrayInputStream;
import java.io.File;


/**
 * MyBatis的XML文件变更监听处理
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
@LazyBean(value = "MyBatisXmlListener", manager = MyBatisAgentService.class)
public class MyBatisXmlListener implements FileChangedListener {
	
	/**
	 * The constant DOC_TYPE.
	 */
	public static final String DOC_TYPE = "mapper";
	private static final Logger logger = new GaloisLog(MyBatisXmlListener.class);
	private static final MyBatisBeanReloader reloader = MyBatisBeanReloader.getInstance();
	
	@Override
	public boolean isUseful(File file) {
		boolean fileTypeCheck = FileUtil.validFileType(file, FileType.XML_FILE);
		
		if (!fileTypeCheck) {
			return false;
		}
		
		// check xml file node contains mapper
		try {
			DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
			dbf.setValidating(false);
			DocumentBuilder db = dbf.newDocumentBuilder();
			// do no validate dtd
			db.setEntityResolver(((publicId, systemId) -> new InputSource(new ByteArrayInputStream(new byte[0]))));
			Document document = db.parse(file);
			DocumentType documentType = document.getDoctype();
			return documentType != null && documentType.toString().contains(DOC_TYPE);
			
		} catch (Throwable e) {
			logger.error("Parse xml file fail. Check it's file type.", e);
			return false;
		}
	}
	
	@Override
	public void createdHandle(File file) {
		if (logger.isDebugEnabled()) {
			logger.debug("MybatisXmlListener detect file created: {}", file.getName());
		}
		
		reloader.updateBean(file);
	}
	
	@Override
	public void modifiedHandle(File file) {
		if (logger.isDebugEnabled()) {
			logger.debug("MybatisXmlListener detect file modified: {}", file.getName());
		}
		
		reloader.updateBean(file);
	}
	
	@Override
	public void deletedHandle(File file) {
	
	}
	
	@Override
	public String toString() {
		return "MyBatisXmlListener";
	}
}
