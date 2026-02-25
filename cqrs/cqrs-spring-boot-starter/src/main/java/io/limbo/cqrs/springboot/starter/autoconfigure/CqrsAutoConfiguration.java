package io.limbo.cqrs.springboot.starter.autoconfigure;

import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.CommandGateway;
import io.limbo.cqrs.core.command.DefaultCommandGateway;
import io.limbo.cqrs.core.command.InMemoryCommandBus;
import io.limbo.cqrs.core.event.EventBus;
import io.limbo.cqrs.core.event.EventStore;
import io.limbo.cqrs.core.event.InMemoryEventBus;
import io.limbo.cqrs.core.event.InMemoryEventStore;
import io.limbo.cqrs.core.query.DefaultQueryGateway;
import io.limbo.cqrs.core.query.InMemoryQueryBus;
import io.limbo.cqrs.core.query.QueryBus;
import io.limbo.cqrs.core.query.QueryGateway;
import io.limbo.cqrs.spring.scanner.HandlerScanner;
import io.limbo.cqrs.springboot.starter.properties.CqrsProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring Boot auto-configuration for CQRS framework.
 * Provides default implementations for all CQRS components when not already defined.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({CommandBus.class, QueryBus.class, EventBus.class})
@EnableConfigurationProperties(CqrsProperties.class)
@ConditionalOnProperty(prefix = "limbo.cqrs", name = "enabled", havingValue = "true", matchIfMissing = true)
public class CqrsAutoConfiguration {

    /**
     * Command bus configuration.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(CommandBus.class)
    @ConditionalOnProperty(prefix = "limbo.cqrs.command", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class CommandBusConfiguration {

        @Bean
        @ConditionalOnMissingBean(CommandBus.class)
        public CommandBus commandBus() {
            return new InMemoryCommandBus();
        }

        @Bean
        @ConditionalOnMissingBean(CommandGateway.class)
        public CommandGateway commandGateway(CommandBus commandBus) {
            return new DefaultCommandGateway(commandBus);
        }
    }

    /**
     * Query bus configuration.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(QueryBus.class)
    @ConditionalOnProperty(prefix = "limbo.cqrs.query", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class QueryBusConfiguration {

        @Bean
        @ConditionalOnMissingBean(QueryBus.class)
        public QueryBus queryBus() {
            return new InMemoryQueryBus();
        }

        @Bean
        @ConditionalOnMissingBean(QueryGateway.class)
        public QueryGateway queryGateway(QueryBus queryBus) {
            return new DefaultQueryGateway(queryBus);
        }
    }

    /**
     * Event bus configuration.
     */
    @Configuration(proxyBeanMethods = false)
    @ConditionalOnClass(EventBus.class)
    @ConditionalOnProperty(prefix = "limbo.cqrs.event", name = "enabled", havingValue = "true", matchIfMissing = true)
    public static class EventBusConfiguration {

        @Bean
        @ConditionalOnMissingBean(EventBus.class)
        public EventBus eventBus() {
            return new InMemoryEventBus();
        }

        @Bean
        @ConditionalOnMissingBean(EventStore.class)
        public EventStore eventStore() {
            return new InMemoryEventStore();
        }
    }

    /**
     * Handler scanner for automatic handler registration.
     */
    @Bean
    @ConditionalOnMissingBean(HandlerScanner.class)
    public HandlerScanner handlerScanner() {
        return new HandlerScanner();
    }
}
