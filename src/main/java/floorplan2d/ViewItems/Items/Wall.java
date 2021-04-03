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
public class Wall extends ConstructionItem implements java.io.Serializable {

    private static String name = "Wall";

    public static String getItemName() {
        return name;
    }

    public Wall(View c) {
        super("Wall", new Point2D.Double(0, 0), new Dimension(80, 20), c);
    }

    /**
     *
     * @param pos
     * @param dim
     * @param c
     */
    public Wall(Point2D.Double pos, Dimension dim, View c) {
        super("Wall", pos, dim, c);

    }

    @Override
    protected void setItems() {
        super.setItems();
        double s = 0;
        Line2D.Double a;
        while (s + 10 <= dimension.width) {
            a = new Line2D.Double(s, 0, s + 10, dimension.height);
            innerShapes.add(new LineShape(a, this, ItemShape.BindPoint.TopLeft));
            s += 10;
        }
    }

    @Override
    public Wall clone() {
        Wall temp = new Wall(new Point2D.Double(position.x, position.y), new Dimension(dimension.width, dimension.height), view);

        for (int i = 0; i < 4; i++) {
            temp.transformedPoints[i].x = transformedPoints[i].x;
            temp.transformedPoints[i].y = transformedPoints[i].y;
            temp.originalPoints[i].x = originalPoints[i].x;
            temp.originalPoints[i].y = originalPoints[i].y;
        }
        temp.rotation = rotation;
        for (Item t : childItems) {
            temp.childItems.add(t);
            //Item t2 = t.clone();          
        }
        temp.id = id;
        temp.original = this;
        temp.setItems();
        return temp;
    }
}
