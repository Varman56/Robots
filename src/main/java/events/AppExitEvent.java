package events;

import java.awt.AWTEvent;

public class AppExitEvent extends AWTEvent {
    public static final int APP_EXIT = AWTEvent.RESERVED_ID_MAX + 200;

    public AppExitEvent(Object source) {
        super(source, APP_EXIT);
    }
}