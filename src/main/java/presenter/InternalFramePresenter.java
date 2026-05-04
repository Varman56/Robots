package presenter;

import backend.SaveManager;
import backend.WindowId;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class IntrenalFramePresenter {
    private final SaveManager saveManager;
    private final JInternalFrame view;
    private final WindowId windowId;

    public IntrenalFramePresenter(SaveManager saveManager, JInternalFrame view, WindowId  windowId) {
        this.saveManager = saveManager;
        this.view = view;
        this.windowId = windowId;

        view.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                saveManager.saveWindow(view, windowId);
            }
        });
    }

    public JInternalFrame GetWindow() {
        return this.view;
    }

    public WindowId GetWindowId() {
        return WindowId.GAME;
    }


}
