package io.limbo.cqrs.core.commandhandling;

import java.io.Serial;

/**
 * Exception thrown when no handler is found for a command or query.
 */
public class HandlerNotFoundException extends RuntimeException {

    @Serial
    private static final long serialVersionUID = 1L;

    public HandlerNotFoundException(String message) {
        super(message);
    }

    public HandlerNotFoundException(String message, Throwable cause) {
        super(message, cause);
    }
}
