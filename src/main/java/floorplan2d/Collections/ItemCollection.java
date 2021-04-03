package floorplan2d.Collections;

import floorplan2d.ViewItems.Item;

import java.awt.geom.Path2D;

import java.util.ArrayList;

/**
 *
 * @author Chrysa
 */
public class ItemCollection extends ArrayList<Item> implements java.io.Serializable {
    public Path2D.Double getPath() {
        Path2D.Double path = new Path2D.Double();
        for (Item item : this) {
            path.append(item.getFillPath(),false);
        }
        return path;
    }
    
    public ItemCollection clone(boolean recurse) {
        ItemCollection temp = new ItemCollection();    
        for (Item item : this) {
            temp.add((recurse?item.clone():item));
        }
        return temp;
    }
}
