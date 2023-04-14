/*
 * Copyright 2002-2018 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.liuguangsheng.galois.demo.asm;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.locks.ReentrantReadWriteLock;
import java.util.function.BiConsumer;
import org.springframework.aop.support.AopUtils;
import org.springframework.beans.factory.BeanFactoryUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.core.MethodIntrospector;
import org.springframework.lang.Nullable;
import org.springframework.util.Assert;
import org.springframework.util.ClassUtils;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerMapping;
import org.springframework.web.servlet.handler.AbstractHandlerMapping;
import org.springframework.web.servlet.handler.HandlerMethodMappingNamingStrategy;

/**
 * Abstract base class for {@link HandlerMapping} implementations that define
 * a mapping between a request and a {@link HandlerMethod}.
 *
 * <p>For each registered handler method, a unique mapping is maintained with
 * subclasses defining the details of the mapping type {@code <T>}.
 *
 * @param <T> the mapping for a {@link HandlerMethod} containing the conditions
 *            needed to match the handler method to incoming request.
 * @author Arjen Poutsma
 * @author Rossen Stoyanchev
 * @author Juergen Hoeller
 * @since 3.1
 */
public abstract class AbstractHandlerMethodMapping<T> extends AbstractHandlerMapping implements InitializingBean {

    /**
     * Bean name prefix for target beans behind scoped proxies. Used to exclude those
     * targets from handler method detection, in favor of the corresponding proxies.
     * <p>We're not checking the autowire-candidate status here, which is how the
     * proxy target filtering problem is being handled at the autowiring level,
     * since autowire-candidate may have been turned to {@code false} for other
     * reasons, while still expecting the bean to be eligible for handler methods.
     * <p>Originally defined in {@link org.springframework.aop.scope.ScopedProxyUtils}
     * but duplicated here to avoid a hard dependency on the spring-aop module.
     */
    private static final String SCOPED_TARGET_NAME_PREFIX = "scopedTarget.";

    private static final HandlerMethod PREFLIGHT_AMBIGUOUS_MATCH =
            new HandlerMethod(new EmptyHandler(), ClassUtils.getMethod(EmptyHandler.class, "handle"));

    private static final CorsConfiguration ALLOW_CORS_CONFIG = new CorsConfiguration();

    static {
        ALLOW_CORS_CONFIG.addAllowedOrigin("*");
        ALLOW_CORS_CONFIG.addAllowedMethod("*");
        ALLOW_CORS_CONFIG.addAllowedHeader("*");
        ALLOW_CORS_CONFIG.setAllowCredentials(true);
    }


    private boolean detectHandlerMethodsInAncestorContexts = false;

    @Nullable
    private HandlerMethodMappingNamingStrategy<T> namingStrategy;

    private final MappingRegistry mappingRegistry = new MappingRegistry();


    /**
     * Whether to detect handler methods in beans in ancestor ApplicationContexts.
     * <p>Default is "false": Only beans in the current ApplicationContext are
     * considered, i.e. only in the context that this HandlerMapping itself
     * is defined in (typically the current DispatcherServlet's context).
     * <p>Switch this flag on to detect handler beans in ancestor contexts
     * (typically the Spring root WebApplicationContext) as well.
     */
    public void setDetectHandlerMethodsInAncestorContexts(boolean detectHandlerMethodsInAncestorContexts) {
        this.detectHandlerMethodsInAncestorContexts = detectHandlerMethodsInAncestorContexts;
    }

    /**
     * Configure the naming strategy to use for assigning a default name to every
     * mapped handler method.
     * <p>The default naming strategy is based on the capital letters of the
     * class name followed by "#" and then the method name, e.g. "TC#getFoo"
     * for a class named TestController with method getFoo.
     */
    public void setHandlerMethodMappingNamingStrategy(HandlerMethodMappingNamingStrategy<T> namingStrategy) {
        this.namingStrategy = namingStrategy;
    }

    /**
     * Return the configured naming strategy or {@code null}.
     */
    @Nullable
    public HandlerMethodMappingNamingStrategy<T> getNamingStrategy() {
        return this.namingStrategy;
    }

    /**
     * Return a (read-only) map with all mappings and HandlerMethod's.
     */
    public Map<T, HandlerMethod> getHandlerMethods() {
        this.mappingRegistry.acquireReadLock();
        try {
            return Collections.unmodifiableMap(this.mappingRegistry.getMappings());
        } finally {
            this.mappingRegistry.releaseReadLock();
        }
    }

    /**
     * Return the handler methods for the given mapping name.
     *
     * @param mappingName the mapping name
     * @return a list of matching HandlerMethod's or {@code null}; the returned
     * list will never be modified and is safe to iterate.
     * @see #setHandlerMethodMappingNamingStrategy
     */
    @Nullable
    public List<HandlerMethod> getHandlerMethodsForMappingName(String mappingName) {
        return this.mappingRegistry.getHandlerMethodsByMappingName(mappingName);
    }

