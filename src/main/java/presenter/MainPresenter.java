package presenter;

import backend.SaveManager;
import backend.WindowId;
import events.EventBus;
import gui.main.MainApplicationFrame;
import events.app.AppExitEvent;
import io.reactivex.rxjava3.disposables.CompositeDisposable;

import javax.swing.*;

public class MainPresenter {
    private final SaveManager saveManager;
    private final EventBus eventBus;
    private final CompositeDisposable disposables = new CompositeDisposable();
    private final MainApplicationFrame mainFrame;

    public MainPresenter(EventBus eventBus, SaveManager saveManager, MainApplicationFrame mainFrame) {
        this.saveManager = saveManager;
        this.eventBus = eventBus;
        this.mainFrame = mainFrame;

        disposables.add(eventBus.listen(AppExitEvent.class)
                .subscribe(this::closeMain));

        SwingUtilities.invokeLater(() -> saveManager.loadWindow(mainFrame, WindowId.MAIN));
        mainFrame.pack();
        mainFrame.setVisible(true);
    }

    public void closeMain(AppExitEvent event) {
        saveManager.saveWindow(mainFrame, WindowId.MAIN);
        disposables.clear();
        mainFrame.dispose();
        // System.exit(0);
    }
}