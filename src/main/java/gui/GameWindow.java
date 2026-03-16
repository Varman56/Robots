package gui;

import presenter.RobotPresenter;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame
{
    private final RobotPresenter robotPresenter = new RobotPresenter();

    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(robotPresenter.gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
