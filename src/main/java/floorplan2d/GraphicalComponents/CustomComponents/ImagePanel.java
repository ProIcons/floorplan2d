package floorplan2d.GraphicalComponents.CustomComponents;

/**
 *
 * @author Nikolas
 */
import java.awt.Graphics;

import java.awt.image.BufferedImage;

import javax.swing.JPanel;

public class ImagePanel extends JPanel {

    private BufferedImage image;
    public ImagePanel() {
        image = null;
    }
    public ImagePanel(BufferedImage img) {
        image = img;
        repaint();
    }
    public void setImage(BufferedImage img) {
        image = img;
        repaint();
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        if (image != null) {
            g.drawImage(image, 0, 0, null); // see javadoc for more info on the parameters            
        }
    }

}
