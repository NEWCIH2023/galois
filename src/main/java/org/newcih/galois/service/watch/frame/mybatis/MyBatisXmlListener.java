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

package org.newcih.galois.service.watch.frame.mybatis;

import org.newcih.galois.service.agent.frame.mybatis.MyBatisBeanReloader;
import org.newcih.galois.service.watch.frame.FileChangedListener;
import org.newcih.galois.utils.FileUtils;
import org.newcih.galois.utils.GaloisLog;

import java.io.File;
import java.util.Objects;

import static org.newcih.galois.constants.FileTypeConstant.XML_FILE;

/**
 * MyBatis的XML文件变更监听处理
 */
public class MyBatisXmlListener implements FileChangedListener {

    private static final GaloisLog logger = GaloisLog.getLogger(MyBatisXmlListener.class);

    private static final MyBatisBeanReloader reloader = MyBatisBeanReloader.getInstance();

    @Override
    public boolean validFile(File file) {
        return Objects.equals(FileUtils.getFileType(file), XML_FILE);
    }

    /**
     * determine whether a file is mybatis mapper xml file
     *
     * @param file
     * @return
     */
    private boolean isMybatisXml(File file) {
// TODO
        return false;
    }

    @Override
    public void fileCreatedHandle(File file) {
        reloader.updateBean(file);
    }

    @Override
    public void fileModifiedHandle(File file) {
        reloader.updateBean(file);
    }

    @Override
    public void fileDeletedHandle(File file) {

    }
}
