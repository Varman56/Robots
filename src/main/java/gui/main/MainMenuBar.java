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

    public MainMenuBar(MainApplicationFrame frame, NetworkController netController) {
        this.frame = frame;
        add(createLookAndFeelMenu());
        add(createTestMenu());
        add(createExitMenu());
        add(createNetworkMenu(netController));
    }

    private JMenu createNetworkMenu(NetworkController netController) {
        JMenu menu = new JMenu("Сеть");

        addMenuItem(menu, "Старт Сервера (Хост)", KeyEvent.VK_H, (e) -> {
            String input = JOptionPane.showInputDialog(frame, "Введите порт:", "1234");
            if (input != null) {
                try {
                    int port = Integer.parseInt(input);
                    netController.startHost(port);
                } catch (NumberFormatException ex) {
                    JOptionPane.showMessageDialog(frame, "Неверный формат порта");
                }
            }
        });

        addMenuItem(menu, "Подключиться к Серверу", KeyEvent.VK_C, (e) -> {
            String host = JOptionPane.showInputDialog(frame, "IP:", "127.0.0.1");
            String portStr = JOptionPane.showInputDialog(frame, "Порт:", "1234");
            if (host != null && portStr != null) {
                netController.connectTo(host, Integer.parseInt(portStr));
            }
        });

        menu.addSeparator();

        addMenuItem(menu, "Вернуться в локальный режим", KeyEvent.VK_D, (e) -> {
            netController.stopAll();
            JOptionPane.showMessageDialog(frame, "Работа сети остановлена. Локальный режим включен.");
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