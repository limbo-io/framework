package io.limbo.cqrs.springboot.starter.autoconfigure;

import io.limbo.cqrs.core.command.ICommand;
import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.CommandHandler;
import io.limbo.cqrs.core.query.IQuery;
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
 * Scans beans for @CommandHandler and @QueryHandler annotations and registers them.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter(CqrsAutoConfiguration.class)
@ConditionalOnClass({CommandBus.class, QueryBus.class})
@ConditionalOnProperty(prefix = "limbo.cqrs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class HandlerRegistrarAutoConfiguration {

    private static final Logger log = LoggerFactory.getLogger(HandlerRegistrarAutoConfiguration.class);

    @Bean
    @ConditionalOnBean({CommandBus.class, HandlerScanner.class})
    @ConditionalOnProperty(prefix = "limbo.cqrs.command", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeanPostProcessor commandHandlerPostProcessor(
            ObjectProvider<CommandBus> commandBusProvider,
            HandlerScanner handlerScanner) {
        return new HandlerRegistrationPostProcessor(commandBusProvider, null, handlerScanner);
    }

    @Bean
    @ConditionalOnBean({QueryBus.class, HandlerScanner.class})
    @ConditionalOnProperty(prefix = "limbo.cqrs.query", name = "enabled", havingValue = "true", matchIfMissing = true)
    public BeanPostProcessor queryHandlerPostProcessor(
            ObjectProvider<QueryBus> queryBusProvider,
            HandlerScanner handlerScanner) {
        return new HandlerRegistrationPostProcessor(null, queryBusProvider, handlerScanner);
    }

    /**
     * BeanPostProcessor that registers handlers after bean initialization.
     */
    private static class HandlerRegistrationPostProcessor implements BeanPostProcessor {

        private final ObjectProvider<CommandBus> commandBusProvider;
        private final ObjectProvider<QueryBus> queryBusProvider;
        private final HandlerScanner handlerScanner;

        HandlerRegistrationPostProcessor(
                ObjectProvider<CommandBus> commandBusProvider,
                ObjectProvider<QueryBus> queryBusProvider,
                HandlerScanner handlerScanner) {
            this.commandBusProvider = commandBusProvider;
            this.queryBusProvider = queryBusProvider;
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
                            CommandHandler<ICommand, Object> handler = createCommandHandler(bean, method);
                            commandBus.register((Class<ICommand>) payloadType, handler);
                            log.debug("Registered command handler for {} in bean {}",
                                    payloadType.getName(), bean.getClass().getName());
                        }
                    }
                    break;

                case QUERY:
                    if (queryBusProvider != null) {
                        QueryBus queryBus = queryBusProvider.getIfAvailable();
                        if (queryBus != null) {
                            QueryHandler<IQuery, Object> handler = createQueryHandler(bean, method);
                            queryBus.register((Class<IQuery>) payloadType, handler);
                            log.debug("Registered query handler for {} in bean {}",
                                    payloadType.getName(), bean.getClass().getName());
                        }
                    }
                    break;
            }
        }

        private <R> CommandHandler<ICommand, R> createCommandHandler(Object bean, Method method) {
            method.setAccessible(true);
            return command -> {
                try {
                    return (R) method.invoke(bean, command);
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

        @SuppressWarnings("unchecked")
        private <R> QueryHandler<IQuery, R> createQueryHandler(Object bean, Method method) {
            method.setAccessible(true);
            return query -> {
                try {
                    return (R) method.invoke(bean, query);
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
        }
    }
}
