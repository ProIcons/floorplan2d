package floorplan2d.Commands;

import floorplan2d.Collections.ItemCollection;
import floorplan2d.View;
import floorplan2d.Interfaces.ICommand;

/**
 *
 * @author Chrysa
 */
public final class CutCommand implements ICommand, java.io.Serializable {

    ItemCollection items;
    View view;

    public CutCommand(View _view) {
        view = _view;
        items = _view.getSelectedItems().clone(false);
        doit();
    }
    
    @Override
    public void doit() {
        view.setCopiedItems(items);
        view.clearSelectedItems();
        view.removeItems(items);
        view.undoStack.push(this);
    }
    
    @Override
    public void undo() {
        //canvas.addItem(item);
        view.addItems(items);
        view.redoStack.push(this);
    }
    
    @Override
    public void redo() {
        doit();
    }

}
