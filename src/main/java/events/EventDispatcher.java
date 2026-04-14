package events;

import java.awt.AWTEvent;
import java.awt.EventQueue;
import java.awt.Toolkit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

public class EventDispatcher {
    private static EventDispatcher instance;
    private final Map<Class<?>, List<Consumer<AWTEvent>>> handlers = new HashMap<>();

    private EventDispatcher() {
    }

    public static synchronized EventDispatcher getInstance() {
        if (instance == null) {
            instance = new EventDispatcher();
            install();
        }
        return instance;
    }

    private static void install() {
        EventQueue systemQueue = Toolkit.getDefaultToolkit().getSystemEventQueue();
        systemQueue.push(new EventQueue() {
            @Override
            protected void dispatchEvent(AWTEvent event) {
                getInstance().dispatch(event);
                super.dispatchEvent(event);
            }
        });
    }

    private void dispatch(AWTEvent event) {
        List<Consumer<AWTEvent>> list = handlers.get(event.getClass());
        if (list != null) {
            for (Consumer<AWTEvent> handler : list) {
                handler.accept(event);
            }
        }
    }

    public <T extends AWTEvent> void registerHandler(Class<T> eventType, Consumer<T> handler) {
        handlers.computeIfAbsent(eventType, k -> new ArrayList<>()).add((Consumer<AWTEvent>) handler);
    }

    public <T extends AWTEvent> void unregisterHandler(Class<T> eventType, Consumer<T> handler) {
        List<Consumer<AWTEvent>> list = handlers.get(eventType);
        if (list != null) {
            list.remove(handler);
        }
    }
}