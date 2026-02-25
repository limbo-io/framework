package io.limbo.cqrs.springboot;

import io.limbo.cqrs.core.command.CommandBus;
import io.limbo.cqrs.core.command.CommandGateway;
import io.limbo.cqrs.core.event.EventBus;
import io.limbo.cqrs.core.event.EventStore;
import io.limbo.cqrs.core.query.QueryBus;
import io.limbo.cqrs.core.query.QueryGateway;
import io.limbo.cqrs.springboot.starter.autoconfigure.CqrsAutoConfiguration;
import org.junit.jupiter.api.Test;
import org.springframework.boot.autoconfigure.AutoConfigurations;
import org.springframework.boot.test.context.runner.ApplicationContextRunner;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CQRS Spring Boot Starter auto-configuration.
 */
class CqrsSpringBootStarterTest {

    private final ApplicationContextRunner contextRunner = new ApplicationContextRunner()
            .withConfiguration(AutoConfigurations.of(CqrsAutoConfiguration.class));

    @Test
    void shouldAutoConfigureCommandBus() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(CommandBus.class);
            assertThat(context).hasSingleBean(CommandGateway.class);
        });
    }

    @Test
    void shouldAutoConfigureQueryBus() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(QueryBus.class);
            assertThat(context).hasSingleBean(QueryGateway.class);
        });
    }

    @Test
    void shouldAutoConfigureEventBus() {
        this.contextRunner.run(context -> {
            assertThat(context).hasSingleBean(EventBus.class);
            assertThat(context).hasSingleBean(EventStore.class);
        });
    }

    @Test
    void shouldNotAutoConfigureWhenDisabled() {
        this.contextRunner
                .withPropertyValues("limbo.cqrs.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(CommandBus.class);
                    assertThat(context).doesNotHaveBean(QueryBus.class);
                    assertThat(context).doesNotHaveBean(EventBus.class);
                });
    }

    @Test
    void shouldNotConfigureCommandBusWhenDisabled() {
        this.contextRunner
                .withPropertyValues("limbo.cqrs.command.enabled=false")
                .run(context -> {
                    assertThat(context).doesNotHaveBean(CommandBus.class);
                    assertThat(context).doesNotHaveBean(CommandGateway.class);
                    assertThat(context).hasSingleBean(QueryBus.class);
                    assertThat(context).hasSingleBean(EventBus.class);
                });
    }

    @Test
    void shouldNotConfigureQueryBusWhenDisabled() {
        this.contextRunner
                .withPropertyValues("limbo.cqrs.query.enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(CommandBus.class);
                    assertThat(context).doesNotHaveBean(QueryBus.class);
                    assertThat(context).doesNotHaveBean(QueryGateway.class);
                    assertThat(context).hasSingleBean(EventBus.class);
                });
    }

    @Test
    void shouldNotConfigureEventBusWhenDisabled() {
        this.contextRunner
                .withPropertyValues("limbo.cqrs.event.enabled=false")
                .run(context -> {
                    assertThat(context).hasSingleBean(CommandBus.class);
                    assertThat(context).hasSingleBean(QueryBus.class);
                    assertThat(context).doesNotHaveBean(EventBus.class);
                    assertThat(context).doesNotHaveBean(EventStore.class);
                });
    }

    // Test commands, queries, and events
    static class TestCommand {
        private final String value;

        TestCommand(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }

    static class TestQuery {
        private final String criteria;

        TestQuery(String criteria) {
            this.criteria = criteria;
        }

        String getCriteria() {
            return criteria;
        }
    }

    static class TestEvent {
        private final String data;

        TestEvent(String data) {
            this.data = data;
        }

        String getData() {
            return data;
        }
    }

    static class TestResult {
        private final String value;

        TestResult(String value) {
            this.value = value;
        }

        String getValue() {
            return value;
        }
    }
}
