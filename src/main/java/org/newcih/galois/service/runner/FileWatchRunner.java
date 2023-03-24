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

package org.newcih.galois.service.runner;

import org.newcih.galois.service.FileWatchService;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 需要兼容 SpringBoot 2.0.3版本，该版本的 SpringApplicationRunListener 接口全是抽象方法，没有默认default实现
 *
 * @author liuguangsheng
 * @see org.newcih.galois.service.FileWatchService
 * @since 1.0.0
 */
public class FileWatchRunner implements SpringApplicationRunListener {

  private static final FileWatchService fileWatchService = FileWatchService.getInstance();

  /**
   * Instantiates a new File watch runner.
   *
   * @param rootPath the root path
   */
  public FileWatchRunner(String rootPath) {
    fileWatchService.init(rootPath);
  }

  @Override
  public void starting(ConfigurableBootstrapContext bootstrapContext) {
    // TODO
  }

  @Override
  public void starting() {
    // TODO
  }

  @Override
  public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
      ConfigurableEnvironment environment) {
    // TODO
  }

  @Override
  public void environmentPrepared(ConfigurableEnvironment environment) {
    // TODO
  }

  @Override
  public void contextPrepared(ConfigurableApplicationContext context) {
    // TODO

  }

  @Override
  public void contextLoaded(ConfigurableApplicationContext context) {
    // TODO

  }

  @Override
  public void started(ConfigurableApplicationContext context) {
    fileWatchService.start();
  }

  @Override
  public void running(ConfigurableApplicationContext context) {
    // TODO

  }

  @Override
  public void failed(ConfigurableApplicationContext context, Throwable exception) {
    // TODO

  }
}
