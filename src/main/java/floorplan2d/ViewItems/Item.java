package floorplan2d.ViewItems;

import floorplan2d.Collections.ItemCollection;
import floorplan2d.ExtendedInternalClasses.*;
import floorplan2d.View;
import floorplan2d.ViewItems.Shapes.*;

import java.awt.*;

import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Path2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import java.math.*;

import java.security.SecureRandom;

import java.util.*;

/**
 *
 * @author Nikolas
 */
@SuppressWarnings({"Convert2Diamond", "LeakingThisInConstructor"})
public abstract class Item implements java.io.Serializable {

    protected String name;
    protected String id;
    protected String typeName;

    protected ItemShape mainShape;
    protected String mainShapeId;

    protected ArrayList<ItemShape> innerShapes;
    protected ArrayList<ItemShape> editShapes;
    protected ArrayList<ItemShape> cornerShapes;

    private ArrayList<String> innerShapeIds;
    private ArrayList<String> editShapeIds;
    private ArrayList<String> cornerShapeIds;

    protected ItemCollection childItems;
    private ArrayList<String> childItemIds = new ArrayList<String>();
    protected Dimension dimension;
    protected View view;
    private String canvasId;
    protected Point2D.Double position;
    protected Point2D.Double[] transformedPoints;
    protected Point2D.Double[] originalPoints;
    protected ArrayList<Point2D.Double> points;
    protected double rotation;
    protected Item parentItem;
    protected Item original;
    private String parentItemId;

    protected boolean isNameVisible;
    protected boolean isRotateable;
    protected boolean isResizeable;
    protected boolean isSelected;
    protected boolean canResize;
    protected boolean canContainItems;
    protected boolean mustBeMounted;

    private final SecureRandom random = new SecureRandom();

    @SuppressWarnings("OverridableMethodCallInConstructor")

    public Item(String itemName, Point2D.Double pos, Dimension dim, View c) {
        name = itemName;
        view = c;
        canvasId = c.getId();

        childItems = new ItemCollection();

        editShapes = new ArrayList<ItemShape>();
        innerShapes = new ArrayList<ItemShape>();
        cornerShapes = new ArrayList<ItemShape>();

        editShapeIds = new ArrayList<String>();
        innerShapeIds = new ArrayList<String>();
        cornerShapeIds = new ArrayList<String>();

        points = new ArrayList<Point2D.Double>();
        transformedPoints = new Point2D.Double[4];
        originalPoints = new Point2D.Double[4];

        position = new Point2D.Double(pos.x, pos.y);
        isNameVisible = true;
        dimension = dim;
        isSelected = false;
        rotation = 0;

        id = new BigInteger(130, random).toString(32);

        if (parentItem != null) {
            parentItem.childItems.add(this);
            parentItem.childItemIds.add(id);
        }
        mustBeMounted = false;
        canContainItems = false;
        setMainShape();
        setItems();

        setPosition(position);

    }

    protected void addInnerShape(ItemShape s) {
        innerShapeIds.add(s.getId());
        innerShapes.add(s);
    }

    protected void addEditShape(ItemShape s) {
        editShapeIds.add(s.getId());
        editShapes.add(s);
    }

    protected void addCornerShape(ItemShape s) {
        cornerShapeIds.add(s.getId());
        cornerShapes.add(s);
    }

    public void setRandomId() {
        id = new BigInteger(130, random).toString(32);
    }

    protected void setMainShape() {
        Rectangle2D.Double MainRectangle = new Rectangle2D.Double(0, 0, dimension.width, dimension.height);
        transformedPoints[0] = new Point2D.Double(position.x, position.y);
        transformedPoints[1] = new Point2D.Double(position.x + MainRectangle.width, position.y);
        transformedPoints[2] = new Point2D.Double(position.x + MainRectangle.width, position.y + MainRectangle.height);
        transformedPoints[3] = new Point2D.Double(position.x, position.y + MainRectangle.height);

        for (int i = 0; i < 4; i++) {
            originalPoints[i] = new Point2D.Double(transformedPoints[i].x, transformedPoints[i].y);
        }
        mainShape = new RectangleShape(MainRectangle, this, ItemShape.BindPoint.TopLeft);
    }

    protected void clearShapes() {
        innerShapes.clear();
        editShapes.clear();
        cornerShapes.clear();
        innerShapeIds.clear();
        editShapeIds.clear();
        cornerShapeIds.clear();
    }

