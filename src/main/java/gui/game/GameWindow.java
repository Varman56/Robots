package gui.game;


import java.awt.BorderLayout;

import javax.swing.*;

public class GameWindow extends JInternalFrame
{
    public GameWindow(GameVisualizer g)
    {
        super("Игровое поле", true, true, true, true);
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(g, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }
}
