package floorplan2d.ViewItems;

import floorplan2d.View;

import java.awt.Dimension;

import java.awt.geom.Point2D;

/**
 *
 * @author Nikolas
 */
public abstract class MountItem extends Item implements java.io.Serializable {

    public MountItem(String itemName, Point2D.Double pos, Dimension dim, View c) throws Exception {
        super(itemName, pos, dim, c);
        for (Item t : c.getItems()) {
            if (t.canContainItems) {
                setParentItem(t);
            }
        }
        if (parentItem != null) {
            mustBeMounted = true;
        } else {
            throw new Exception();
        }
    }

    public MountItem(String itemName, Point2D.Double pos, Dimension dim, Item container, View c) {
        super(itemName, pos, dim, c);
        setParentItem(container);
        mustBeMounted = true;
    }

    @Override
    public boolean isIntersecting(Point2D.Double point) {
        
        Item t = clone();
        t.setDeltaPosition(point);
        if (parentItem.getFillPath().contains(t.getFillPath().getBounds2D())) {
            return false;
        }
        else return true;
        
       
    }

    @Override
    public boolean isIntersecting() {

        return !parentItem.getFillPath().getBounds2D().contains(getFillPath().getBounds2D());
    }

    public abstract MountItem clone();
}
