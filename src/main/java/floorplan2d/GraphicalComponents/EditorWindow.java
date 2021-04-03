package floorplan2d.GraphicalComponents;

import floorplan2d.GraphicalComponents.CustomComponents.ViewContainer;
import floorplan2d.GraphicalComponents.CustomComponents.Canvas;
import floorplan2d.View;
import floorplan2d.Interfaces.IEditorEventListener;
import floorplan2d.Project;

import java.awt.*;
import java.awt.event.*;

import java.lang.reflect.InvocationTargetException;

import java.util.*;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.swing.*;

/**
 *
 * @author Chrysa
 */
public class EditorWindow extends JFrame {

    protected ArrayList<IEditorEventListener> commandListenerList = new ArrayList<>();
    protected ArrayList<JButton> toolBoxItems = new ArrayList<>();
    DefaultListModel model;
    private boolean isEditMenuEnabled = false;
    private boolean isViewMenuEnabled = false;

    /**
     * Creates a new EditorWindow
     */
    public EditorWindow() {
        setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("/rss/icon.png")));
        initComponents();
        this.setMinimumSize(new Dimension(800, 400));
        jList1.addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent evt) {
                clearListSelection(evt);
            }

            @Override
            public void mouseReleased(MouseEvent evt) {
                clearListSelection(evt);
                if (evt.getClickCount() == 2) {
                    for (IEditorEventListener listener : commandListenerList) {
                        listener.changeViewCommand(getListName(jList1.getSelectedIndex()));
                    }
                }
            }
        });
        model = new DefaultListModel();

        jList1.setModel(model);
        refreshMenuItemStates();

    }

    /**
     * Sets whether the Menus related with edit controls should be enabled or
     * not.
     *
     * @param b
     */
    public void setEditMenuEnabled(boolean b) {
        isEditMenuEnabled = b;
        refreshMenuItemStates();
    }

    /**
     * Sets whether the Menus related with views should be enabled or not.
     *
     * @param b
     */
    public void setViewMenuEnabled(boolean b) {
        isViewMenuEnabled = b;
        refreshMenuItemStates();
    }

    private void refreshMenuItemStates() {
        jMenuItem8.setEnabled(isEditMenuEnabled);
        jMenuItem9.setEnabled(isEditMenuEnabled);
        jMenuItem10.setEnabled(isEditMenuEnabled);
        jMenuItem11.setEnabled(isEditMenuEnabled);
        jMenuItem12.setEnabled(isEditMenuEnabled);
        jMenuItem13.setEnabled(isEditMenuEnabled);
        jMenuItem15.setEnabled(isEditMenuEnabled);
        jMenuItem16.setEnabled(isEditMenuEnabled);
        jMenuItem17.setEnabled(isEditMenuEnabled);
        jMenuItem18.setEnabled(isEditMenuEnabled);

        jMenuItem2.setEnabled(isViewMenuEnabled);
        jMenuItem14.setEnabled(isViewMenuEnabled);
        jMenuItem6.setEnabled(isViewMenuEnabled);
        jMenuItem5.setEnabled(isViewMenuEnabled);
        jMenuItem4.setEnabled(isViewMenuEnabled);

        for (JButton but : toolBoxItems) {
            but.setEnabled(isEditMenuEnabled);
        }
    }

    /**
     * Add a View on the View List
     *
     * @param view
     */
    public void addListItem(View view) {
        model.addElement(view.getName());
        jList1.repaint();
    }

    /**
     * Clears the View List
     */
    public void clearList() {
        model.clear();
    }

    /**
     * Removes a given View from the View List
     *
     * @param view name of the View
     */
    public void removeListItem(View view) {
        model.removeElement(view.getName());
        jList1.repaint();
    }

    /**
     * Gets the view of the tab in given index.
     *
     * @param index
     * @return View of the given index.
     */
    public View getTabView(int index) {
        return getTabCanvas(index).getView();
    }

    /**
     * Gets the canvas of the tab in given index.
     *
     * @param index
     * @return Canvas of the given index.
     */
    public Canvas getTabCanvas(int index) {
        return (Canvas) jTabbedPane1.getComponent(index);
    }

    /**
     * Gets the active tab view.
     *
     * @return View of the Active tab.
     */
    public View getActiveTabView() {
        if (getActiveTabCanvas() != null) {
            return getActiveTabCanvas().getView();
        }
        return null;
    }

    /**
     * Gets the canvas of active tab.
     *
     * @return Canvas of the Active tab.
     */
    public Canvas getActiveTabCanvas() {
        return (Canvas) jTabbedPane1.getSelectedComponent();
    }

    /**
     * Changes the tab to the given view
     *
     * @param view
     */
    public void changeTab(View view) {
        if (indexOfView(view) >= 0) {
            jTabbedPane1.setSelectedIndex(indexOfView(view));
            setEditMenuEnabled(true);
        }
    }

    /**
     * Changes the tab to the given index
     *
     * @param index
     */
    public void changeTab(int index) {
        jTabbedPane1.setSelectedIndex(index);
        setEditMenuEnabled(true);
    }

    /**
     *
     * @param view
     * @return the index of the given View if it is opened on the Tab Container.
     * if not returns -1
     */
    public int indexOfView(View view) {
        return jTabbedPane1.indexOfComponent(view.getCanvas());
    }

    /**
     * Gets the View List Item Name on the given index
     *
     * @param index
     *
     * @return view list name
     */
    public String getListName(int index) {
        return (String) model.get(index);
    }

    /**
     *
     * @param listener
     */
    public synchronized void addCommandListener(IEditorEventListener listener) {
        if (!commandListenerList.contains(listener)) {
            commandListenerList.add(listener);
        }
    }

    /**
     *
     * @param listener
     */
    public synchronized void removeCommandListener(IEditorEventListener listener) {
        if (commandListenerList.contains(listener)) {
            commandListenerList.remove(listener);
        }
    }

    /**
     * Refresh the window's View List, Tabs, and Title
     *
     * @param p
     */
    public void reloadWindow(Project p) {
        setTitle(p.getName());
        reloadList(p);
        clearTabs();
    }
    
    public void reloadList(Project p){
        model.clear();
        for (View view : p.getViews()) {
            model.addElement(view.getName());
        }
    }
    /**
     * Adds a view on the Tab Container of the editor.
     *
     * @param view
     */
    public void addTab(View view) {
        jTabbedPane1.add(view.getCanvas());
        jTabbedPane1.repaint();
    }

    /**
     * Removes the given View from the Tab Container of the editor.
     *
     * @param view
     */
    public void closeTab(View view) {
        jTabbedPane1.remove(view.getCanvas());
        jTabbedPane1.repaint();
    }

    /**
     * Remove all Views from Tab Container of the editor.
     */
    public void clearTabs() {
        while (jTabbedPane1.getTabCount()>1) {
            jTabbedPane1.remove(1);
        }
     
        jTabbedPane1.repaint();
    }

    /**
     * Adds a button on Editor's Toolbox
     *
     * @param _class - The class Object of the item will be added on the Canvas.
     */
    public void addItemButton(Class _class) {
        try {
            JButton _button = new JButton((String) _class.getMethod("getItemName").invoke(null));
            _button.setHorizontalAlignment(SwingConstants.CENTER);
            _button.setVerticalTextPosition(SwingConstants.BOTTOM);
            _button.setMaximumSize(new Dimension(120, 25));
            _button.setPreferredSize(new Dimension(120, 25));
            _button.addActionListener((ActionEvent evt) -> {
                for (IEditorEventListener listener : commandListenerList) {
                    listener.itemPressedCommand(_class);
                }
            });
            _button.repaint();
            _button.setEnabled(isEditMenuEnabled);
            jToolBar1.add(_button);
            toolBoxItems.add(_button);
            jToolBar1.repaint();
        } catch (SecurityException | IllegalArgumentException | IllegalAccessException | NoSuchMethodException | InvocationTargetException ex) {
            Logger.getLogger(EditorWindow.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    /**
     * Deselects Any selected item on the Project View List.
     *
     * @param evt
     */
    private void clearListSelection(MouseEvent evt) {
        Point pClicked = evt.getPoint();
        JList<?> list = (JList<?>) evt.getSource();
        int index = list.locationToIndex(pClicked);
        Rectangle rec = list.getCellBounds(index, index);
        if (rec == null || !rec.contains(pClicked)) {
            list.clearSelection();
            jPanel1.requestFocusInWindow();
        }
    }
    
    public DefaultListModel getListModel() {
        return model;
    }
    

    /**
     * This method is called from within the constructor to initialize the form.
     * WARNING: Do NOT modify this code. The content of this method is always
     * regenerated by the Form Editor.
     */
    @SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        jScrollBar1 = new JScrollBar();
        jScrollBar2 = new JScrollBar();
        jPanel2 = new JPanel();
        jTabbedPane1 = new ViewContainer();
        jPanel4 = new JPanel();
        jPanel1 = new JPanel();
        jToolBar1 = new JToolBar();
        jLabel1 = new JLabel();
        jScrollPane1 = new JScrollPane();
        jList1 = new JList<>();
        jLabel2 = new JLabel();
        jScrollBar3 = new JScrollBar();
        jMenuBar1 = new JMenuBar();
        jMenu1 = new JMenu();
        jMenuItem1 = new JMenuItem();
        jMenuItem3 = new JMenuItem();
        jMenuItem4 = new JMenuItem();
        jMenuItem5 = new JMenuItem();
        jSeparator1 = new JPopupMenu.Separator();
        jMenuItem2 = new JMenuItem();
        jMenuItem14 = new JMenuItem();
        jMenuItem6 = new JMenuItem();
        jSeparator4 = new JPopupMenu.Separator();
        jMenuItem7 = new JMenuItem();
        jMenu2 = new JMenu();
        jMenuItem8 = new JMenuItem();
        jMenuItem9 = new JMenuItem();
        jSeparator5 = new JPopupMenu.Separator();
        jMenuItem10 = new JMenuItem();
        jMenuItem11 = new JMenuItem();
        jMenuItem12 = new JMenuItem();
        jSeparator6 = new JPopupMenu.Separator();
        jMenuItem13 = new JMenuItem();
        jMenuItem17 = new JMenuItem();
        jSeparator3 = new JPopupMenu.Separator();
        jMenuItem18 = new JMenuItem();
        jMenuItem15 = new JMenuItem();
        jMenuItem16 = new JMenuItem();

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        setTitle("FloorPlan2D");

        jScrollBar1.setEnabled(false);

        jScrollBar2.setOrientation(JScrollBar.HORIZONTAL);
        jScrollBar2.setToolTipText("");
        jScrollBar2.setEnabled(false);

        jTabbedPane1.setTabLayoutPolicy(JTabbedPane.SCROLL_TAB_LAYOUT);
        jTabbedPane1.addChangeListener(new javax.swing.event.ChangeListener() {
            public void stateChanged(javax.swing.event.ChangeEvent evt) {
                jTabbedPane1StateChanged(evt);
            }
        });

        GroupLayout jPanel2Layout = new GroupLayout(jPanel2);
        jPanel2.setLayout(jPanel2Layout);
        jPanel2Layout.setHorizontalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );
        jPanel2Layout.setVerticalGroup(
            jPanel2Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addComponent(jTabbedPane1)
        );

        jPanel4.setBorder(BorderFactory.createLineBorder(SystemColor.activeCaptionBorder));
        jPanel4.setPreferredSize(new Dimension(2, 20));

        GroupLayout jPanel4Layout = new GroupLayout(jPanel4);
        jPanel4.setLayout(jPanel4Layout);
        jPanel4Layout.setHorizontalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        jPanel4Layout.setVerticalGroup(
            jPanel4Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGap(0, 18, Short.MAX_VALUE)
        );

        jPanel1.setPreferredSize(new Dimension(140, 423));

        jToolBar1.setFloatable(false);
        jToolBar1.setOrientation(SwingConstants.VERTICAL);
        jToolBar1.setRollover(true);

        jLabel1.setFont(new Font("DialogInput", 1, 14)); // NOI18N
        jLabel1.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel1.setText("Toolbox");
        jLabel1.setHorizontalTextPosition(SwingConstants.CENTER);

        jScrollPane1.setVerticalScrollBar(jScrollBar3);

        jList1.setDragEnabled(true);
        jScrollPane1.setViewportView(jList1);

        jLabel2.setFont(new Font("DialogInput", 1, 14)); // NOI18N
        jLabel2.setHorizontalAlignment(SwingConstants.CENTER);
        jLabel2.setText("Project Views");
        jLabel2.setHorizontalTextPosition(SwingConstants.CENTER);

        jScrollBar3.setUnitIncrement(10);

        GroupLayout jPanel1Layout = new GroupLayout(jPanel1);
        jPanel1.setLayout(jPanel1Layout);
        jPanel1Layout.setHorizontalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(GroupLayout.Alignment.TRAILING, jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(jPanel1Layout.createSequentialGroup()
                        .addComponent(jScrollPane1, GroupLayout.PREFERRED_SIZE, 103, GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(jScrollBar3, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jToolBar1, GroupLayout.PREFERRED_SIZE, 119, GroupLayout.PREFERRED_SIZE))
                .addContainerGap(12, Short.MAX_VALUE))
            .addComponent(jLabel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
            .addComponent(jLabel1, GroupLayout.Alignment.TRAILING, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
        );
        jPanel1Layout.setVerticalGroup(
            jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(jPanel1Layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(jLabel1, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jToolBar1, GroupLayout.DEFAULT_SIZE, 202, Short.MAX_VALUE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(jLabel2, GroupLayout.PREFERRED_SIZE, 19, GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(jPanel1Layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollPane1, GroupLayout.DEFAULT_SIZE, 151, Short.MAX_VALUE)
                    .addComponent(jScrollBar3, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addContainerGap())
        );

        jMenu1.setText("File");

        jMenuItem1.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.CTRL_MASK));
        jMenuItem1.setText("New Project");
        jMenuItem1.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem1ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem1);

        jMenuItem3.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O, InputEvent.CTRL_MASK));
        jMenuItem3.setText("Open Project");
        jMenuItem3.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem3ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem3);

        jMenuItem4.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S, InputEvent.CTRL_MASK));
        jMenuItem4.setText("Save Project");
        jMenuItem4.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem4ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem4);

        jMenuItem5.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.CTRL_MASK));
        jMenuItem5.setText("Close Project");
        jMenuItem5.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem5ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem5);
        jMenu1.add(jSeparator1);

        jMenuItem2.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        jMenuItem2.setText("New View");
        jMenuItem2.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem2ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem2);

        jMenuItem14.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_W, InputEvent.SHIFT_MASK | InputEvent.CTRL_MASK));
        jMenuItem14.setText("Close View");
        jMenuItem14.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem14ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem14);

        jMenuItem6.setText("Delete View");
        jMenuItem6.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem6ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem6);
        jMenu1.add(jSeparator4);

        jMenuItem7.setText("Exit");
        jMenuItem7.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem7ActionPerformed(evt);
            }
        });
        jMenu1.add(jMenuItem7);

        jMenuBar1.add(jMenu1);

        jMenu2.setText("Edit");

        jMenuItem8.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Z, InputEvent.CTRL_MASK));
        jMenuItem8.setText("Undo");
        jMenuItem8.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem8ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem8);

        jMenuItem9.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_Y, InputEvent.CTRL_MASK));
        jMenuItem9.setText("Redo");
        jMenuItem9.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem9ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem9);
        jMenu2.add(jSeparator5);

        jMenuItem10.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_X, InputEvent.CTRL_MASK));
        jMenuItem10.setText("Cut");
        jMenuItem10.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem10ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem10);

        jMenuItem11.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C, InputEvent.CTRL_MASK));
        jMenuItem11.setText("Copy");
        jMenuItem11.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem11ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem11);

        jMenuItem12.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_V, InputEvent.CTRL_MASK));
        jMenuItem12.setText("Paste");
        jMenuItem12.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem12ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem12);
        jMenu2.add(jSeparator6);

        jMenuItem13.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, 0));
        jMenuItem13.setText("Delete");
        jMenuItem13.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem13ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem13);

        jMenuItem17.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_DELETE, InputEvent.CTRL_MASK));
        jMenuItem17.setText("Clear");
        jMenuItem17.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem17ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem17);
        jMenu2.add(jSeparator3);

        jMenuItem18.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A, InputEvent.CTRL_MASK));
        jMenuItem18.setText("Select All");
        jMenuItem18.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem18ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem18);

        jMenuItem15.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0));
        jMenuItem15.setText("Deselect");
        jMenuItem15.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem15ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem15);

        jMenuItem16.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_I, InputEvent.CTRL_MASK));
        jMenuItem16.setText("Invert Selection");
        jMenuItem16.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent evt) {
                jMenuItem16ActionPerformed(evt);
            }
        });
        jMenu2.add(jMenuItem16);

        jMenuBar1.add(jMenu2);

        setJMenuBar(jMenuBar1);

        GroupLayout layout = new GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(1, 1, 1)
                .addComponent(jPanel1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addComponent(jScrollBar2, GroupLayout.DEFAULT_SIZE, 758, Short.MAX_VALUE)
                    .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addGap(0, 0, 0)
                .addComponent(jScrollBar1, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
            .addComponent(jPanel4, GroupLayout.DEFAULT_SIZE, 912, Short.MAX_VALUE)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(jPanel2, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(jScrollBar1, GroupLayout.DEFAULT_SIZE, GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addGap(0, 0, 0)
                        .addComponent(jScrollBar2, GroupLayout.PREFERRED_SIZE, 13, GroupLayout.PREFERRED_SIZE))
                    .addComponent(jPanel1, GroupLayout.DEFAULT_SIZE, 427, Short.MAX_VALUE))
                .addComponent(jPanel4, GroupLayout.PREFERRED_SIZE, GroupLayout.DEFAULT_SIZE, GroupLayout.PREFERRED_SIZE))
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void jMenuItem6ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem6ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.deleteViewCommand();
        }
    }//GEN-LAST:event_jMenuItem6ActionPerformed

    private void jMenuItem1ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem1ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.newProjectCommand();
        }
    }//GEN-LAST:event_jMenuItem1ActionPerformed

    private void jMenuItem3ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem3ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.openProjectCommand();
        }
    }//GEN-LAST:event_jMenuItem3ActionPerformed

    private void jMenuItem2ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem2ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.newViewCommand();
        }
    }//GEN-LAST:event_jMenuItem2ActionPerformed

    private void jMenuItem15ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem15ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.deselectCommand();
        }
    }//GEN-LAST:event_jMenuItem15ActionPerformed

    private void jMenuItem8ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem8ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.undoCommand();
        }
    }//GEN-LAST:event_jMenuItem8ActionPerformed

    private void jMenuItem9ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem9ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.redoCommand();
        }
    }//GEN-LAST:event_jMenuItem9ActionPerformed

    private void jMenuItem10ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem10ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.cutCommand();
        }
    }//GEN-LAST:event_jMenuItem10ActionPerformed

    private void jMenuItem11ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem11ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.copyCommand();
        }
    }//GEN-LAST:event_jMenuItem11ActionPerformed

    private void jMenuItem12ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem12ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.pasteCommand();
        }
    }//GEN-LAST:event_jMenuItem12ActionPerformed

    private void jMenuItem13ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem13ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.deleteCommand();
        }
    }//GEN-LAST:event_jMenuItem13ActionPerformed

    private void jMenuItem17ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem17ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.clearCommand();
        }
    }//GEN-LAST:event_jMenuItem17ActionPerformed

    private void jMenuItem18ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem18ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.selectAllCommand();
        }
    }//GEN-LAST:event_jMenuItem18ActionPerformed

    private void jMenuItem16ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem16ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.invertSelectionCommand();
        }
    }//GEN-LAST:event_jMenuItem16ActionPerformed

    private void jMenuItem4ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem4ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.saveProjectCommand();
        }
    }//GEN-LAST:event_jMenuItem4ActionPerformed

    private void jMenuItem5ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem5ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.closeProjectCommand();
        }
    }//GEN-LAST:event_jMenuItem5ActionPerformed

    private void jMenuItem7ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem7ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.exitCommand();
        }
    }//GEN-LAST:event_jMenuItem7ActionPerformed

    private void jTabbedPane1StateChanged(javax.swing.event.ChangeEvent evt) {//GEN-FIRST:event_jTabbedPane1StateChanged
        for (IEditorEventListener listener : commandListenerList) {
            if (jTabbedPane1.getSelectedIndex() > 0) {
                listener.changeTabCommand(getActiveTabView());
            }
        }

    }//GEN-LAST:event_jTabbedPane1StateChanged

    private void jMenuItem14ActionPerformed(ActionEvent evt) {//GEN-FIRST:event_jMenuItem14ActionPerformed
        for (IEditorEventListener listener : commandListenerList) {
            listener.closeViewCommand();
        }
    }//GEN-LAST:event_jMenuItem14ActionPerformed
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private JLabel jLabel1;
    private JLabel jLabel2;
    private JList<String> jList1;
    private JMenu jMenu1;
    private JMenu jMenu2;
    private JMenuBar jMenuBar1;
    private JMenuItem jMenuItem1;
    private JMenuItem jMenuItem10;
    private JMenuItem jMenuItem11;
    private JMenuItem jMenuItem12;
    private JMenuItem jMenuItem13;
    private JMenuItem jMenuItem14;
    private JMenuItem jMenuItem15;
    private JMenuItem jMenuItem16;
    private JMenuItem jMenuItem17;
    private JMenuItem jMenuItem18;
    private JMenuItem jMenuItem2;
    private JMenuItem jMenuItem3;
    private JMenuItem jMenuItem4;
    private JMenuItem jMenuItem5;
    private JMenuItem jMenuItem6;
    private JMenuItem jMenuItem7;
    private JMenuItem jMenuItem8;
    private JMenuItem jMenuItem9;
    private JPanel jPanel1;
    private JPanel jPanel2;
    private JPanel jPanel4;
    private JScrollBar jScrollBar1;
    private JScrollBar jScrollBar2;
    private JScrollBar jScrollBar3;
    private JScrollPane jScrollPane1;
    private JPopupMenu.Separator jSeparator1;
    private JPopupMenu.Separator jSeparator3;
    private JPopupMenu.Separator jSeparator4;
    private JPopupMenu.Separator jSeparator5;
    private JPopupMenu.Separator jSeparator6;
    private ViewContainer jTabbedPane1;
    private JToolBar jToolBar1;
    // End of variables declaration//GEN-END:variables
}
