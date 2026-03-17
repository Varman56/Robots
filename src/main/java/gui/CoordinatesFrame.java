package gui;

import javax.swing.*;
import java.awt.*;

public class CoordinatesFrame extends JInternalFrame {
    private final JLabel xLabel = new JLabel("X: 0");
    private final JLabel yLabel = new JLabel("Y: 0");

    public CoordinatesFrame(JFrame owner) {
        super("Координаты робота", true, true, true, true);
        setLayout(new GridLayout(2, 2, 5, 5));
        setLayout(new FlowLayout());
        add(xLabel);
        add(yLabel);
        setSize(150, 80);
        setDefaultCloseOperation(HIDE_ON_CLOSE);
    }

    public void updateCoordinates(int x, int y) {
        EventQueue.invokeLater(() -> {
            xLabel.setText("X: " + x);
            yLabel.setText("Y: " + y);
        });
    }
}
