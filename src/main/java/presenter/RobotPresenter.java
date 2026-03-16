package presenter;

import gui.GameVisualizer;
import model.RobotEvent;
import model.RobotModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import static com.sun.java.accessibility.util.AWTEventMonitor.addMouseListener;

public class RobotPresenter {
    private final Timer m_timer = initTimer();
    private final RobotModel robot = new RobotModel();
    public final GameVisualizer gameVisualizer = new GameVisualizer();

    private static Timer initTimer()
    {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public RobotPresenter(){
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                gameVisualizer.onRedrawEvent();
            }
        }, 0, 50);
        m_timer.schedule(new TimerTask()
        {
            @Override
            public void run()
            {
                robot.onModelUpdateEvent();
            }
        }, 0, 10);
        addMouseListener(new MouseAdapter()
        {
            @Override
            public void mouseClicked(MouseEvent e)
            {
                robot.setTargetPosition(e.getPoint());
            }
        });

        Toolkit.getDefaultToolkit().addAWTEventListener(event -> {
            if (event instanceof RobotEvent) {
                RobotEvent re = (RobotEvent) event;
                gameVisualizer.setRobotPos(re.getX(), re.getY(),  re.getT_x(), re.getT_y(), re.getDir());
                gameVisualizer.repaint();
            }
        }, RobotEvent.ROBOT_MOVED);
        gameVisualizer.setDoubleBuffered(true);
    }
}
