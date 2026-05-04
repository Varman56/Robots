package events.robots;

public class RobotEvent {
    private final int x, y, t_x, t_y;
    private final double dir;
    private final int id;

    public RobotEvent(int x, int y, double dir, int t_x, int t_y, int id) {
        this.x = x;
        this.y = y;
        this.dir = dir;
        this.t_x = t_x;
        this.t_y = t_y;
        this.id = id;
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

    public int getId() {
        return id;
    }
}
