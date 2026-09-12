package io.limbo.cqrs.core.commandhandling;

import io.limbo.cqrs.core.message.CommandMessage;
import io.limbo.cqrs.core.message.CommandResultMessage;
import io.limbo.cqrs.core.message.IdentifierFactory;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * In-memory implementation of CommandBus.
 */
public class InMemoryCommandBus implements CommandBus {

    private final ConcurrentHashMap<Class<? extends ICommand>, CommandHandler<?, ?>> handlers = new ConcurrentHashMap<>();
    private final ExecutorService asyncExecutor;

    public InMemoryCommandBus() {
        this.asyncExecutor = Executors.newVirtualThreadPerTaskExecutor();
    }

    public InMemoryCommandBus(ExecutorService asyncExecutor) {
        this.asyncExecutor = asyncExecutor;
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends ICommand, R> R dispatch(C command) {
        if (command == null) {
            throw new IllegalArgumentException("Command cannot be null");
        }

        CommandHandler<C, R> handler = (CommandHandler<C, R>) handlers.get(command.getClass());
        if (handler == null) {
            throw new HandlerNotFoundException("No handler found for command: " + command.getClass().getName());
        }

        return handler.handle(command);
    }

    @Override
    public <C extends ICommand, R> CompletableFuture<R> dispatchAsync(C command) {
        return CompletableFuture.supplyAsync(() -> dispatch(command), asyncExecutor);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends ICommand, R> CommandResultMessage<R> dispatch(CommandMessage<C> message) {
        if (message == null) {
            throw new IllegalArgumentException("Message cannot be null");
        }

        try {
            C payload = message.getPayload();
            CommandHandler<C, R> handler = (CommandHandler<C, R>) handlers.get(payload.getClass());
            if (handler == null) {
                throw new HandlerNotFoundException("No handler found for command: " + payload.getClass().getName());
            }

            R result = handler.handle(payload);
            return new CommandResultMessage<>(message.getIdentifier(), result, null);
        } catch (Exception e) {
            return new CommandResultMessage<>(message.getIdentifier(), null, e);
        }
    }

    @Override
    public <C extends ICommand, R> HandlerRegistration register(Class<C> payloadType, CommandHandler<C, R> handler) {
        handlers.put(payloadType, handler);
        return () -> handlers.remove(payloadType, handler);
    }

    @SuppressWarnings("unchecked")
    @Override
    public <C extends ICommand, R> HandlerRegistration registerMulti(Class<C>[] payloadTypes, CommandHandler<C, R> handler) {
        for (Class<C> payloadType : payloadTypes) {
            handlers.put(payloadType, handler);
        }
        return () -> {
            for (Class<C> payloadType : payloadTypes) {
                handlers.remove(payloadType, handler);
            }
        };
    }

    @Override
    public void shutdown() {
        asyncExecutor.shutdown();
    }
}
