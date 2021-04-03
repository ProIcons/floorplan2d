package floorplan2d;

import floorplan2d.GraphicalComponents.EditorWindow;
import floorplan2d.ViewItems.Item;
import floorplan2d.GraphicalComponents.Dialogs.NewViewDialog;
import floorplan2d.Interfaces.IEditorEventListener;
import floorplan2d.Interfaces.IProjectHandlingMethod;
import floorplan2d.ProjectHandlingMethods.FileHandlingMethod;

import java.awt.Font;
import java.awt.FontFormatException;

import java.io.IOException;
import java.io.InputStream;

import java.lang.reflect.*;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.JOptionPane;

import org.reflections.Reflections;

/**
 *
 * @author Nikolas, Chrysa
 */
public final class Editor implements IEditorEventListener {

    public static EditorWindow window;
    public static Font customFont;
    public static Project project;
    public static IProjectHandlingMethod projectHandlingMethod = new FileHandlingMethod();
            
    public Editor() {

        InputStream is = this.getClass().getResourceAsStream("/rss/Storopia.ttf");
        try {
            customFont = Font.createFont(Font.TRUETYPE_FONT, is);
            is.close();
        } catch (FontFormatException | IOException ex) {
            Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
        }

        window = new EditorWindow();

        window.addCommandListener(this);

        window.setVisible(true);

        Reflections reflections = new Reflections("floorplan2d");
        Set<Class<? extends Item>> classes = reflections.getSubTypesOf(Item.class);
        for (Class<?> clazz : classes) {
            if (!Modifier.isAbstract(clazz.getModifiers())) {
                window.addItemButton(clazz);
            }
        }
    }

    @Override
    public void undoCommand() {
        if (project != null) {
            project.getActiveView().undo();
        }
    }

    @Override
    public void redoCommand() {
        if (project != null) {
            project.getActiveView().redo();
        }
    }

    @Override
    public void copyCommand() {
        if (project != null) {
            project.getActiveView().copy();
        }
    }

    @Override
    public void cutCommand() {
        if (project != null) {
            project.getActiveView().cut();
        }
    }

    @Override
    public void pasteCommand() {
        if (project != null) {
            project.getActiveView().paste();
        }
    }

    @Override
    public void newProjectCommand() {
        Project _project = Project.New();
        if (_project != null) {
            if (project != null) {
                closeProjectCommand();
            }
            project = _project;
            window.setTitle(project.getName() + " - FloorPlan2D Pro");
            window.setViewMenuEnabled(true);
            window.setEditMenuEnabled(false);
        }
    }

    @Override
    public void saveProjectCommand() {
        if (project != null) {
            project.save();
        }
    }

    @Override
    public void closeProjectCommand() {
        if (project != null) {
            project.close();
        }
        window.clearTabs();
        window.clearList();
        project = null;
        window.setViewMenuEnabled(false);
        window.setEditMenuEnabled(false);

    }

    @Override
    public void openProjectCommand() {

        Project _project = Project.Open();
        if (_project != null) {
            if (project != null) {
                project.close();
            }
            project = _project;
            window.reloadWindow(project);
            window.setViewMenuEnabled(true);
            window.setEditMenuEnabled(false);
        }
    }

    @Override
    public void newViewCommand() {
        if (project != null) {
            String viewName = NewViewDialog.Show(project.getViews());
            if (!viewName.isEmpty()) {
                View view = project.addView(viewName);
                window.addListItem(view);
                project.setActiveView(view);
                window.changeTab(view);
                window.setEditMenuEnabled(true);
            }

        }
    }

    @Override
    public void deleteViewCommand() {
        if (project != null) {
            if (project.getActiveView() != null) {
                window.removeListItem(project.getActiveView());
                project.removeView(project.getActiveView());
                if (project.getViews().isEmpty()) {
                    window.setEditMenuEnabled(false);
                }
            }
        }
    }

    @Override
    public void deleteCommand() {
        if (project != null) {
            project.getActiveView().delete();
        }
    }

    @Override
    public void clearCommand() {
        if (project != null) {
            project.getActiveView().clear();
        }
    }

    @Override
    public void exitCommand() {
        if (project != null) {
            closeProjectCommand();
        }
        System.exit(0);
    }

    @Override
    public void selectAllCommand() {
        if (project != null) {
            project.getActiveView().selectAll();
        }
    }

    @Override
    public void invertSelectionCommand() {
        if (project != null) {
            project.getActiveView().invertSelection();
        }
    }

    @Override
    public void deselectCommand() {
        if (project != null) {
            project.getActiveView().deselect();
        }
    }

    @Override
    public void changeTabCommand(View view) {
        if (project != null && view != null) {
            View _view = view;
            if (_view != null) {
                project.setActiveView(_view);
            }
        }
    }

    @Override
    public void itemPressedCommand(Class _class) {
        if (project != null && project.getActiveView() != null) {
            Item item = null;
            try {
                item = (Item) _class.getConstructor(View.class)
                        .newInstance(project.getActiveView());
                project.getActiveView().setHandItem(item);
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(null, "There is no Construction Item on Canvas so the "+_class.getName()+" can be mounted on.", "FloorPlan2D", JOptionPane.ERROR_MESSAGE);
                Logger.getLogger(Editor.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    @Override
    public void changeViewCommand(String entryName) {

        if (project != null) {
            for (View view : project.getViews()) {
                if (view.getName().equals(entryName)) {
                    if (window.indexOfView(view) > -1) {
                        project.setActiveView(view);
                        window.changeTab(view);
                    } else {
                        window.addTab(view);
                        project.setActiveView(view);
                        window.changeTab(view);
                    }

                }
            }

        }
    }

    @Override
    public void closeViewCommand() {
        if (project != null && project.getActiveView() != null) {
            window.closeTab(project.getActiveView());
            project.setActiveView(project.getViewByCanvas(window.getActiveTabCanvas()));
            if (project.getActiveView() == null) {
                window.setEditMenuEnabled(false);
            }
        }
    }

}
