package io.limbo.cqrs.core.interception;

import io.limbo.cqrs.core.message.Message;
import io.limbo.cqrs.core.message.ResultMessage;

/**
 * Interceptor for results (after handler execution).
 *
 * @param <M> the message type
 * @param <R> the result type
 */
@FunctionalInterface
public interface ResultInterceptor<M extends Message<?>, R> {

    /**
     * Intercept the result after handler execution.
     *
     * @param message the original message
     * @param result  the result from the handler
     * @return the (possibly modified) result
     */
    ResultMessage<R> handle(M message, ResultMessage<R> result);
}
