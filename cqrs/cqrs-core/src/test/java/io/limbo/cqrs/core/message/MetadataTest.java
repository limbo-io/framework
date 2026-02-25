package io.limbo.cqrs.core.message;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

class MetadataTest {

    @Test
    void shouldReturnEmptyInstance() {
        Metadata metadata = Metadata.empty();
        assertNotNull(metadata);
        assertNull(metadata.get("key"));
    }

    @Test
    void shouldReturnNewInstanceWhenAddingValue() {
        Metadata empty = Metadata.empty();
        Metadata withValue = empty.with("key", "value");

        assertNotSame(empty, withValue);
        assertNull(empty.get("key"));
        assertEquals("value", withValue.get("key"));
    }

    @Test
    void shouldPreserveExistingValuesWhenAdding() {
        Metadata metadata = Metadata.empty()
                .with("key1", "value1")
                .with("key2", "value2");

        assertEquals("value1", metadata.get("key1"));
        assertEquals("value2", metadata.get("key2"));
    }

    @Test
    void shouldReturnTrueWhenKeyExists() {
        Metadata metadata = Metadata.empty().with("key", "value");
        assertTrue(metadata.containsKey("key"));
    }
}
