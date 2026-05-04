package model;

import events.robots.RobotEventBus;

import java.util.concurrent.ThreadLocalRandom;

public final class BotRobot extends Robot {

    public BotRobot(RobotEventBus bus, int id) {
        super(bus, id);
    }

    @Override
    protected void bootstrapTargets(ThreadLocalRandom rnd) {
        pickRandomTarget(rnd);
    }

    @Override
    protected void onArrivedAtTarget() {
        pickRandomTarget(ThreadLocalRandom.current());
        emitState();
    }
}
