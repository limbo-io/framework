package io.limbo.cqrs.core.interception;

import io.limbo.cqrs.core.message.Message;

import java.util.List;

/**
 * Interceptor for dispatched messages (before handler execution).
 *
 * @param <M> the message type
 */
@FunctionalInterface
public interface DispatchInterceptor<M extends Message<?>> {

    /**
     * Intercept the message before it reaches the handler.
     *
     * @param message the original message
     * @return the (possibly modified) message to pass to the handler
     */
    M handle(M message);

    /**
     * Chain multiple interceptors together.
     *
     * @param interceptors the interceptors to chain
     * @return a composite interceptor
     */
    @SafeVarargs
    static <M extends Message<?>> DispatchInterceptor<M> chain(DispatchInterceptor<M>... interceptors) {
        return message -> {
            M result = message;
            for (DispatchInterceptor<M> interceptor : interceptors) {
                result = interceptor.handle(result);
            }
            return result;
        };
    }

    /**
     * Chain multiple interceptors from a list.
     *
     * @param interceptors the interceptors to chain
     * @return a composite interceptor
     */
    static <M extends Message<?>> DispatchInterceptor<M> chain(List<DispatchInterceptor<M>> interceptors) {
        return message -> {
            M result = message;
            for (DispatchInterceptor<M> interceptor : interceptors) {
                result = interceptor.handle(result);
            }
            return result;
        };
    }
}
