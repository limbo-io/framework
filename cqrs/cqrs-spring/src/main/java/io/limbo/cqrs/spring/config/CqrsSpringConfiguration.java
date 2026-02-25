package io.limbo.cqrs.spring.config;

import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.CommandGateway;
import io.limbo.cqrs.core.command.DefaultCommandGateway;
import io.limbo.cqrs.core.command.InMemoryCommandBus;
import io.limbo.cqrs.core.event.EventBus;
import io.limbo.cqrs.core.event.InMemoryEventBus;
import io.limbo.cqrs.core.event.InMemoryEventStore;
import io.limbo.cqrs.core.event.EventStore;
import io.limbo.cqrs.core.query.DefaultQueryGateway;
import io.limbo.cqrs.core.query.InMemoryQueryBus;
import io.limbo.cqrs.core.query.QueryBus;
import io.limbo.cqrs.core.query.QueryGateway;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for CQRS framework.
 * Provides default implementations for all CQRS components.
 */
@Configuration
public class CqrsSpringConfiguration {

    @Bean
    public CommandBus commandBus() {
        return new InMemoryCommandBus();
    }

    @Bean
    public CommandGateway commandGateway(CommandBus commandBus) {
        return new DefaultCommandGateway(commandBus);
    }

    @Bean
    public QueryBus queryBus() {
        return new InMemoryQueryBus();
    }

    @Bean
    public QueryGateway queryGateway(QueryBus queryBus) {
        return new DefaultQueryGateway(queryBus);
    }

    @Bean
    public EventBus eventBus() {
        return new InMemoryEventBus();
    }

    @Bean
    public EventStore eventStore() {
        return new InMemoryEventStore();
    }
}
