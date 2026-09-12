package io.limbo.cqrs.core.interception;

import io.limbo.cqrs.core.message.Message;

/**
 * Interceptor for handler execution (wraps the actual handler call).
 *
 * @param <M> the message type
 * @param <R> the result type
 */
@FunctionalInterface
public interface HandlerInterceptor<M extends Message<?>, R> {

    /**
     * Intercept the handler execution.
     *
     * @param message the message being handled
     * @param chain   the interceptor chain to proceed with (or not)
     * @return the result
     * @throws Exception if handling fails
     */
    R handle(M message, InterceptorChain<M, R> chain) throws Exception;
}
