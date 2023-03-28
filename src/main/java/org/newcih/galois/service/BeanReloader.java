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

package org.newcih.galois.service;

/**
 * 类实例对象重载服务
 *
 * @param <T> the type parameter
 * @author liuguangsheng
 * @since 1.0.0
 */
public interface BeanReloader<T> extends LazyInit {

  /**
   * 使框架重新加载类实例对象
   *
   * @param object 待更新的实例对象
   */
  void updateBean(T object);

  /**
   * Is useful boolean.
   *
   * @param object 用来判断当前实例对象是否可以被框架重新加载
   * @return 验证结果 boolean
   */
  boolean isUseful(T object);

  /**
   * Lazy init after all necessary class was loaded
   *
   * @return
   */
  @Override
  Class<? extends BeanReloader<?>> lazyInit();
}
