package io.limbo.cqrs.core.interception;

import io.limbo.cqrs.core.message.Message;

/**
 * Chain for interceptor execution. Allows interceptors to proceed to the next interceptor
 * or the actual handler.
 *
 * @param <M> the message type
 * @param <R> the result type
 */
@FunctionalInterface
public interface InterceptorChain<M extends Message<?>, R> {

    /**
     * Proceed to the next interceptor or the handler.
     *
     * @param message the message to handle
     * @return the result
     * @throws Exception if handling fails
     */
    R proceed(M message) throws Exception;

    /**
     * Create a terminal chain that directly invokes a handler.
     *
     * @param handler the handler to invoke
     * @param <M>     the message type
     * @param <R>     the result type
     * @return the terminal chain
     */
    static <M extends Message<?>, R> InterceptorChain<M, R> terminal(java.util.function.Function<M, R> handler) {
        return handler::apply;
    }
}
