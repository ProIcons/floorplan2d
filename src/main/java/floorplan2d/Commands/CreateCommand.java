package floorplan2d.Commands;

import floorplan2d.ViewItems.Item;
import floorplan2d.Interfaces.ICommand;
import floorplan2d.View;

/**
 *
 * @author Chrysa
 */
public class CreateCommand implements ICommand, java.io.Serializable {
    
    private View view;
    private Item item;
    
    public CreateCommand (View _view){
        view = _view;
        item = view.getHandItems().get(0);
        doit();
    }

    @Override
    public void doit() {
        view.undoStack.push(this);
        view.addItem(item);
    }

    @Override
    public void undo() {
        view.redoStack.push(this);
        view.removeItem(item);
    }

    @Override
    public void redo() {
        doit();
    }
    
}