    protected void setItems() {
        clearShapes();

        Ellipse2D.Double Extra = new Ellipse2D.Double(- 5, 0, 10, 10);
        addEditShape(new EllipseShape(Extra, this, ItemShape.BindPoint.TopCenter));

        Rectangle.Double Corner = new Rectangle.Double(0, 0, 10, 10);
        addCornerShape(new RectangleShape(Corner, this, ItemShape.BindPoint.TopLeft));

        Corner = new Rectangle.Double(-10, 0, 10, 10);
        addCornerShape(new RectangleShape(Corner, this, ItemShape.BindPoint.TopRight));

        Corner = new Rectangle.Double(0, -10, 10, 10);
        addCornerShape(new RectangleShape(Corner, this, ItemShape.BindPoint.BottomLeft));

        Corner = new Rectangle.Double(-10, -10, 10, 10);
        addCornerShape(new RectangleShape(Corner, this, ItemShape.BindPoint.BottomRight));

        Rectangle.Double Ex = new Rectangle.Double(
                -5,
                0,
                10, 0.1);
        addEditShape(new RectangleShape(Ex, this, ItemShape.BindPoint.Center));

        Ex = new Rectangle.Double(
                0,
                -5,
                0.1, 10);
        addEditShape(new RectangleShape(Ex, this, ItemShape.BindPoint.Center));

    }

    public void setPosition(Point2D.Double pos) {
        Point2D.Double oldPos = new Point2D.Double(getPath().getBounds2D().getX(), getPath().getBounds2D().getY());
        updatePosition(pos, oldPos);

        transformItem(new Point2D.Double(pos.x - oldPos.x, pos.y - oldPos.y));
    }

    public void setDeltaPosition(Point2D.Double pos) {
        updatePosition(pos, new Point2D.Double(0, 0));

        transformItem(pos);

    }

    public void setDimension(int w, int h) {
        dimension.width = w;
        dimension.height = h;
        transformItem(position);
    }

    public void setDimension(Dimension dim) {
        dimension.width = dim.width;
        dimension.height = dim.height;
        transformItem(position);
    }

    public void setDeltaDimension(int point, Point2D.Double p) {

        Point2D.Double tPos = new Point2D.Double(originalPoints[0].x, originalPoints[0].y);

        if (!childItems.isEmpty()) {
            for (Item i : childItems) {
                for (Point2D.Double po : i.transformedPoints) {
                    if (transformedPoints[point].x + p.x + 10 == po.x || transformedPoints[point].x + p.x - 10 == po.x || transformedPoints[point].y + p.y - 10 == po.y || transformedPoints[point].y + p.y + 10 == po.y) {
                        return;
                    }
                }
            }
        }

        switch (point) {
            case 0:
                dimension.width -= p.x;
                dimension.height -= p.y;
                tPos.x += p.x;
                tPos.y += p.y;

                break;
            case 1:
                dimension.width += p.x;
                dimension.height -= p.y;
                tPos.y += p.y;
                break;
            case 2:
                dimension.width += p.x;
                dimension.height += p.y;
                break;
            case 3:
                dimension.width -= p.x;
                dimension.height += p.y;
                tPos.x += p.x;

                break;
        }
        for (int i = 0; i < 4; i++) {
            if (i != point) {
                if (transformedPoints[point].x == transformedPoints[i].x) {
                    transformedPoints[i].x += p.x;
                    transformedPoints[point].x += p.x;
                    originalPoints[i].x += p.x;
                    originalPoints[point].x += p.x;
                } else if (transformedPoints[point].y == transformedPoints[i].y) {
                    transformedPoints[i].y += p.y;
                    transformedPoints[point].y += p.y;
                    originalPoints[i].y += p.y;
                    originalPoints[point].y += p.y;
                }
            }
        }

        mainShape.setDimension(new Dimension2D.Double(dimension.width, dimension.height));
        mainShape.updateShape();
        setItems();

    }

    public final void setParentItem(Item t) {
        if (parentItem != null) {
            parentItem.childItems.remove(this);
        }
        parentItem = t;
        parentItem.childItems.add(this);
    }

    public void setRotationDegrees(double deg) {
        setRotationRadians(Math.toRadians(deg));
    }

    public final void setState(Item item) {
        transformedPoints = item.transformedPoints;
        originalPoints = item.originalPoints;
        position = item.position;
        dimension = item.dimension;
        rotation = item.rotation;

        mainShape.updateShape();
        setItems();
    }

    public void setRotationRadians(double deg) {
        rotation = deg;
        transformPoints(originalPoints, rotation, transformedPoints);
        position.x = transformedPoints[0].x;
        position.y = transformedPoints[0].y;
        if (rotation == 0) {
            canResize = true;
        } else {
            canResize = false;
        }
    }

    public void setCustomRotation(double rads, Point2D.Double center) {
        rotation = rads;
        transformPoints(originalPoints, rotation, center, transformedPoints);
        position.x = transformedPoints[0].x;
        position.y = transformedPoints[0].y;
        if (rotation == 0) {
            canResize = true;
        } else {
            canResize = false;
        }
    }

