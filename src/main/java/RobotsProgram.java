import backend.SaveManager;
import events.app.AppEventBus;
import events.robots.RobotEventBus;
import gui.main.MainApplicationFrame;
import model.RobotModel;
import presenter.*;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;
import java.util.ArrayList;
import java.util.List;

public class RobotsProgram {
    private static final int ROBOT_COUNT = 100;

    private static MainPresenter presenter;

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception e) {
            e.printStackTrace();
        }
        RobotEventBus rEventBus = new RobotEventBus();
        List<RobotModel> robots = new ArrayList<>(ROBOT_COUNT);
        for (int i = 0; i < ROBOT_COUNT; i++) {
            robots.add(new RobotModel(rEventBus, i));
        }
        SaveManager saveManager = new SaveManager();
        AppEventBus appEventBus = new AppEventBus();

        List<InternalFramePresenter> presenters = List.of(
                new LogPresenter(saveManager),
                new GamePresenter(saveManager, rEventBus, robots),
                new CoordsPresenter(saveManager, rEventBus)
        );

        MainApplicationFrame mainFrame = new MainApplicationFrame(appEventBus);
        for (InternalFramePresenter presenter: presenters) {
            mainFrame.addWindow(presenter.getView());
        }
        SwingUtilities.invokeLater(() -> {
            presenter = new MainPresenter(appEventBus, saveManager, mainFrame);
        });
    }
}
