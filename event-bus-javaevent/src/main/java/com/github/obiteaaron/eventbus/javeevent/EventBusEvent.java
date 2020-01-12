package com.github.obiteaaron.eventbus.javeevent;

import com.github.obiteaaron.eventbus.core.Event;

import java.util.EventObject;

/**
 * @author ObiteAaron
 * @since 0.1
*/
public abstract class EventBusEvent extends EventObject implements Event {

    private static final long serialVersionUID = 1087501014885221773L;

    /**
     * System time when the event happened.
     */
    protected final long timestamp;


    /**
     * Create a new EventBusEvent.
     *
     * @param source the object on which the event initially occurred (never {@code null})
     */
    public EventBusEvent(Object source) {
        super(source);
        this.timestamp = System.currentTimeMillis();
    }


    /**
     * Return the system time in milliseconds when the event happened.
     */
    public final long getTimestamp() {
        return this.timestamp;
    }

    @Override
    public Object getEventObject() {
        return getSource();
    }
}
