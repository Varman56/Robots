package model;

import events.robots.RobotEventBus;

import java.awt.Point;
import java.util.concurrent.ThreadLocalRandom;


public final class PlayerRobot extends Robot {

    public PlayerRobot(RobotEventBus bus, int id) {
        super(bus, id);
    }

    @Override
    protected void bootstrapTargets(ThreadLocalRandom rnd) {
        setTargetPixels(playfieldMidX(), playfieldMidY());
    }

    @Override
    protected void onArrivedAtTarget() {
    }

    @Override
    public void applyLocalPointerTarget(Point p) {
        setTargetPixels(p.x, p.y);
        emitState();
    }
}
