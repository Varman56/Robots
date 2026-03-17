package gui;

import presenter.RobotPresenter;

import java.awt.BorderLayout;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    private RobotPresenter robotPresenter;

    public GameWindow(JFrame owner)
    {
        robotPresenter = new RobotPresenter(owner);
        super("Игровое поле", true, true, true, true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(robotPresenter.gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
