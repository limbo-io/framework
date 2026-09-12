package io.limbo.cqrs.springboot.starter.autoconfigure;

import io.limbo.cqrs.springboot.starter.interceptor.BeanValidationInterceptor;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;
import javax.validation.executable.ExecutableValidator;

/**
 * Auto-configuration for bean validation interceptor.
 * Provides a global handler interceptor that validates message payloads
 * using JSR-303 constraints.
 * <p>
 * Enabled by default when Validator is on the classpath and can be disabled via
 * property: limbo.cqrs.validation.enabled=false
 */
@Configuration(proxyBeanMethods = false)
@ConditionalOnClass({Validator.class, ExecutableValidator.class})
@ConditionalOnProperty(prefix = "limbo.cqrs.validation", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BeanValidationInterceptorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BeanValidationInterceptor beanValidationInterceptor(Validator validator) {
        return new BeanValidationInterceptor(validator);
    }
}
