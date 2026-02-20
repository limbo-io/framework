package io.limbo.cqrs.core.message;

import java.util.Objects;
import java.util.UUID;

/**
 * Immutable identifier for messages.
 * Wraps a string identifier with type safety and validation.
 */
public final class MessageIdentifier {

    private final String identifier;

    private MessageIdentifier(String identifier) {
        this.identifier = Objects.requireNonNull(identifier, "Identifier cannot be null");
        if (identifier.isEmpty()) {
            throw new IllegalArgumentException("Identifier cannot be empty");
        }
    }

    /**
     * Generates a new random identifier.
     *
     * @return new message identifier
     */
    public static MessageIdentifier generate() {
        return new MessageIdentifier(UUID.randomUUID().toString());
    }

    /**
     * Creates an identifier from a string value.
     *
     * @param identifier the identifier string
     * @return new message identifier
     * @throws IllegalArgumentException if identifier is null or empty
     */
    public static MessageIdentifier fromString(String identifier) {
        return new MessageIdentifier(identifier);
    }

    /**
     * Returns the identifier as a string.
     *
     * @return the identifier string
     */
    public String asString() {
        return identifier;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        MessageIdentifier that = (MessageIdentifier) o;
        return Objects.equals(identifier, that.identifier);
    }

    @Override
    public int hashCode() {
        return Objects.hash(identifier);
    }

    @Override
    public String toString() {
        return identifier;
    }
}
