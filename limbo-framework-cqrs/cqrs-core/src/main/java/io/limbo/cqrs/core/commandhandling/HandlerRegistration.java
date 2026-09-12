package io.limbo.cqrs.core.commandhandling;

/**
 * Registration handle for subscribed handlers. Unregistering a handler removes it from the command bus.
 */
@FunctionalInterface
public interface HandlerRegistration {

    /**
     * Creates a simple registration with the given unregister action.
     *
     * @param unregisterAction the action to unregister
     * @return the registration
     */
    static HandlerRegistration of(Runnable unregisterAction) {
        return unregisterAction::run;
    }

    /**
     * Cancel the handler subscription.
     */
    void unregister();
}
