package io.limbo.cqrs.core.message;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Immutable key-value metadata container.
 * Modifications return new instances instead of mutating.
 */
public final class Metadata {

    private final Map<String, Object> values;

    private Metadata(Map<String, Object> values) {
        this.values = Collections.unmodifiableMap(new HashMap<>(values));
    }

    /**
     * Returns empty metadata instance.
     *
     * @return empty metadata
     */
    public static Metadata empty() {
        return new Metadata(Collections.emptyMap());
    }

    /**
     * Returns new metadata with the given key-value pair.
     * Returns same instance if both key and value are null.
     *
     * @param key   the metadata key
     * @param value the metadata value
     * @return new metadata instance with added value, or this if key or value null
     */
    public Metadata with(String key, Object value) {
        if (key == null || value == null) {
            Map<String, Object> newValues = new HashMap<>(values);
            if (key != null) {
                newValues.remove(key);
            }
            return new Metadata(newValues);
        }

        Map<String, Object> newValues = new HashMap<>(values);
        newValues.put(key, value);
        return new Metadata(newValues);
    }

    /**
     * Merges another metadata into this one.
     * Returns this instance if other is empty.
     *
     * @param other the metadata to merge
     * @return new metadata containing values from both
     */
    public Metadata merge(Metadata other) {
        if (other.values.isEmpty()) {
            return this;
        }

        Map<String, Object> newValues = new HashMap<>(values);
        newValues.putAll(other.values);
        return new Metadata(newValues);
    }

    /**
     * Gets a value by key.
     *
     * @param key the metadata key
     * @return the value, or null if not found
     */
    @SuppressWarnings("unchecked")
    public <T> T get(String key) {
        return (T) values.get(key);
    }

    /**
     * Checks if a key exists in metadata.
     *
     * @param key the metadata key
     * @return true if key exists and value is non-null
     */
    public boolean containsKey(String key) {
        return values.containsKey(key) && values.get(key) != null;
    }

    /**
     * Returns all keys in metadata.
     *
     * @return set of keys
     */
    public Set<String> keys() {
        return values.keySet();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Metadata metadata = (Metadata) o;
        return Objects.equals(values, metadata.values);
    }

    @Override
    public int hashCode() {
        return Objects.hash(values);
    }

    @Override
    public String toString() {
        return "Metadata{" + values + "}";
    }
}
