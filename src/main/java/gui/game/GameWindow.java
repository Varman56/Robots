package gui.game;


import java.awt.BorderLayout;

import javax.swing.*;

import static javax.swing.WindowConstants.DISPOSE_ON_CLOSE;

public class GameWindow extends JInternalFrame
{
    private final GameVisualizer visualizer;
    public GameWindow(GameVisualizer g)
    {
        super("Игровое поле", true, true, true, true);
        setDefaultCloseOperation(DISPOSE_ON_CLOSE);
        this.visualizer = g;
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(g, BorderLayout.CENTER);
        getContentPane().add(panel);
        pack();
    }

    public  GameVisualizer getVisualizer()
    {
        return visualizer;
    }
}
