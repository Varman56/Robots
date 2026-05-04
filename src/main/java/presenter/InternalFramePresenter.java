package presenter;

import backend.SaveManager;
import backend.WindowId;

import javax.swing.*;
import javax.swing.event.InternalFrameAdapter;
import javax.swing.event.InternalFrameEvent;

public class InternalFramePresenter<V extends JInternalFrame>{
    private final SaveManager saveManager;
    protected final V view;
    protected final WindowId windowId;

    public InternalFramePresenter(SaveManager saveManager, V view, WindowId windowId) {
        this.saveManager = saveManager;
        this.view = view;
        this.windowId = windowId;

        view.addInternalFrameListener(new InternalFrameAdapter() {
            @Override
            public void internalFrameClosing(InternalFrameEvent e) {
                saveManager.saveWindow(view, windowId);
            }
        });

        this.LoadWindwow();
    }

    public void LoadWindwow(){
        saveManager.loadWindow(this.view, this.windowId);
    }

    public V getView() { return view; }

    public WindowId GetWindowId() {
        return this.windowId;
    }
}
