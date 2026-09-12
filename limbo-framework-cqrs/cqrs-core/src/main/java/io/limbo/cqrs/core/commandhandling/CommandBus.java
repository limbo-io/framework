package io.limbo.cqrs.core.commandhandling;

import io.limbo.cqrs.core.message.CommandMessage;
import io.limbo.cqrs.core.message.CommandResultMessage;

import java.util.concurrent.CompletableFuture;

/**
 * Bus for dispatching commands to their handlers.
 */
public interface CommandBus {

    /**
     * Dispatch a command synchronously.
     *
     * @param command the command
     * @param <C>     the command type
     * @param <R>     the result type
     * @return the result
     * @throws HandlerNotFoundException if no handler is found
     */
    <C extends ICommand, R> R dispatch(C command);

    /**
     * Dispatch a command asynchronously.
     *
     * @param command the command
     * @param <C>     the command type
     * @param <R>     the result type
     * @return a CompletableFuture with the result
     */
    <C extends ICommand, R> CompletableFuture<R> dispatchAsync(C command);

    /**
     * Dispatch a command message enveloped.
     *
     * @param message the command message
     * @param <C>     the command type
     * @param <R>     the result type
     * @return the result message
     */
    <C extends ICommand, R> CommandResultMessage<R> dispatch(CommandMessage<C> message);

    /**
     * Register a command handler.
     *
     * @param payloadType the command type
     * @param handler     the handler
     * @param <C>         the command type
     * @param <R>         the result type
     * @return a registration that can be used to unregister
     */
    <C extends ICommand, R> HandlerRegistration register(Class<C> payloadType, CommandHandler<C, R> handler);

    /**
     * Register a command handler for multiple command types.
     *
     * @param payloadTypes the command types
     * @param handler      the handler
     * @param <C>          the command type
     * @param <R>          the result type
     * @return a registration that can be used to unregister
     */
    <C extends ICommand, R> HandlerRegistration registerMulti(Class<C>[] payloadTypes, CommandHandler<C, R> handler);

    /**
     * shutdown the command bus.
     */
    void shutdown();
}
