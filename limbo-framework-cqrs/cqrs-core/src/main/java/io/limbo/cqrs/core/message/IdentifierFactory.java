package io.limbo.cqrs.core.message;

import java.util.UUID;

/**
 * Factory for generating message identifiers.
 */
public final class IdentifierFactory {

    private IdentifierFactory() {
    }

    /**
     * Generate a new unique identifier.
     *
     * @return the identifier string
     */
    public static String generate() {
        return UUID.randomUUID().toString();
    }
}
