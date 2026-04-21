package presenter;

import backend.WindowId;
import gui.log.LogWindow;
import gui.log.Logger;

import java.awt.*;

public class LogPresenter implements IJInternalFramePresenter {
    private final LogWindow view;

    public LogPresenter() {
        this.view = new LogWindow(Logger.getDefaultLogSource());
        view.setMinimumSize(new Dimension(300, 800));
        Logger.debug("Протокол работает");
    }

    public LogWindow GetWindow() {
        return this.view;
    }

    public WindowId GetWindowId() {
        return WindowId.LOGGER;
    }

}
