package floorplan2d.ViewItems.Shapes;

import floorplan2d.ViewItems.Item;
import floorplan2d.ExtendedInternalClasses.Dimension2D;

import java.awt.Shape;

import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author Tasos
 */
public class RoundedRectangleShape extends ItemShape {

    private RoundRectangle2D.Double originalShape;
    private RoundRectangle2D.Double translatedShape;
    
    public RoundedRectangleShape(RoundRectangle2D.Double shape, Item bItem, BindPoint bp) {
        this(new Dimension2D.Double(shape.arcwidth, shape.archeight),new Dimension2D.Double(shape.width, shape.height),new Point2D.Double(shape.x, shape.y),bItem,bp);
    }

    public RoundedRectangleShape(Dimension2D.Double arcDim, Dimension2D.Double dim, Point2D.Double pos, Item bItem, BindPoint bp) {
        super(dim, pos, bItem, bp);
        originalShape = new RoundRectangle2D.Double(pos.x, pos.y, dim.width, dim.height, arcDim.width, arcDim.height);
        setShapeType(Type.RoundRectangle);
        updateShape();
    }

    @Override
    public void updateShape() {
        double x1, y1, width, height, arcw, arch;

        width = originalShape.width;
        height = originalShape.height;
        x1 = originalShape.x;
        y1 = originalShape.y;
        arcw = originalShape.arcwidth;
        arch = originalShape.archeight;
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
        translatedShape = new RoundRectangle2D.Double(x1, y1, width, height, arcw, arch);
        setDimension(new Dimension2D.Double(translatedShape.getBounds2D().getWidth(), translatedShape.getBounds2D().getHeight()));
        setActualPosition(new Point2D.Double(translatedShape.getBounds2D().getX(), translatedShape.getBounds2D().getY()));
    }

    @Override
    public Shape getShape() {
        return translatedShape;
    }
}
