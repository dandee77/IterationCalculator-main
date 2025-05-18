import javax.swing.*;
import java.awt.*;


public class RoundedPanel extends JPanel {
    private final int radius;
    private final Color bg;
    // Outline color is not currently used but kept for future enhancements
    private final Color outline;

    public RoundedPanel(int radius, Color bg) {
        this(radius, bg, new Color(200, 210, 230)); // default outline color
    }

    public RoundedPanel(int radius, Color bg, Color outline) {
        this.radius = radius;
        this.bg = bg;
        this.outline = outline;
        setOpaque(false);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g.create();
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        // Fill rounded background only, no outline
        g2.setColor(bg);
        g2.fillRoundRect(0, 0, getWidth(), getHeight(), radius, radius);

        g2.dispose();
    }
}

class YourTextField extends JTextField {
    public YourTextField() {
        setOpaque(false);
        setBackground(new Color(0, 0, 0, 0));
    }

    // Other methods and constructors as needed
}
