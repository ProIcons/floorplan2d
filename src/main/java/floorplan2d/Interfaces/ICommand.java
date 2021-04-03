package floorplan2d.Interfaces;

/**
 *
 * @author Chrysa
 */
public interface ICommand {
    void doit();
    void undo();
    void redo();
}
