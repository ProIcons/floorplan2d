package floorplan2d.ViewItems;

import floorplan2d.View;

import java.awt.Dimension;

import java.awt.geom.Point2D;

/**
 *
 * @author Nikolas
 */
public abstract class DecorationItem extends Item {

    public DecorationItem(String itemName, Point2D.Double pos, Dimension dim, View c) {
        super(itemName, pos, dim, c);
        isRotateable = true;
        isResizeable = true;
        canResize = true;
        
    }
    
    public abstract DecorationItem clone();
}
