package io.limbo.cqrs.core.handler;

/**
 * Thrown when no handler is registered for a message type.
 */
public class HandlerNotFoundException extends RuntimeException {

    private final Class<?> messageType;

    /**
     * Creates exception for the given message type.
     *
     * @param messageType the type that has no handler
     */
    public HandlerNotFoundException(Class<?> messageType) {
        super("No handler registered for type: " + messageType.getName());
        this.messageType = messageType;
    }

    /**
     * Gets the message type that has no handler.
     *
     * @return the message type
     */
    public Class<?> getMessageType() {
        return messageType;
    }
}
