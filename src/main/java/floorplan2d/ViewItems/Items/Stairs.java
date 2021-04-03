package floorplan2d.ViewItems.Items;

import floorplan2d.View;
import floorplan2d.ViewItems.*;
import floorplan2d.ViewItems.Shapes.*;

import java.awt.*;
import java.awt.geom.*;

/**
 *
 * @author Nikolas
 */
public class Stairs extends Item {

    private static String name = "Stairs";

    public static String getItemName() {
        return name;
    }

    public Stairs(View c) {
        super("Stairs", new Point2D.Double(0, 0), new Dimension(100, 100), c);
        isResizeable = false;
        isRotateable = false;
    }

    public Stairs(Point2D.Double pos, Dimension dim, View c) {
        super("Stairs", pos, dim, c);
        isResizeable = false;
        isRotateable = false;
    }

    @Override
    public void setMainShape() {
        Ellipse2D.Double MainRectangle = new Ellipse2D.Double(0, 0, dimension.width, dimension.height);

        transformedPoints[0] = new Point2D.Double(position.x, position.y);
        transformedPoints[1] = new Point2D.Double(position.x + MainRectangle.width, position.y);
        transformedPoints[2] = new Point2D.Double(position.x + MainRectangle.width, position.y + MainRectangle.height);
        transformedPoints[3] = new Point2D.Double(position.x, position.y + MainRectangle.height);

        for (int i = 0; i < 4; i++) {
            originalPoints[i] = new Point2D.Double(transformedPoints[i].x, transformedPoints[i].y);
        }
        ItemShape iShape = new EllipseShape(MainRectangle, this, ItemShape.BindPoint.TopLeft);
        mainShape = iShape;
        mainShapeId = mainShape.getId();
    }

    @Override
    protected void setItems() {
        super.setItems();

        double a = mainShape.getShape().getBounds2D().getWidth() / 2;
        double b = mainShape.getShape().getBounds2D().getHeight() / 2;
        double m = Math.min(a, b);
        double r = 16 * m / 16;
        for (int i = 0; i < 20; i++) {
            double t = 2 * Math.PI * i / 20;
            double x = Math.round(a + r * Math.cos(t));
            double y = Math.round(b + r * Math.sin(t));
            Line2D.Double line = new Line2D.Double(a, b, x, y);
            innerShapes.add(new LineShape(line, this, ItemShape.BindPoint.TopLeft));
            if (i == 10) {
                break;
            }
        }

    }

    @Override
    public Stairs clone() {
        Stairs temp = new Stairs(new Point2D.Double(position.x, position.y), new Dimension(dimension.width, dimension.height), view);

        for (int i = 0; i < 4; i++) {
            temp.transformedPoints[i].x = transformedPoints[i].x;
            temp.transformedPoints[i].y = transformedPoints[i].y;
            temp.originalPoints[i].x = originalPoints[i].x;
            temp.originalPoints[i].y = originalPoints[i].y;
        }
        temp.rotation = rotation;

        for (Item t : childItems) {
            Item t2 = t.clone();
            t2.setParentItem(temp);
            temp.childItems.add(t2);
        }
        temp.parentItem = parentItem;
        setItems();
        temp.id = id;
        return temp;
    }
}
