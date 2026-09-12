package io.limbo.cqrs.springboot.starter.autoconfigure;

import io.limbo.cqrs.core.commandhandling.CommandBus;
import io.limbo.cqrs.core.queryhandling.QueryBus;
import io.limbo.cqrs.spring.config.CqrsSpringConfiguration;
import io.limbo.cqrs.springboot.starter.properties.CqrsProperties;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Import;

/**
 * Spring Boot auto-configuration for Limbo CQRS framework.
 * Imports the core Spring configuration with gateways and handler scanning.
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({CommandBus.class, QueryBus.class})
@EnableConfigurationProperties(CqrsProperties.class)
@ConditionalOnProperty(prefix = "limbo.cqrs", name = "enabled", havingValue = "true", matchIfMissing = true)
@Import(CqrsSpringConfiguration.class)
public class CqrsAutoConfiguration {
    // Configuration is imported from CqrsSpringConfiguration
}
