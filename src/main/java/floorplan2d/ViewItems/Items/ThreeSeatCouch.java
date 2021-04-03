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
public class ThreeSeatCouch extends DecorationItem implements java.io.Serializable {
    private static String name = "Three Seat Couch";
    public static String getItemName() {
        return name;
    }
    public ThreeSeatCouch(View c) {
        this(new Point2D.Double(0,0),c);
    }
    public ThreeSeatCouch(Point2D.Double pos, View c) {
        super("Three Seat Couch", pos, new Dimension(150, 70), c);
        isResizeable = false;
        typeName = "ThreeSeatCouch";
    }

    @Override
    protected void setItems() {
        super.setItems();
        RoundRectangle2D.Double rRect;
        //Top
        rRect = new RoundRectangle2D.Double(0, 0, dimension.width, 20, 10, 10);
        innerShapes.add(new RoundedRectangleShape(rRect, this, ItemShape.BindPoint.TopLeft));
        
        //Sides
        rRect = new RoundRectangle2D.Double(0, 20, 15, dimension.height - 20, 10, 10);
        innerShapes.add(new RoundedRectangleShape(rRect, this, ItemShape.BindPoint.TopLeft));
        rRect = new RoundRectangle2D.Double(dimension.width-15, 20, 15, dimension.height - 20, 10, 10);
        innerShapes.add(new RoundedRectangleShape(rRect, this, ItemShape.BindPoint.TopLeft));
        
        //Main
        rRect = new RoundRectangle2D.Double(15, 20, 40, dimension.height - 20, 10, 10);
        innerShapes.add(new RoundedRectangleShape(rRect, this, ItemShape.BindPoint.TopLeft));
        rRect = new RoundRectangle2D.Double(55, 20, 40, dimension.height - 20, 10, 10);
        innerShapes.add(new RoundedRectangleShape(rRect, this, ItemShape.BindPoint.TopLeft));
        rRect = new RoundRectangle2D.Double(95, 20, 40, dimension.height - 20, 10, 10);
        innerShapes.add(new RoundedRectangleShape(rRect, this, ItemShape.BindPoint.TopLeft));
        
    }

    @Override
    public void setMainShape() {
        RoundRectangle2D.Double MainRectangle = new RoundRectangle2D.Double(0, 0, dimension.width, dimension.height,10, 10);

        transformedPoints[0] = new Point2D.Double(position.x, position.y);
        transformedPoints[1] = new Point2D.Double(position.x + MainRectangle.width, position.y);
        transformedPoints[2] = new Point2D.Double(position.x + MainRectangle.width, position.y + MainRectangle.height);
        transformedPoints[3] = new Point2D.Double(position.x, position.y + MainRectangle.height);

        for (int i = 0; i < 4; i++) {
            originalPoints[i] = new Point2D.Double(transformedPoints[i].x, transformedPoints[i].y);
        }
        ItemShape iShape = new RoundedRectangleShape(MainRectangle, this, ItemShape.BindPoint.TopLeft);
        mainShape = iShape;
    }

    @Override
    public DecorationItem clone() {
        ThreeSeatCouch temp = new ThreeSeatCouch(new Point2D.Double(position.x, position.y), view);

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
        temp.original = this;
        setItems();
        temp.id = id;
        return temp;
    }

}
