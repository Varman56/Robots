package gui.game;

import events.robots.RobotEvent;
import gui.Visualizer;

import java.awt.Color;
import java.awt.EventQueue;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.geom.AffineTransform;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.swing.JPanel;

public class GameVisualizer extends JPanel implements Visualizer {
    private final Map<Integer, RobotEvent> robotStates = new ConcurrentHashMap<>();

    public void setRobotPos(RobotEvent re) {
        robotStates.put(re.getId(), re);
        redraw();
    }

    public void removeRobotState(int robotId) {
        robotStates.remove(robotId);
        redraw();
    }

    public void redraw() {
        EventQueue.invokeLater(this::repaint);
    }

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        if (robotStates.isEmpty()) {
            return;
        }

        Graphics2D g2d = (Graphics2D) g;
        AffineTransform base = g2d.getTransform();

        for (RobotEvent state : robotStates.values()) {
            g2d.setTransform(base);
            Color target = state.getId() == 0 ? Color.RED : Color.GREEN;
            drawTarget(g2d, state.getT_x(), state.getT_y(),  target);
        }

        for (RobotEvent state : robotStates.values()) {
            g2d.setTransform(base);
            Color body = state.getId() == 0 ? Color.MAGENTA : Color.ORANGE;
            drawRobot(g2d, state.getX(), state.getY(), state.getDir(), body);
        }
    }

    private static void fillOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.fillOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawOval(Graphics g, int centerX, int centerY, int diam1, int diam2) {
        g.drawOval(centerX - diam1 / 2, centerY - diam2 / 2, diam1, diam2);
    }

    private static void drawRobot(Graphics2D g, int robotCenterX, int robotCenterY, double direction, Color body) {
        AffineTransform t = AffineTransform.getRotateInstance(direction, robotCenterX, robotCenterY);
        g.setTransform(t);
        g.setColor(body);
        fillOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX, robotCenterY, 30, 10);
        g.setColor(Color.WHITE);
        fillOval(g, robotCenterX + 10, robotCenterY, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, robotCenterX + 10, robotCenterY, 5, 5);
    }

    private static void drawTarget(Graphics2D g, int x, int y, Color targetColor) {
        g.setTransform(new AffineTransform());
        g.setColor(targetColor);
        fillOval(g, x, y, 5, 5);
        g.setColor(Color.BLACK);
        drawOval(g, x, y, 5, 5);
    }
}