    /**
     * Return the internal mapping registry. Provided for testing purposes.
     */
    MappingRegistry getMappingRegistry() {
        return this.mappingRegistry;
    }

    /**
     * Register the given mapping.
     * <p>This method may be invoked at runtime after initialization has completed.
     *
     * @param mapping the mapping for the handler method
     * @param handler the handler
     * @param method  the method
     */
    public void registerMapping(T mapping, Object handler, Method method) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    /**
     * Un-register the given mapping.
     * <p>This method may be invoked at runtime after initialization has completed.
     *
     * @param mapping the mapping to unregister
     */
    public void unregisterMapping(T mapping) {
        this.mappingRegistry.unregister(mapping);
    }


    // Handler method detection

    /**
     * Detects handler methods at initialization.
     *
     * @see #initHandlerMethods
     */
    @Override
    public void afterPropertiesSet() {
        initHandlerMethods();
    }

    /**
     * Scan beans in the ApplicationContext, detect and register handler methods.
     *
     * @see #isHandler
     * @see #detectHandlerMethods
     * @see #handlerMethodsInitialized
     */
    protected void initHandlerMethods() {
        if (logger.isDebugEnabled()) {
            logger.debug("Looking for request mappings in application context: " + getApplicationContext());
        }
        String[] beanNames = (this.detectHandlerMethodsInAncestorContexts ?
                BeanFactoryUtils.beanNamesForTypeIncludingAncestors(obtainApplicationContext(), Object.class) :
                obtainApplicationContext().getBeanNamesForType(Object.class));

        for (String beanName : beanNames) {
            if (!beanName.startsWith(SCOPED_TARGET_NAME_PREFIX)) {
                Class<?> beanType = null;
                try {
                    beanType = obtainApplicationContext().getType(beanName);
                } catch (Throwable ex) {
                    // An unresolvable bean type, probably from a lazy bean - let's ignore it.
                    if (logger.isDebugEnabled()) {
                        logger.debug("Could not resolve target class for bean with name '" + beanName + "'", ex);
                    }
                }
                if (beanType != null && isHandler(beanType)) {
                    detectHandlerMethods(beanName);
                }
            }
        }
        handlerMethodsInitialized(getHandlerMethods());
    }

    /**
     * Look for handler methods in the specified handler bean.
     *
     * @param handler either a bean name or an actual handler instance
     * @see #getMappingForMethod
     */
    protected void detectHandlerMethods(Object handler) {
        Class<?>
                handlerType =
                (handler instanceof String ? obtainApplicationContext().getType((String) handler) : handler.getClass());

        if (handlerType != null) {
            Class<?> userType = ClassUtils.getUserClass(handlerType);
            Map<Method, T> methods = MethodIntrospector.selectMethods(userType,
                    new MethodIntrospector.MetadataLookup<T>() {
                        @Override public T inspect(Method method) {
                            try {
                                return AbstractHandlerMethodMapping.this.getMappingForMethod(method, userType);
                            } catch (Throwable ex) {
                                throw new IllegalStateException("Invalid mapping on handler class [" +
                                        userType.getName() + "]: " + method, ex);
                            }
                        }
                    });
            methods.forEach(new BiConsumer<Method, T>() {
                @Override public void accept(Method method, T mapping) {
                    Method invocableMethod = AopUtils.selectInvocableMethod(method, userType);
                    AbstractHandlerMethodMapping.this.unregisterMapping(mapping);
                    AbstractHandlerMethodMapping.this.registerHandlerMethod(handler, invocableMethod, mapping);
                }
            });
        }
    }

    /**
     * Register a handler method and its unique mapping. Invoked at startup for
     * each detected handler method.
     *
     * @param handler the bean name of the handler or the handler instance
     * @param method  the method to register
     * @param mapping the mapping conditions associated with the handler method
     * @throws IllegalStateException if another method was already registered
     *                               under the same mapping
     */
    protected void registerHandlerMethod(Object handler, Method method, T mapping) {
        this.mappingRegistry.register(mapping, handler, method);
    }

    /**
     * Create the HandlerMethod instance.
     *
     * @param handler either a bean name or an actual handler instance
     * @param method  the target method
     * @return the created HandlerMethod
     */
    protected HandlerMethod createHandlerMethod(Object handler, Method method) {
        HandlerMethod handlerMethod;
        if (handler instanceof String) {
            String beanName = (String) handler;
            handlerMethod = new HandlerMethod(beanName,
                    obtainApplicationContext().getAutowireCapableBeanFactory(), method);
        } else {
            handlerMethod = new HandlerMethod(handler, method);
        }
        return handlerMethod;
    }

