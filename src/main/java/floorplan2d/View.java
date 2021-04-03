package floorplan2d;

import floorplan2d.ViewItems.Item;
import floorplan2d.Collections.ItemCollection;
import floorplan2d.Interfaces.*;
import floorplan2d.Commands.*;
import floorplan2d.ExtendedInternalClasses.*;
import floorplan2d.GraphicalComponents.CustomComponents.Canvas;
import floorplan2d.GraphicalComponents.Popups.CanvasPopupMenu;
import floorplan2d.GraphicalComponents.CustomComponents.Grid;

import java.awt.Point;
import java.math.BigInteger;
import java.security.SecureRandom;
import java.util.ArrayList;
import java.util.Stack;

/**
 *
 * @author Nikolas, Tasos, Chrysa
 *
 */
@SuppressWarnings({"OverridableMethodCallInConstructor", "LeakingThisInConstructor", "Convert2Diamond"})
public final class View implements java.io.Serializable {

    // <editor-fold defaultstate="collapsed" desc="Members">   
    private final String id;
    private String name;

    private Point viewPort;
    private double scale;
    private final Grid grid;

    private final ArrayList<String> itemIds;

    private final CanvasPopupMenu popup;
    private final Canvas canvas;

    private final SecureRandom random = new SecureRandom();
    private final ItemCollection items;
    private final ItemCollection selectedItems;
    private ItemCollection copiedItems;
    private ItemCollection handItems;
    public Stack<ICommand> undoStack = new Stack<ICommand>();
    public Stack<ICommand> redoStack = new Stack<ICommand>();
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Constructor">   
    public View(String _name) {
        name = _name;
        id = new BigInteger(130, random).toString(32);

        items = new ItemCollection();
        selectedItems = new ItemCollection();
        handItems = new ItemCollection();
        copiedItems = new ItemCollection();

        itemIds = new ArrayList<String>();

        viewPort = new Point(20, 20);
        scale = 1;
        grid = new Grid(new Dimension2D.Double(10, 10), 20, 50, this);

        canvas = new Canvas(this);
        canvas.setGrid(grid);
        popup = new CanvasPopupMenu(this);
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Commands">      
    public void showPopupMenu(Point p) {
        popup.show(p);
    }

    public void undo() {
        if (!undoStack.isEmpty()) {
            ICommand command = undoStack.pop();
            command.undo();
        }
    }

    public void redo() {
        if (!redoStack.isEmpty()) {
            ICommand command = redoStack.pop();
            command.redo();
        }
    }

    public void copy() {
        if (!selectedItems.isEmpty()) {
            copiedItems.clear();
            for (Item item : selectedItems) {
                copiedItems.add(item.clone());
            }
        }
    }

    public void cut() {
        if (!selectedItems.isEmpty()) {
            copiedItems.clear();
            for (Item item : selectedItems) {
                copiedItems.add(item.clone());
            }
            new CutCommand(this);
        }
    }

    public void paste() {
        if (!copiedItems.isEmpty()) {
            new PasteCommand(this);
        }
    }

    public void delete() {

        if (!selectedItems.isEmpty()) {
            new DeleteCommand(this);
        }

    }

    public void selectAll() {
        clearSelectedItems();
        for (Item item : items) {
            selectedItems.add(item);
            item.setSelected(true);
        }
        canvas.repaint();
    }

    public void deselect() {
        if (!handItems.isEmpty()) {
            handItems.clear();
            canvas.repaint();
        } else {
            clearSelectedItems();
        }

    }

    public void invertSelection() {
        selectedItems.clear();
        for (Item item : items) {
            if (item.isSelected()) {
                item.setSelected(false);
            } else {
                selectedItems.add(item);
                item.setSelected(true);
            }
        }
        canvas.repaint();
    }

    public void clear() {
        if (!items.isEmpty()) {
            new ClearCommand(this);
        }
    }

    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Getters">      
    public double getScale() {
        return scale;
    }

    public final Item getItem(String id) {
        for (Item t : items) {
            if (t.getID().equals(id)) {
                return t;
            }
        }
        return null;
    }

    public final String getName() {
        return name;
    }

    public CanvasPopupMenu getPopup() {
        return popup;
    }

    public ItemCollection getItems() {
        return items;
    }

    public Point getViewPort() {
        return viewPort;
    }

    public String getId() {
        return id;
    }

    public Grid getGrid() {
        return grid;
    }

    public ArrayList<String> getItemIds() {
        return itemIds;
    }

    public ItemCollection getSelectedItems() {
        return selectedItems;
    }

    public ItemCollection getCopiedItems() {
        return copiedItems;
    }

    public Canvas getCanvas() {
        return canvas;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Setters">      
    public void setCopiedItems(ItemCollection items) {
        copiedItems = items;
    }

    public ItemCollection getHandItems() {
        return handItems;
    }

    public void setSelectedItem(Item item) {
        clearSelectedItems();
        selectedItems.add(item);
        item.setSelected(true);
    }

    public void setHandItems(ItemCollection c) {
        handItems = c;
        clearSelectedItems();
    }

    public void setHandItem(Item item) {
        clearHandItems();
        addHandItem(item);
        clearSelectedItems();
    }

    public void setViewPort(Point v) {
        viewPort = v;
        canvas.repaint();
    }

    public void setScale(double newscale) {
        scale = newscale;
        canvas.repaint();
    }
    
    public void setName(String _name){
        name = _name;
    }
    // </editor-fold>

    // <editor-fold defaultstate="collapsed" desc="Various Methods">   
    public final boolean isItemOnCanvas(Item Obj) {
        return items.contains(Obj);
    }

    public final void addItem(Item Obj) {
        items.add(Obj);
        itemIds.add(Obj.getID());
        canvas.repaint();
    }

    public void addItems(ItemCollection Obj) {
        for (Item item : Obj) {
            items.add(item);
        }
        canvas.repaint();
    }

    public void addHandItem(Item item) {
        handItems.add(item);
    }

    public void clearSelectedItems() {
        for (Item item : selectedItems) {
            item.setSelected(false);
        }
        selectedItems.clear();
        canvas.repaint();
    }

    public void clearHandItems() {
        handItems.clear();
    }

    public void removeItem(Item Obj) {
        items.remove(Obj);
        canvas.repaint();
    }

    public void removeItems(ItemCollection Obj) {
        for (Item item : Obj) {
            items.remove(item);
        }
        canvas.repaint();
    }
    // </editor-fold>

}
