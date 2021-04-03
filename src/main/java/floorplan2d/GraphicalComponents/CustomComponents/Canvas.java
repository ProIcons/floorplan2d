package floorplan2d.GraphicalComponents.CustomComponents;

import floorplan2d.View;
import floorplan2d.ViewItems.*;
import floorplan2d.ViewItems.Shapes.*;
import floorplan2d.Collections.ItemCollection;
import floorplan2d.Commands.CreateCommand;
import floorplan2d.Commands.TransformCommand;
import floorplan2d.Editor;
import floorplan2d.ExtendedInternalClasses.Dimension2D;

import java.awt.AlphaComposite;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Cursor;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.RenderingHints;
import java.awt.Stroke;
import java.awt.Toolkit;

import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;

import java.awt.geom.AffineTransform;
import java.awt.geom.Line2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import javax.swing.JComponent;
import javax.swing.SwingUtilities;

/**
 *
 * @author Nikolas
 */
public class Canvas extends JComponent implements MouseListener, MouseWheelListener, MouseMotionListener, java.io.Serializable {

    public enum DragBehavior {
        Move,
        MoveGroup,
        Rotate,
        Resize
    }

    public enum MouseBehavior {
        Select,
        Add
    }
    protected Grid grid;
    private boolean isViewPortChanged;
    private boolean handItemsCanBeAdded;
    private View view;
    protected Point currentCanvasMousePosition, startCanvasMousePosition, lastCanvasMousePosition, endCanvasMousePosition, actualCanvasMousePosition;
    private int resizePoint;
    private ItemCollection unTransformedItems;
    private Rectangle2D.Double selectionRectangle;
    private DragBehavior dragBehavior;

    private transient Graphics2D graphicsHandler;

    public Canvas(View _view) {
        view = _view;
        grid = new Grid(new Dimension2D.Double(10, 10), 20, 50, view);
        unTransformedItems = new ItemCollection();
        addMouseListener(this);
        addMouseMotionListener(this);
        addMouseWheelListener(this);
        setName(_view.getName());
    }

    public void setGrid(Grid _grid) {
        grid = _grid;
        repaint();
    }

    public Grid getGrid() {
        return grid;
    }

    public View getView() {
        return view;
    }

    @Override
    public void mouseClicked(MouseEvent me) {

    }

