package io.limbo.cqrs.core.query;

import io.limbo.cqrs.core.message.MessageIdentifier;
import io.limbo.cqrs.core.message.Metadata;

import java.util.Objects;

/**
 * Immutable wrapper for query payloads with metadata.
 *
 * @param <T> the query payload type
 */
public final class QueryMessage<T> {

    private final MessageIdentifier identifier;
    private final T payload;
    private final Metadata metadata;

    private QueryMessage(MessageIdentifier identifier, T payload, Metadata metadata) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.payload = Objects.requireNonNull(payload, "Payload cannot be null");
        this.metadata = Objects.requireNonNull(metadata, "Metadata cannot be null");
    }

    /**
     * Creates a query message from a payload.
     *
     * @param payload the query payload
     * @param <T>     the payload type
     * @return new query message
     */
    public static <T> QueryMessage<T> asQueryMessage(T payload) {
        return new QueryMessage<>(
                MessageIdentifier.generate(),
                payload,
                Metadata.empty()
        );
    }

    /**
     * Returns new query message with added metadata.
     *
     * @param key   the metadata key
     * @param value the metadata value
     * @return new query message with added metadata
     */
    public QueryMessage<T> withMetadata(String key, Object value) {
        return new QueryMessage<>(
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
     * Gets the query payload.
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
        QueryMessage<?> that = (QueryMessage<?>) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "QueryMessage{" +
                "identifier=" + identifier +
                ", payloadType=" + getPayloadType().getSimpleName() +
                '}';
    }
}
