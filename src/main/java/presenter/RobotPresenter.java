package presenter;

import backend.SaveManager;
import gui.*;
import log.Logger;
import model.RobotEvent;
import model.RobotModel;

import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.awt.Toolkit;
import java.awt.Dimension;

public class RobotPresenter {
    private final Timer m_timer = initTimer();
    private final RobotModel robot = new RobotModel();
    public final GameVisualizer gameVisualizer = new GameVisualizer();

    private final SaveManager saveManager = new SaveManager("WindowsPosition.json");
    private MainApplicationFrame mainFrame;

    private static Timer initTimer() {
        Timer timer = new Timer("events generator", true);
        return timer;
    }

    public RobotPresenter(MainApplicationFrame owner) {
        this.mainFrame = owner;
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        LogWindow logWindow = createLogWindow();
        addWindow(owner, logWindow, "logger");

        GameWindow gameWindow = new GameWindow(this.gameVisualizer);
        tryToLoad(gameWindow, "gameWindow", 400, 400, 320, 10, false);
        addWindow(owner, gameWindow, "gameWindow");

        CoordinatesFrame coordsWindow = new CoordinatesFrame(owner);
        tryToLoad(coordsWindow, "coords", 150, 80, 650, 10, false);
        addWindow(owner, coordsWindow, "coords");

        SwingUtilities.invokeLater(() -> {
            tryToLoad(owner, "main", screenSize.width, screenSize.height, 0, 0, true);
        });


        gameVisualizer.setRobotPos(robot.getRobotCenterX(), robot.getRobotCenterY(), robot.getTargetX(), robot.getTargetY(), robot.getDirection());
//        m_timer.schedule(new TimerTask() {
//            @Override
//            public void run() {
//                gameVisualizer.onRedrawEvent();
//            }
//        }, 0, 50);
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

        for (String title : owner.internalFrames.keySet()) {
            SaveInternalFrameListener(owner.internalFrames.get(title), title);
        }
        owner.setPresenter(this);
    }

    public void closeMain() {
        saveManager.saveWindow(mainFrame, "main");
    }

    protected void addWindow(MainApplicationFrame owner, JInternalFrame frame, String title) {
        owner.internalFrames.put(title, frame);
        owner.addWindow(frame);
    }

    protected LogWindow createLogWindow() {
        LogWindow logWindow = new LogWindow(Logger.getDefaultLogSource());
        tryToLoad(logWindow, "logger", 300, 800, 10, 10, false);
        logWindow.setMinimumSize(new Dimension(300, 800));
        logWindow.pack();
        Logger.debug("Протокол работает");
        return logWindow;
    }

    protected void tryToLoad(Component window, String title, int defaultWidth, int defaultHeight, int locX, int locY, boolean defaultMaximized) {
        if (!saveManager.loadWindow(window, title)) {
            if (defaultMaximized && window instanceof Frame) {
                Frame frame = (Frame) window;
                SwingUtilities.invokeLater(() -> {
                    frame.setExtendedState(Frame.MAXIMIZED_BOTH);
                });
            } else {
                window.setLocation(locX, locY);
                window.setSize(defaultWidth, defaultHeight);
            }
        }
    }

    protected void SaveInternalFrameListener(JInternalFrame frame, String title) {
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                saveManager.saveWindow(frame, title);
            }
        });
    }


}