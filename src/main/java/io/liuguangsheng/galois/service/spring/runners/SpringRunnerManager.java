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

package io.liuguangsheng.galois.service.spring.runners;

import io.liuguangsheng.galois.service.spring.visitors.SpringApplicationRunListenersVisitor;
import io.liuguangsheng.galois.utils.GaloisLog;
import org.slf4j.Logger;
import org.springframework.boot.SpringApplicationRunListener;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * spring runner manager
 *
 * @author liuguangsheng
 */
public class SpringRunnerManager implements SpringApplicationRunListenersVisitor.NecessaryMethods {

    private static final Logger logger = new GaloisLog(SpringRunnerManager.class);
    private final List<AbstractRunner> runners = new ArrayList<>();

    private static class SpringRunnerManagerHolder {
        private static final SpringRunnerManager instance = new SpringRunnerManager();
    }

    /**
     * Gets instance.
     *
     * @return the instance
     */
    public static SpringRunnerManager getInstance() {
        return SpringRunnerManagerHolder.instance;
    }

    /**
     * Add runner.
     *
     * @param runner the runner
     */
    public void addRunner(AbstractRunner runner) {
        if (runner != null) {
            runners.add(runner);
        }
    }

    @Override
    public List<SpringApplicationRunListener> getRunners() {
        List<SpringApplicationRunListener> result = runners.stream()
                .sorted(Comparator.comparingInt(AbstractRunner::getRank).reversed())
                .collect(Collectors.toList());

        logger.info("Now register these runner in ordered: {}.", result);
        return result;
    }

}
