package io.limbo.cqrs.springboot.starter.autoconfigure;

import io.limbo.cqrs.springboot.starter.interceptor.BeanValidationInterceptor;
import org.springframework.boot.autoconfigure.AutoConfigureAfter;
import org.springframework.boot.autoconfigure.condition.ConditionalOnClass;
import org.springframework.boot.autoconfigure.condition.ConditionalOnMissingBean;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.autoconfigure.validation.ValidationAutoConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import javax.validation.Validator;

/**
 * Auto-configuration for bean validation interceptor.
 */
@Configuration(proxyBeanMethods = false)
@AutoConfigureAfter({CqrsAutoConfiguration.class, ValidationAutoConfiguration.class})
@ConditionalOnClass(Validator.class)
@ConditionalOnProperty(prefix = "limbo.cqrs.validation", name = "enabled", havingValue = "true", matchIfMissing = true)
public class BeanValidationInterceptorAutoConfiguration {

    @Bean
    @ConditionalOnMissingBean
    public BeanValidationInterceptor beanValidationInterceptor(Validator validator) {
        return new BeanValidationInterceptor(validator);
    }
}
