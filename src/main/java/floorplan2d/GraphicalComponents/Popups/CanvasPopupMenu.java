package floorplan2d.GraphicalComponents.Popups;

import floorplan2d.View;

import java.awt.Point;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;

/**
 *
 * @author Chrysa
 */
public class CanvasPopupMenu extends JPopupMenu implements java.io.Serializable {
    private JMenuItem copyItem,pasteItem,cutItem,deleteItem,selectAllItem,invertSelectionItem;
    private View view;
    
    public CanvasPopupMenu(View _view) {
        
        view = _view;
        
        copyItem = new JMenuItem("Copy");
        copyItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                _view.copy();
            }
        });
        add(copyItem);
        
        
        cutItem = new JMenuItem("Cut");
        cutItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                _view.copy();
            }
        });
        add(cutItem);
        
        pasteItem = new JMenuItem("Paste");
        pasteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                _view.paste();
            }
        });
        add(pasteItem);
        addSeparator();
        
        deleteItem = new JMenuItem("Delete");
        deleteItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                _view.delete();
            }
        });
        add(deleteItem);
        addSeparator();
        
        selectAllItem = new JMenuItem("Select All");
        selectAllItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                _view.selectAll();
            }
        });
        add(selectAllItem);
        
        invertSelectionItem = new JMenuItem("Invert Selection");
        invertSelectionItem.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                _view.invertSelection();
            }
        });
        
        
    }

    public void show(Point point) {
        if (!view.getSelectedItems().isEmpty()) {
            deleteItem.setEnabled(true);
            copyItem.setEnabled(true);
            cutItem.setEnabled(true);
        }
        else {
            deleteItem.setEnabled(false);
            copyItem.setEnabled(false);
            cutItem.setEnabled(false);
        }
        if (!view.getCopiedItems().isEmpty()) {
            pasteItem.setEnabled(true);
        }
        else {
            pasteItem.setEnabled(false);
        }
        show(view.getCanvas(),point.x,point.y);
    }
}
