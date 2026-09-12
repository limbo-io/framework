package io.limbo.cqrs.springboot.starter.test;

import io.limbo.cqrs.spring.config.CqrsSpringConfiguration;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Import;

/**
 * Test configuration for Limbo CQRS.
 * Use this in @SpringBootTest to enable CQRS handler scanning.
 *
 * <p>Example usage:
 * <pre>
 * @SpringBootTest
 * @Import(CqrsTestConfiguration.class)
 * class MyTest {
 *     // Handler scanning is now enabled
 * }
 * </pre>
 *
 * Alternatively, use @EnableCqrs:
 * <pre>
 * @SpringBootTest
 * @EnableCqrs
 * class MyTest {
 *     // CQRS is enabled
 * }
 * </pre>
 *
 * @see io.limbo.cqrs.spring.config.EnableCqrs
 */
@TestConfiguration
@Import(CqrsSpringConfiguration.class)
public class CqrsTestConfiguration {
    // Test configuration that imports core CQRS configuration
}
