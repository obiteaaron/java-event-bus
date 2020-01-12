package com.github.obiteaaron.eventbus.javeevent;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Executor;

/**
 * @author ObiteAaron
 * @since 0.1
*/
public class SimpleEventMulticaster extends AbstractEventMulticaster {

    private static final Logger LOGGER = LoggerFactory.getLogger(SimpleEventMulticaster.class);

    private ErrorHandler errorHandler;
    private Executor taskExecutor;

    @Override
    public void multicastEvent(EventBusEvent event) {
        Class<? extends EventBusEvent> type = event.getClass();
        Executor executor = getTaskExecutor();
        for (EventBusListener<?> listener : getListeners(event, type)) {
            if (executor != null) {
                executor.execute(() -> invokeListener(listener, event));
            } else {
                invokeListener(listener, event);
            }
        }
    }

    /**
     * Invoke the given listener with the given event.
     *
     * @param listener the Listener to invoke
     * @param event    the current event to propagate
     * @since 0.1
*/
    protected void invokeListener(EventBusListener<?> listener, EventBusEvent event) {
        ErrorHandler errorHandler = getErrorHandler();
        if (errorHandler != null) {
            try {
                doInvokeListener(listener, event);
            } catch (Throwable err) {
                errorHandler.handleError(err);
            }
        } else {
            doInvokeListener(listener, event);
        }
    }

    public ErrorHandler getErrorHandler() {
        return errorHandler;
    }

    public void setErrorHandler(ErrorHandler errorHandler) {
        this.errorHandler = errorHandler;
    }

    @SuppressWarnings({"rawtypes", "unchecked"})
    private void doInvokeListener(EventBusListener listener, EventBusEvent event) {
        if (listener == null) {
            if (LOGGER.isTraceEnabled()) {
                LOGGER.trace("Non-matching listener for event type " + event.getClass());
            }
            return;
        }
        listener.onEvent(event);
    }

    public Executor getTaskExecutor() {
        return taskExecutor;
    }

    public void setTaskExecutor(Executor taskExecutor) {
        this.taskExecutor = taskExecutor;
    }
}
