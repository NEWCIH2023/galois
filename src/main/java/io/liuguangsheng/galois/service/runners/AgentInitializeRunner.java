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

package io.liuguangsheng.galois.service.runners;

import io.liuguangsheng.galois.service.AgentService;
import io.liuguangsheng.galois.service.BeanReloader;
import io.liuguangsheng.galois.service.annotation.LazyBean;
import io.liuguangsheng.galois.service.monitor.ApacheFileWatchService;
import io.liuguangsheng.galois.service.monitor.FileChangedListener;
import io.liuguangsheng.galois.service.monitor.FileWatchService;
import io.liuguangsheng.galois.utils.ClassUtil;
import io.liuguangsheng.galois.utils.GaloisLog;
import org.slf4j.Logger;
import org.springframework.context.ConfigurableApplicationContext;

import java.lang.reflect.Modifier;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static io.liuguangsheng.galois.constants.ClassNameConstant.SERVICE_PACKAGE;

/**
 * agent service init runner
 *
 * @author liuguangsheng
 */
public class AgentInitializeRunner extends AbstractRunner {
	
	private static final FileWatchService fileWatchService = ApacheFileWatchService.getInstance();
	private static final Logger logger = new GaloisLog(AgentInitializeRunner.class);
	
	/**
	 * Instantiates a new Agent service init runner.
	 */
	public AgentInitializeRunner() {
		setRank(0);
	}
	
	@Override
	public void started(ConfigurableApplicationContext context) {
		if (!isCanInvoke()) {
			return;
		}
		
		logger.info("{} with context {} is {}.", getClass().getSimpleName(), context.getId(), "started");
		
		try {
			Set<Class<?>> lazyBeanFactorys = ClassUtil.scanAnnotationClass(SERVICE_PACKAGE, LazyBean.class);
			Set<AgentService> agentServices = ClassUtil.scanBaseClass(SERVICE_PACKAGE, AgentService.class)
					.stream()
					.filter(clazz -> !Modifier.isAbstract(clazz.getModifiers()))
					.map(clazz -> (AgentService) ClassUtil.getInstance(clazz))
					.collect(Collectors.toSet());
			
			Map<String, FileChangedListener> tmpRankMap = new HashMap<>(64);
			
			for (AgentService agentService : agentServices) {
				if (!agentService.isUseful()) {
					continue;
				}
				
				for (Class<?> factory : lazyBeanFactorys) {
					LazyBean lazyBean = factory.getAnnotation(LazyBean.class);
					boolean byManager = lazyBean.manager().equals(agentService.getClass());
					if (!ClassUtil.isClass(factory) || !byManager) {
						continue;
					}
					
					if (BeanReloader.class.isAssignableFrom(factory)) {
						BeanReloader<?> beanReloader = (BeanReloader<?>) ClassUtil.getInstance(factory);
						if (beanReloader != null && beanReloader.isPrepared()) {
							agentService.setBeanReloader(beanReloader);
						}
					} else if (FileChangedListener.class.isAssignableFrom(factory)) {
						FileChangedListener listener = (FileChangedListener) ClassUtil.getInstance(factory);
						agentService.registerFileChangedListener(listener);
						tmpRankMap.put(lazyBean.value() + "_" + lazyBean.rank(), listener);
					}
				}
			}
			
			tmpRankMap.keySet().stream()
					.sorted((a, b) -> {
						int aRank = Integer.parseInt(a.substring(a.indexOf("_") + 1));
						int bRank = Integer.parseInt(b.substring(b.indexOf("_") + 1));
						return bRank - aRank;
					})
					.forEach(key -> fileWatchService.registerListener(tmpRankMap.get(key)));
			
			fileWatchService.init();
			fileWatchService.start();
		} catch (Exception e) {
			logger.error("AgentInitializeRunner invoke failed.", e);
		}
	}
}