    /**
     * Extract and return the CORS configuration for the mapping.
     */
    @Nullable
    protected CorsConfiguration initCorsConfiguration(Object handler, Method method, T mapping) {
        return null;
    }

    /**
     * Invoked after all handler methods have been detected.
     *
     * @param handlerMethods a read-only map with handler methods and mappings.
     */
    protected void handlerMethodsInitialized(Map<T, HandlerMethod> handlerMethods) {
    }


    // Abstract template methods

    /**
     * Whether the given type is a handler with handler methods.
     *
     * @param beanType the type of the bean being checked
     * @return "true" if this a handler type, "false" otherwise.
     */
    protected abstract boolean isHandler(Class<?> beanType);

    /**
     * Provide the mapping for a handler method. A method for which no
     * mapping can be provided is not a handler method.
     *
     * @param method      the method to provide a mapping for
     * @param handlerType the handler type, possibly a sub-type of the method's
     *                    declaring class
     * @return the mapping, or {@code null} if the method is not mapped
     */
    @Nullable
    protected abstract T getMappingForMethod(Method method, Class<?> handlerType);

    /**
     * Extract and return the URL paths contained in a mapping.
     */
    protected abstract Set<String> getMappingPathPatterns(T mapping);

    /**
     * A registry that maintains all mappings to handler methods, exposing methods
     * to perform lookups and providing concurrent access.
     * <p>Package-private for testing purposes.
     */
    class MappingRegistry {

        private final Map<T, MappingRegistration<T>> registry = new HashMap<>();

        private final Map<T, HandlerMethod> mappingLookup = new LinkedHashMap<>();

        private final MultiValueMap<String, T> urlLookup = new LinkedMultiValueMap<>();

        private final Map<String, List<HandlerMethod>> nameLookup = new ConcurrentHashMap<>();

        private final Map<HandlerMethod, CorsConfiguration> corsLookup = new ConcurrentHashMap<>();

        private final ReentrantReadWriteLock readWriteLock = new ReentrantReadWriteLock();

        /**
         * Return all mappings and handler methods. Not thread-safe.
         *
         * @see #acquireReadLock()
         */
        public Map<T, HandlerMethod> getMappings() {
            return this.mappingLookup;
        }

        /**
         * Return matches for the given URL path. Not thread-safe.
         *
         * @see #acquireReadLock()
         */
        @Nullable
        public List<T> getMappingsByUrl(String urlPath) {
            return this.urlLookup.get(urlPath);
        }

        /**
         * Return handler methods by mapping name. Thread-safe for concurrent use.
         */
        public List<HandlerMethod> getHandlerMethodsByMappingName(String mappingName) {
            return this.nameLookup.get(mappingName);
        }

        /**
         * Return CORS configuration. Thread-safe for concurrent use.
         */
        public CorsConfiguration getCorsConfiguration(HandlerMethod handlerMethod) {
            HandlerMethod original = handlerMethod.getResolvedFromHandlerMethod();
            return this.corsLookup.get(original != null ? original : handlerMethod);
        }

        /**
         * Acquire the read lock when using getMappings and getMappingsByUrl.
         */
        public void acquireReadLock() {
            this.readWriteLock.readLock().lock();
        }

        /**
         * Release the read lock after using getMappings and getMappingsByUrl.
         */
        public void releaseReadLock() {
            this.readWriteLock.readLock().unlock();
        }

