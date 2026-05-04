package presenter;

import backend.WindowId;
import events.RxEventBus;
import gui.coords.CoordinatesFrame;
import events.RobotEvent;
import io.reactivex.rxjava3.disposables.Disposable;

import javax.swing.*;

public class CoordsPresenter implements IJInternalFramePresenter {
    private final CoordinatesFrame view;
    private final Disposable subscription;

    public CoordsPresenter(RxEventBus eventBus) {
        this.view = new CoordinatesFrame();
        this.subscription = eventBus.listen(RobotEvent.class)
                .subscribe(event -> view.updateCoordinates(event.getX(), event.getY()));
    }

    public JInternalFrame GetWindow() {
        return this.view;
    }

    public WindowId GetWindowId() {
        return WindowId.COORDS;
    }
}
