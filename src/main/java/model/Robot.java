package model;

import events.robots.RobotEvent;
import events.robots.RobotEventBus;

import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;


public abstract class Robot {
    private final int id;

    private volatile double m_robotPositionX = -10;
    private volatile double m_robotPositionY = -10;
    private volatile double m_robotDirection = 0;

    private volatile int m_targetPositionX = -10;
    private volatile int m_targetPositionY = -10;

    private volatile int boundMinX;
    private volatile int boundMinY;
    private volatile int boundMaxX;
    private volatile int boundMaxY;
    private volatile boolean layoutApplied;

    private static final double maxVelocity = 0.1;
    private static final double maxAngularVelocity = 0.0025;

    private static final double TARGET_REACHED_DISTANCE = 2.5;
    private static final double TERMINAL_APPROACH_PX = 15.0;
    private static final double CRUISE_ANGULAR_GAIN = maxAngularVelocity / (Math.PI / 2);
    private static final double MIN_FORWARD_FRACTION = 0.22;
    private static final double TERMINAL_ALIGN_THRESHOLD_RAD = Math.toRadians(48);
    private static final double TERMINAL_FINE_TURN_GAIN = maxAngularVelocity / Math.toRadians(26);

    private final RobotEventBus bus;

    protected Robot(RobotEventBus bus, int id) {
        this.bus = bus;
        this.id = id;
    }

    protected final int playfieldMidX() {
        return (boundMinX + boundMaxX) / 2;
    }

    protected final int playfieldMidY() {
        return (boundMinY + boundMaxY) / 2;
    }

    public final int getId() {
        return id;
    }

    public void onPlayfieldBoundsChanged(int minX, int minY, int maxX, int maxY) {
        if (maxX <= minX || maxY <= minY) {
            return;
        }
        boundMinX = minX;
        boundMinY = minY;
        boundMaxX = maxX;
        boundMaxY = maxY;
        if (!layoutApplied) {
            layoutApplied = true;
            bootstrapWithinBounds(ThreadLocalRandom.current());
        }
    }

    private void bootstrapWithinBounds(ThreadLocalRandom rnd) {
        m_robotPositionX = randomBoundedDouble(rnd, boundMinX, boundMaxX);
        m_robotPositionY = randomBoundedDouble(rnd, boundMinY, boundMaxY);
        m_robotDirection = rnd.nextDouble() * Math.PI * 2;
        bootstrapTargets(rnd);
        emitState();
    }

    protected abstract void bootstrapTargets(ThreadLocalRandom rnd);

    protected final void pickRandomTarget(ThreadLocalRandom rnd) {
        m_targetPositionX = boundMinX + rnd.nextInt(boundMaxX - boundMinX + 1);
        m_targetPositionY = boundMinY + rnd.nextInt(boundMaxY - boundMinY + 1);
    }

    protected final void setTargetPixels(int x, int y) {
        m_targetPositionX = x;
        m_targetPositionY = y;
    }

    private static double randomBoundedDouble(ThreadLocalRandom rnd, int min, int max) {
        return min + rnd.nextDouble() * (max - min);
    }

    private static double distance(double x1, double y1, double x2, double y2) {
        double diffX = x1 - x2;
        double diffY = y1 - y2;
        return Math.sqrt(diffX * diffX + diffY * diffY);
    }

    private static double angleTo(double fromX, double fromY, double toX, double toY) {
        double diffX = toX - fromX;
        double diffY = toY - fromY;
        return asNormalizedRadians(Math.atan2(diffY, diffX));
    }

    public void onModelUpdateEvent() {
        if (!layoutApplied) {
            return;
        }
        double distance = distance(m_targetPositionX, m_targetPositionY,
                m_robotPositionX, m_robotPositionY);
        if (distance <= TARGET_REACHED_DISTANCE) {
            onArrivedAtTarget();
            return;
        }

        double angleToTarget = angleTo(m_robotPositionX, m_robotPositionY, m_targetPositionX, m_targetPositionY);
        double diff = Math.atan2(Math.sin(angleToTarget - m_robotDirection), Math.cos(angleToTarget - m_robotDirection));

        final double angularVelocity;
        final double velocity;
        if (distance < TERMINAL_APPROACH_PX) {
            if (Math.abs(diff) > TERMINAL_ALIGN_THRESHOLD_RAD) {
                angularVelocity = Math.copySign(maxAngularVelocity, diff);
                velocity = maxVelocity * 0.07;
            } else {
                angularVelocity = applyLimits(diff * TERMINAL_FINE_TURN_GAIN, -maxAngularVelocity, maxAngularVelocity);
                double v = maxVelocity;
                if (distance < 7.0) {
                    v *= Math.max(0.4, distance / 7.0);
                }
                velocity = v;
            }
        } else {
            angularVelocity = applyLimits(diff * CRUISE_ANGULAR_GAIN, -maxAngularVelocity, maxAngularVelocity);
            double alignment = Math.abs(Math.cos(diff));
            velocity = maxVelocity * (MIN_FORWARD_FRACTION + (1.0 - MIN_FORWARD_FRACTION) * alignment);
        }

        moveRobot(velocity, angularVelocity, 10);
    }

    protected abstract void onArrivedAtTarget();

    protected final void emitState() {
        RobotEvent event = new RobotEvent(getRobotCenterX(), getRobotCenterY(), getDirection(), getTargetX(), getTargetY(), id);
        bus.send(event);
    }

    private void moveRobot(double velocity, double angularVelocity, double duration) {
        velocity = applyLimits(velocity, 0, maxVelocity);
        angularVelocity = applyLimits(angularVelocity, -maxAngularVelocity, maxAngularVelocity);
        double newX = m_robotPositionX + velocity / angularVelocity *
                (Math.sin(m_robotDirection + angularVelocity * duration) -
                        Math.sin(m_robotDirection));
        if (!Double.isFinite(newX)) {
            newX = m_robotPositionX + velocity * duration * Math.cos(m_robotDirection);
        }
        double newY = m_robotPositionY - velocity / angularVelocity *
                (Math.cos(m_robotDirection + angularVelocity * duration) -
                        Math.cos(m_robotDirection));
        if (!Double.isFinite(newY)) {
            newY = m_robotPositionY + velocity * duration * Math.sin(m_robotDirection);
        }
        m_robotPositionX = newX;
        m_robotPositionY = newY;
        double newDirection = asNormalizedRadians(m_robotDirection + angularVelocity * duration);
        m_robotDirection = newDirection;
        emitState();
    }

    private static double asNormalizedRadians(double angle) {
        while (angle < 0) {
            angle += 2 * Math.PI;
        }
        while (angle >= 2 * Math.PI) {
            angle -= 2 * Math.PI;
        }
        return angle;
    }

    private static int round(double value) {
        return (int) (value + 0.5);
    }

    private static double applyLimits(double value, double min, double max) {
        if (value < min) {
            return min;
        }
        if (value > max) {
            return max;
        }
        return value;
    }

    public int getRobotCenterX() {
        return round(this.m_robotPositionX);
    }

    public int getRobotCenterY() {
        return round(this.m_robotPositionY);
    }

    public int getTargetX() {
        return this.m_targetPositionX;
    }

    public int getTargetY() {
        return this.m_targetPositionY;
    }

    public double getDirection() {
        return this.m_robotDirection;
    }

    public void applyLocalPointerTarget(Point p) {
    }
}
