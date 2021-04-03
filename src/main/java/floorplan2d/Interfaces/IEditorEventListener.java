package floorplan2d.Interfaces;

import floorplan2d.View;

/**
 *
 * @author Chrysa
 */
public interface IEditorEventListener {
    public void undoCommand();
    public void redoCommand();
    public void copyCommand();
    public void cutCommand();
    public void pasteCommand();
    public void newProjectCommand();
    public void saveProjectCommand();
    public void closeProjectCommand();
    public void openProjectCommand();
    public void newViewCommand();
    public void closeViewCommand();
    public void deleteViewCommand();
    public void deleteCommand();
    public void clearCommand();
    public void exitCommand();
    public void selectAllCommand();
    public void invertSelectionCommand();
    public void deselectCommand();
    public void changeTabCommand(View view);
    public void itemPressedCommand(Class _class);
    public void changeViewCommand(String entryName);
}
