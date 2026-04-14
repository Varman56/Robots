package presenter;

import backend.WindowId;

import javax.swing.*;

public interface IJInternalFramePresenter {
    public JInternalFrame GetWindow();
    public WindowId GetWindowId();
}
