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

package org.newcih.galois.service;

import java.io.File;

/**
 * file monitor service interface
 *
 * @author liuguangsheng
 * @since 1.0.0
 */
public interface FileChangedListener {

  /**
   * is listener useful for this file object
   *
   * @param file the changed file
   * @return is the listener monitor this file change
   */
  boolean isUseful(File file);

  /**
   * handler for file created
   *
   * @param file the changed file
   */
  void createdHandle(File file);

  /**
   * handler for file modifed
   *
   * @param file the changed file
   */
  void modifiedHandle(File file);

  /**
   * handler for file deleted
   *
   * @param file the changed file
   */
  void deletedHandle(File file);
}
