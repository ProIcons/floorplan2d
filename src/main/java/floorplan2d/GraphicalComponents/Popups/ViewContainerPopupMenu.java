/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package floorplan2d.GraphicalComponents.Popups;

import floorplan2d.GraphicalComponents.Dialogs.RenameViewDialog;
import floorplan2d.GraphicalComponents.CustomComponents.ViewContainer;
import static floorplan2d.Editor.project;
import static floorplan2d.Editor.window;
import floorplan2d.View;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Tasos
 */
public class ViewContainerPopupMenu extends JPopupMenu implements java.io.Serializable {

    private JMenuItem closeTabItem, closeAllItem, closeAllButThisItem, deleteTabItem, renameTabItem;
    private boolean showItemControls;
    private ViewContainer viewContainer;
    private View view;
    private int tabIndex;

    public ViewContainerPopupMenu(ViewContainer viewCont) {
        viewContainer = viewCont;
        showItemControls = false;

        closeTabItem = new JMenuItem("Close");
        closeTabItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                viewCont.remove(viewCont.getSelectedIndex());
                if (viewContainer.getTabCount() > 1) {
                    project.setActiveView(project.getViewByCanvas(window.getActiveTabCanvas()));
                } else {
                    project.setActiveView(null);
                }
                if (project.getActiveView() == null) {
                    window.setEditMenuEnabled(false);
                }
            }
        });

        closeAllButThisItem = new JMenuItem("Close All But This");
        closeAllButThisItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                int rem = 1;
                while (viewContainer.getTabCount() > rem) {
                    if (viewContainer.getSelectedIndex() == 1 && rem == 1) {
                        rem++;

                    }
                    viewContainer.remove(rem);
                }
                if (viewContainer.getTabCount() > 1) {
                    project.setActiveView(project.getViewByCanvas(window.getActiveTabCanvas()));
                } else {
                    project.setActiveView(null);
                }
                if (project.getActiveView() == null) {
                    window.setEditMenuEnabled(false);
                }

            }
        });

        closeAllItem = new JMenuItem("Close All");
        closeAllItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                while (viewContainer.getTabCount() > 1) {
                    viewContainer.remove(1);
                }
                window.setEditMenuEnabled(
                        false);
            }

        });

        deleteTabItem = new JMenuItem("Delete");
        deleteTabItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                window.removeListItem(view);
                project.removeView(view);
                window.closeTab(view);
                if (viewContainer.getTabCount() > 1) {
                    project.setActiveView(project.getViewByCanvas(window.getActiveTabCanvas()));

                } else {
                    project.setActiveView(null);
                }
                if (project.getActiveView() == null) {
                    window.setEditMenuEnabled(false);
                }
            }
        });

        renameTabItem = new JMenuItem("Rename");
        renameTabItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                String name = RenameViewDialog.Show(project.getViews(), view);
                if (!name.isEmpty()) {

                    view.setName(name);
                    view.getCanvas().setName(name);
                    window.reloadList(project);
                    viewContainer.setTitleAt(tabIndex, name + "   ");
                    viewContainer.repaint();
                }
            }
        });

        add(closeTabItem);
        add(closeAllItem);
        add(closeAllButThisItem);
        addSeparator();
        add(deleteTabItem);
        addSeparator();
        add(renameTabItem);

    }

    public void show(View _view, int index, Point point) {
        view = _view;
        tabIndex = index;
        show(viewContainer, point.x, point.y);
    }
}
