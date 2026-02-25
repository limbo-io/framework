package io.limbo.cqrs.springboot.starter.autoconfigure;

import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.CommandHandler;
import io.limbo.cqrs.core.event.EventBus;
import io.limbo.cqrs.core.event.EventHandler;
import io.limbo.cqrs.core.query.QueryBus;
import io.limbo.cqrs.core.query.QueryHandler;
import io.limbo.cqrs.spring.scanner.HandlerScanner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * Auto-configuration for automatic handler registration.
 * Scans beans for handler annotations and registers them with the appropriate bus.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(CqrsAutoConfiguration.class)
@ConditionalOnClass({CommandBus.class, QueryBus.class, EventBus.class})
@ConditionalOnProperty(prefix = "limbo.cqrs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HandlerRegistrarAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(HandlerRegistrarAutoConfiguration.class);

    @Bean
    @ConditionalOnBean({CommandBus.class, HandlerScanner.class})
    @ConditionalOnProperty(prefix = "limbo.cqrs.command", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeanPostProcessor commandHandlerPostProcessor(
            ObjectProvider<CommandBus> commandBusProvider,
            HandlerScanner handlerScanner) {
        return new HandlerRegistrationPostProcessor(commandBusProvider, null, null, handlerScanner);
    }

    @Bean
    @ConditionalOnBean({QueryBus.class, HandlerScanner.class})
    @ConditionalOnProperty(prefix = "limbo.cqrs.query", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeanPostProcessor queryHandlerPostProcessor(
            ObjectProvider<QueryBus> queryBusProvider,
            HandlerScanner handlerScanner) {
        return new HandlerRegistrationPostProcessor(null, queryBusProvider, null, handlerScanner);
    }

    @Bean
    @ConditionalOnBean({EventBus.class, HandlerScanner.class})
    @ConditionalOnProperty(prefix = "limbo.cqrs.event", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeanPostProcessor eventHandlerPostProcessor(
            ObjectProvider<EventBus> eventBusProvider,
            HandlerScanner handlerScanner) {
        return new HandlerRegistrationPostProcessor(null, null, eventBusProvider, handlerScanner);
    }

    /**
     * BeanPostProcessor that registers handlers after bean initialization.
     */
    private static class HandlerRegistrationPostProcessor implements BeanPostProcessor {

        private final ObjectProvider<CommandBus> commandBusProvider;
        private final ObjectProvider<QueryBus> queryBusProvider;
        private final ObjectProvider<EventBus> eventBusProvider;
        private final HandlerScanner handlerScanner;

        HandlerRegistrationPostProcessor(
                ObjectProvider<CommandBus> commandBusProvider,
                ObjectProvider<QueryBus> queryBusProvider,
                ObjectProvider<EventBus> eventBusProvider,
                HandlerScanner handlerScanner) {
            this.commandBusProvider = commandBusProvider;
            this.queryBusProvider = queryBusProvider;
            this.eventBusProvider = eventBusProvider;
            this.handlerScanner = handlerScanner;
        }

        @Override
        public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
            List<HandlerScanner.HandlerMethod> handlerMethods = handlerScanner.extractHandlerMethods(bean.getClass());

            for (HandlerScanner.HandlerMethod handlerMethod : handlerMethods) {
                registerHandler(bean, handlerMethod);
            }

            return bean;
        }

        @SuppressWarnings("unchecked")
        private <T> void registerHandler(Object bean, HandlerScanner.HandlerMethod handlerMethod) {
            Class<T> payloadType = (Class<T>) handlerMethod.getPayloadType();
            Method method = handlerMethod.getMethod();

            switch (handlerMethod.getHandlerType()) {
                case COMMAND:
                    if (commandBusProvider != null) {
                        CommandBus commandBus = commandBusProvider.getIfAvailable();
                        if (commandBus != null) {
                            CommandHandler<T> handler = createCommandHandler(bean, method, payloadType);
                            commandBus.register(payloadType, handler);
                            log.debug("Registered command handler for {} in bean {}",
                                    payloadType.getName(), bean.getClass().getName());
                        }
                    }
                    break;

                case QUERY:
                    if (queryBusProvider != null) {
                        QueryBus queryBus = queryBusProvider.getIfAvailable();
                        if (queryBus != null) {
                            registerQueryHandler(bean, method, payloadType, queryBus);
                            log.debug("Registered query handler for {} in bean {}",
                                    payloadType.getName(), bean.getClass().getName());
                        }
                    }
                    break;

                case EVENT:
                    if (eventBusProvider != null) {
                        EventBus eventBus = eventBusProvider.getIfAvailable();
                        if (eventBus != null) {
                            EventHandler<T> handler = createEventHandler(bean, method, payloadType);
                            eventBus.subscribe(payloadType, handler);
                            log.debug("Registered event handler for {} in bean {}",
                                    payloadType.getName(), bean.getClass().getName());
                        }
                    }
                    break;
            }
        }

        @SuppressWarnings("unchecked")
        private <T> CommandHandler<T> createCommandHandler(Object bean, Method method, Class<T> payloadType) {
            method.setAccessible(true);
            return commandMessage -> {
                try {
                    return method.invoke(bean, commandMessage.getPayload());
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getTargetException();
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    throw new RuntimeException("Command handler failed: " + method, cause);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access command handler: " + method, e);
                }
            };
        }

        @SuppressWarnings({"unchecked", "rawtypes"})
        private <Q, R> void registerQueryHandler(Object bean, Method method, Class<Q> queryType, QueryBus queryBus) {
            method.setAccessible(true);
            Class<R> resultType = (Class<R>) method.getReturnType();
            QueryHandler<Q, R> handler = queryMessage -> {
                try {
                    return (R) method.invoke(bean, queryMessage.getPayload());
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getTargetException();
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    throw new RuntimeException("Query handler failed: " + method, cause);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access query handler: " + method, e);
                }
            };
            queryBus.register(queryType, resultType, handler);
        }

        @SuppressWarnings("unchecked")
        private <T> EventHandler<T> createEventHandler(Object bean, Method method, Class<T> payloadType) {
            method.setAccessible(true);
            return eventMessage -> {
                try {
                    method.invoke(bean, eventMessage.getPayload());
                } catch (InvocationTargetException e) {
                    Throwable cause = e.getTargetException();
                    if (cause instanceof RuntimeException) {
                        throw (RuntimeException) cause;
                    }
                    throw new RuntimeException("Event handler failed: " + method, cause);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("Cannot access event handler: " + method, e);
                }
            };
        }
    }
}
