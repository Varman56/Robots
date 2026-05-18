package gui.main;

import gui.log.Logger;
import network.NetworkController;
import presenter.GamePresenter;
import presenter.RobotStatePresenter;

import javax.swing.*;
import java.awt.event.KeyEvent;
import java.util.function.Consumer;

public class MainMenuBar extends JMenuBar {
    private final MainApplicationFrame frame;

    public MainMenuBar(MainApplicationFrame frame, NetworkController netController, GamePresenter gamePresenter, RobotStatePresenter rsp) {
        this.frame = frame;
        add(createLookAndFeelMenu());
        add(createTestMenu());
        add(createExitMenu());
        add(createNetworkMenu(netController, gamePresenter, rsp));
    }

    private JMenu createNetworkMenu(NetworkController netController, GamePresenter gamePresenter, RobotStatePresenter rsp) {
        JMenu menu = new JMenu("Сеть");

        addMenuItem(menu, "Хост (Старт сервера)", KeyEvent.VK_H, (e) -> {
            netController.startHost(1234);
            JOptionPane.showMessageDialog(frame, "Сервер запущен на порту 1234");
        });

        addMenuItem(menu, "Подключиться", KeyEvent.VK_C, (e) -> {
            String host = JOptionPane.showInputDialog(frame, "Введите IP адрес:", "127.0.0.1");
            if (host != null && !host.isEmpty()) {
                netController.connectTo(host, 1234, gamePresenter, rsp);
            }
        });

        return menu;
    }

    private JMenu createExitMenu() {
        JMenu menu = new JMenu("Приложение");
        menu.setMnemonic(KeyEvent.VK_A);

        addMenuItem(menu, "Выход", KeyEvent.VK_X, (e) -> frame.confirmExit());
        return menu;
    }

    private JMenu createLookAndFeelMenu() {
        JMenu menu = new JMenu("Режим отображения");
        menu.setMnemonic(KeyEvent.VK_V);

        addMenuItem(menu, "Системная схема", KeyEvent.VK_S,
                (e) -> frame.setLookAndFeel(UIManager.getSystemLookAndFeelClassName()));

        addMenuItem(menu, "Универсальная схема", KeyEvent.VK_U,
                (e) -> frame.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName()));

        addMenuItem(menu, "Nimbus", KeyEvent.VK_N,
                (e) -> frame.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel"));

        addMenuItem(menu, "Metal", KeyEvent.VK_M,
                (e) -> frame.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel"));
        return menu;
    }

    private JMenu createTestMenu() {
        JMenu menu = new JMenu("Тесты");
        menu.setMnemonic(KeyEvent.VK_T);

        addMenuItem(menu, "Сообщение в лог", KeyEvent.VK_L,
                (e) -> Logger.debug("Новая строка"));

        return menu;
    }

    private void addMenuItem(JMenu parent, String title, int mnemonic, Consumer<java.awt.event.ActionEvent> action) {
        JMenuItem item = new JMenuItem(title, mnemonic);
        item.addActionListener(action::accept);
        parent.add(item);
    }
}