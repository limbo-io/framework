package io.limbo.cqrs.spring.discovery;

import io.limbo.cqrs.core.commandhandling.CommandBus;
import io.limbo.cqrs.core.commandhandling.ICommand;
import io.limbo.cqrs.core.queryhandling.IQuery;
import io.limbo.cqrs.core.queryhandling.QueryBus;
import io.limbo.cqrs.spring.annotation.CommandHandler;
import io.limbo.cqrs.spring.annotation.QueryHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.config.BeanPostProcessor;
import org.springframework.core.annotation.AnnotationUtils;

import java.lang.reflect.Method;
import java.lang.reflect.Parameter;

/**
 * Bean post processor that scans and registers CQRS handlers.
 */
public class CqrsAnnotationBeanPostProcessor implements BeanPostProcessor {

    private static final Logger log = LoggerFactory.getLogger(CqrsAnnotationBeanPostProcessor.class);

    private final CommandBus commandBus;
    private final QueryBus queryBus;

    public CqrsAnnotationBeanPostProcessor(CommandBus commandBus, QueryBus queryBus) {
        this.commandBus = commandBus;
        this.queryBus = queryBus;
    }

    @Override
    public Object postProcessAfterInitialization(Object bean, String beanName) throws BeansException {
        Class<?> beanClass = bean.getClass();

        for (Method method : beanClass.getMethods()) {
            // Check for CommandHandler annotation
            CommandHandler commandHandler = AnnotationUtils.findAnnotation(method, CommandHandler.class);
            if (commandHandler != null) {
                registerCommandHandler(bean, method);
            }

            // Check for QueryHandler annotation
            QueryHandler queryHandler = AnnotationUtils.findAnnotation(method, QueryHandler.class);
            if (queryHandler != null) {
                registerQueryHandler(bean, method);
            }
        }

        return bean;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerCommandHandler(Object bean, Method method) {
        if (method.getParameterCount() != 1) {
            log.warn("CommandHandler method must have exactly one parameter: {}.{}",
                    bean.getClass().getSimpleName(), method.getName());
            return;
        }

        Parameter param = method.getParameters()[0];
        Class<?> paramType = param.getType();

        if (!ICommand.class.isAssignableFrom(paramType)) {
            log.warn("CommandHandler parameter must implement ICommand: {}.{}",
                    bean.getClass().getSimpleName(), method.getName());
            return;
        }

        Class commandType = paramType;

        commandBus.register(commandType, command -> {
            try {
                return method.invoke(bean, command);
            } catch (Exception e) {
                throw new RuntimeException("Failed to handle command: " + commandType.getSimpleName(), e);
            }
        });

        log.debug("Registered CommandHandler for {} on {}.{}",
                commandType.getSimpleName(), bean.getClass().getSimpleName(), method.getName());
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    private void registerQueryHandler(Object bean, Method method) {
        if (method.getParameterCount() != 1) {
            log.warn("QueryHandler method must have exactly one parameter: {}.{}",
                    bean.getClass().getSimpleName(), method.getName());
            return;
        }

        Parameter param = method.getParameters()[0];
        Class<?> paramType = param.getType();

        if (!IQuery.class.isAssignableFrom(paramType)) {
            log.warn("QueryHandler parameter must implement IQuery: {}.{}",
                    bean.getClass().getSimpleName(), method.getName());
            return;
        }

        Class queryType = paramType;

        queryBus.register(queryType, query -> {
            try {
                return method.invoke(bean, query);
            } catch (Exception e) {
                throw new RuntimeException("Failed to handle query: " + queryType.getSimpleName(), e);
            }
        });

        log.debug("Registered QueryHandler for {} on {}.{}",
                queryType.getSimpleName(), bean.getClass().getSimpleName(), method.getName());
    }
}
