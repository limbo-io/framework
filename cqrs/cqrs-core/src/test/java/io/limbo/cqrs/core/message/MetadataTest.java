package io.limbo.cqrs.core.message;

import org.junit.jupiter.api.Test;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Tests for {@link Metadata}.
 */
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
    void shouldReplaceValueWhenKeyExists() {
        Metadata metadata = Metadata.empty()
                .with("key", "original")
                .with("key", "replaced");

        assertEquals("replaced", metadata.get("key"));
    }

    @Test
    void shouldReturnTrueWhenKeyExists() {
        Metadata metadata = Metadata.empty().with("key", "value");

        assertTrue(metadata.containsKey("key"));
    }

    @Test
    void shouldReturnFalseWhenKeyDoesNotExist() {
        Metadata metadata = Metadata.empty();

        assertFalse(metadata.containsKey("key"));
    }

    @Test
    void shouldReturnKeys() {
        Metadata metadata = Metadata.empty()
                .with("key1", "value1")
                .with("key2", "value2");

        Set<String> keys = metadata.keys();
        assertEquals(2, keys.size());
        assertTrue(keys.contains("key1"));
        assertTrue(keys.contains("key2"));
    }

    @Test
    void shouldMergeTwoMetadatas() {
        Metadata metadata1 = Metadata.empty().with("key1", "value1");
        Metadata metadata2 = Metadata.empty().with("key2", "value2");

        Metadata merged = metadata1.merge(metadata2);

        assertEquals("value1", merged.get("key1"));
        assertEquals("value2", merged.get("key2"));
    }

    @Test
    void shouldReturnSameInstanceWhenMergingEmpty() {
        Metadata metadata = Metadata.empty().with("key", "value");
        Metadata empty = Metadata.empty();

        Metadata merged = metadata.merge(empty);

        assertSame(metadata, merged);
    }

    @Test
    void shouldReturnNewInstanceWhenAddingNullValue() {
        Metadata metadata = Metadata.empty().with("key", null);

        assertNull(metadata.get("key"));
        assertFalse(metadata.containsKey("key"));
    }
}
