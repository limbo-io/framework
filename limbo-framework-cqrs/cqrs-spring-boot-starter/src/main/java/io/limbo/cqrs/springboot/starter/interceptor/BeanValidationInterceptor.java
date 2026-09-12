package io.limbo.cqrs.springboot.starter.interceptor;

import io.limbo.cqrs.core.interception.HandlerInterceptor;
import io.limbo.cqrs.core.interception.InterceptorChain;
import io.limbo.cqrs.core.message.Message;

import javax.validation.ConstraintViolation;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Set;

/**
 * Sample interceptor that performs JSR-303 bean validation on the message payload.
 * <p>
 * This interceptor validates the payload using the configured Validator and throws
 * ConstraintViolationException if validation fails.
 * <p>
 * Usage: BeanValidationInterceptor is auto-configured when JSR-303 Validator is available.
 * Add validation annotations to your command/query payloads.
 * <p>
 * Note: This requires a JSR-303 provider (like Hibernate Validator) on the runtime classpath.
 */
public class BeanValidationInterceptor implements HandlerInterceptor<Message<?>, Object> {

    private final Validator validator;

    public BeanValidationInterceptor(Validator validator) {
        this.validator = validator;
    }

    @SuppressWarnings("unchecked")
    @Override
    public Object handle(Message<?> message, InterceptorChain<Message<?>, Object> chain) {
        Object payload = message.getPayload();

        if (payload != null) {
            Set<ConstraintViolation<Object>> violations = (Set<ConstraintViolation<Object>>) validator.validate(payload);
            if (!violations.isEmpty()) {
                throw new ConstraintViolationException("Payload validation failed", violations);
            }
        }

        return chain.proceed(message);
    }
}
