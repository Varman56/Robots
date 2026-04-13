package backend;

import javax.swing.*;
import gui.log.Logger;

public class SwingLocalization {
    public static void initialize() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
            initRussianResources();
            Logger.debug("Локализация: RU");
        } catch (Exception e) {
            Logger.debug("Ошибка локализации: RU");
            e.printStackTrace();
        }
    }

    private static void initRussianResources() {
        UIManager.put("OptionPane.yesButtonText", "Да");
        UIManager.put("OptionPane.noButtonText", "Нет");
        UIManager.put("OptionPane.cancelButtonText", "Отмена");
        UIManager.put("OptionPane.okButtonText", "OK");

        UIManager.put("FileChooser.openButtonText", "Открыть");
        UIManager.put("FileChooser.cancelButtonText", "Отмена");
        UIManager.put("FileChooser.lookInLabelText", "Папка");
        UIManager.put("FileChooser.fileNameLabelText", "Имя файла");
        UIManager.put("FileChooser.filesOfTypeLabelText", "Тип файлов");
        UIManager.put("FileChooser.acceptAllFileFilterText", "Все файлы");

        UIManager.put("ColorChooser.okText", "OK");
        UIManager.put("ColorChooser.cancelText", "Отмена");
        UIManager.put("ColorChooser.resetText", "Сброс");

        UIManager.put("InternalFrame.closeButtonToolTipText", "Закрыть");
        UIManager.put("InternalFrame.iconButtonToolTipText", "Свернуть");
        UIManager.put("InternalFrame.maxButtonToolTipText", "Развернуть");
    }
}