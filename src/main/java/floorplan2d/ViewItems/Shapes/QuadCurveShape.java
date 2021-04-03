package floorplan2d.ViewItems.Shapes;

import floorplan2d.ViewItems.Item;
import floorplan2d.ExtendedInternalClasses.Dimension2D;

import java.awt.Shape;

import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;

/**
 *
 * @author Tasos
 */
public class QuadCurveShape extends ItemShape {

    private QuadCurve2D.Double originalShape;
    private QuadCurve2D.Double translatedShape;

    public QuadCurveShape(QuadCurve2D.Double shape, Item bItem, BindPoint bp) {
        this(new Point2D.Double(shape.x1, shape.x2), new Point2D.Double(shape.ctrlx, shape.ctrly), new Point2D.Double(shape.x2, shape.y2), bItem, bp);
        
    }

    public QuadCurveShape(Point2D.Double pos1, Point2D.Double cpos, Point2D.Double pos2, Item bItem, BindPoint bp) {
        super(new Dimension2D.Double(pos2.x - pos1.x, pos2.y - pos1.y), pos1, bItem, bp);
        originalShape = new QuadCurve2D.Double(pos1.x, pos1.y, cpos.x, cpos.y, pos2.x, pos2.y);
        setShapeType(Type.QuadCurve);
        updateShape();
    }

    @Override
    public void updateShape() {
        double x1, x2, y1, y2, cx, cy;

        x1 = originalShape.x1;
        y1 = originalShape.y1;
        x2 = originalShape.x2;
        y2 = originalShape.y2;
        cx = originalShape.ctrlx;
        cy = originalShape.ctrlx;

        switch (getBindPoint()) {
            case TopLeft:
                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y;

                x2 += getBindItem().getOriginalPoints()[0].x;
                y2 += getBindItem().getOriginalPoints()[0].y;

                cx += getBindItem().getOriginalPoints()[0].x;
                cy += getBindItem().getOriginalPoints()[0].y;
                break;
            case TopRight:
                x1 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y1 += getBindItem().getOriginalPoints()[0].y;

                x2 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y2 += getBindItem().getOriginalPoints()[0].y;

                cx += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                cy += getBindItem().getOriginalPoints()[0].y;
                break;
            case TopCenter:
                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getOriginalPoints()[0].y;

                x2 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y2 += getBindItem().getOriginalPoints()[0].y;

                cx += getBindItem().getFillPath().getBounds2D().getCenterX();
                cy += getBindItem().getOriginalPoints()[0].y;
                break;
            case BottomLeft:
                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                x2 += getBindItem().getOriginalPoints()[0].x;
                y2 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                cx += getBindItem().getOriginalPoints()[0].x;
                cy += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;
                break;
            case BottomRight:
                x1 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                x2 += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                y2 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                cx += getBindItem().getOriginalPoints()[0].x + getBindItem().getDimension().width;
                cy += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;
                break;
            case BottomCenter:
                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                x2 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y2 += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;

                cx += getBindItem().getFillPath().getBounds2D().getCenterX();
                cy += getBindItem().getOriginalPoints()[0].y + getBindItem().getDimension().height;
                break;
            case Center:
                x1 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y1 += getBindItem().getFillPath().getBounds2D().getCenterY();

                x2 += getBindItem().getFillPath().getBounds2D().getCenterX();
                y2 += getBindItem().getFillPath().getBounds2D().getCenterY();

                cx += getBindItem().getFillPath().getBounds2D().getCenterX();
                cy += getBindItem().getFillPath().getBounds2D().getCenterY();
                break;
            default:
                x1 += getBindItem().getOriginalPoints()[0].x;
                y1 += getBindItem().getOriginalPoints()[0].y;

                x2 += getBindItem().getOriginalPoints()[0].x;
                y2 += getBindItem().getOriginalPoints()[0].y;

                cx += getBindItem().getOriginalPoints()[0].x;
                cy += getBindItem().getOriginalPoints()[0].y;
        }

        translatedShape = new QuadCurve2D.Double(x1, y1, cx, cy, x2, y2);
        setDimension(new Dimension2D.Double(translatedShape.getBounds2D().getWidth(), translatedShape.getBounds2D().getHeight()));
        setActualPosition(new Point2D.Double(translatedShape.getBounds2D().getX(), translatedShape.getBounds2D().getY()));
    }

    @Override
    public Shape getShape() {
        return translatedShape;
    }
}
