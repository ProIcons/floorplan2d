/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package floorplan2d.ViewItems.Items;

import floorplan2d.View;
import floorplan2d.ViewItems.DecorationItem;
import floorplan2d.ViewItems.Item;
import floorplan2d.ViewItems.Shapes.ItemShape;
import floorplan2d.ViewItems.Shapes.RoundedRectangleShape;
import java.awt.Dimension;
import java.awt.geom.Point2D;
import java.awt.geom.RoundRectangle2D;

/**
 *
 * @author Nikolas
 */
public class Bed extends DecorationItem implements java.io.Serializable {

    private static String name = "2 Person Bed";
    public static String getItemName() {
        return name;
    }
    public Bed(View c) {
        this(new Point2D.Double(0,0),c);
    }
    public Bed(Point2D.Double pos, View c) {
        super("2PersonBed", pos, new Dimension(90, 100), c);
        isResizeable = false;
        typeName = "2PersonBed";
    }

    @Override
    protected void setItems() {
        super.setItems();
        RoundRectangle2D.Double rRect;
        //Main
        rRect = new RoundRectangle2D.Double(5, 30, dimension.width-10, dimension.height-35, 10, 10);
        innerShapes.add(new RoundedRectangleShape(rRect, this, ItemShape.BindPoint.TopLeft));
        
        //Pillows
        rRect = new RoundRectangle2D.Double(3, 3, 40, 24, 5, 5);
        innerShapes.add(new RoundedRectangleShape(rRect, this, ItemShape.BindPoint.TopLeft));
        rRect = new RoundRectangle2D.Double(47, 3, 40, 24, 5, 5);
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
        Bed temp = new Bed(new Point2D.Double(position.x, position.y), view);

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
