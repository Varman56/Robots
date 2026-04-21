package presenter;

import backend.WindowId;
import events.RxEventBus;
import gui.game.GameVisualizer;
import gui.game.GameWindow;
import events.RobotEvent;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import model.RobotModel;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.Timer;
import java.util.TimerTask;

public class GamePresenter implements IJInternalFramePresenter {
    private final RobotModel robot;
    private final GameWindow view;
    private final GameVisualizer gameVisualizer = new GameVisualizer();
    private final Timer timer = new Timer("events generator", true);
    private final CompositeDisposable disposables = new CompositeDisposable();

    public GamePresenter(RxEventBus eventBus, RobotModel robotModel) {
        this.view = new GameWindow(this.gameVisualizer);
        this.robot = robotModel;

        initLogic();
    }

    public JInternalFrame GetWindow() {
        return this.view;
    }

    public WindowId GetWindowId() {
        return WindowId.GAME;
    }

    private void initLogic() {
        disposables.add(robot.getStateObservable()
                .subscribe(this::onRobotMoved));

        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                robot.onModelUpdateEvent();
            }
        }, 0, 10);
        timer.schedule(new TimerTask() {
            @Override
            public void run() {
                gameVisualizer.repaint();
            }
        }, 0, 100);

        gameVisualizer.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                robot.setTargetPosition(e.getPoint());
            }
        });

        gameVisualizer.setDoubleBuffered(true);
    }

    private void onRobotMoved(RobotEvent re) {
        gameVisualizer.setRobotPos(re.getX(), re.getY(), re.getT_x(), re.getT_y(), re.getDir());
        gameVisualizer.repaint();
    }
}
