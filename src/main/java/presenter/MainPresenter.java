package presenter;

import events.EventDispatcher;
import backend.SaveManager;
import backend.WindowId;
import gui.main.MainApplicationFrame;
import events.AppExitEvent;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class MainPresenter {
    private final SaveManager saveManager = new SaveManager();
    private final List<IJInternalFramePresenter> internalPresenters = new ArrayList<>();

    private final EventDispatcher eventDispatcher = EventDispatcher.getInstance();
    private final MainApplicationFrame mainFrame = new MainApplicationFrame(eventDispatcher);

    public MainPresenter() {

        registerPresenter(new LogPresenter(eventDispatcher));
        registerPresenter(new GamePresenter(eventDispatcher));
        registerPresenter(new CoordsPresenter(eventDispatcher));

        SwingUtilities.invokeLater(() -> tryToLoad(mainFrame, WindowId.MAIN));
        mainFrame.pack();
        mainFrame.setVisible(true);

        eventDispatcher.registerHandler(AppExitEvent.class, this::closeMain);
    }

    protected void registerPresenter(IJInternalFramePresenter p) {
        internalPresenters.add(p);
        registerWindow(p.GetWindow(), p.GetWindowId());
    }

    protected void registerWindow(JInternalFrame frame, WindowId id) {
        tryToLoad(frame, id);
        mainFrame.addWindow(frame);
        saveInternalFrameListener(frame, id);
    }

    protected void tryToLoad(Component window, WindowId id) {
        saveManager.loadWindow(window, id);
    }

    protected void saveInternalFrameListener(JInternalFrame frame, WindowId id) {
        frame.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                saveManager.saveWindow(frame, id);
            }
        });
    }

    public void closeMain(AppExitEvent event) {
        for (IJInternalFramePresenter frames : internalPresenters) {
            frames.GetWindow().doDefaultCloseAction();
        }
        saveManager.saveWindow(mainFrame, WindowId.MAIN);
        mainFrame.dispose();
    }
}