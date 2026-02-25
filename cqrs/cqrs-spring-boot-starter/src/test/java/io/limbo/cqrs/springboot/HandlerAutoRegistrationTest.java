package io.limbo.cqrs.springboot;

import io.limbo.cqrs.core.command.Command;
import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.CommandGateway;
import io.limbo.cqrs.core.event.Event;
import io.limbo.cqrs.core.event.EventBus;
import io.limbo.cqrs.core.event.EventMessage;
import io.limbo.cqrs.core.query.Query;
import io.limbo.cqrs.core.query.QueryGateway;
import io.limbo.cqrs.spring.annotation.CommandHandler;
import io.limbo.cqrs.spring.annotation.EventHandler;
import io.limbo.cqrs.spring.annotation.QueryHandler;
import io.limbo.cqrs.springboot.starter.autoconfigure.CqrsAutoConfiguration;
import io.limbo.cqrs.springboot.starter.autoconfigure.HandlerRegistrarAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for automatic handler registration.
 */
class HandlerAutoRegistrationTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CqrsAutoConfiguration.class, HandlerRegistrarAutoConfiguration.class));

    @Test
    void shouldAutoRegisterCommandHandler() {
        this.contextRunner
                .withUserConfiguration(HandlerConfig.class)
                .run(context -> {
                    CommandBus commandBus = context.getBean(CommandBus.class);
                    CommandGateway gateway = context.getBean(CommandGateway.class);

                    // Send command and verify handler was invoked
                    TestCommand command = new TestCommand("test");
                    gateway.send(command);

                    TestHandler handler = context.getBean(TestHandler.class);
                    assertThat(handler.receivedCommand).isEqualTo(command);
                });
    }

    @Test
    void shouldAutoRegisterQueryHandler() {
        this.contextRunner
                .withUserConfiguration(HandlerConfig.class)
                .run(context -> {
                    QueryGateway gateway = context.getBean(QueryGateway.class);

                    // Send query and verify handler returns result
                    TestQuery query = new TestQuery("criteria");
                    String result = gateway.query(query, String.class);

                    assertThat(result).isEqualTo("result-for-criteria");
                });
    }

    @Test
    void shouldAutoRegisterEventHandler() {
        this.contextRunner
                .withUserConfiguration(HandlerConfig.class)
                .run(context -> {
                    EventBus eventBus = context.getBean(EventBus.class);

                    // Publish event and verify handler was invoked
                    TestEvent event = new TestEvent("event-data");
                    eventBus.publish(EventMessage.asEventMessage(event));

                    TestHandler handler = context.getBean(TestHandler.class);
                    assertThat(handler.receivedEvent).isEqualTo(event);
                });
    }

    // Test domain classes
    static class TestCommand implements Command {
        private final String value;

        TestCommand(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    static class TestQuery implements Query {
        private final String criteria;

        TestQuery(String criteria) {
            this.criteria = criteria;
        }

        String getCriteria() {
            return criteria;
        }
    }

    static class TestEvent implements Event {
        private final String data;

        TestEvent(String data) {
            this.data = data;
        }

        String getData() {
            return data;
        }
    }

    // Handler class with annotated methods
    static class TestHandler {
        volatile TestCommand receivedCommand;
        volatile TestEvent receivedEvent;

        @CommandHandler
        public void handle(TestCommand command) {
            this.receivedCommand = command;
        }

        @QueryHandler
        public String handle(TestQuery query) {
            return "result-for-" + query.getCriteria();
        }

        @EventHandler
        public void handle(TestEvent event) {
            this.receivedEvent = event;
        }
    }

    @Configuration
    static class HandlerConfig {
        @Bean
        public TestHandler testHandler() {
            return new TestHandler();
        }
    }
}