        public void register(T mapping, Object handler, Method method) {
            this.readWriteLock.writeLock().lock();
            try {
                HandlerMethod handlerMethod = createHandlerMethod(handler, method);
                assertUniqueMethodMapping(handlerMethod, mapping);
                this.mappingLookup.put(mapping, handlerMethod);

                if (logger.isInfoEnabled()) {
                    logger.info("Mapped \"" + mapping + "\" onto " + handlerMethod);
                }

                List<String> directUrls = getDirectUrls(mapping);
                for (String url : directUrls) {
                    this.urlLookup.add(url, mapping);
                }

                String name = null;
                if (getNamingStrategy() != null) {
                    name = getNamingStrategy().getName(handlerMethod, mapping);
                    addMappingName(name, handlerMethod);
                }

                CorsConfiguration corsConfig = initCorsConfiguration(handler, method, mapping);
                if (corsConfig != null) {
                    this.corsLookup.put(handlerMethod, corsConfig);
                }

                this.registry.put(mapping, new MappingRegistration<>(mapping, handlerMethod, directUrls, name));
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void assertUniqueMethodMapping(HandlerMethod newHandlerMethod, T mapping) {
            HandlerMethod handlerMethod = this.mappingLookup.get(mapping);
            if (handlerMethod != null && !handlerMethod.equals(newHandlerMethod)) {
                throw new IllegalStateException(
                        "Ambiguous mapping. Cannot map '" + newHandlerMethod.getBean() + "' method \n" +
                                newHandlerMethod + "\nto " + mapping + ": There is already '" +
                                handlerMethod.getBean() + "' bean method\n" + handlerMethod + " mapped.");
            }
        }

        private List<String> getDirectUrls(T mapping) {
            List<String> urls = new ArrayList<>(1);
            for (String path : getMappingPathPatterns(mapping)) {
                if (!getPathMatcher().isPattern(path)) {
                    urls.add(path);
                }
            }
            return urls;
        }

        private void addMappingName(String name, HandlerMethod handlerMethod) {
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                oldList = Collections.emptyList();
            }

            for (HandlerMethod current : oldList) {
                if (handlerMethod.equals(current)) {
                    return;
                }
            }

            if (logger.isTraceEnabled()) {
                logger.trace("Mapping name '" + name + "'");
            }

            List<HandlerMethod> newList = new ArrayList<>(oldList.size() + 1);
            newList.addAll(oldList);
            newList.add(handlerMethod);
            this.nameLookup.put(name, newList);

            if (newList.size() > 1) {
                if (logger.isTraceEnabled()) {
                    logger.trace("Mapping name clash for handlerMethods " + newList +
                            ". Consider assigning explicit names.");
                }
            }
        }

        public void unregister(T mapping) {
            this.readWriteLock.writeLock().lock();
            try {
                MappingRegistration<T> definition = this.registry.remove(mapping);
                if (definition == null) {
                    return;
                }

                this.mappingLookup.remove(definition.getMapping());

                for (String url : definition.getDirectUrls()) {
                    List<T> list = this.urlLookup.get(url);
                    if (list != null) {
                        list.remove(definition.getMapping());
                        if (list.isEmpty()) {
                            this.urlLookup.remove(url);
                        }
                    }
                }

                removeMappingName(definition);

                this.corsLookup.remove(definition.getHandlerMethod());
            } finally {
                this.readWriteLock.writeLock().unlock();
            }
        }

        private void removeMappingName(MappingRegistration<T> definition) {
            String name = definition.getMappingName();
            if (name == null) {
                return;
            }
            HandlerMethod handlerMethod = definition.getHandlerMethod();
            List<HandlerMethod> oldList = this.nameLookup.get(name);
            if (oldList == null) {
                return;
            }
            if (oldList.size() <= 1) {
                this.nameLookup.remove(name);
                return;
            }
            List<HandlerMethod> newList = new ArrayList<>(oldList.size() - 1);
            for (HandlerMethod current : oldList) {
                if (!current.equals(handlerMethod)) {
                    newList.add(current);
                }
            }
            this.nameLookup.put(name, newList);
        }
    }


    private static class MappingRegistration<T> {

        private final T mapping;

        private final HandlerMethod handlerMethod;

        private final List<String> directUrls;

        @Nullable
        private final String mappingName;

        public MappingRegistration(T mapping, HandlerMethod handlerMethod,
                                   @Nullable List<String> directUrls, @Nullable String mappingName) {

            Assert.notNull(mapping, "Mapping must not be null");
            Assert.notNull(handlerMethod, "HandlerMethod must not be null");
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
            this.directUrls = (directUrls != null ? directUrls : Collections.emptyList());
            this.mappingName = mappingName;
        }

        public T getMapping() {
            return this.mapping;
        }

        public HandlerMethod getHandlerMethod() {
            return this.handlerMethod;
        }

        public List<String> getDirectUrls() {
            return this.directUrls;
        }

        @Nullable
        public String getMappingName() {
            return this.mappingName;
        }
    }


    /**
     * A thin wrapper around a matched HandlerMethod and its mapping, for the purpose of
     * comparing the best match with a comparator in the context of the current request.
     */
    private class Match {

        private final T mapping;

        private final HandlerMethod handlerMethod;

        public Match(T mapping, HandlerMethod handlerMethod) {
            this.mapping = mapping;
            this.handlerMethod = handlerMethod;
        }

        @Override
        public String toString() {
            return this.mapping.toString();
        }
    }


    private class MatchComparator implements Comparator<Match> {

        private final Comparator<T> comparator;

        public MatchComparator(Comparator<T> comparator) {
            this.comparator = comparator;
        }

        @Override
        public int compare(Match match1, Match match2) {
            return this.comparator.compare(match1.mapping, match2.mapping);
        }
    }


    private static class EmptyHandler {

        @SuppressWarnings("unused")
        public void handle() {
            throw new UnsupportedOperationException("Not implemented");
        }
    }

}
