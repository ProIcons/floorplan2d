package floorplan2d.ViewItems.Shapes;

import floorplan2d.ViewItems.Item;
import floorplan2d.ExtendedInternalClasses.Dimension2D;

import java.awt.Shape;
import java.awt.geom.Point2D;

import java.math.BigInteger;

import java.security.SecureRandom;

/**
 *
 * @author Tasos
 */
public abstract class ItemShape implements java.io.Serializable {

    public enum BindPoint {
        TopLeft,
        TopRight,
        TopCenter,
        BottomLeft,
        BottomRight,
        BottomCenter,
        Center,
        Free
    }

    public enum Type {
        Rectangle,
        RoundRectangle,
        Line,
        QuadCurve,
        CubicCurve,
        Arc,
        Ellipse

    }
    Dimension2D.Double dimension;
    Point2D.Double relativePosition;
    Point2D.Double actualPosition;
    BindPoint bindPoint;
    Item bindItem;
    private String bindItemId;
    private String id;
    Type shapeType;
    private SecureRandom random = new SecureRandom();

    /**
     *
     * @param s the new Shape for ItemShape
     * @param b the Shape that the ItemShape will bind into.
     * @param bp the Bind Point of the bind Shape.
     */
    public ItemShape(Dimension2D.Double dim, Point2D.Double pos, Item b, BindPoint bp) {

        id = new BigInteger(130, random).toString(32);
        relativePosition = pos;
        bindItem = b;
        bindItemId = b.getID();
        bindPoint = bp;
        dimension = dim;

    }

    public String getId() {
        return id;
    }

    public String getBindItemId() {
        return bindItemId;
    }

    public abstract void updateShape();

    /**
     * Update the ItemShape according to bound item transition.
     *
     * @throws Exception if ShapeType is CubicCurve since is not implemented
     * yet.
     */
    /*
  

    /**
     * It's highly NOT RECOMMANDED to use this function. This function changes
     * the Bind Point of the ItemsShape without changing its Orientation.
     *
     * This must be followed after setShape(Shape s);
     *
     * @see ItemShape#setShape(java.awt.Shape) setShape(Shape);
     * @param bp new BindPoint
     */
    public void setBindPoint(BindPoint bp) {
        bindPoint = bp;
        updateShape();
    }

    public void setBindItem(Item bi) {
        bindItem = bi;
        bindItemId = bi.getID();
        updateShape();
    }

    public void setShapeType(Type s) {
        shapeType = s;
    }

    public void setDimension(Dimension2D.Double d) {
        dimension = d;
    }

    public void setActualPosition(Point2D.Double p) {
        actualPosition = p;
    }

    public void setRelativePosition(Point2D.Double p) {
        relativePosition = p;
    }

    /**
     *
     * @return the Shape Type
     */

    public Type getShapeType() {
        return shapeType;
    }

    /**
     *
     * @return the ItemShape's Shape.
     */
    public abstract Shape getShape();

    /**
     *
     * @return the bind Point of the ItemShape.
     */
    public BindPoint getBindPoint() {
        return bindPoint;
    }

    public Item getBindItem() {
        return bindItem;
    }

    /**
     *
     * @return a Point2D.Double with the Actual Position of the ItemShape, on
     * canvas.
     */
    public Point2D.Double getActualPosition() {
        return actualPosition;
    }

    /**
     *
     * @return a Point2D.Double with the Relative Position of the ItemShape, on
     * bind item.
     */
    public Point2D.Double getRelativePosition() {
        return relativePosition;
    }

    /**
     *
     * @return a Dimension2D.Double with the ItemShape's Dimensions.
     */
    public Dimension2D.Double getDimension() {
        return dimension;
    }

}
