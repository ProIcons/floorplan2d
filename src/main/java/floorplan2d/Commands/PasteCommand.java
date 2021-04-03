package floorplan2d.Commands;

import floorplan2d.ViewItems.Item;
import floorplan2d.Collections.ItemCollection;
import floorplan2d.View;
import floorplan2d.Interfaces.ICommand;

/**
 *
 * @author Chrysa
 */
public final class PasteCommand implements ICommand, java.io.Serializable {

    ItemCollection items;
    View view;

    public PasteCommand(View _view) {
        items = _view.getCopiedItems();
        view = _view;
        doit();
    }

    @Override
    public void doit() {
        
        if (view.isItemOnCanvas(items.get(0))) {
            items = items.clone(true);
            for (Item item : items) {
                item.setRandomId();
            }
            view.setCopiedItems(items);
        }
        view.setHandItems(items);
       view.undoStack.push(this);
    }

    @Override
    public void undo() {
        view.removeItems(items);
        view.redoStack.push(this);
    }

    @Override
    public void redo() {
        doit();
    }

}