    public final void setSelected(boolean selected) {
        isSelected = selected;
    }

    public final boolean isRotetable() {
        return isRotateable;
    }

    public final boolean isResizeable() {
        return isResizeable;
    }

    public final boolean isSelected() {
        return isSelected;
    }

    public final boolean canResize() {
        return canResize;
    }

    public final View getCanvas() {
        return view;
    }

    public final String getCanvasId() {
        return canvasId;
    }

    public final Dimension2D.Double getDimensions() {
        return new Dimension2D.Double((double) dimension.width / view.getGrid().getPixelsPerMeter(), (double) dimension.height / view.getGrid().getPixelsPerMeter());
    }

    public final double getSquareMeters() {
        return (double) dimension.width / view.getGrid().getPixelsPerMeter() * (double) dimension.height / view.getGrid().getPixelsPerMeter();
    }

    public final Point2D.Double getPosition() {
        return position;
    }

    public final Dimension getDimension() {
        return dimension;
    }

    public final String getID() {
        return id;
    }

    public final ItemShape getMainShape() {
        return mainShape;
    }

    public final String getMainShapeId() {
        return mainShapeId;
    }

    public final String getName() {
        return name;
    }

    public final double getRotationRadians() {
        return rotation;
    }

    public final double getRotationDegrees() {
        return Math.toDegrees(rotation);
    }

    public final Item getParentItem() {
        return parentItem;
    }

    public final String getParentItemId() {
        return parentItemId;
    }

    public final ItemCollection getChildItems() {
        return childItems;
    }

    public final ArrayList<String> getChildItemIds() {
        return childItemIds;
    }

    public final ArrayList<ItemShape> getEditShapes() {
        return editShapes;
    }

    public final ArrayList<String> getEditShapeIds() {
        return editShapeIds;
    }

    public final ArrayList<ItemShape> getInnerShapes() {
        return innerShapes;
    }

    public final ArrayList<String> getInnerShapeIds() {
        return innerShapeIds;
    }

    public final ArrayList<ItemShape> getCornerShapes() {
        return cornerShapes;
    }

    public final ArrayList<String> getCornerShapeIds() {
        return cornerShapeIds;
    }

    public final Point2D.Double[] getTransformedPoints() {
        return transformedPoints;
    }

    public final ArrayList<Point2D.Double> getPoints() {
        return points;
    }

    public final Point2D.Double[] getOriginalPoints() {
        return originalPoints;
    }

    public final Path2D.Double getFillPath() {
        Path2D.Double path = new Path2D.Double();
        AffineTransform transform = new AffineTransform();
        path.append(mainShape.getShape(), false);
        transform.setTransform(
                AffineTransform.getRotateInstance(
                        rotation,
                        mainShape.getShape().getBounds2D().getCenterX(),
                        mainShape.getShape().getBounds2D().getCenterY()
                )
        );
        path.transform(transform);
        return path;
    }

    public final Path2D.Double getPath() {
        Path2D.Double path = new Path2D.Double();
        AffineTransform transform = new AffineTransform();

        if (isSelected) {
            for (ItemShape s : cornerShapes) {
                if (isResizeable && canResize) {
                    path.append(s.getShape(), false);
                }
            }
            for (ItemShape s : editShapes) {
                if (isRotateable) {
                    path.append(s.getShape(), false);
                }
            }
        }
        path.append(mainShape.getShape(), false);
        for (ItemShape s : innerShapes) {
            path.append(s.getShape(), false);
        }

        transform.setTransform(
                AffineTransform.getRotateInstance(
                        rotation,
                        mainShape.getShape().getBounds2D().getCenterX(),
                        mainShape.getShape().getBounds2D().getCenterY()
                )
        );
        path.transform(transform);
        return path;
    }

    public boolean mustBeMounted() {
        return mustBeMounted;
    }

    public boolean canContainItems() {
        return canContainItems;
    }

    @Override
    public boolean equals(Object ob) {
        return ob != null ? ((Item) ob).id.equals(id) : false;
    }

    private void transformPoints(Point2D.Double[] origPoints, double angle, Point2D.Double[] storeTo) {
        double centerX = getPath().getBounds2D().getCenterX();
        double centerY = getPath().getBounds2D().getCenterY();
        transformPoints(origPoints, angle, new Point2D.Double(centerX, centerY), storeTo);

//AffineTransform.getRotateInstance(angle, centerX, centerY).transform(origPoints, 0, storeTo, 0, 4);

        /*PathIterator it = mainShape.getShape().getPathIterator(AffineTransform.getRotateInstance(angle, centerX, centerY));

        FlatteningPathIterator iter = new FlatteningPathIterator(it, 0.05);

        int index = 1;
        double[] cords = new double[10];
        do {
            iter.currentSegment(cords);
            double x = cords[0];
            double y = cords[1];
            System.out.println("Point " + index + " : Point(" + x + "," + y + ")");
            index++;
            iter.next();

        } while (!iter.isDone());*/
    }

