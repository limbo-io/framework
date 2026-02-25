package io.limbo.cqrs.core.command;

import io.limbo.cqrs.core.message.MessageIdentifier;
import io.limbo.cqrs.core.message.Metadata;

import java.util.Objects;

/**
 * Immutable wrapper for command payloads with metadata.
 *
 * @param <T> the command payload type
 */
public final class CommandMessage<T> {

    private final MessageIdentifier identifier;
    private final T payload;
    private final Metadata metadata;

    private CommandMessage(MessageIdentifier identifier, T payload, Metadata metadata) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.payload = Objects.requireNonNull(payload, "Payload cannot be null");
        this.metadata = Objects.requireNonNull(metadata, "Metadata cannot be null");
    }

    /**
     * Creates a command message from a payload.
     *
     * @param payload the command payload
     * @param <T>     the payload type
     * @return new command message
     */
    public static <T> CommandMessage<T> asCommandMessage(T payload) {
        return new CommandMessage<>(
                MessageIdentifier.generate(),
                payload,
                Metadata.empty()
        );
    }

    /**
     * Returns new command message with added metadata.
     *
     * @param key   the metadata key
     * @param value the metadata value
     * @return new command message with added metadata
     */
    public CommandMessage<T> withMetadata(String key, Object value) {
        return new CommandMessage<>(
                identifier,
                payload,
                metadata.with(key, value)
        );
    }

    /**
     * Gets the message identifier.
     *
     * @return the identifier
     */
    public MessageIdentifier getIdentifier() {
        return identifier;
    }

    /**
     * Gets the command payload.
     *
     * @return the payload
     */
    public T getPayload() {
        return payload;
    }

    /**
     * Gets the payload type.
     *
     * @return the payload class
     */
    public Class<?> getPayloadType() {
        return payload.getClass();
    }

    /**
     * Gets the metadata.
     *
     * @return the metadata
     */
    public Metadata getMetadata() {
        return metadata;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CommandMessage<?> that = (CommandMessage<?>) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "CommandMessage{" +
                "identifier=" + identifier +
                ", payloadType=" + getPayloadType().getSimpleName() +
                '}';
    }
}
