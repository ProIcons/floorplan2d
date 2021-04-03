package floorplan2d.GraphicalComponents.CustomComponents;

import floorplan2d.GraphicalComponents.Popups.ViewContainerPopupMenu;
import floorplan2d.Editor;
import floorplan2d.View;

import static floorplan2d.Editor.project;
import static floorplan2d.Editor.window;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Component;
import java.awt.Cursor;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;

import java.awt.geom.Rectangle2D;

import javax.swing.*;

/**
 *
 * @author Nikolas
 */
public class ViewContainer extends JTabbedPane implements java.io.Serializable {

    public ViewContainer() {
        super.addTab("+", new JPanel());

    }

    private TabCloseUI closeUI = new TabCloseUI(this);
    private ViewContainerPopupMenu popup = new ViewContainerPopupMenu(this);

    @Override
    public void paint(Graphics g) {
        super.paint(g);
        closeUI.paint(g);
    }

    @Override
    public void addTab(String title, Component component) {
        super.addTab(title + "   ", component);
    }

    public String getTabTitleAt(int index) {
        return super.getTitleAt(index).trim();
    }

    private class TabCloseUI implements MouseListener, MouseMotionListener {

        private ViewContainer tabbedPane;
        private int closeX = 0, closeY = 0, meX = 0, meY = 0;
        private int selectedTab;
        private final int width = 8, height = 8;
        private Rectangle rectangle = new Rectangle(0, 0, width, height);

        private TabCloseUI() {
        }

        public TabCloseUI(ViewContainer pane) {

            tabbedPane = pane;
            tabbedPane.addMouseMotionListener(this);
            tabbedPane.addMouseListener(this);
        }

        @Override
        public void mouseEntered(MouseEvent me) {
            tabbedPane.repaint();
        }

        @Override
        public void mouseExited(MouseEvent me) {
            tabbedPane.repaint();
        }

        @Override
        public void mousePressed(MouseEvent me) {
        }

        @Override
        public void mouseClicked(MouseEvent me) {
        }

        @Override
        public void mouseDragged(MouseEvent me) {
        }

        @Override
        public void mouseReleased(MouseEvent me) {
            if (SwingUtilities.isRightMouseButton(me)) {
                int tabind = mouseUnderTabName(new Point(me.getX(), me.getY()));
                if (tabind > -1) {
                    popup.show(project.getViewByCanvas((Canvas) tabbedPane.getComponentAt(tabind)), tabind, new Point(me.getX(), me.getY()));
                }
            } else if (SwingUtilities.isLeftMouseButton(me)) {

                if (tabbedPane.getSelectedIndex() == 0 && project != null) {
                    int tabind = mouseUnderTabName(new Point(me.getX(), me.getY()));
                    if (tabind == 0) {
                        View view = Editor.project.addView("Floor " + (project.getViews().size() + 1));
                        window.addListItem(view);
                        project.setActiveView(view);
                        window.changeTab(view);
                        window.setEditMenuEnabled(true);
                    }

                } else if (closeUnderMouse(me.getX(), me.getY())) {

                    boolean isToCloseTab = tabAboutToClose(selectedTab);
                    if (isToCloseTab && selectedTab > -1) {
                        tabbedPane.removeTabAt(selectedTab);
                    }
                    selectedTab = tabbedPane.getSelectedIndex();
                    if (getTabCount() > 1) {
                        project.setActiveView(project.getViewByCanvas(window.getActiveTabCanvas()));
                    } else {
                        project.setActiveView(null);
                    }
                    if (project.getActiveView() == null) {
                        window.setEditMenuEnabled(false);
                    }
                }
            }

        }

        public int mouseUnderTabName(Point point) {
            for (int i = 0; i < tabbedPane.getTabCount(); i++) {
                double x = tabbedPane.getBoundsAt(i).x,
                        width = tabbedPane.getBoundsAt(i).width,
                        y = tabbedPane.getBoundsAt(i).y,
                        height = tabbedPane.getBoundsAt(i).height;
                Rectangle2D.Double a = new Rectangle2D.Double(x, y, width, height);
                if (a.contains(point)) {
                    return i;
                }
            }
            return -1;
        }

        @Override
        public void mouseMoved(MouseEvent me) {
            meX = me.getX();
            meY = me.getY();
            if (mouseOverTab(meX, meY)) {
                controlCursor();

            }
            tabbedPane.repaint();
        }

        private void controlCursor() {
            if (tabbedPane.getTabCount() > 0 && tabbedPane.getSelectedIndex() != 0) {
                if (closeUnderMouse(meX, meY)) {
                    tabbedPane.setCursor(new Cursor(Cursor.HAND_CURSOR));
                    if (selectedTab > -1) {
                        tabbedPane.setToolTipTextAt(selectedTab, "Close " + tabbedPane.getTitleAt(selectedTab));
                    }
                } else {
                    tabbedPane.setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
                    if (selectedTab > -1) {
                        tabbedPane.setToolTipTextAt(selectedTab, "");
                    }
                }
            }
        }

        private boolean closeUnderMouse(int x, int y) {
            rectangle.x = closeX;
            rectangle.y = closeY;
            return rectangle.contains(x, y);
        }

        public void paint(Graphics g) {

            int tabCount = tabbedPane.getTabCount();
            for (int j = 1; j < tabCount; j++) {
                int x = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width - width - 5;
                int y = tabbedPane.getBoundsAt(j).y + 5;
                drawClose(g, x, y);

            }

        }

        private void drawClose(Graphics g, int x, int y) {
            if (tabbedPane != null && tabbedPane.getTabCount() > 0) {
                Graphics2D g2 = (Graphics2D) g;
                drawColored(g2, isUnderMouse(x, y) ? Color.DARK_GRAY : Color.WHITE, x, y);
            }
        }

        private void drawColored(Graphics2D g2, Color color, int x, int y) {

            g2.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
            g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
            g2.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
            g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            g2.setColor(color);
            g2.fill(new Rectangle(x - 2, y - 1, width + 4, height + 3));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            g2.setColor(Color.BLACK);
            g2.draw(new Rectangle(x - 2, y - 1, width + 4, height + 3));
            g2.setStroke(new BasicStroke(2, BasicStroke.JOIN_ROUND, BasicStroke.CAP_BUTT));
            g2.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g2.drawLine(x, y + 1, x + width, y + height);
            g2.drawLine(x + width, y + 1, x, y + height);

        }

        private boolean isUnderMouse(int x, int y) {
            return Math.abs(x - meX) < width && Math.abs(y - meY) < height;
        }

        private boolean mouseOverTab(int x, int y) {
            int tabCount = tabbedPane.getTabCount();
            for (int j = 0; j < tabCount; j++) {
                if (tabbedPane.getBoundsAt(j).contains(meX, meY)) {
                    selectedTab = j;
                    closeX = tabbedPane.getBoundsAt(j).x + tabbedPane.getBoundsAt(j).width - width - 5;
                    closeY = tabbedPane.getBoundsAt(j).y + 5;
                    return true;
                }
            }
            return false;
        }

    }

    public boolean tabAboutToClose(int tabIndex) {
        return true;
    }

}
