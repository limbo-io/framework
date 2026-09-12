package io.limbo.cqrs.core.message;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

/**
 * Base message envelope that wraps a payload with metadata.
 *
 * @param <T> the payload type
 */
public interface Message<T> extends Serializable {

    /**
     * Get the unique identifier of this message.
     *
     * @return the identifier
     */
    String getIdentifier();

    /**
     * Get the payload (command or query).
     *
     * @return the payload
     */
    T getPayload();

    /**
     * Get the timestamp when this message was created.
     *
     * @return the timestamp
     */
    Instant getTimestamp();

    /**
     * Get metadata associated with this message.
     *
     * @return the metadata map
     */
    Map<String, Object> getMetadata();

    /**
     * Get a specific metadata value.
     *
     * @param key the metadata key
     * @return the value, or null if not present
     */
    Object getMetadataValue(String key);

    /**
     * Create a new message with additional metadata.
     *
     * @param key   the metadata key
     * @param value the metadata value
     * @return a new message with the added metadata
     */
    Message<T> withMetadata(String key, Object value);
}
