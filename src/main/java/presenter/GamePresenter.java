package presenter;

import backend.SaveManager;
import backend.WindowId;
import events.robots.RobotEventBus;
import gui.game.GameVisualizer;
import gui.game.GameWindow;
import events.robots.RobotEvent;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import model.Robot;
import model.RobotFleet;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public class GamePresenter extends InternalFramePresenter<GameWindow> {
    private final RobotFleet fleet;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final RobotEventBus robotBus;
    private final GameVisualizer visualizer;

    public GamePresenter(SaveManager saveManager, RobotEventBus eventBus, RobotFleet fleet) {
        super(saveManager, new GameWindow(new GameVisualizer()), WindowId.GAME);
        this.visualizer = view.getVisualizer();
        this.fleet = fleet;
        this.robotBus = eventBus;

        this.fleet.setOnRobotRemoved(visualizer::removeRobotState);
        initLogic();
    }

    private void initLogic() {
        disposables.add(robotBus.listen(RobotEvent.class)
                .subscribe(visualizer::setRobotPos));

        visualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Robot robot : fleet.getRobots()) {
                    robot.applyLocalPointerTarget(e.getPoint());
                }
            }
        });

        visualizer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                fleet.applyViewportSize(visualizer.getWidth(), visualizer.getHeight());
            }
        });

        view.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                fleet.shutdownSimulation();
                disposables.clear();
            }
        });

        visualizer.setDoubleBuffered(true);
    }
}
