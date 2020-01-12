package com.github.obiteaaron.eventbus.javeevent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @author ObiteAaron
 * @since 0.1
*/
public abstract class AbstractEventMulticaster implements EventMulticaster {

    private final ListenerRetriever defaultRetriever = new ListenerRetriever();

    final Map<ListenerCacheKey, ListenerRetriever> retrieverCache = new ConcurrentHashMap<>(64);

    private Object retrievalMutex = this.defaultRetriever;

    @Override
    public void addListener(EventBusListener<? extends EventBusEvent> listener) {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.listeners.add(listener);
            this.retrieverCache.clear();
        }
    }

    @Override
    public void removeListener(EventBusListener<? extends EventBusEvent> listener) {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.listeners.remove(listener);
            this.retrieverCache.clear();
        }
    }

    @Override
    public void removeAllListeners() {
        synchronized (this.retrievalMutex) {
            this.defaultRetriever.listeners.clear();
            this.retrieverCache.clear();
        }
    }

    /**
     * Return a Collection containing all EventBusListeners.
     */
    protected List<EventBusListener<?>> getListeners() {
        synchronized (this.retrievalMutex) {
            return this.defaultRetriever.getListeners();
        }
    }

    /**
     * Return a Collection of EventBusListeners matching the given
     * event type. Non-matching listeners get excluded early.
     *
     * @param event     the event to be propagated. Allows for excluding
     *                  non-matching listeners early, based on cached matching information.
     * @param eventType the event type
     */
    protected List<EventBusListener<?>> getListeners(EventBusEvent event, Class<? extends EventBusEvent> eventType) {

        Object source = event.getSource();
        Class<?> sourceType = (source != null ? source.getClass() : null);
        ListenerCacheKey cacheKey = new ListenerCacheKey(eventType, sourceType);

        // Quick check for existing entry on ConcurrentHashMap...
        ListenerRetriever retriever = this.retrieverCache.get(cacheKey);
        if (retriever != null) {
            return retriever.getListeners();
        }

        // Fully synchronized building and caching of a ListenerRetriever
        synchronized (this.retrievalMutex) {
            retriever = this.retrieverCache.get(cacheKey);
            if (retriever != null) {
                return retriever.getListeners();
            }
            retriever = new ListenerRetriever();
            List<EventBusListener<?>> listeners = retrieveListeners(eventType, sourceType, retriever);
            this.retrieverCache.put(cacheKey, retriever);
            return listeners;
        }
    }

    /**
     * Actually retrieve the listeners for the given event and source type.
     *
     * @param eventType  the event type
     * @param sourceType the event source type
     * @param retriever  the ListenerRetriever, if supposed to populate one (for caching purposes)
     * @return the pre-filtered list of listeners for the given event and source type
     */
    private List<EventBusListener<?>> retrieveListeners(
            Class<? extends EventBusEvent> eventType, @Nullable Class<?> sourceType, @Nullable ListenerRetriever retriever) {

        List<EventBusListener<?>> allListeners = new ArrayList<>();
        Set<EventBusListener<?>> listeners;
        synchronized (this.retrievalMutex) {
            listeners = new LinkedHashSet<>(this.defaultRetriever.listeners);
        }
        for (EventBusListener<?> listener : listeners) {
            if (supportsEvent(listener, eventType, sourceType)) {
                if (retriever != null) {
                    retriever.listeners.add(listener);
                }
                allListeners.add(listener);
            }
        }

        // sort allListeners
        if (retriever != null) {
            retriever.listeners.clear();
            retriever.listeners.addAll(allListeners);
        }
        return allListeners;
    }

    /**
     * Determine whether the given listener supports the given event.
     * <p>The default implementation detects the {@link GenericEventBusListener} interfaces.
     * In case of a standard {@link EventBusListener} will supported all event.
     *
     * @param listener   the target listener to check
     * @param eventType  the event type to check against
     * @param sourceType the source type to check against
     * @return whether the given listener should be included in the candidates for the given event type
     */
    protected boolean supportsEvent(EventBusListener<?> listener, Class<? extends EventBusEvent> eventType, @Nullable Class<?> sourceType) {

        if (listener instanceof GenericEventBusListener) {
            GenericEventBusListener genericListener = (GenericEventBusListener) listener;
            return (genericListener.supportsEventType(eventType) && genericListener.supportsSourceType(sourceType));
        } else {
            return true;
        }
    }

    /**
     * Cache key for ListenerRetrievers, based on event type and source type.
     */
    private static final class ListenerCacheKey implements Comparable<ListenerCacheKey> {

        private final Class<?> eventType;

        private final Class<?> eventObjectType;

        public ListenerCacheKey(Class<?> eventType, @Nullable Class<?> eventObjectType) {
            Objects.requireNonNull(eventType, "Event type must not be null");
            this.eventType = eventType;
            this.eventObjectType = eventObjectType;
        }

        @Override
        public boolean equals(Object other) {
            if (this == other) {
                return true;
            }
            if (!(other instanceof ListenerCacheKey)) {
                return false;
            }
            ListenerCacheKey otherKey = (ListenerCacheKey) other;
            return (this.eventType.equals(otherKey.eventType) &&
                    Objects.equals(this.eventObjectType, otherKey.eventObjectType));
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.eventType, this.eventObjectType);
        }

        @Override
        public String toString() {
            return "ListenerCacheKey [eventType = " + this.eventType + ", eventObjectType = " + this.eventObjectType + "]";
        }

        @Override
        public int compareTo(ListenerCacheKey other) {
            int result = this.eventType.toString().compareTo(other.eventType.toString());
            if (result == 0) {
                if (this.eventObjectType == null) {
                    return (other.eventObjectType == null ? 0 : -1);
                }
                if (other.eventObjectType == null) {
                    return 1;
                }
                result = this.eventObjectType.getName().compareTo(other.eventObjectType.getName());
            }
            return result;
        }
    }


    /**
     * Helper class that encapsulates a specific set of target listeners,
     * allowing for efficient retrieval of pre-filtered listeners.
     * <p>An instance of this helper gets cached per event type and source type.
     */
    private class ListenerRetriever {

        public final Set<EventBusListener<?>> listeners = new LinkedHashSet<>();

        public ListenerRetriever() {
        }

        public List<EventBusListener<?>> getListeners() {
            List<EventBusListener<?>> allListeners = new ArrayList<>(this.listeners.size());
            allListeners.addAll(this.listeners);
            return allListeners;
        }
    }

}
