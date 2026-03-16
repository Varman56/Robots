package model;

import java.awt.*;

public class RobotEvent extends AWTEvent {
    public static final int ROBOT_MOVED = AWTEvent.RESERVED_ID_MAX + 1;

    private final int x, y, t_x, t_y;
    private final double dir;

    public RobotEvent(Object source, int x, int y, double dir, int t_x, int t_y) {
        super(source, ROBOT_MOVED);
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.t_x = t_x;
        this.t_y = t_y;
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public double getDir() {
        return dir;
    }

    public int getT_y() {
        return t_y;
    }

    public int getT_x() {
        return t_x;
    }
}
