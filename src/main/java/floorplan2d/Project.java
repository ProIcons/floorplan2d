package floorplan2d;

import floorplan2d.Collections.ViewCollection;
import floorplan2d.GraphicalComponents.CustomComponents.Canvas;
import floorplan2d.GraphicalComponents.Dialogs.NewProjectDialog;

import java.util.*;

import javax.swing.JOptionPane;

/**
 *
 * @author Nikolas,Chrysa
 */
public class Project implements java.io.Serializable {

    private ViewCollection views = new ViewCollection();
    private transient String fileName;
    private Date dateCreated;
    private Date dateAccessed;
    private String name;

    public static Project Open() {
        return Editor.projectHandlingMethod.load();
    }

    public static Project New() {
        String name = NewProjectDialog.Show();
        Project temp;
        if (!name.isEmpty()) {
            temp = new Project();
            temp.name = name;
            temp.fileName = "";
            temp.dateCreated = new Date();
            temp.dateAccessed = new Date();
            return temp;
        }
        return null;
    }

    public boolean save() {
        return Editor.projectHandlingMethod.save();
    }

    public void close() {
        int reply = JOptionPane.showConfirmDialog(null, "Do you want to save the project before closing it?", "FloorPlan 2D", JOptionPane.YES_NO_OPTION);
        if (reply == JOptionPane.YES_OPTION) {
            save();
        }
    }

    public View addView(String _name) {
        View _view = new View(_name);
        views.add(_view);
        Editor.window.addTab(_view);
        return _view;
    }

    public View getViewByCanvas(Canvas _canvas) {
        if (_canvas != null) {
            for (View _view : views) {
                if (_canvas.equals(_view.getCanvas())) {
                    return _view;
                }
            }
        }
        return null;
    }

    public String getName() {
        return name;
    }
    public Date getDateCreated() {
        return dateCreated;
    }
    public Date getDateAccessed() {
        return dateAccessed;
    }

    public ViewCollection getViews() {
        return views;
    }

    public View getActiveView() {
        return views.getActiveView();
    }

    public String getFileName() {
        return fileName;
    }

    public void setName(String _name) {
        name = _name;
    }

    public boolean setActiveView(View _view) {
        return views.setActiveView(_view);
    }

    public void setFileName(String _fileName) {
        fileName = _fileName;
    }

    public void updateTime() {
       dateAccessed = new Date();
    }

    public void removeView(View _view) {
        views.remove(_view);
    }

}
