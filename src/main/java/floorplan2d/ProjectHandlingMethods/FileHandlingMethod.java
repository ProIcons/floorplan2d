package floorplan2d.ProjectHandlingMethods;

import floorplan2d.Editor;
import floorplan2d.GraphicalComponents.Dialogs.ProjectPreviewDialog;
import floorplan2d.Interfaces.IProjectHandlingMethod;
import floorplan2d.Project;
import java.io.File;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Tasos
 */
public class FileHandlingMethod implements IProjectHandlingMethod, java.io.Serializable {

    @Override
    public Project load() {
        String _fileName;
        JFileChooser c = new JFileChooser();
        FileNameExtensionFilter filter = new FileNameExtensionFilter("FloorPlan2D Save File", "fpsf");
        c.setFileFilter(filter);
        int rVal = c.showOpenDialog(Editor.window);
        if (rVal == JFileChooser.APPROVE_OPTION) {
            FileInputStream fileIn = null;
            try {
                _fileName = c.getCurrentDirectory().toString() + File.separator + c.getSelectedFile().getName();
                fileIn = new FileInputStream(_fileName);
                ObjectInputStream in = new ObjectInputStream(fileIn);
                Project temp = (Project) in.readObject();
                in.close();
                fileIn.close();
                if (ProjectPreviewDialog.Show(temp)) {
                    temp.updateTime();
                    temp.setFileName(_fileName);
                    return temp;
                } else {
                    return null;
                }
            } catch (FileNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "The file you tried to open does not exists anymore.", "FloorPlan2D", JOptionPane.ERROR_MESSAGE);
            } catch (IOException ex) {
                JOptionPane.showMessageDialog(null, "The file you tried to open is not a valid FloorPlan2D Project.", "FloorPlan2D", JOptionPane.ERROR_MESSAGE);
            } catch (ClassNotFoundException ex) {
                JOptionPane.showMessageDialog(null, "Save files from previous version of FloorPlan2D are not supported.", "FloorPlan2D", JOptionPane.ERROR_MESSAGE);
            } finally {
                try {
                    fileIn.close();
                } catch (IOException ex) {
                    Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
                }
            }

        }
        return null;
    }

    @Override
    public boolean save() {
        if (Editor.project.getFileName().isEmpty()) {
            JFileChooser c = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("FloorPlan2D Save File", "fpsf");
            c.setFileFilter(filter);

            int rVal = c.showSaveDialog(Editor.window);
            if (rVal == JFileChooser.APPROVE_OPTION) {
                Editor.project.setFileName(c.getCurrentDirectory().toString() + File.separator + c.getSelectedFile().getName());
                if (!Editor.project.getFileName().endsWith(".fpsf")) {
                    Editor.project.setFileName(Editor.project.getFileName() + ".fpsf");
                }
            }
            if (rVal == JFileChooser.CANCEL_OPTION) {
                return false;
            }
        }

        try {
            FileOutputStream fout = new FileOutputStream(Editor.project.getFileName());
            ObjectOutputStream oos = new ObjectOutputStream(fout);
            oos.writeObject(Editor.project);
            oos.close();
            fout.close();
            return true;
        } catch (FileNotFoundException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        } catch (IOException ex) {
            Logger.getLogger(Project.class.getName()).log(Level.SEVERE, null, ex);
            return false;
        }
    }

}
