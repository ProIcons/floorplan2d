package floorplan2d.ViewItems.Items;

import floorplan2d.View;
import floorplan2d.ViewItems.*;

import java.awt.*;
import java.awt.geom.*;

/**
 *
 * @author Nikolas
 */
public class Door extends MountItem {

    private static String name = "Door";

    public static String getItemName() {
        return name;
    }

    public Door(View c) throws Exception {
        super("Door", new Point2D.Double(0, 0), new Dimension(50, 20), c);
    }

    public Door(Item mount, Point2D.Double pos, View c) {
        super("Door", pos, new Dimension(50, 20), mount, c);

    }

    @Override
    public Door clone() {
        try {
            Door temp = new Door(parentItem, new Point2D.Double(position.x, position.y), view);

            for (int i = 0; i < 4; i++) {
                temp.transformedPoints[i].x = transformedPoints[i].x;
                temp.transformedPoints[i].y = transformedPoints[i].y;
                temp.originalPoints[i].x = originalPoints[i].x;
                temp.originalPoints[i].y = originalPoints[i].y;
            }
            temp.rotation = rotation;
            temp.setItems();
            return temp;
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }

    }

    @Override
    protected void setItems() {
        super.setItems();

    }
}
