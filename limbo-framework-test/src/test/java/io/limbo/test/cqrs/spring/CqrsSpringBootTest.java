package io.limbo.test.cqrs.spring;

import io.limbo.cqrs.core.commandhandling.CommandBus;
import io.limbo.cqrs.core.queryhandling.QueryBus;
import io.limbo.cqrs.spring.config.EnableCqrs;
import io.limbo.cqrs.spring.gateway.CommandGateway;
import io.limbo.cqrs.spring.gateway.QueryGateway;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;
import org.springframework.test.context.ContextConfiguration;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Tests for CQRS handler scanning in Spring Boot test environment.
 */
@SpringBootTest
@ContextConfiguration(classes = {CqrsSpringBootTest.TestConfig.class})
class CqrsSpringBootTest {

    @Autowired
    private CommandBus commandBus;

    @Autowired
    private QueryBus queryBus;

    @Autowired
    private CommandGateway commandGateway;

    @Autowired
    private QueryGateway queryGateway;

    @Test
    void contextLoads() {
        assertThat(commandBus).isNotNull();
        assertThat(queryBus).isNotNull();
        assertThat(commandGateway).isNotNull();
        assertThat(queryGateway).isNotNull();
    }

    @Test
    void shouldExecuteCommandHandlerViaGateway() {
        // Given
        SampleCommandHandler.TestCommand command = new SampleCommandHandler.TestCommand("test data");

        // When
        SampleCommandHandler.TestResult result = commandGateway.sendAndWait(command);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo("test data");
    }

    @Test
    void shouldExecuteQueryHandlerViaGateway() {
        // Given
        SampleQueryHandler.TestQuery query = new SampleQueryHandler.TestQuery("search criteria");

        // When
        SampleQueryHandler.TestResult result = queryGateway.query(query);

        // Then
        assertThat(result).isNotNull();
        assertThat(result.getValue()).isEqualTo("search criteria");
    }

    /**
     * Test configuration with @EnableCqrs to enable handler scanning.
     */
    @Configuration
    @EnableCqrs
    @ComponentScan(basePackageClasses = {CqrsSpringBootTest.class})
    static class TestConfig {
        // Configuration class that enables CQRS with bean scanning
    }
}
