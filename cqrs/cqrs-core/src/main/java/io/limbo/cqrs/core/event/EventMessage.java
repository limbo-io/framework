package io.limbo.cqrs.core.event;

import io.limbo.cqrs.core.message.MessageIdentifier;
import io.limbo.cqrs.core.message.Metadata;

import java.time.Instant;
import java.util.Objects;

/**
 * Immutable wrapper for event payloads with metadata and timestamps.
 *
 * @param <T> the event payload type
 */
public final class EventMessage<T> {

    private final MessageIdentifier identifier;
    private final T payload;
    private final Metadata metadata;
    private final Instant timestamp;
    private final String aggregateIdentifier;

    private EventMessage(MessageIdentifier identifier, T payload, Metadata metadata,
                         Instant timestamp, String aggregateIdentifier) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        this.payload = Objects.requireNonNull(payload, "Payload cannot be null");
        this.metadata = Objects.requireNonNull(metadata, "Metadata cannot be null");
        this.timestamp = Objects.requireNonNull(timestamp, "Timestamp cannot be null");
        this.aggregateIdentifier = aggregateIdentifier;
    }

    /**
     * Creates an event message from a payload.
     *
     * @param payload the event payload
     * @param <T>     the payload type
     * @return new event message
     */
    public static <T> EventMessage<T> asEventMessage(T payload) {
        return new EventMessage<>(
                MessageIdentifier.generate(),
                payload,
                Metadata.empty(),
                Instant.now(),
                null
        );
    }

    /**
     * Creates an event message from a payload with aggregate identifier.
     *
     * @param payload             the event payload
     * @param aggregateIdentifier the aggregate identifier
     * @param <T>                 the payload type
     * @return new event message
     */
    public static <T> EventMessage<T> asEventMessage(T payload, String aggregateIdentifier) {
        return new EventMessage<>(
                MessageIdentifier.generate(),
                payload,
                Metadata.empty(),
                Instant.now(),
                aggregateIdentifier
        );
    }

    /**
     * Returns new event message with added metadata.
     *
     * @param key   the metadata key
     * @param value the metadata value
     * @return new event message with added metadata
     */
    public EventMessage<T> withMetadata(String key, Object value) {
        return new EventMessage<>(
                identifier,
                payload,
                metadata.with(key, value),
                timestamp,
                aggregateIdentifier
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
     * Gets the event payload.
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

    /**
     * Gets the timestamp.
     *
     * @return the timestamp
     */
    public Instant getTimestamp() {
        return timestamp;
    }

    /**
     * Gets the aggregate identifier.
     *
     * @return the aggregate identifier, or null if not applicable
     */
    public String getAggregateIdentifier() {
        return aggregateIdentifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        EventMessage<?> that = (EventMessage<?>) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return "EventMessage{" +
                "identifier=" + identifier +
                ", payloadType=" + getPayloadType().getSimpleName() +
                ", timestamp=" + timestamp +
                '}';
    }
}
