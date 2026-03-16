package gui;

import presenter.RobotPresenter;

import java.awt.BorderLayout;

import javax.swing.JInternalFrame;
import javax.swing.JPanel;

public class GameWindow extends JInternalFrame
{
    private RobotPresenter robotPresenter;

    public GameWindow() 
    {
        super("Игровое поле", true, true, true, true);
        robotPresenter = new RobotPresenter();
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(robotPresenter.gameVisualizer, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
