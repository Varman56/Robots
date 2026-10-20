package presenter;

import backend.SaveManager;
import backend.WindowId;
import com.google.gson.Gson;
import events.robots.ClearRobotsEvent;
import events.robots.RobotEventBus;
import events.robots.RobotRemovedEvent;
import gui.game.GameVisualizer;
import gui.game.GameWindow;
import events.robots.RobotEvent;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import model.Robot;
import model.RobotFleet;
import network.GameClient;
import network.NetworkMessage;

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

    private GameClient networkClient;

    public GamePresenter(SaveManager saveManager, RobotEventBus eventBus, RobotFleet fleet) {
        super(saveManager, new GameWindow(new GameVisualizer()), WindowId.GAME);
        this.visualizer = view.getVisualizer();
        this.fleet = fleet;
        this.robotBus = eventBus;

        this.fleet.setOnRobotRemoved(visualizer::removeRobotState);
        initLogic();
    }

    public void setNetworkClient(GameClient client) {
        this.networkClient = client;
    }

    private void initLogic() {
        disposables.add(robotBus.listen(RobotEvent.class)
                .subscribe(visualizer::setRobotPos));
        disposables.add(robotBus.listen(RobotRemovedEvent.class)
                .subscribe(event -> visualizer.removeRobotState(event.id())));

        disposables.add(robotBus.listen(ClearRobotsEvent.class).subscribe(e -> {
            visualizer.clear();
        }));

        visualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (networkClient != null) {
                    networkClient.sendRequest(NetworkMessage.Type.MOVE_REQUEST,
                            new Gson().toJson(e.getPoint()));
                }
                else {
                    for (Robot robot : fleet.getRobots()) {
                        robot.applyLocalPointerTarget(e.getPoint());
                    }
                }
            }
        });

        visualizer.addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                if (networkClient == null) fleet.applyViewportSize(visualizer.getWidth(), visualizer.getHeight());
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
