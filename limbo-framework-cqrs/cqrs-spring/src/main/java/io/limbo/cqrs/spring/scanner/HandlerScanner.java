package io.limbo.cqrs.spring.scanner;

import io.limbo.cqrs.spring.annotation.CommandHandler;
import io.limbo.cqrs.spring.annotation.QueryHandler;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.core.type.classreading.CachingMetadataReaderFactory;
import org.springframework.core.type.classreading.MetadataReader;
import org.springframework.core.type.classreading.MetadataReaderFactory;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Scans for handler classes and methods annotated with CQRS annotations.
 */
public class HandlerScanner {

    private static final String CLASS_RESOURCE_PATTERN = "/**/*.class";

    private final ResourcePatternResolver resourcePatternResolver = new PathMatchingResourcePatternResolver();
    private final MetadataReaderFactory metadataReaderFactory = new CachingMetadataReaderFactory();

    /**
     * Scans for command handler classes in the given base package.
     *
     * @param basePackage the base package to scan
     * @return set of handler classes
     */
    public Set<Class<?>> scanCommandHandlers(String basePackage) {
        return scanHandlers(basePackage, CommandHandler.class);
    }

    /**
     * Scans for query handler classes in the given base package.
     *
     * @param basePackage the base package to scan
     * @return set of handler classes
     */
    public Set<Class<?>> scanQueryHandlers(String basePackage) {
        return scanHandlers(basePackage, QueryHandler.class);
    }

    /**
     * Extracts all handler methods from a class.
     *
     * @param handlerClass the handler class
     * @return list of handler methods
     */
    public List<HandlerMethod> extractHandlerMethods(Class<?> handlerClass) {
        List<HandlerMethod> methods = new ArrayList<>();

        for (Method method : handlerClass.getMethods()) {
            HandlerType type = detectHandlerType(method);
            if (type != null) {
                Class<?> payloadType = extractPayloadType(method);
                if (payloadType != null) {
                    methods.add(new HandlerMethod(method, handlerClass, payloadType, type));
                }
            }
        }

        return methods;
    }

    private Set<Class<?>> scanHandlers(String basePackage, Class<?> annotationType) {
        Set<Class<?>> handlers = new HashSet<>();

        try {
            String pattern = ResourcePatternResolver.CLASSPATH_ALL_URL_PREFIX +
                    basePackage.replace(".", "/") + CLASS_RESOURCE_PATTERN;

            Resource[] resources = resourcePatternResolver.getResources(pattern);

            for (Resource resource : resources) {
                if (resource.isReadable()) {
                    try {
                        MetadataReader metadataReader = metadataReaderFactory.getMetadataReader(resource);
                        // Check if class has method with the annotation
                        String className = metadataReader.getClassMetadata().getClassName();
                        Class<?> clazz = Class.forName(className);
                        if (hasMethodWithAnnotation(clazz, annotationType)) {
                            handlers.add(clazz);
                        }
                    } catch (Exception e) {
                        // Skip unreadable classes
                    }
                }
            }
        } catch (Exception e) {
            // Return empty set if scanning fails
        }

        return handlers;
    }

    private boolean hasMethodWithAnnotation(Class<?> clazz, Class<?> annotationType) {
        for (Method method : clazz.getMethods()) {
            if (method.isAnnotationPresent((Class<? extends java.lang.annotation.Annotation>) annotationType)) {
                return true;
            }
        }
        return false;
    }

    private HandlerType detectHandlerType(Method method) {
        if (method.isAnnotationPresent(CommandHandler.class)) {
            return HandlerType.COMMAND;
        }
        if (method.isAnnotationPresent(QueryHandler.class)) {
            return HandlerType.QUERY;
        }
        return null;
    }

    private Class<?> extractPayloadType(Method method) {
        Parameter[] parameters = method.getParameters();
        if (parameters.length == 0) {
            return null;
        }
        return parameters[0].getType();
    }

    /**
     * Type of handler.
     */
    public enum HandlerType {
        COMMAND, QUERY
    }

    /**
     * Represents a handler method.
     */
    public static class HandlerMethod {
        private final Method method;
        private final Class<?> beanClass;
        private final Class<?> payloadType;
        private final HandlerType handlerType;

        public HandlerMethod(Method method, Class<?> beanClass, Class<?> payloadType, HandlerType handlerType) {
            this.method = method;
            this.beanClass = beanClass;
            this.payloadType = payloadType;
            this.handlerType = handlerType;
        }

        public Method getMethod() {
            return method;
        }

        public Class<?> getBeanClass() {
            return beanClass;
        }

        public Class<?> getPayloadType() {
            return payloadType;
        }

        public HandlerType getHandlerType() {
            return handlerType;
        }
    }
}
