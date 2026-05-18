package network;

import com.google.gson.Gson;
import events.robots.RobotEvent;
import model.RobotFleet;
import java.io.*;
import java.net.*;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

public class GameServer {
    private final int port;
    private final RobotFleet fleet;
    private final Gson gson = new Gson();
    private final CopyOnWriteArrayList<ClientHandler> clients = new CopyOnWriteArrayList<>();

    private ServerSocket serverSocket;
    private Thread serverThread;

    public GameServer(int port, RobotFleet fleet) {
        this.port = port;
        this.fleet = fleet;
    }

    public void start() {
        serverThread = new Thread(() -> {
            try {
                serverSocket = new ServerSocket(port);
                System.out.println("Сервер запущен на порту: " + port);

                while (!Thread.currentThread().isInterrupted()) {
                    try {
                        Socket socket = serverSocket.accept();
                        ClientHandler handler = new ClientHandler(socket);
                        clients.add(handler);
                        new Thread(handler).start();
                    } catch (SocketException e) {
                        if (!serverSocket.isClosed()) e.printStackTrace();
                        break;
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
        serverThread.setDaemon(true);
        serverThread.start();
    }

    public void broadcast( NetworkMessage msg) {
        String packet = gson.toJson(msg) + "\n";

        for (ClientHandler client : clients) {
            client.send(packet);
        }
    }

    private class ClientHandler implements Runnable {
        private final Socket socket;
        private PrintWriter out;

        public ClientHandler(Socket socket) { this.socket = socket; }

        public void send(String data) {
            if (out != null) {
                out.print(data);
                out.flush();
            }
        }

        @Override
        public void run() {
            try (BufferedReader in = new BufferedReader(new InputStreamReader(socket.getInputStream()))) {
                out = new PrintWriter(new OutputStreamWriter(socket.getOutputStream(), "UTF-8"), true);

                List<RobotEvent> currentStates = fleet.getRobots().stream()
                        .map(r -> new RobotEvent(r.getRobotCenterX(), r.getRobotCenterY(),
                                r.getDirection(), r.getTargetX(), r.getTargetY(), r.getId()))
                        .toList();

                String initPayload = gson.toJson(currentStates);
                out.println(gson.toJson(new NetworkMessage(NetworkMessage.Type.SYNC_INIT, initPayload)));

                String line;
                while ((line = in.readLine()) != null) {
                    try {
                        NetworkMessage msg = gson.fromJson(line, NetworkMessage.class);
                        if (msg == null) continue;

                        switch (msg.type) {
                            case MOVE_REQUEST -> {
                                java.awt.Point p = gson.fromJson(msg.payload, java.awt.Point.class);
                                fleet.getRobots().forEach(r -> r.applyLocalPointerTarget(p));
                            }
                            case REMOVE_ROBOT_REQUEST -> {
                                int idToRemove = Integer.parseInt(msg.payload);
                                fleet.removeRobot(idToRemove);
                            }
                            case ADD_ROBOT_REQUEST -> {
                                fleet.addBot();
                            }
                        }
                    } catch (Exception ex) {
                        System.err.println("Ошибка обработки сообщения от клиента: " + ex.getMessage());
                    }
                }
            } catch (IOException e) {
            } finally {
                cleanup();
            }
        }

        private void cleanup() {
            clients.remove(this);
            try {
                socket.close();
            } catch (IOException e) {
            }
        }
    }

    public void stop() {
        try {
            if (serverThread != null) {
                serverThread.interrupt();
            }
            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
            }
            for (ClientHandler c : clients) {
                c.socket.close();
            }
            clients.clear();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}