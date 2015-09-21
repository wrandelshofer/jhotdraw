/*
 * @(#)ClearSelectionAction.java
 *
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.edit;

import javafx.scene.Node;
import org.jhotdraw.app.Application;
import org.jhotdraw.util.*;


/**
 * Clears (de-selects) the selected region.
 *
 * @author Werner Randelshofer.
 * @version $Id: ClearSelectionAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class ClearSelectionAction extends AbstractSelectionAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.clearSelection";

    /** Creates a new instance which acts on the currently focused component.
     * @param app the application */
    public ClearSelectionAction(Application app) {
        this(app, null);
    }

    /** Creates a new instance which acts on the specified component.
     *
     * @param app the application
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public ClearSelectionAction(Application app, Node target) {
        super(app,target);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
/*
    @Override
    public void actionPerformed(ActionEvent evt) {
        JComponent c = target;
        if (c == null && (KeyboardFocusManager.getCurrentKeyboardFocusManager().
                getPermanentFocusOwner() instanceof JComponent)) {
            c = (JComponent) KeyboardFocusManager.getCurrentKeyboardFocusManager().
                    getPermanentFocusOwner();
        }
        if (c != null && c.isEnabled()) {
            if (c instanceof EditableComponent) {
                ((EditableComponent) c).clearSelection();
            } else if (c instanceof JTextComponent) {
                JTextComponent tc = ((JTextComponent) c);
                tc.select(tc.getSelectionStart(), tc.getSelectionStart());
            } else {
                c.getToolkit().beep();
            }
        }
    }

    @Override
    protected void updateEnabled() {
        if (target != null) {
            setEnabled(target.isEnabled());
        }
    }*/

    @Override
    public void handle(javafx.event.ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
