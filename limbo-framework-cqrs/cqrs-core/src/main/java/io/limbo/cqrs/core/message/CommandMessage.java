package io.limbo.cqrs.core.message;

import io.limbo.cqrs.core.commandhandling.ICommand;

import java.time.Instant;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * Message envelope for commands.
 *
 * @param <C> the command type
 */
public class CommandMessage<C extends ICommand> implements Message<C> {

    private static final long serialVersionUID = 1L;

    private final String identifier;
    private final C payload;
    private final Instant timestamp;
    private final Map<String, Object> metadata;

    public CommandMessage(C payload) {
        this(IdentifierFactory.generate(), payload, Instant.now(), new HashMap<>());
    }

    public CommandMessage(String identifier, C payload) {
        this(identifier, payload, Instant.now(), new HashMap<>());
    }

    public CommandMessage(String identifier, C payload, Instant timestamp, Map<String, Object> metadata) {
        this.identifier = identifier;
        this.payload = payload;
        this.timestamp = timestamp;
        this.metadata = new HashMap<>(metadata);
    }

    @Override
    public String getIdentifier() {
        return identifier;
    }

    @Override
    public C getPayload() {
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
    public Message<C> withMetadata(String key, Object value) {
        Map<String, Object> newMeta = new HashMap<>(metadata);
        newMeta.put(key, value);
        return new CommandMessage<>(identifier, payload, timestamp, newMeta);
    }
}
