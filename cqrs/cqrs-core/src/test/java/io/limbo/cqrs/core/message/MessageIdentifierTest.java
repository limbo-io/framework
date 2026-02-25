package io.limbo.cqrs.core.message;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class MessageIdentifierTest {

    @Test
    void shouldGenerateUniqueIdentifiers() {
        MessageIdentifier id1 = MessageIdentifier.generate();
        MessageIdentifier id2 = MessageIdentifier.generate();

        assertNotEquals(id1, id2);
        assertNotEquals(id1.asString(), id2.asString());
    }

    @Test
    void shouldCreateFromString() {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        MessageIdentifier id = MessageIdentifier.fromString(uuid);

        assertEquals(uuid, id.asString());
    }

    @Test
    void shouldBeEqualWhenSameValue() {
        String uuid = "550e8400-e29b-41d4-a716-446655440000";
        MessageIdentifier id1 = MessageIdentifier.fromString(uuid);
        MessageIdentifier id2 = MessageIdentifier.fromString(uuid);

        assertEquals(id1, id2);
        assertEquals(id1.hashCode(), id2.hashCode());
    }
}
