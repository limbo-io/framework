package io.limbo.cqrs.core.message;

import io.limbo.cqrs.core.queryhandling.IQuery;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Message envelope for queries.
 *
 * @param <Q> the query type
 * @param <R> the result type
 */
public class QueryMessage<Q extends IQuery<R>, R> implements Message<Q> {

    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final Q payload;
    private final Instant timestamp;
    private final Map<String, Object> metadata;
    private final Class<R> resultType;

    public QueryMessage(Q payload, Class<R> resultType) {
        this(IdentifierFactory.generate(), payload, Instant.now(), new HashMap<>(), resultType);
    }

    public QueryMessage(String identifier, Q payload, Class<R> resultType) {
        this(identifier, payload, Instant.now(), new HashMap<>(), resultType);
    }

    public QueryMessage(String identifier, Q payload, Instant timestamp, Map<String, Object> metadata, Class<R> resultType) {
        this.identifier = identifier;
        this.payload = payload;
        this.timestamp = timestamp;
        this.metadata = new HashMap<>(metadata);
        this.resultType = resultType;
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public Q getPayload() {
        return payload;
    }

    @Override
    public Instant getTimestamp() {
        return timestamp;
    }

    @Override
    public Map<String, Object> getMetadata() {
        return Collections.unmodifiableMap(metadata);
    }

    @Override
    public Object getMetadataValue(String key) {
        return metadata.get(key);
    }

    @Override
    public Message<Q> withMetadata(String key, Object value) {
        Map<String, Object> newMeta = new HashMap<>(metadata);
        newMeta.put(key, value);
        return new QueryMessage<>(identifier, payload, timestamp, newMeta, resultType);
    }

    public Class<R> getResultType() {
        return resultType;
    }
}
