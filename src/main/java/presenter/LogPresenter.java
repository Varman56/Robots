package presenter;

import backend.SaveManager;
import backend.WindowId;
import gui.log.LogWindow;
import gui.log.Logger;

import java.awt.*;

public class LogPresenter extends InternalFramePresenter {

    public LogPresenter(SaveManager saveManager) {
        super(saveManager, new LogWindow(Logger.getDefaultLogSource()), WindowId.LOGGER);
        this.getView().setMinimumSize(new Dimension(300, 800));
        Logger.debug("Протокол работает");
    }
}
