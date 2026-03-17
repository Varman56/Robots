package presenter;

import gui.GameVisualizer;
import gui.CoordinatesDialog;
import model.RobotEvent;
import model.RobotModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;

public class RobotPresenter {
    private final Timer m_timer = initTimer();
    private final RobotModel robot = new RobotModel();
    public final GameVisualizer gameVisualizer = new GameVisualizer();
    private CoordinatesDialog coordsWindow;

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public RobotPresenter(JFrame owner) {
        this.coordsWindow = new CoordinatesDialog(owner);
        coordsWindow.setVisible(true);
        gameVisualizer.setRobotPos(robot.getRobotCenterX(), robot.getRobotCenterY(), robot.getTargetX(), robot.getTargetY(), robot.getDirection());
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameVisualizer.onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask() {
            @Override
            public void run() {
                robot.onModelUpdateEvent();
            }
        }, 0, 10);
        gameVisualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                robot.setTargetPosition(e.getPoint());
            }
        });
        EventQueue q = Toolkit.getDefaultToolkit().getSystemEventQueue();
        q.push(new EventQueue() {
            @Override
            protected void dispatchEvent(AWTEvent event) {
                if (event instanceof RobotEvent re) {
                    gameVisualizer.setRobotPos(re.getX(), re.getY(), re.getT_x(), re.getT_y(), re.getDir());
                    gameVisualizer.repaint();
                    coordsWindow.updateCoordinates(re.getX(), re.getY());
                }
                super.dispatchEvent(event);
            }
        });
        gameVisualizer.setDoubleBuffered(true);
    }
}
