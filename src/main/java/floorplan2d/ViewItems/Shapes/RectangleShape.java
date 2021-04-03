package floorplan2d.ViewItems.Shapes;

import floorplan2d.ViewItems.Item;
import floorplan2d.ExtendedInternalClasses.Dimension2D;

import java.awt.Shape;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 *
 * @author Tasos
 */
public class RectangleShape extends ItemShape {

    private Rectangle2D.Double originalShape;
    private Rectangle2D.Double translatedShape;
    
    public RectangleShape(Rectangle2D.Double shape, Item bItem, BindPoint bp) {
        this(new Dimension2D.Double(shape.getWidth(), shape.getHeight()),new Point2D.Double(shape.x, shape.y),bItem,bp);
    }

    public RectangleShape(Dimension2D.Double dim, Point2D.Double pos, Item bItem, BindPoint bp) {
        super(dim, pos, bItem, bp);
        originalShape = new Rectangle2D.Double(pos.x, pos.y, dim.width, dim.height);
        setShapeType(Type.Rectangle);
        updateShape();
    }

    @Override
    public void updateShape() {
        double x1, y1, width, height;

        width = getDimension().width;
        height = getDimension().height;
        x1 = originalShape.x;
        y1 = originalShape.y;
        switch (getBindPoint()) {
            case TopLeft:
                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y;
                break;
            case TopRight:
                x1 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y1 += getBindItem().getOriginalPoints()[0].y;
                break;
            case TopCenter:
                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getOriginalPoints()[0].y;
                break;
            case BottomLeft:
                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;
                break;
            case BottomRight:
                x1 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;
                break;
            case BottomCenter:
                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;
                break;
            case Center:
                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getFillPath().getBounds2D().getCenterY();
                break;
            default:
                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y;
        }
        translatedShape = new Rectangle2D.Double(x1, y1, width, height);
        setDimension(new Dimension2D.Double(translatedShape.getBounds2D().getWidth(), translatedShape.getBounds2D().getHeight()));
        setActualPosition(new Point2D.Double(translatedShape.getBounds2D().getX(), translatedShape.getBounds2D().getY()));
    }

    @Override
    public Shape getShape() {
        return translatedShape;
    }
}
