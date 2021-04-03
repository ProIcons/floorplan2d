package floorplan2d.Commands;

import floorplan2d.ViewItems.Item;
import floorplan2d.Collections.ItemCollection;
import floorplan2d.Interfaces.ICommand;
import floorplan2d.View;

/**
 *
 * @author Chrysa
 */
public final class TransformCommand implements ICommand, java.io.Serializable {

    ItemCollection startItems, finalItems, originalItems;
    View view;

    public TransformCommand(View _view, ItemCollection _originalItems, ItemCollection _finalItems) {
        startItems = _originalItems;
        finalItems = _finalItems.clone(true);
        originalItems = _finalItems.clone(false);
        view = _view;
        doit();
    }

    @Override
    public void doit() {
        view.undoStack.push(this);
    }

    @Override
    public void undo() {
        for (Item oItem : originalItems) {
            for (Item sItem : startItems) {
                if (oItem.compare(sItem)) {
                    oItem.setState(sItem);
                }
            }
        }

        view.getCanvas().repaint();
        view.redoStack.push(this);
    }

    @Override
    public void redo() {
        for (Item oItem : originalItems) {
            for (Item fItem : finalItems) {
                if (oItem.compare(fItem)) {
                    oItem.setState(fItem);
                }
            }
        }
        view.getCanvas().repaint();
        doit();
    }

}
