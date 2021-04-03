package floorplan2d.GraphicalComponents.CustomComponents;

import floorplan2d.View;
import floorplan2d.Editor;
import floorplan2d.ExtendedInternalClasses.Dimension2D;

import java.awt.*;
import java.awt.geom.*;

/**
 *
 * @author Nikolas
 */
public final class Grid implements java.io.Serializable {

    private final Dimension2D.Double blockDimension;
    private final double pixelsPerMeter;
    private final View view;
    private final double borderSize;

    public Grid(Dimension2D.Double dimension, double _borderSize, double ppm, View _view) {
        pixelsPerMeter = ppm;
        this.blockDimension = new Dimension2D.Double(dimension.width, dimension.height);
        view = _view;
        borderSize = _borderSize;
    }

    public Dimension2D.Double getBlockDimension() {
        return blockDimension;
    }

    public double getPixelsPerMeter() {
        return pixelsPerMeter;
    }

    public double getBorderSize() {
        return borderSize;
    }

    public void drawStatusBar() {
        Graphics2D g2d = view.getCanvas().getGraphicsHandler();
        if (view.getCanvas().currentCanvasMousePosition != null) {
            String t;
            Font oldFont = view.getCanvas().getFont();
            Font font = Editor.customFont.deriveFont(Font.BOLD,12);
            g2d.setFont(font);
            FontMetrics metrics = g2d.getFontMetrics(font);
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            Rectangle2D.Double p = new Rectangle2D.Double(borderSize * view.getScale() + 5, g2d.getClipBounds().height - 15, g2d.getClipBounds().width, 15);
            g2d.setColor(Color.white);
            g2d.draw(p);
            g2d.setColor(Color.black);
            g2d.fill(p);
            g2d.setColor(Color.black);

            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2d.setStroke(new BasicStroke(2.0f));

            t = String.format("(x,y)=(%1$d,%2$d)", view.getCanvas().currentCanvasMousePosition.x, view.getCanvas().currentCanvasMousePosition.y);
            g2d.drawString(t, g2d.getClipBounds().width - metrics.stringWidth(t) - 5, g2d.getClipBounds().height - metrics.getHeight() + 12);

            t = String.format("%1$.0f%%", view.getScale() * 100);
            g2d.drawString(t, (int) (borderSize * view.getScale() + 5 * view.getScale()), g2d.getClipBounds().height - metrics.getHeight() + 12);

            g2d.setStroke(new BasicStroke(0f));
            g2d.setFont(oldFont);
        }
    }

    public void drawBorders() {

        Graphics2D g2d = view.getCanvas().getGraphicsHandler();

        int width = view.getCanvas().getWidth();
        int height = view.getCanvas().getHeight();

        int xstart = 0;
        int ystart = 0;
        int index = 0;
        int meters = 0;

        g2d.scale(view.getScale(), view.getScale());
        g2d.translate(borderSize, borderSize);

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
        g2d.setPaint(Color.LIGHT_GRAY);
        g2d.fill(new Rectangle2D.Double(0, 0, width / view.getScale(), height / view.getScale()));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
        g2d.setPaint(Color.white);
        g2d.fill(new Rectangle2D.Double(-borderSize, -borderSize, width / view.getScale(), borderSize));
        g2d.fill(new Rectangle2D.Double(-borderSize, -borderSize, borderSize, height / view.getScale()));

        g2d.setPaint(Color.black);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.drawString("m", -15, -5);
        
        Font font = Editor.customFont.deriveFont(Font.PLAIN,12);
        
        while (xstart <= width / view.getScale()) {
            if (index % 5 == 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                
                drawCenterString(meters + "", g2d, new Dimension(50, (int) borderSize), new Point(xstart, 5), font);

                meters += 50 / getPixelsPerMeter();
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                g2d.drawLine(xstart, 0, xstart, -5);
            }
            xstart += getBlockDimension().width;
            index++;
        }
        index = 0;
        meters = 0;
        while (ystart <= height / view.getScale()) {
            if (index % 5 == 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                drawCenterString(meters + "", g2d, new Dimension(0, 50), new Point(-10, ystart - 25), font);

                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
                g2d.drawLine(0, ystart, -10, ystart);

                meters += 50 / getPixelsPerMeter();
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
                g2d.drawLine(0, ystart, -5, ystart);
            }
            ystart += getBlockDimension().height;
            index++;
        }
    }

    public void drawWorkingSpace() {

        Graphics2D g2d = view.getCanvas().getGraphicsHandler();

        int width = view.getCanvas().getWidth();
        int height = view.getCanvas().getHeight();

        int xstart = 0;
        int ystart = 0;
        int index = 0;
        while (xstart <= width / view.getScale()) {
            if (index % 5 == 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
            }
            g2d.draw(new Line2D.Double(xstart, 0, xstart, height / view.getScale()));

            xstart += getBlockDimension().width;
            index++;
        }
        index = 0;
        while (ystart <= height / view.getScale()) {
            if (index % 5 == 0) {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            } else {
                g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.05f));
            }
            g2d.draw(new Line2D.Double(0, ystart, width / view.getScale(), ystart));
            index++;
            ystart += getBlockDimension().height;
        }

        g2d.setPaint(Color.white);
        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
        g2d.drawLine(0, 0, width, 0);
        g2d.drawLine(0, 0, 0, height);

        g2d.translate(view.getViewPort().x - getBorderSize(), view.getViewPort().y - getBorderSize());

        g2d.setPaint(Color.black);
        g2d.draw(new Line2D.Double(-10, 0, width / view.getScale(), 0));
        g2d.draw(new Line2D.Double(0, -10, 0, height / view.getScale()));

        g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));

    }

    private void drawCenterString(String s, Graphics g, Dimension d, Point p, Font f) {
        FontMetrics metrics = g.getFontMetrics(f);
        int x = (d.width - metrics.stringWidth(s)) / 2;
        int y = ((d.height - metrics.getHeight()) / 2) - metrics.getAscent();
        Font oldFont = g.getFont();
        g.setFont(f);

        g.drawString(s, p.x + x, p.y + y);
        g.setFont(oldFont);
    }

    public void drawGrid() {
        drawStatusBar();
        drawBorders();
        drawWorkingSpace();
    }

}
