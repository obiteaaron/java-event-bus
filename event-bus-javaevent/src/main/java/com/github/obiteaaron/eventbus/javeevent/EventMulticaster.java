package com.github.obiteaaron.eventbus.javeevent;

/**
 * @author ObiteAaron
 * @since 0.1
*/
public interface EventMulticaster {
    /**
     * Add a listener to be notified of all events.
     *
     * @param listener the listener to add
     */
    void addListener(EventBusListener<? extends EventBusEvent> listener);

    /**
     * Remove a listener from the notification list.
     *
     * @param listener the listener to remove
     */
    void removeListener(EventBusListener<? extends EventBusEvent> listener);

    /**
     * Remove all listeners registered with this multicaster.
     * <p>After a remove call, the multicaster will perform no action
     * on event notification until new listeners are being registered.
     */
    void removeAllListeners();

    /**
     * Multicast the given event to appropriate listeners.
     *
     * @param event the event to multicast
     */
    void multicastEvent(EventBusEvent event);
}