    @Override
    public void mousePressed(MouseEvent e) {
        currentCanvasMousePosition = translateMousePoint(e.getX(), e.getY());
        actualCanvasMousePosition = new Point(e.getX(), e.getY());
        startCanvasMousePosition = currentCanvasMousePosition;

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!view.getHandItems().isEmpty()) {
                //smth
            } else {
                Item item = isItem(currentCanvasMousePosition.x, currentCanvasMousePosition.y);
                if (item != null) {
                    selectionRectangle = null;

                    Item selectedItem;
                    if (view.getSelectedItems().contains(item)) {
                        if (view.getSelectedItems().size() == 1) {
                            selectedItem = item;
                            if (isRotateShape(new Point(currentCanvasMousePosition.x, currentCanvasMousePosition.y)) && selectedItem.isRotetable()) {
                                dragBehavior = DragBehavior.Rotate;
                            } else if (isResizeShape(currentCanvasMousePosition) > -1 && selectedItem.isResizeable()) {
                                dragBehavior = DragBehavior.Resize;
                                resizePoint = isResizeShape(currentCanvasMousePosition);

                            } else {
                                dragBehavior = DragBehavior.Move;
                            }
                        } else {
                            dragBehavior = DragBehavior.Move;
                        }

                    } else {
                        view.clearSelectedItems();
                        view.getSelectedItems().add(item);
                        item.setSelected(true);
                        selectedItem = item;
                        if (isRotateShape(new Point(currentCanvasMousePosition.x, currentCanvasMousePosition.y)) && selectedItem.isRotetable()) {
                            dragBehavior = DragBehavior.Rotate;
                        } else if (isResizeShape(currentCanvasMousePosition) > -1 && selectedItem.isResizeable()) {
                            dragBehavior = DragBehavior.Resize;
                            resizePoint = isResizeShape(currentCanvasMousePosition);
                        } else {
                            dragBehavior = DragBehavior.Move;
                        }

                    }
                    unTransformedItems = view.getSelectedItems().clone(true);
                } else {
                    view.clearSelectedItems();
                    selectionRectangle = new Rectangle2D.Double(startCanvasMousePosition.x, startCanvasMousePosition.y, 0, 0);
                }
                repaint();
            }
        } else if (e.isPopupTrigger()) {
            view.showPopupMenu(currentCanvasMousePosition);
        }
        lastCanvasMousePosition = currentCanvasMousePosition;
    }

    @Override
    public void mouseReleased(MouseEvent e) {
        currentCanvasMousePosition = translateMousePoint(e.getX(), e.getY());
        actualCanvasMousePosition = new Point(e.getX(), e.getY());
        endCanvasMousePosition = currentCanvasMousePosition;
        lastCanvasMousePosition = currentCanvasMousePosition;

        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!view.getSelectedItems().isEmpty()) {
                new TransformCommand(view, unTransformedItems, view.getSelectedItems());
            } else if (!view.getHandItems().isEmpty()) {
                boolean isCreated = true;
                for (Item item : view.getHandItems()) {
                    boolean found = false;
                    for (Item _item : view.getCopiedItems()) {
                        if (item.equals(_item)) {
                            found = true;
                            break;
                        }
                    }
                    if (found) {
                        isCreated = false;
                        break;
                    }
                }
                if (handItemsCanBeAdded) {
                    if (isCreated) {
                        new CreateCommand(view);
                    } else {
                        for (Item handItem : view.getHandItems()) {
                            view.addItem(handItem);
                        }
                    }
                    view.getHandItems().clear();
                    handItemsCanBeAdded = false;
                }
            }
            if (startCanvasMousePosition.equals(lastCanvasMousePosition)) {
                Item item = isItem(currentCanvasMousePosition.x, currentCanvasMousePosition.y);

                if (item != null) {
                    if (e.isControlDown()) {
                        if (view.getSelectedItems().contains(item)) {
                            view.getSelectedItems().remove(item);
                            item.setSelected(false);
                        } else {
                            view.getSelectedItems().add(item);
                            item.setSelected(true);
                        }

                    } else {
                        view.clearSelectedItems();
                        view.getSelectedItems().add(item);
                        item.setSelected(true);
                    }
                } else {
                    view.clearSelectedItems();
                }
                repaint();
            }
        } else if (e.isPopupTrigger()) {
            view.showPopupMenu(currentCanvasMousePosition);
        }
        selectionRectangle = null;

        repaint();

    }

    @Override
    public void mouseEntered(MouseEvent me) {

    }

    @Override
    public void mouseExited(MouseEvent me) {

    }

    @Override
    public void mouseWheelMoved(MouseWheelEvent e) {
        double ammountToAdd = 0.1;
        if (e.isAltDown()) {
            ammountToAdd = 0.01;
        }
        if (e.getWheelRotation() < 0) {
            if (view.getScale() + ammountToAdd <= 3.1) {
                view.setScale(view.getScale() + ammountToAdd);
                repaint();
            }
        } else if (view.getScale() - ammountToAdd > 0.5) {
            view.setScale(view.getScale() - ammountToAdd);
            repaint();
        }
    }

    @Override
    public void mouseDragged(MouseEvent e) {
        currentCanvasMousePosition = translateMousePoint(e.getX(), e.getY());
        actualCanvasMousePosition = new Point(e.getX(), e.getY());
        if (SwingUtilities.isLeftMouseButton(e)) {
            if (!view.getHandItems().isEmpty()) {
                showHandItems(e);
            } else if (selectionRectangle != null) {
                double width;
                double height;
                double x, y;
                if (currentCanvasMousePosition.x - startCanvasMousePosition.x > 0) {
                    width = currentCanvasMousePosition.x - startCanvasMousePosition.x;
                    x = startCanvasMousePosition.x;
                } else {
                    width = Math.abs(currentCanvasMousePosition.x - startCanvasMousePosition.x);
                    x = startCanvasMousePosition.x - width;
                }
                if (currentCanvasMousePosition.y - startCanvasMousePosition.y > 0) {
                    height = currentCanvasMousePosition.y - startCanvasMousePosition.y;
                    y = startCanvasMousePosition.y;
                } else {
                    height = Math.abs(currentCanvasMousePosition.y - startCanvasMousePosition.y);
                    y = startCanvasMousePosition.y - height;
                }
                selectionRectangle.setFrame(
                        x, y, width, height);

                view.clearSelectedItems();
                for (Item item : view.getItems()) {
                    if (selectionRectangle.contains(item.getFillPath().getBounds2D()) || selectionRectangle.intersects(item.getFillPath().getBounds2D())) {
                        view.getSelectedItems().add(item);
                        item.setSelected(true);
                    }
                }
                repaint();
            } else if (!view.getSelectedItems().isEmpty()) {
                if (dragBehavior == DragBehavior.Resize) {
                    resizeCanvasItem(e);
                } else if (dragBehavior == DragBehavior.Move) {
                    moveCanvasItem(e);
                } else if (dragBehavior == DragBehavior.Rotate) {
                    rotateCanvasItem(e);
                }

            }
        } else if (SwingUtilities.isMiddleMouseButton(e)) {
            if (!isViewPortChanged) {
                double mx = Math.floor(currentCanvasMousePosition.x / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                double my = Math.floor(currentCanvasMousePosition.y / grid.getBlockDimension().height) * grid.getBlockDimension().height;

                double lx = Math.floor(lastCanvasMousePosition.x / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                double ly = Math.floor(lastCanvasMousePosition.y / grid.getBlockDimension().height) * grid.getBlockDimension().height;

                double ix = Math.floor((view.getViewPort().x) / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                double iy = Math.floor((view.getViewPort().y) / grid.getBlockDimension().height) * grid.getBlockDimension().height;
                Point2D.Double transPoint = new Point2D.Double(ix + (mx - lx), iy + (my - ly));

                view.setViewPort(new Point((int) transPoint.x, (int) transPoint.y));
                isViewPortChanged = true;
                repaint();

            } else if (isViewPortChanged) {
                isViewPortChanged = false;
            }
        } else if (SwingUtilities.isRightMouseButton(e)) {
            if (view.getHandItems().size() == 1) {
                rotateHandItem(e);
            }
        }

        lastCanvasMousePosition = currentCanvasMousePosition;
    }

    @Override
    public void mouseMoved(MouseEvent e) {
        currentCanvasMousePosition = translateMousePoint(e.getX(), e.getY());
        actualCanvasMousePosition = new Point(e.getX(), e.getY());
        if (lastCanvasMousePosition == null) {
            lastCanvasMousePosition = currentCanvasMousePosition;
        }

        if (!view.getSelectedItems().isEmpty()) {
            if (view.getSelectedItems().size() == 1) {
                showRotateCursor();
            }
        } else if (!view.getHandItems().isEmpty()) {
            if (view.getHandItems().size() == 1) {
                if (view.getHandItems().get(0).mustBeMounted()) {
                    Item oItem = isItem(e.getX(), e.getY());
                    if (oItem != null) {
                        if (oItem.canContainItems() && !oItem.getChildItems().contains(view.getHandItems().get(0))) {
                            view.getHandItems().get(0).setParentItem(oItem);
                        }
                    }
                }
            }
            showHandItems(e);

        }
        repaint();

        lastCanvasMousePosition = currentCanvasMousePosition;
    }

    private void resizeCanvasItem(MouseEvent e) {
        Item selectedItem = view.getSelectedItems().get(0);
        if (selectedItem.isResizeable() && selectedItem.canResize()) {
            Point2D.Double movePoint;

            Point2D.Double transitionPoint = new Point2D.Double(
                    currentCanvasMousePosition.x - lastCanvasMousePosition.x,
                    currentCanvasMousePosition.y - lastCanvasMousePosition.y);

            /*            if (selectedItem.getItemType() == Item.Type.constructionItem) {
                if (selectedItem.getDimension().width > selectedItem.getDimension().height) {
                    transitionPoint.y = 0;
                } else {
                    transitionPoint.x = 0;
                }
            }*/
            Item temp = selectedItem.clone();
            temp.setDeltaDimension(resizePoint, transitionPoint);

            if (!temp.isIntersecting()
                    && temp.getDimension().width >= 20
                    && temp.getDimension().height >= 20) {
                if (!e.isControlDown()
                        && (selectedItem.getRotationRadians() == 0
                        || (selectedItem.getRotationRadians() / Math.PI) % 0.5 == 0)) {

                    double mx = Math.floor(currentCanvasMousePosition.x / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                    double my = Math.floor(currentCanvasMousePosition.y / grid.getBlockDimension().height) * grid.getBlockDimension().height;

                    double lx = Math.floor(lastCanvasMousePosition.x / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                    double ly = Math.floor(lastCanvasMousePosition.y / grid.getBlockDimension().height) * grid.getBlockDimension().height;

                    double ix = Math.floor(selectedItem.getTransformedPoints()[resizePoint].x / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                    double iy = Math.floor(selectedItem.getTransformedPoints()[resizePoint].y / grid.getBlockDimension().height) * grid.getBlockDimension().height;

                    movePoint = new Point2D.Double(ix + (mx - lx), iy + (my - ly));
                    transitionPoint = new Point2D.Double(movePoint.x - selectedItem.getTransformedPoints()[resizePoint].x, movePoint.y - selectedItem.getTransformedPoints()[resizePoint].y);

                    /*                    if (selectedItem.getItemType() == Item.Type.constructionItem) {
                        if (selectedItem.getDimension().width > selectedItem.getDimension().height) {
                            transitionPoint.y = 0;
                        } else {
                            transitionPoint.x = 0;
                        }
                    }
                     */
                    temp = selectedItem.clone();
                    temp.setDeltaDimension(resizePoint, transitionPoint);

                    if (!temp.isIntersecting()
                            && movePoint.x + view.getViewPort().x - 20 >= 0
                            && movePoint.y + view.getViewPort().y - 20 >= 0
                            && temp.getDimension().width >= 20
                            && temp.getDimension().height >= 20) {

                        selectedItem.setDeltaDimension(resizePoint, transitionPoint);
                    }
                } else {
                    selectedItem.setDeltaDimension(resizePoint, transitionPoint);
                }
            }
        } else {
            dragBehavior = DragBehavior.Move;
        }
        repaint();
    }

    private void rotateCanvasItem(MouseEvent e) {
        Item selectedItem = view.getSelectedItems().get(0);
        double centerX = selectedItem.getPath().getBounds2D().getCenterX();
        double centerY = selectedItem.getPath().getBounds2D().getCenterY();
        double rotation = Math.atan2((centerY - currentCanvasMousePosition.y), (centerX - currentCanvasMousePosition.x)) - Math.toRadians(90);

        Item temp = selectedItem.clone();

        AffineTransform.getRotateInstance((rotation), centerX, centerY).transform(temp.getOriginalPoints(), 0, temp.getTransformedPoints(), 0, 4);
        if (Math.round(Math.toDegrees(rotation)) % 10 == 0 && !e.isAltDown()) {
            AffineTransform.getRotateInstance(
                    Math.toRadians(Math.round(Math.toDegrees(rotation))),
                    centerX,
                    centerY).transform(
                            temp.getOriginalPoints(),
                            0,
                            temp.getTransformedPoints(),
                            0,
                            4);
            if (!temp.isIntersecting()) {
                selectedItem.setRotationRadians(Math.toRadians(Math.round(Math.toDegrees(rotation))));
                repaint();
            }
        } else if (!temp.isIntersecting() && e.isAltDown()) {
            selectedItem.setRotationRadians(rotation);
            repaint();
        }
    }

    private void moveCanvasItem(MouseEvent e) {
        double selectedItemPositionX = view.getSelectedItems().getPath().getBounds2D().getX();
        double selectedItemPositionY = view.getSelectedItems().getPath().getBounds2D().getY();

        Point2D.Double movePoint = new Point2D.Double(
                selectedItemPositionX + (currentCanvasMousePosition.x - lastCanvasMousePosition.x),
                selectedItemPositionY + (currentCanvasMousePosition.y - lastCanvasMousePosition.y)
        );
        Point2D.Double deltaMovePoint = new Point2D.Double(
                (currentCanvasMousePosition.x - lastCanvasMousePosition.x),
                (currentCanvasMousePosition.y - lastCanvasMousePosition.y)
        );
        if (!e.isControlDown() && (view.getSelectedItems().get(0).getRotationRadians() == 0 || (view.getSelectedItems().get(0).getRotationRadians() / Math.PI) % 0.5 == 0)) {
            double mx = Math.floor(currentCanvasMousePosition.x / grid.getBlockDimension().width) * grid.getBlockDimension().width;
            double my = Math.floor(currentCanvasMousePosition.y / grid.getBlockDimension().height) * grid.getBlockDimension().height;

            double lx = Math.floor(lastCanvasMousePosition.x / grid.getBlockDimension().width) * grid.getBlockDimension().width;
            double ly = Math.floor(lastCanvasMousePosition.y / grid.getBlockDimension().height) * grid.getBlockDimension().height;

            double ix = Math.floor(selectedItemPositionX / grid.getBlockDimension().width) * grid.getBlockDimension().width;
            double iy = Math.floor(selectedItemPositionY / grid.getBlockDimension().height) * grid.getBlockDimension().height;

            movePoint = new Point2D.Double(ix + (mx - lx), iy + (my - ly));
            deltaMovePoint = new Point2D.Double((mx - lx), (my - ly));
        }
        if (view.getSelectedItems().size() == 1) {
            if (view.getSelectedItems().get(0).isIntersecting(deltaMovePoint)) {
                if (!view.getSelectedItems().get(0).isIntersecting(new Point2D.Double(deltaMovePoint.x, 0))) {
                    movePoint = new Point2D.Double(movePoint.x, selectedItemPositionY);
                    view.getSelectedItems().get(0).setPosition(movePoint);
                } else if (!view.getSelectedItems().get(0).isIntersecting(new Point2D.Double(0, deltaMovePoint.y))) {
                    movePoint = new Point2D.Double(selectedItemPositionX, movePoint.y);
                    view.getSelectedItems().get(0).setPosition(movePoint);
                }
            } else {
                view.getSelectedItems().get(0).setDeltaPosition(deltaMovePoint);
            }
            repaint();
        } else {
            boolean isIntersecting = false;
            for (Item selectedItem : view.getSelectedItems()) {
                if (selectedItem.isIntersecting(deltaMovePoint)) {
                    isIntersecting = true;
                }
            }
            if (!isIntersecting) {
                for (Item selectedItem : view.getSelectedItems()) {
                    selectedItem.setDeltaPosition(deltaMovePoint);
                }
                repaint();
            }
        }
    }

    private void showHandItems(MouseEvent e) {
        if (!view.getHandItems().isEmpty()) {
            double handItemCenterPositionX = view.getHandItems().getPath().getBounds2D().getCenterX();
            double handItemCenterPositionY = view.getHandItems().getPath().getBounds2D().getCenterY();

            for (Item handItem : view.getHandItems()) {

                double hItemDX = handItemCenterPositionX - handItem.getPosition().x;
                double hItemDY = handItemCenterPositionY - handItem.getPosition().y;

                boolean bypass = false;
                if (handItem.getPosition().x == 0 && handItem.getPosition().y == 0) {
                    handItem.setPosition(new Point2D.Double(currentCanvasMousePosition.x - hItemDX, currentCanvasMousePosition.y - hItemDY));
                    bypass = true;
                }
                if (!e.isControlDown() && !bypass && (handItem.getRotationRadians() == 0 || (handItem.getRotationRadians() / Math.PI) % 0.5 == 0)) {
                    double mx = Math.floor((currentCanvasMousePosition.x - handItem.getDimension().width / 2) / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                    double my = Math.floor((currentCanvasMousePosition.y - handItem.getDimension().height / 2) / grid.getBlockDimension().height) * grid.getBlockDimension().height;

                    double lx = Math.floor((lastCanvasMousePosition.x - handItem.getDimension().width / 2) / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                    double ly = Math.floor((lastCanvasMousePosition.y - handItem.getDimension().height / 2) / grid.getBlockDimension().height) * grid.getBlockDimension().height;

                    double ix = Math.floor(handItem.getPosition().x / grid.getBlockDimension().width) * grid.getBlockDimension().width;
                    double iy = Math.floor(handItem.getPosition().y / grid.getBlockDimension().height) * grid.getBlockDimension().height;

                    handItem.setPosition(new Point2D.Double(ix + (mx - lx), iy + (my - ly)));
                } else {
                    handItem.setPosition(new Point2D.Double(currentCanvasMousePosition.x - hItemDX, currentCanvasMousePosition.y - hItemDY));
                }
                if (handItem.isIntersecting()) {
                    handItemsCanBeAdded = false;

                } else {
                    handItemsCanBeAdded = true;
                }
            }
        }
        repaint();
    }

    private void rotateHandItem(MouseEvent e) {
        if (view.getHandItems().size() == 1) {

            Item handItem = view.getHandItems().get(0);
            double centerX = handItem.getPath().getBounds2D().getCenterX();
            double centerY = handItem.getPath().getBounds2D().getCenterY();
            double rotation = Math.atan2((centerY - currentCanvasMousePosition.y), (centerX - currentCanvasMousePosition.x)) - Math.toRadians(90);

            if (Math.round(Math.toDegrees(rotation)) % 10 == 0 && !e.isAltDown()) {
                handItem.setRotationRadians(Math.toRadians(Math.round(Math.toDegrees(rotation))));

            } else {
                handItem.setRotationRadians(rotation);

            }
            if (handItem.isIntersecting()) {
                handItemsCanBeAdded = false;
            } else {
                handItemsCanBeAdded = true;
            }
            repaint();
        }
    }

    private void showRotateCursor() {
        if (!view.getSelectedItems().isEmpty()) {
            if (isRotateShape(currentCanvasMousePosition)) {
                Toolkit toolkit = Toolkit.getDefaultToolkit();
                Image image = toolkit.getImage(getClass().getResource("/rss/rotate.png"));
                Cursor c = toolkit.createCustomCursor(image, new Point(10, 10), "Cursor");
                setCursor(c);
            } else {
                setCursor(new Cursor(Cursor.DEFAULT_CURSOR));
            }
        }
    }

    private Item isItem(int x, int y) {
        for (Item t : view.getItems()) {
            if (t.getParentItem() != null) {
                Path2D.Double tPath = t.getFillPath();
                if (tPath.contains(new Point(x, y))) {
                    return t;
                }
            }
        }
        for (Item t : view.getItems()) {
            Path2D.Double tPath = t.getFillPath();
            if (tPath.contains(new Point(x, y))) {
                return t;
            }
        }
        return null;
    }

    private boolean isRotateShape(Point p) {
        if (!view.getSelectedItems().isEmpty()) {
            Item selectedItem = view.getSelectedItems().get(0);
            if (selectedItem.isRotetable()) {

                for (ItemShape s : selectedItem.getEditShapes()) {

                    if (s.getShapeType() == ItemShape.Type.Ellipse) {
                        Path2D.Double path = new Path2D.Double();

                        path.append(new Rectangle2D.Double((selectedItem.getTransformedPoints()[0].x + selectedItem.getTransformedPoints()[1].x) / 2 - 5, (selectedItem.getTransformedPoints()[0].y + selectedItem.getTransformedPoints()[1].y) / 2, 10, 10), false);

                        AffineTransform transform = new AffineTransform();
                        transform.setTransform(AffineTransform.getRotateInstance(selectedItem.getRotationRadians(), (selectedItem.getTransformedPoints()[0].x + selectedItem.getTransformedPoints()[1].x) / 2,
                                (selectedItem.getTransformedPoints()[0].y + selectedItem.getTransformedPoints()[1].y) / 2));
                        path.transform(transform);

                        if (path.contains(p)) {
                            return true;
                        }
                    }

                }
            }
        }
        return false;
    }

    private int isResizeShape(Point p) {
        if (!view.getSelectedItems().isEmpty()) {
            Item selectedItem = view.getSelectedItems().get(0);
            for (ItemShape s : selectedItem.getCornerShapes()) {
                if (s.getShapeType() == ItemShape.Type.Rectangle) {
                    Rectangle2D.Double[] rTable = new Rectangle2D.Double[4];
                    Path2D.Double path;
                    for (int i = 0; i < 4; i++) {
                        rTable[0] = new Rectangle2D.Double(selectedItem.getTransformedPoints()[i].x, selectedItem.getTransformedPoints()[i].y, 10, 10);
                        rTable[1] = new Rectangle2D.Double(selectedItem.getTransformedPoints()[i].x - 10, selectedItem.getTransformedPoints()[i].y, 10, 10);
                        rTable[2] = new Rectangle2D.Double(selectedItem.getTransformedPoints()[i].x - 10, selectedItem.getTransformedPoints()[i].y - 10, 10, 10);
                        rTable[3] = new Rectangle2D.Double(selectedItem.getTransformedPoints()[i].x, selectedItem.getTransformedPoints()[i].y - 10, 10, 10);
                        for (int j = 0; j < 4; j++) {
                            path = new Path2D.Double();
                            path.append(rTable[j], false);
                            AffineTransform transform = new AffineTransform();
                            transform.setTransform(AffineTransform.getRotateInstance(selectedItem.getRotationRadians(), path.getBounds2D().getCenterX(), path.getBounds2D().getCenterY()));
                            path.transform(transform);
                            if (path.contains(p) && selectedItem.getFillPath().contains(p)) {
                                return i;
                            }
                        }
                    }
                }
            }
        }
        return -1;
    }

    private Point translateMousePoint(int px, int py) {
        Point mouse;
        if (view.getScale() == 1) {
            mouse = new Point(px - view.getViewPort().x, py - view.getViewPort().y);
        } else {
            double x = px - view.getViewPort().x * view.getScale();
            double y = py - view.getViewPort().y * view.getScale();
            double dx = x / view.getScale();
            double dy = y / view.getScale();
            mouse = new Point((int) dx, (int) dy);
        }
        return mouse;
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

    private void drawRotatedString(String s, Graphics2D g, Point p, Font f, Item t) {
        FontMetrics metrics = g.getFontMetrics(f);
        int x = (t.getPath().getBounds().width - metrics.stringWidth(s)) / 2;
        int y = (t.getPath().getBounds().height / 2) - (metrics.getHeight() / 2);
        Font oldFont = g.getFont();
        g.setFont(f);

        AffineTransform transform = new AffineTransform();
        transform.setTransform(AffineTransform.getRotateInstance(t.getRotationRadians(), t.getPath().getBounds().x + t.getPath().getBounds().width / 2,
                t.getPath().getBounds().y + t.getPath().getBounds().height / 2));
        g.transform(transform);

        g.drawString(s, p.x + x, p.y + y);

        transform.setTransform(AffineTransform.getRotateInstance(-t.getRotationRadians(), t.getPath().getBounds().x + t.getPath().getBounds().width / 2,
                t.getPath().getBounds().y + t.getPath().getBounds().height / 2));
        g.transform(transform);

        g.setFont(oldFont);
    }

    private void drawResizeBorders(Path2D.Double path, Dimension2D.Double dim, Graphics2D g) {
        Color a = g.getColor();
        Font f = new Font("Storopia", Font.PLAIN, 12);
        FontMetrics metrics = g.getFontMetrics(f);
        int w = metrics.stringWidth(String.format("%.2f", dim.width));
        int h = metrics.getHeight();
        double iX = path.getBounds2D().getX();
        double iY = path.getBounds2D().getY();
        double iW = path.getBounds2D().getWidth();
        double iH = path.getBounds2D().getHeight();
        Font oldFont = g.getFont();
        g.setFont(f);
        Path2D.Double Path = new Path2D.Double();
        int my;
        int mx = path.getBounds().x + (int) ((iW - w) / 2) - 12;

        double max;
        double min;
        if (dim.width > dim.height) {
            max = dim.width;
            min = dim.height;
        } else {
            max = dim.height;
            min = dim.width;
        }

        if (w + 30 < iW) {
            Path.append(new Line2D.Double(iX + iW, iY - 10, iX + iW, iY - 20), false);
            Path.append(new Line2D.Double(iX, iY - 10, iX, iY - 20), false);
            Path.append(new Line2D.Double(iX, iY - 15, iX + (iW / 2 - w / 2) - 15, iY - 15), false);
            Path.append(new Line2D.Double(iX + (iW / 2 + w / 2) + 15, iY - 15, iX + iW, iY - 15), false);
            g.setColor(Color.red);
            if (iW > iH) {
                g.drawString(String.format("  %.2fm  ", max), mx, path.getBounds().y - 10);
            } else {
                g.drawString(String.format("  %.2fm  ", min), mx, path.getBounds().y - 10);
            }
        } else {
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            g.setColor(Color.white);
            Rectangle r = new Rectangle(mx + 2, path.getBounds().y - 25, w + 20, h);
            g.fill(r);
            g.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.setColor(Color.red);
            if (iW > iH) {
                g.drawString(String.format("  %.2fm  ", max), mx, path.getBounds().y - 10);
            } else {
                g.drawString(String.format("  %.2fm  ", min), mx, path.getBounds().y - 10);
            }
        }

        Path.append(new Line2D.Double(iX + iW + 10, iY + iH, iX + iW + 20, iY + iH), false);
        Path.append(new Line2D.Double(iX + iW + 10, iY, iX + iW + 20, iY), false);
        Path.append(new Line2D.Double(iX + iW + 15, iY, iX + iW + 15, iY + iH / 2 - h / 2 - 6), false);
        Path.append(new Line2D.Double(iX + iW + 15, iY + iH / 2 - h / 2 + h + 6, iX + iW + 15, iY + iH), false);

        mx = path.getBounds().x + path.getBounds().width + 5;
        my = path.getBounds().y + path.getBounds().height / 2 - h / 2 + 12;
        if (iW > iH) {
            g.drawString(String.format("%.2fm", min), mx, my);
        } else {
            g.drawString(String.format("%.2fm", max), mx, my);
        }

        g.draw(Path);
        g.setColor(a);
        g.setFont(oldFont);
    }

    public Point getLastMousePosition() {
        return lastCanvasMousePosition;
    }

    public Graphics2D getGraphicsHandler() {
        return graphicsHandler;
    }

    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D G2D = (Graphics2D) g;
        graphicsHandler = G2D;

        grid.drawGrid();

        Path2D.Double path;
        G2D.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BICUBIC);
        G2D.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        G2D.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        G2D.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);
        Font font = Editor.customFont.deriveFont(Font.PLAIN, 12);
        G2D.setStroke(new BasicStroke(0));
        for (Item t : view.getItems()) {
            path = t.getPath();
            G2D.setPaint(Color.white);
            G2D.fill(t.getPath());
            G2D.setPaint(Color.black);
            if (t.isSelected()) {
                G2D.setStroke(new BasicStroke(2.0f));
                G2D.draw(path);
                G2D.setStroke(new BasicStroke(0));
            } else {
                G2D.draw(path);
            }
            if (t.isNameVisible()) {
                drawRotatedString(t.getName(), G2D, new Point(path.getBounds().x, path.getBounds().y), font, t);
            }

        }
        float dash1[] = {4};
        Stroke def = G2D.getStroke();
        BasicStroke dashed = new BasicStroke(1.0f, BasicStroke.CAP_SQUARE, BasicStroke.JOIN_MITER, 10.0f, dash1, 0.0f);
        if (view.getSelectedItems().size() > 0) {
            Path2D.Double _path = view.getSelectedItems().getPath();
            G2D.setStroke(dashed);
            G2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            G2D.draw(_path.getBounds2D());
            G2D.setStroke(def);
            G2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            drawResizeBorders(
                    view.getSelectedItems().getPath(),
                    new Dimension2D.Double(_path.getBounds2D().getWidth() / getGrid().getPixelsPerMeter(), _path.getBounds2D().getHeight() / getGrid().getPixelsPerMeter()),
                    G2D);
        }
        if (selectionRectangle != null) {
            G2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.5f));
            G2D.draw(selectionRectangle);
            G2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.2f));
            G2D.fill(selectionRectangle);

        }
        for (Item item : view.getHandItems()) {
            G2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.3f));
            if (!handItemsCanBeAdded) {
                G2D.setColor(Color.red);
            } else {
                G2D.setColor(Color.green);
            }
            G2D.fill(item.getPath());
            G2D.setColor(Color.black);
            G2D.draw(item.getPath());
            G2D.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.6f));
            drawRotatedString(item.getName(), G2D, new Point(item.getPath().getBounds().x, item.getPath().getBounds().y), font, item);

        }
    }
}
