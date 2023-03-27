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

package org.newcih.galois.service.runners;

import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.ConfigurableBootstrapContext;
import org.springframework.boot.SpringApplicationRunListener;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ConfigurableApplicationContext;
import org.springframework.core.env.ConfigurableEnvironment;

/**
 * 兼容2.0.3与2.5.5版本的SpringBoot，在低版本中，这个接口没有任何默认实现，而在2.5.5版本中的SpringBoot，这个接口的所有方法都有默认实现
 *
 * @author liuguangsheng
 **/
public abstract class AbstractRunner implements SpringApplicationRunListener {

  /**
   * the rank mean which runer will run first if got a higher rank value
   */
  protected int rank;

  @Override
  public void starting(ConfigurableBootstrapContext bootstrapContext) {
    // TODO
  }

  /**
   * Called immediately when the run method has first started. Can be used for very early
   * initialization.
   */
  @Override
  public void starting() {
    // TODO
  }

  @Override
  public void environmentPrepared(ConfigurableBootstrapContext bootstrapContext,
      ConfigurableEnvironment environment) {
    // TODO
  }

  /**
   * Called once the environment has been prepared, but before the {@link ApplicationContext} has
   * been created.
   *
   * @param environment the environment
   */
  @Override
  public void environmentPrepared(ConfigurableEnvironment environment) {
    // TODO
  }

  /**
   * Called once the {@link ApplicationContext} has been created and prepared, but before sources
   * have been loaded.
   *
   * @param context the application context
   */
  @Override
  public void contextPrepared(ConfigurableApplicationContext context) {
    // TODO
  }

  /**
   * Called once the application context has been loaded but before it has been refreshed.
   *
   * @param context the application context
   */
  @Override
  public void contextLoaded(ConfigurableApplicationContext context) {
    // TODO
  }

  /**
   * The context has been refreshed and the application has started but
   * {@link CommandLineRunner CommandLineRunners} and {@link ApplicationRunner ApplicationRunners}
   * have not been called.
   *
   * @param context the application context.
   * @since 2.0.0
   */
  @Override
  public void started(ConfigurableApplicationContext context) {
    // TODO
  }

  /**
   * Called immediately before the run method finishes, when the application context has been
   * refreshed and all {@link CommandLineRunner CommandLineRunners} and
   * {@link ApplicationRunner ApplicationRunners} have been called.
   *
   * @param context the application context.
   * @since 2.0.0
   */
  @Override
  public void running(ConfigurableApplicationContext context) {
    // TODO
  }

  /**
   * Called when a failure occurs when running the application.
   *
   * @param context   the application context or {@code null} if a failure occurred before the
   *                  context was created
   * @param exception the failure
   * @since 2.0.0
   */
  @Override
  public void failed(ConfigurableApplicationContext context, Throwable exception) {
    // TODO
  }

  public int getRank() {
    return rank;
  }

  public void setRank(int rank) {
    this.rank = rank;
  }
}
