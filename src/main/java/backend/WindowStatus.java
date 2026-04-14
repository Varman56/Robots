package backend;

import javax.swing.*;
import java.awt.*;
import java.beans.PropertyVetoException;

public class WindowStatus {
    private int x, y, width, height;
    private boolean isMaxed;

    public WindowStatus() {
    }

    public WindowStatus(Component window) {
        this.x = window.getX();
        this.y = window.getY();
        this.width = window.getWidth();
        this.height = window.getHeight();

        if (window instanceof Frame frame) {
            this.isMaxed = (frame.getExtendedState() & Frame.MAXIMIZED_BOTH) == Frame.MAXIMIZED_BOTH;
        } else if (window instanceof JInternalFrame internalFrame) {
            this.isMaxed = internalFrame.isMaximum();
        }
    }

    public WindowStatus(WindowId.WindowDefaults defaults) {
        this.isMaxed = defaults.maximized();
        this.x = defaults.locX();
        this.y = defaults.locY();
        this.width = defaults.width();
        this.height = defaults.height();
    }

    public void apply(Component window) {
        window.setSize(width, height);
        window.setLocation(x, y);
        if (window instanceof Frame frame) {
            if (isMaxed) {
                SwingUtilities.invokeLater(() -> frame.setExtendedState(Frame.MAXIMIZED_BOTH));
            } else {
                frame.setExtendedState(Frame.NORMAL);
            }
        } else if (window instanceof JInternalFrame internalFrame) {
            try {
                internalFrame.setMaximum(isMaxed);
            } catch (PropertyVetoException e) {
                e.printStackTrace();
            }
        }
    }
}