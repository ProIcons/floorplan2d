package floorplan2d.ViewItems;

import floorplan2d.ExtendedInternalClasses.Dimension2D;
import floorplan2d.View;

import java.awt.Dimension;

import java.awt.geom.Point2D;

/**
 *
 * @author Nikolas
 */
public abstract class ConstructionItem extends Item {

    public ConstructionItem(String itemName, Point2D.Double pos, Dimension dim, View c) {
        super(itemName, pos, dim, c);
        canContainItems = true;
        isResizeable = true;
        isRotateable = true;
        canResize = true;
        isNameVisible = false;

    }
    
    @Override
    public void setDeltaDimension(int point, Point2D.Double p) {
        Point2D.Double tPos = new Point2D.Double(originalPoints[0].x, originalPoints[0].y);

        if (!childItems.isEmpty()) {
            for (Item i : childItems) {
                for (Point2D.Double po : i.transformedPoints) {
                    if (transformedPoints[point].x + p.x + 10 == po.x || transformedPoints[point].x + p.x - 10 == po.x || transformedPoints[point].y + p.y - 10 == po.y || transformedPoints[point].y + p.y + 10 == po.y) {
                        return;
                    }
                }
            }
        }
        switch (point) {
            case 0:
                dimension.width -= p.x;
                tPos.x += p.x;
                break;
            case 1:
                dimension.width += p.x;
                break;
            case 2:
                dimension.width += p.x;
                break;
            case 3:
                dimension.width -= p.x;
                tPos.x += p.x;
                break;
        }
        for (int i = 0; i < 4; i++) {
            if (i != point) {
                if (transformedPoints[point].x == transformedPoints[i].x) {
                    transformedPoints[i].x += p.x;
                    transformedPoints[point].x += p.x;
                    originalPoints[i].x += p.x;
                    originalPoints[point].x += p.x;
                } 
            }
        }
        mainShape.setDimension(new Dimension2D.Double(dimension.width, dimension.height));
        mainShape.updateShape();
        setItems();
    }
    

    public abstract ConstructionItem clone();
}
