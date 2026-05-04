package presenter;

import backend.SaveManager;
import backend.WindowId;
import events.robots.RobotEventBus;
import gui.game.GameVisualizer;
import gui.game.GameWindow;
import events.robots.RobotEvent;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import model.Robot;

import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

public class GamePresenter extends InternalFramePresenter<GameWindow> {
    private static final int SIMULATION_PERIOD_MS = 10;
    private static final int FIELD_MARGIN_PX = 15;

    private final List<Robot> robots;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final RobotEventBus robotBus;
    private final GameVisualizer visualizer;

    private final AtomicBoolean simulationStarted = new AtomicBoolean(false);
    private final List<Thread> virtualThreads = new ArrayList<>();

    public GamePresenter(SaveManager saveManager, RobotEventBus eventBus, List<Robot> robotModels) {
        super(saveManager, new GameWindow(new GameVisualizer()), WindowId.GAME);
        this.visualizer = view.getVisualizer();
        this.robots = robotModels;
        this.robotBus = eventBus;

        initLogic();
    }

    private void initLogic() {
        disposables.add(robotBus.listen(RobotEvent.class)
                .subscribe(visualizer::setRobotPos));

        visualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                for (Robot robot : robots) {
                    robot.applyLocalPointerTarget(e.getPoint());
                }
            }
        });

        visualizer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                applyPlayfieldBounds(visualizer.getWidth(), visualizer.getHeight());
            }
        });

        view.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                shutdownSimulation();
                disposables.clear();
            }
        });

        visualizer.setDoubleBuffered(true);
    }

    private void applyPlayfieldBounds(int width, int height) {
        int innerMaxX = width - FIELD_MARGIN_PX;
        int innerMaxY = height - FIELD_MARGIN_PX;
        if (innerMaxX <= FIELD_MARGIN_PX || innerMaxY <= FIELD_MARGIN_PX) {
            return;
        }
        if (robots.isEmpty()) {
            return;
        }
        for (Robot robot : robots) {
            robot.onPlayfieldBoundsChanged(FIELD_MARGIN_PX, FIELD_MARGIN_PX, innerMaxX, innerMaxY);
        }
        if (simulationStarted.compareAndSet(false, true)) {
            startConcurrentSimulation();
        }
    }

    private void startConcurrentSimulation() {
        for (Robot robot : robots) {
            Thread vt = Thread.ofVirtual()
                    .name("robot-sim-" + robot.getId())
                    .start(() -> {
                        while (!Thread.currentThread().isInterrupted()) {
                            robot.onModelUpdateEvent();
                            try {
                                Thread.sleep(SIMULATION_PERIOD_MS);
                            } catch (InterruptedException e) {
                                Thread.currentThread().interrupt();
                                break;
                            }
                        }
                    });
            virtualThreads.add(vt);
        }
    }

    private void shutdownSimulation() {
        for (Thread vt : virtualThreads) {
            vt.interrupt();
        }
        virtualThreads.clear();
        simulationStarted.set(false);
    }
}
