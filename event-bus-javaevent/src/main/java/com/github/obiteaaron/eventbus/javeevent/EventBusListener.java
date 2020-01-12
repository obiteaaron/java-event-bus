package com.github.obiteaaron.eventbus.javeevent;

import java.util.EventListener;

/**
 * @author ObiteAaron
 * @since 0.1
*/
@FunctionalInterface
public interface EventBusListener<E extends EventBusEvent> extends com.github.obiteaaron.eventbus.core.EventListener<E>, EventListener {

    /**
     * Handle an EventBusEvent.
     *
     * @param event the event to respond to
     */
    @Override
    void onEvent(E event);
}
