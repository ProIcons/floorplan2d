package floorplan2d.ViewItems.Shapes;

import floorplan2d.ViewItems.Item;
import floorplan2d.ExtendedInternalClasses.Dimension2D;

import java.awt.Shape;

import java.awt.geom.Line2D;
import java.awt.geom.Point2D;

/**
 *
 * @author Tasos
 */
public class LineShape extends ItemShape {

    Line2D.Double originalShape, translatedShape;
    
    public LineShape(Line2D.Double shape, Item b, BindPoint bp) {
        this(new Point2D.Double(shape.x1, shape.y1),new Point2D.Double(shape.x2, shape.y2),b,bp);
    }

    public LineShape(Point2D.Double pos1, Point2D.Double pos2, Item b, BindPoint bp) {
        super(new Dimension2D.Double(pos2.x - pos1.x, pos2.y - pos1.y), pos1, b, bp);
        originalShape = new Line2D.Double(pos1, pos2);
        setShapeType(Type.Line);
        updateShape();
    }

    public void updateShape() {
        double x1, x2, y1, y2;

        x1 = originalShape.x1;
        y1 = originalShape.y1;
        x2 = originalShape.x2;
        y2 = originalShape.y2;

        switch (getBindPoint()) {
            case TopLeft:

                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y;

                x2 += getBindItem().getOriginalPoints()[0].x;
                y2 += getBindItem().getOriginalPoints()[0].y;

                break;
            case TopRight:

                x1 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y1 += getBindItem().getOriginalPoints()[0].y;

                x2 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y2 += getBindItem().getOriginalPoints()[0].y;

                break;
            case TopCenter:

                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getOriginalPoints()[0].y;

                x2 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y2 += getBindItem().getOriginalPoints()[0].y;

                break;
            case BottomLeft:

                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                x2 += getBindItem().getOriginalPoints()[0].x;
                y2 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                break;
            case BottomRight:

                x1 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                x2 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y2 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                break;
            case BottomCenter:

                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                x2 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y2 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                break;
            case Center:

                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getFillPath().getBounds2D().getCenterY();

                x2 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y2 += getBindItem().getFillPath().getBounds2D().getCenterY();

                break;
            default:
                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y;

                x2 += getBindItem().getOriginalPoints()[0].x;
                y2 += getBindItem().getOriginalPoints()[0].y;
        }
        if (x2 > x1 + getBindItem().getDimension().width) {
            x2 = x1 + getBindItem().getDimension().width;
        }
        translatedShape = new Line2D.Double(x1, y1, x2, y2);
        setDimension(new Dimension2D.Double(translatedShape.getBounds2D().getWidth(), translatedShape.getBounds2D().getHeight()));
        setActualPosition(new Point2D.Double(translatedShape.getBounds2D().getX(), translatedShape.getBounds2D().getY()));
    }

    @Override
    public Shape getShape() {
        return translatedShape;
    }
}
