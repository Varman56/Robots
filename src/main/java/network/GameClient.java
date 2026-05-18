package network;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import events.robots.ClearRobotsEvent;
import events.robots.RobotEvent;
import events.robots.RobotEventBus;
import events.robots.RobotRemovedEvent;

import java.io.*;
import java.lang.reflect.Type;
import java.net.Socket;
import java.util.List;

public class GameClient {
    private final String host;
    private final int port;
    private final RobotEventBus bus;
    private final Gson gson = new Gson();
    private PrintWriter out;

    public GameClient(String host, int port, RobotEventBus bus) {
        this.host = host;
        this.port = port;
        this.bus = bus;
    }

    public void connect() {
        Thread networkThread = new Thread(() -> {
            try (Socket socket = new Socket(host, port);
                 BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(socket.getOutputStream(), true);

                String line;
                while ((line = in.readLine()) != null) {
                    NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                    if (msg.type == NetworkMessage.Type.SYNC_INIT) {
                        bus.send(new ClearRobotsEvent());

                        Type listType = new TypeToken<List<RobotEvent>>(){}.getType();
                        List<RobotEvent> initialRobots = gson.fromJson(msg.payload, listType);
                        for (RobotEvent re : initialRobots) {
                            bus.send(re);
                        }
                    }
                    if (msg.type == NetworkMessage.Type.STATE_UPDATE) {
                        RobotEvent re = gson.fromJson(msg.payload, RobotEvent.class);
                        bus.send(re);
                    }
                    if (msg.type == NetworkMessage.Type.ROBOT_REMOVED) {
                        RobotRemovedEvent removed = gson.fromJson(msg.payload, RobotRemovedEvent.class);
                        bus.send(removed);
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        networkThread.setDaemon(true);
        networkThread.start();
    }

    public void sendRequest(NetworkMessage.Type type, String payload) {
        if (out != null) {
            out.println(gson.toJson(new NetworkMessage(type, payload)));
        }
    }

    public void stop() {
        try {
            if (out != null) out.close();
        } catch (Exception e) { e.printStackTrace(); }
    }
}