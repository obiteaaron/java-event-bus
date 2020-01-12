package com.github.obiteaaron.eventbus.javeevent;

/**
 * @author ObiteAaron
 * @since 0.1
*/
public interface GenericEventBusListener extends EventBusListener<EventBusEvent>, Ordered {
    /**
     * Determine whether this listener actually supports the given event type.
     *
     * @param eventType the event type (never {@code null})
     */
    boolean supportsEventType(Class<? extends EventBusEvent> eventType);

    /**
     * Determine whether this listener actually supports the given source type.
     * <p>The default implementation always returns {@code true}.
     *
     * @param sourceType the source type, or {@code null} if no source
     */
    default boolean supportsSourceType(Class<?> sourceType) {
        return true;
    }

    /**
     * Determine this listener's order in a set of listeners for the same event.
     * <p>The default implementation returns {@link #LOWEST_PRECEDENCE}.
     */
    @Override
    default int getOrder() {
        return LOWEST_PRECEDENCE;
    }
}
