import backend.SaveManager;
import events.app.AppEventBus;
import events.robots.RobotEventBus;
import gui.main.MainApplicationFrame;
import model.BotRobot;
import model.PlayerRobot;
import model.Robot;
import model.RobotFleet;
import presenter.*;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.List;

public class RobotsProgram {
    private static final int ROBOT_COUNT = 10;

    private static MainPresenter presenter;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RobotEventBus rEventBus = new RobotEventBus();
        List<Robot> initialRobots = new ArrayList<>(ROBOT_COUNT);
        initialRobots.add(new PlayerRobot(rEventBus, 0));
        for (int i = 1; i < ROBOT_COUNT; i++) {
            initialRobots.add(new BotRobot(rEventBus, i));
        }
        RobotFleet fleet = new RobotFleet(rEventBus, initialRobots);

        SaveManager saveManager = new SaveManager();
        AppEventBus appEventBus = new AppEventBus();

        List<InternalFramePresenter> presenters = List.of(
                new LogPresenter(saveManager),
                new GamePresenter(saveManager, rEventBus, fleet),
                new CoordsPresenter(saveManager, rEventBus),
                new RobotStatePresenter(saveManager, rEventBus, fleet)
        );

        MainApplicationFrame mainFrame = new MainApplicationFrame(appEventBus);
        for (InternalFramePresenter presenter : presenters) {
            mainFrame.addWindow(presenter.getView());
        }
        SwingUtilities.invokeLater(() -> {
            presenter = new MainPresenter(appEventBus, saveManager, mainFrame);
        });
    }
}
