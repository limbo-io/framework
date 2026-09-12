package io.limbo.cqrs.core.message;

import java.io.Serializable;

/**
 * Base result message envelope.
 *
 * @param <R> the result type
 */
public interface ResultMessage<R> extends Serializable {

    /**
     * Get the identifier of the original message.
     *
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Get the result payload.
     *
     * @return the result, or null if there was an exception
     */
    R getPayload();

    /**
     * Check if the handling resulted in an exception.
     *
     * @return true if there was an exception
     */
    boolean isExceptional();

    /**
     * Get the exception if handling failed.
     *
     * @return the exception, or null if successful
     */
    Throwable getException();
}
