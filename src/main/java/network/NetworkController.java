package network;

import com.google.gson.Gson;
import events.robots.ClearRobotsEvent;
import events.robots.RobotEvent;
import events.robots.RobotEventBus;
import events.robots.RobotRemovedEvent;
import model.RobotFleet;
import presenter.GamePresenter;
import presenter.RobotStatePresenter;

public class NetworkController {
    private final RobotEventBus robotBus;
    private final RobotFleet fleet;
    private GameServer server;
    private GameClient client;
    private boolean isClientMode = false;

    public NetworkController(RobotEventBus robotBus, RobotFleet fleet) {
        this.robotBus = robotBus;
        this.fleet = fleet;
    }

    public void startHost(int port) {
        stopAll();
        server = new GameServer(port, fleet);
        server.start();

        robotBus.listen(RobotEvent.class).subscribe(event -> {
            String json = new Gson().toJson(event);
            if (server != null) server.broadcast(new NetworkMessage(NetworkMessage.Type.STATE_UPDATE, json));
        });

        robotBus.listen(RobotRemovedEvent.class).subscribe(event -> {
            if (server != null) {
                String json = new Gson().toJson(event);
                server.broadcast(new NetworkMessage(NetworkMessage.Type.ROBOT_REMOVED, json));
            }
        });
    }

    public void connectTo(String host, int port, GamePresenter gp, RobotStatePresenter rsp) {
        stopAll();
        isClientMode = true;

        if (fleet != null) {
            fleet.shutdownSimulation();
        }

        robotBus.send(new ClearRobotsEvent());

        client = new GameClient(host, port, robotBus);
        client.connect();

        gp.setNetworkClient(client);
        rsp.setNetworkClient(client);
    }

    public void stopAll() {
        isClientMode = false;
        if (server != null) {
            server.stop();
            server = null;
        }
        if (client != null) {
            client.stop();
            client = null;
        }
    }

    public boolean isClientMode() { return isClientMode; }
}