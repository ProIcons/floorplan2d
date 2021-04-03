package floorplan2d.Commands;

import floorplan2d.Collections.ItemCollection;
import floorplan2d.Interfaces.ICommand;
import floorplan2d.View;

/**
 *
 * @author Chrysa
 */
public final class DeleteCommand implements ICommand, java.io.Serializable {

    ItemCollection items;
    View view;

    public DeleteCommand(View _view) {
        items = _view.getSelectedItems().clone(false);
        view = _view;
        doit();
    }

    @Override
    public void doit() {

        view.removeItems(items);
        view.undoStack.push(this);
        view.clearSelectedItems();
    }

    @Override
    public void undo() {
        view.addItems(items);
        view.redoStack.push(this);
        view.clearSelectedItems();
    }

    @Override
    public void redo() {
        doit();
    }

}
