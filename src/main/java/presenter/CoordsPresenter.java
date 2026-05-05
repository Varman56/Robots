package presenter;

import backend.SaveManager;
import backend.WindowId;
import events.robots.RobotEvent;
import events.robots.RobotEventBus;
import gui.coords.CoordinatesFrame;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class CoordsPresenter extends InternalFramePresenter {
    private final CompositeDisposable disposables = new CompositeDisposable();

    public CoordsPresenter(SaveManager saveManager, RobotEventBus robotBus) {
        super(saveManager, new CoordinatesFrame(), WindowId.COORDS);
        disposables.add(
                robotBus.listen(RobotEvent.class)
                        .filter(re -> re.getId() == 0)
                        .subscribe(re -> {
                            CoordinatesFrame cf = (CoordinatesFrame) getView();
                            cf.updateCoordinates(re.getX(), re.getY());
                        })
        );
        view.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                disposables.clear();
            }
        });
    }
}
