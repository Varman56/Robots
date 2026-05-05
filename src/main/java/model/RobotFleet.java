package model;

import events.robots.RobotEventBus;

import java.util.Collection;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Consumer;


public final class RobotFleet {

    private static final int SIMULATION_PERIOD_MS = 10;
    private static final int FIELD_MARGIN_PX = 15;

    private final RobotEventBus bus;
    private final CopyOnWriteArrayList<Robot> robots = new CopyOnWriteArrayList<>();
    private final ConcurrentHashMap<Integer, Thread> simThreads = new ConcurrentHashMap<>();

    private final AtomicInteger nextId;
    private final AtomicBoolean simulationStarted = new AtomicBoolean(false);

    private volatile int boundMinX;
    private volatile int boundMinY;
    private volatile int boundMaxX;
    private volatile int boundMaxY;
    private volatile boolean boundsValid;

    private Consumer<Integer> onRobotRemoved = id -> {
    };

    public RobotFleet(RobotEventBus bus, List<Robot> initialRobots) {
        this.bus = bus;
        this.robots.addAll(initialRobots);
        int max = initialRobots.stream().mapToInt(Robot::getId).max().orElse(-1);
        this.nextId = new AtomicInteger(max + 1);
    }

    public void setOnRobotRemoved(Consumer<Integer> onRobotRemoved) {
        this.onRobotRemoved = onRobotRemoved != null ? onRobotRemoved : id -> {
        };
    }

    public Collection<Robot> getRobots() {
        return robots;
    }

    public int addBot() {
        int id = nextId.getAndIncrement();
        BotRobot bot = new BotRobot(bus, id);
        robots.add(bot);
        if (boundsValid) {
            bot.onPlayfieldBoundsChanged(boundMinX, boundMinY, boundMaxX, boundMaxY);
        }
        if (simulationStarted.get()) {
            startSimThread(bot);
        }
        return id;
    }

    public boolean removeRobot(int id) {
        if (id == 0) {
            return false;
        }
        Robot found = null;
        for (Robot r : robots) {
            if (r.getId() == id) {
                found = r;
                break;
            }
        }
        if (found == null) {
            return false;
        }
        robots.remove(found);
        Thread t = simThreads.remove(id);
        if (t != null) {
            t.interrupt();
        }
        onRobotRemoved.accept(id);
        return true;
    }

    public void applyViewportSize(int width, int height) {
        int innerMaxX = width - FIELD_MARGIN_PX;
        int innerMaxY = height - FIELD_MARGIN_PX;
        if (innerMaxX <= FIELD_MARGIN_PX || innerMaxY <= FIELD_MARGIN_PX) {
            return;
        }
        if (robots.isEmpty()) {
            return;
        }
        boundMinX = FIELD_MARGIN_PX;
        boundMinY = FIELD_MARGIN_PX;
        boundMaxX = innerMaxX;
        boundMaxY = innerMaxY;
        boundsValid = true;

        for (Robot robot : robots) {
            robot.onPlayfieldBoundsChanged(boundMinX, boundMinY, boundMaxX, boundMaxY);
        }
        if (simulationStarted.compareAndSet(false, true)) {
            for (Robot robot : robots) {
                startSimThread(robot);
            }
        }
    }

    private void startSimThread(Robot robot) {
        int id = robot.getId();
        simThreads.computeIfAbsent(id, k -> Thread.ofVirtual()
                .name("robot-sim-" + id)
                .start(() -> runSimulationLoop(robot)));
    }

    private void runSimulationLoop(Robot robot) {
        while (!Thread.currentThread().isInterrupted()) {
            robot.onModelUpdateEvent();
            try {
                Thread.sleep(SIMULATION_PERIOD_MS);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
    }

    public void shutdownSimulation() {
        for (Thread t : simThreads.values()) {
            t.interrupt();
        }
        simThreads.clear();
        simulationStarted.set(false);
    }
}
