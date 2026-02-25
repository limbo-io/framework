package io.limbo.cqrs.spring.config;

import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.CommandGateway;
import io.limbo.cqrs.core.event.EventBus;
import io.limbo.cqrs.core.query.QueryBus;
import io.limbo.cqrs.core.query.QueryGateway;
import org.junit.jupiter.api.Test;
import org.springframework.context.annotation.AnnotationConfigApplicationContext;
import org.springframework.context.annotation.Configuration;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for CQRS Spring configuration.
 */
class CqrsSpringConfigurationTest {

    @Test
    void shouldCreateCommandBusBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TestConfig.class);
        context.refresh();

        CommandBus commandBus = context.getBean(CommandBus.class);

        assertNotNull(commandBus);
    }

    @Test
    void shouldCreateCommandGatewayBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TestConfig.class);
        context.refresh();

        CommandGateway commandGateway = context.getBean(CommandGateway.class);

        assertNotNull(commandGateway);
    }

    @Test
    void shouldCreateQueryBusBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TestConfig.class);
        context.refresh();

        QueryBus queryBus = context.getBean(QueryBus.class);

        assertNotNull(queryBus);
    }

    @Test
    void shouldCreateQueryGatewayBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TestConfig.class);
        context.refresh();

        QueryGateway queryGateway = context.getBean(QueryGateway.class);

        assertNotNull(queryGateway);
    }

    @Test
    void shouldCreateEventBusBean() {
        AnnotationConfigApplicationContext context = new AnnotationConfigApplicationContext();
        context.register(TestConfig.class);
        context.refresh();

        EventBus eventBus = context.getBean(EventBus.class);

        assertNotNull(eventBus);
    }

    @Configuration
    @EnableCqrs
    static class TestConfig {
    }
}
