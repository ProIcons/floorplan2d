package floorplan2d.Interfaces;

import floorplan2d.Project;

/**
 *
 * @author Tasos
 */
public interface IProjectHandlingMethod {
    Project load();
    boolean save();
    
}
