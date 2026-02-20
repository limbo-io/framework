package io.limbo.cqrs.core.handler;

/**
 * Functional interface for message handlers.
 *
 * @param <M> the message type
 * @param <R> the result type
 */
@FunctionalInterface
public interface Handler<M, R> {

    /**
     * Handles the given message.
     *
     * @param message the message to handle
     * @return the handler result
     * @throws Exception if handling fails
     */
    R handle(M message) throws Exception;
}
