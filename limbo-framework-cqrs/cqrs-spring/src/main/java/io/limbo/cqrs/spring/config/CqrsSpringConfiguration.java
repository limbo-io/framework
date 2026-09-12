package io.limbo.cqrs.spring.config;

import io.limbo.cqrs.core.commandhandling.CommandBus;
import io.limbo.cqrs.core.commandhandling.InMemoryCommandBus;
import io.limbo.cqrs.core.queryhandling.InMemoryQueryBus;
import io.limbo.cqrs.core.queryhandling.QueryBus;
import io.limbo.cqrs.spring.discovery.CqrsAnnotationBeanPostProcessor;
import io.limbo.cqrs.spring.gateway.CommandGateway;
import io.limbo.cqrs.spring.gateway.QueryGateway;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for CQRS support.
 */
@Configuration
public class CqrsSpringConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public CommandBus commandBus() {
        InMemoryCommandBus bus = new InMemoryCommandBus();
        // Register shutdown hook for Virtual Thread executor
        Runtime.getRuntime().addShutdownHook(new Thread(bus::shutdown));
        return bus;
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryBus queryBus() {
        InMemoryQueryBus bus = new InMemoryQueryBus();
        // Register shutdown hook for Virtual Thread executor
        Runtime.getRuntime().addShutdownHook(new Thread(bus::shutdown));
        return bus;
    }

    @Bean
    @ConditionalOnMissingBean
    public CommandGateway commandGateway(CommandBus commandBus) {
        return new CommandGateway(commandBus);
    }

    @Bean
    @ConditionalOnMissingBean
    public QueryGateway queryGateway(QueryBus queryBus) {
        return new QueryGateway(queryBus);
    }

    @Bean
    public CqrsAnnotationBeanPostProcessor cqrsAnnotationBeanPostProcessor(
            CommandBus commandBus, QueryBus queryBus) {
        return new CqrsAnnotationBeanPostProcessor(commandBus, queryBus);
    }
}
