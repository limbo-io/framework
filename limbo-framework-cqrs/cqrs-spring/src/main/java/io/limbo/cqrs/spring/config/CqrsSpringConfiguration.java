package io.limbo.cqrs.spring.config;

import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.InMemoryCommandBus;
import io.limbo.cqrs.core.query.InMemoryQueryBus;
import io.limbo.cqrs.core.query.QueryBus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for simplified CQRS framework.
 * Provides CommandBus and QueryBus implementations.
 */
@Configuration
public class CqrsSpringConfiguration {

    @Bean
    public CommandBus commandBus() {
        return new InMemoryCommandBus();
    }

    @Bean
    public QueryBus queryBus() {
        return new InMemoryQueryBus();
    }
}
