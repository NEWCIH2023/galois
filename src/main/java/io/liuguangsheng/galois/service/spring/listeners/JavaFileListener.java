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

package io.liuguangsheng.galois.service.spring.listeners;

import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.monitor.FileChangedListener;
import io.liuguangsheng.galois.service.spring.SpringAgentService;
import io.liuguangsheng.galois.utils.ClassUtil;
import io.liuguangsheng.galois.utils.FileUtil;

import java.io.File;
import java.util.Objects;

import static io.liuguangsheng.galois.constants.FileType.JAVA_FILE;

/**
 * @author liuguangsheng
 * @since 1.0.0
 **/
@LazyBean(value = "JavaFileListener", manager = SpringAgentService.class, rank = 1)
public class JavaFileListener implements FileChangedListener {
	
	private static final ClassChangedCache classChangedCache = ClassChangedCache.getInstance();
	
	/**
	 * is listener useful for this file object
	 *
	 * @param file the changed file
	 * @return is the listener monitor this file change
	 */
	@Override
	public boolean isSuitable(File file) {
		return Objects.equals(FileUtil.getFileType(file), JAVA_FILE.getFileType());
	}
	
	/**
	 * handler for file created
	 *
	 * @param file the changed file
	 */
	@Override
	public void createdHandle(File file) {
		String className = ClassUtil.getClassNameFromSource(file);
		classChangedCache.hadChanged(className);
	}
	
	/**
	 * handler for file modifed
	 *
	 * @param file the changed file
	 */
	@Override
	public void modifiedHandle(File file) {
		String className = ClassUtil.getClassNameFromSource(file);
		classChangedCache.hadChanged(className);
	}
	
	/**
	 * handler for file deleted
	 *
	 * @param file the changed file
	 */
	@Override
	public void deletedHandle(File file) {
	
	}
	
	@Override
	public String toString() {
		return getClass().getSimpleName();
	}
}
