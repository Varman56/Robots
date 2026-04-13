package presenter;

import backend.EventDispatcher;
import backend.WindowId;
import gui.coords.CoordinatesFrame;
import events.RobotEvent;

import javax.swing.*;

public class CoordsPresenter implements IJInternalFramePresenter {
    private final CoordinatesFrame view;
    private final EventDispatcher eventDispatcher;

    public CoordsPresenter(EventDispatcher eventDispatcher) {
        this.view = new CoordinatesFrame();
        this.eventDispatcher = eventDispatcher;
        setupEventHandling();
    }

    public JInternalFrame GetWindow() {
        return this.view;
    }

    public WindowId GetWindowId() {
        return WindowId.COORDS;
    }

    private void setupEventHandling() {
        eventDispatcher.registerHandler(RobotEvent.class, this::onRobotMoved);
    }

    private void onRobotMoved(RobotEvent re) {
        view.updateCoordinates(re.getX(), re.getY());
    }
}
