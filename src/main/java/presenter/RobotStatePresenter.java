package presenter;

import backend.SaveManager;
import backend.WindowId;
import events.robots.ClearRobotsEvent;
import events.robots.RobotEvent;
import events.robots.RobotEventBus;
import events.robots.RobotRemovedEvent;
import gui.robotstate.RobotStateFrame;
import io.reactivex.rxjava3.disposables.CompositeDisposable;
import model.RobotFleet;
import network.GameClient;
import network.NetworkMessage;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;


public class RobotStatePresenter extends InternalFramePresenter<RobotStateFrame> {

    private static final int UI_REFRESH_MS = 80;

    private final RobotFleet fleet;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final ConcurrentHashMap<Integer, RobotEvent> latestById = new ConcurrentHashMap<>();

    private final Timer uiTimer;
    private GameClient networkClient;

    public RobotStatePresenter(SaveManager saveManager, RobotEventBus robotBus, RobotFleet fleet) {
        super(saveManager, new RobotStateFrame(), WindowId.ROBOT_STATES);
        this.fleet = fleet;

        disposables.add(robotBus.listen(RobotEvent.class)
                .subscribe(event -> latestById.put(event.getId(), event)));

        disposables.add(robotBus.listen(RobotRemovedEvent.class)
                .subscribe(event -> {
                    latestById.remove(event.id());

                    SwingUtilities.invokeLater(this::flushTable);
                }));

        disposables.add(robotBus.listen(ClearRobotsEvent.class).subscribe(e -> {
            latestById.clear();
        }));

        uiTimer = new Timer(UI_REFRESH_MS, e -> flushTable());
        uiTimer.setRepeats(true);
        uiTimer.start();

        getView().wireActions(
                this::onAddBot,
                this::onRemoveSelected
        );

        view.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosed(InternalFrameEvent e) {
                stop();
            }
        });
    }

    private void onAddBot() {
        if (networkClient != null) {
            networkClient.sendRequest(NetworkMessage.Type.ADD_ROBOT_REQUEST, "");
        } else {
            fleet.addBot();
        }
    }

    private void onRemoveSelected() {
        int id = getView().getSelectedRobotId();
        if (id <= 0) return;

        if (networkClient != null) {
            networkClient.sendRequest(NetworkMessage.Type.REMOVE_ROBOT_REQUEST, String.valueOf(id));
        } else {
            fleet.removeRobot(id);
        }
    }

    private void flushTable() {
        List<Integer> ids = new ArrayList<>(latestById.keySet());
        Collections.sort(ids);
        int[][] rows = new int[ids.size()][5];
        for (int i = 0; i < ids.size(); i++) {
            RobotEvent re = latestById.get(ids.get(i));
            rows[i][0] = re.getId();
            rows[i][1] = re.getX();
            rows[i][2] = re.getY();
            rows[i][3] = re.getT_x();
            rows[i][4] = re.getT_y();
        }
        getView().syncRows(rows);
    }

    private void stop() {
        uiTimer.stop();
        disposables.clear();
    }

    public void setNetworkClient(GameClient client) {
        this.networkClient = client;
        latestById.clear();
        flushTable();
    }


}
