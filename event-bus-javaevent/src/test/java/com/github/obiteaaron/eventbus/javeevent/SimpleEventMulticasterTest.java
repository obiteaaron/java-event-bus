package com.github.obiteaaron.eventbus.javeevent;

import org.junit.*;
import org.junit.runners.MethodSorters;

import java.util.List;

import static org.junit.Assert.assertEquals;

/**
 * @author ObiteAaron
 * @since 0.1
 */
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
public class SimpleEventMulticasterTest {

    private static SimpleEventMulticaster simpleEventMulticaster;
    private static EventBusListener<EventBusEvent> eventBusListener;
    private static GenericEventBusListener genericEventBusListener;
    private static EventBusEvent eventBusEvent;
    private static EventBusEvent eventBusEvent2;

    @BeforeClass
    public static void init() {
        eventBusListener = event -> System.out.println("eventBusListener#onEvent " + event);
        genericEventBusListener = new GenericEventBusListener() {
            @Override
            public boolean supportsEventType(Class<? extends EventBusEvent> eventType) {
                System.out.println("genericEventBusListener#supportsEventType");
                return EventBusEventB.class.isAssignableFrom(eventType);
            }

            @Override
            public void onEvent(EventBusEvent event) {
                System.out.println("genericEventBusListener#onEvent " + event);
            }
        };
        simpleEventMulticaster = new SimpleEventMulticaster();
        eventBusEvent = new EventBusEventA(1);
        eventBusEvent2 = new EventBusEventB(2);
    }

    @Before
    public void setUp() throws Exception {
    }

    @After
    public void tearDown() throws Exception {
    }

    @Test
    public void test00_addListener() {
        simpleEventMulticaster.addListener(eventBusListener);
        simpleEventMulticaster.addListener(genericEventBusListener);
        List<EventBusListener<?>> listeners = simpleEventMulticaster.getListeners();
        Assert.assertEquals(listeners.get(0), eventBusListener);
        Assert.assertEquals(listeners.get(1), genericEventBusListener);
    }

    @Test
    public void test10_removeListener() {
        simpleEventMulticaster.removeListener(eventBusListener);
        List<EventBusListener<?>> listeners = simpleEventMulticaster.getListeners();
        assertEquals(1, listeners.size());
        simpleEventMulticaster.removeListener(genericEventBusListener);
        listeners = simpleEventMulticaster.getListeners();
        assertEquals(0, listeners.size());
    }

    @Test
    public void test11_addListener() {
        simpleEventMulticaster.addListener(eventBusListener);
        simpleEventMulticaster.addListener(genericEventBusListener);
        List<EventBusListener<?>> listeners = simpleEventMulticaster.getListeners();
        Assert.assertEquals(listeners.get(0), eventBusListener);
        Assert.assertEquals(listeners.get(1), genericEventBusListener);
    }

    @Test
    public void test20_removeAllListeners() {
        simpleEventMulticaster.removeAllListeners();
        List<EventBusListener<?>> listeners = simpleEventMulticaster.getListeners();
        assertEquals(0, listeners.size());
    }

    @Test
    public void test21_addListener() {
        simpleEventMulticaster.addListener(eventBusListener);
        simpleEventMulticaster.addListener(genericEventBusListener);
        List<EventBusListener<?>> listeners = simpleEventMulticaster.getListeners();
        Assert.assertEquals(listeners.get(0), eventBusListener);
        Assert.assertEquals(listeners.get(1), genericEventBusListener);
    }

    @Test
    public void test30_multicastEvent() {
        try {
            simpleEventMulticaster.multicastEvent(eventBusEvent);
            simpleEventMulticaster.multicastEvent(eventBusEvent2);
        } catch (Exception e) {
            Assert.fail(e.toString());
        }
    }

    public static class EventBusEventA extends EventBusEvent {

        /**
         * Create a new EventBusEvent.
         *
         * @param source the object on which the event initially occurred (never {@code null})
         */
        public EventBusEventA(Object source) {
            super(source);
        }
    }

    public static class EventBusEventB extends EventBusEvent {

        /**
         * Create a new EventBusEvent.
         *
         * @param source the object on which the event initially occurred (never {@code null})
         */
        public EventBusEventB(Object source) {
            super(source);
        }
    }
}