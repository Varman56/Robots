package network;

import com.google.gson.Gson;
import events.robots.ClearRobotsEvent;
import events.robots.RobotEvent;
import events.robots.RobotEventBus;
import events.robots.RobotRemovedEvent;
import model.RobotFleet;
import presenter.GamePresenter;
import presenter.RobotStatePresenter;

import javax.swing.*;
import java.io.IOException;
import java.net.ServerSocket;

public class NetworkController {
    private final RobotEventBus robotBus;
    private final RobotFleet fleet;
    private GameServer server;
    private GameClient client;

    private GamePresenter gamePresenter;
    private RobotStatePresenter statePresenter;

    private final java.util.concurrent.ConcurrentHashMap<Integer, RobotEvent> lastKnownStates = new java.util.concurrent.ConcurrentHashMap<>();

    public NetworkController(RobotEventBus robotBus, RobotFleet fleet, GamePresenter gp, RobotStatePresenter rsp) {
        this.robotBus = robotBus;
        this.fleet = fleet;
        this.gamePresenter = gp;
        this.statePresenter = rsp;

        this.robotBus.listen(RobotRemovedEvent.class).subscribe(event -> {
            if (server != null) {
                String json = new com.google.gson.Gson().toJson(event);
                server.broadcast(new NetworkMessage(NetworkMessage.Type.ROBOT_REMOVED, json));
            }
        });
        this.robotBus.listen(RobotEvent.class).subscribe(re -> {
            lastKnownStates.put(re.getId(), re);
        });
    }

    public void startHost(int port) {
        if (!isPortAvailable(port)) {
            JOptionPane.showMessageDialog(null, "Порт " + port + " уже занят!");
            return;
        }
        stopAll();
        server = new GameServer(port, fleet);
        server.start();

        robotBus.listen(RobotEvent.class).subscribe(event -> {
            String json = new Gson().toJson(event);
            if (server != null) server.broadcast(new NetworkMessage(NetworkMessage.Type.STATE_UPDATE, json));
        });

    }

    public void connectTo(String host, int port) {
        stopAll();
        fleet.shutdownSimulation();

        robotBus.send(new ClearRobotsEvent());

        client = new GameClient(host, port, robotBus);
        client.setOnDisconnect(() -> javax.swing.SwingUtilities.invokeLater(this::stopAll));
        client.connect();

        gamePresenter.setNetworkClient(client);
        statePresenter.setNetworkClient(client);
    }

    public void stopAll() {
        if (server != null) {
            server.stop();
            server = null;
        }
        if (client != null) {
            client.stop();
            client = null;
        }
        gamePresenter.setNetworkClient(null);
        statePresenter.setNetworkClient(null);

        for (model.Robot robot : fleet.getRobots()) {
            RobotEvent lastState = lastKnownStates.get(robot.getId());
            if (lastState != null) {
                robot.syncInternalState(
                        lastState.getX(),
                        lastState.getY(),
                        lastState.getDir(),
                        lastState.getT_x(),
                        lastState.getT_y()
                );
            }
        }

        fleet.restartSimulation();
    }

    private boolean isPortAvailable(int port) {
        try (ServerSocket ignored = new ServerSocket(port)) {
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}