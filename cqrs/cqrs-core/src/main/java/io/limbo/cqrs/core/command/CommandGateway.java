package io.limbo.cqrs.core.command;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

/**
 * Simple facade for sending commands.
 * Provides convenient methods for command dispatch with various options.
 */
public interface CommandGateway {

    /**
     * Sends a command and returns immediately (fire-and-forget).
     * The result is ignored.
     *
     * @param command the command to send
     */
    void send(Command command);

    /**
     * Sends a command and waits for the result.
     *
     * @param command the command to send
     * @return the handler result
     */
    Object sendAndWait(Command command);

    /**
     * Sends a command and waits for the result with timeout.
     *
     * @param command the command to send
     * @param timeout the timeout value
     * @param unit    the timeout unit
     * @return the handler result
     * @throws RuntimeException if timeout or execution fails
     */
    Object sendAndWait(Command command, long timeout, TimeUnit unit);

    /**
     * Sends a command asynchronously.
     *
     * @param command the command to send
     * @return future containing the handler result
     */
    CompletableFuture<Object> sendAsync(Command command);
}
