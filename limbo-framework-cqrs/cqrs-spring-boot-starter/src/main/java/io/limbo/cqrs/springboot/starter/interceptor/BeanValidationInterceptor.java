package io.limbo.cqrs.springboot.starter.interceptor;

import io.limbo.cqrs.core.interception.DispatchInterceptor;
import io.limbo.cqrs.core.message.Message;

import javax.validation.ConstraintViolation;
import javax.validation.Validator;
import java.util.Set;

/**
 * Interceptor that performs bean validation on dispatched messages.
 */
public class BeanValidationInterceptor implements DispatchInterceptor<Message<?>> {

    private final Validator validator;

    public BeanValidationInterceptor(Validator validator) {
        this.validator = validator;
    }

    @Override
    public Message<?> handle(Message<?> message) {
        Object payload = message.getPayload();
        if (payload != null) {
            Set<ConstraintViolation<Object>> violations = validator.validate(payload);
            if (!violations.isEmpty()) {
                throw new IllegalArgumentException("Validation failed: " + violations);
            }
        }
        return message;
    }
}
