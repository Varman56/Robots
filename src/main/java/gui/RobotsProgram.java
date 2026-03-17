package gui;

import presenter.RobotPresenter;

import javax.swing.SwingUtilities;
import javax.swing.UIManager;

public class RobotsProgram
{
    private static RobotPresenter presenter;

    public static void main(String[] args) {
      try {
        UIManager.setLookAndFeel("javax.swing.plaf.nimbus.NimbusLookAndFeel");
//        UIManager.setLookAndFeel("javax.swing.plaf.metal.MetalLookAndFeel");
//        UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
//        UIManager.setLookAndFeel(UIManager.getCrossPlatformLookAndFeelClassName());
      } catch (Exception e) {
        e.printStackTrace();
      }
      SwingUtilities.invokeLater(() -> {
        MainApplicationFrame frame = new MainApplicationFrame();
        presenter = new RobotPresenter(frame);
        frame.pack();
        frame.setVisible(true);
      });
    }}