    private void transformPoints(Point2D.Double[] origPoints, double angle, Point2D.Double center, Point2D.Double[] storeTo) {
        AffineTransform.getRotateInstance(angle, center.x, center.y).
                transform(origPoints, 0, storeTo, 0, 4);
    }

    private void updatePosition(Point2D.Double pos, Point2D.Double oldPos) {
        position.x += (pos.x - oldPos.x);
        position.y += (pos.y - oldPos.y);

        for (Point2D.Double p : transformedPoints) {
            p.x += (pos.x - oldPos.x);
            p.y += (pos.y - oldPos.y);
        }
        for (Point2D.Double p : originalPoints) {
            p.x += (pos.x - oldPos.x);
            p.y += (pos.y - oldPos.y);
        }

    }

    private void transformItem(Point2D.Double pos) {

        //mainRect.setFrame(mainRect.x + (pos.x), mainRect.y + (pos.y), dimension.width, dimension.height);
        transformPoints(this.transformedPoints, 0, this.transformedPoints);
        for (Item t : childItems) {
            t.setDeltaPosition(new Point2D.Double(pos.x, pos.y));
            t.setRotationRadians(rotation);
            t.mainShape.updateShape();
            t.setItems();
        }
        mainShape.updateShape();
        setItems();

    }

    @Override
    public abstract Item clone();

    public boolean compare(Item obj) {
        if (obj != null) {
            return obj.id.equals(id);
        }
        return false;
    }

    public final void updateTransformedPoints(Point2D.Double[] points) {
        int index = 0;
        for (Point2D.Double p : points) {
            transformedPoints[index].x = p.x;
            transformedPoints[index].y = p.y;
            index++;
        }
    }

    public final void updateOriginalPoints(Point2D.Double[] points) {
        int index = 0;
        for (Point2D.Double p : points) {
            originalPoints[index].x = p.x;
            originalPoints[index].y = p.y;
            index++;
        }
    }

    public boolean isIntersecting(Item checkItem, Point2D.Double point) {
        for (int x = 0; x < 2; x++) {
            Item item = (x == 0) ? this : checkItem;

            for (int i1 = 0; i1 < item.getTransformedPoints().length; i1++) {
                int i2 = (i1 + 1) % item.getTransformedPoints().length;
                Point2D.Double p1 = item.getTransformedPoints()[i1];
                Point2D.Double p2 = item.getTransformedPoints()[i2];

                Point2D.Double normal = new Point2D.Double(p2.y - p1.y, p1.x - p2.x);

                double minA = Double.POSITIVE_INFINITY;
                double maxA = Double.NEGATIVE_INFINITY;

                for (Point2D.Double p : getTransformedPoints()) {
                    double projected = normal.x * p.x + normal.y * p.y;

                    if (projected < minA) {
                        minA = projected;
                    }
                    if (projected > maxA) {
                        maxA = projected;
                    }
                }

                double minB = Double.POSITIVE_INFINITY;
                double maxB = Double.NEGATIVE_INFINITY;

                for (Point2D.Double p : checkItem.getTransformedPoints()) {
                    Point2D.Double variation;
                    if (point.x == 0 && point.y == 0) {
                        variation = new Point2D.Double(0, 0);
                    } else {
                        variation = new Point2D.Double(point.x, point.y);
                    }

                    double projected = normal.x * (p.x + variation.x) + normal.y * (p.y + variation.y);

                    if (projected < minB) {
                        minB = projected;
                    }
                    if (projected > maxB) {
                        maxB = projected;
                    }
                }

                if (maxA <= minB || maxB <= minA) {
                    return false;
                }
            }
        }
        return true;
    }

    public boolean isIntersecting() {
        for (Item t : view.getItems()) {
            if (t != this
                    && t.original != this
                    && t != parentItem
                    && !childItems.contains(t)
                    && original != t
                    && !view.getSelectedItems().contains(t)) {
                if (t.isIntersecting(this, new Point2D.Double(0, 0))) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isIntersecting(Point2D.Double point) {
        for (Item t : view.getItems()) {
            if (t != this
                    && t.original != this
                    && t != parentItem
                    && !childItems.contains(t)
                    && original != t
                    && !view.getSelectedItems().contains(t)) {
                if (t.isIntersecting(this, point)) {
                    return true;
                }
            }
        }
        return false;
    }

    public boolean isNameVisible() {
        return isNameVisible;
    }

}
