import javax.swing.JPanel;
import java.awt.*;

public class ShadowPanel extends JPanel {
    private static final long serialVersionUID = 1L;

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);

        Graphics2D g2d = (Graphics2D) g.create();

        int shadowWidth = 22; // Width of the shadow
        int cornerRadius = 5; // Radius of the rounded corners
        int width = getWidth() - shadowWidth;
        int height = getHeight() - shadowWidth;

        // Draw the shadow
        Color shadowColor = new Color(0, 0, 0, 5); // Semi-transparent black
        g2d.setColor(shadowColor);
        g2d.fillRoundRect(shadowWidth, shadowWidth, width, height, cornerRadius, cornerRadius);

        g2d.dispose();
    }
}
