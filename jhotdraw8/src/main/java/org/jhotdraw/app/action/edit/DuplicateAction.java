/*
 * @(#)DuplicateAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */

package org.jhotdraw.app.action.edit;

import javafx.scene.Node;
import org.jhotdraw.app.Application;
import org.jhotdraw.util.*;


/**
 * Duplicates the selected region.
 * <p>
 *
 * @author Werner Randelshofer.
 * @version $Id: DuplicateAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class DuplicateAction extends AbstractSelectionAction {
    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.duplicate";
    
    /** Creates a new instance which acts on the currently focused component. 
     * @param app the application */
    public DuplicateAction(Application app) {
        this(app, null);
    }

    /** Creates a new instance which acts on the specified component.
     *
     * @param app the application 
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public DuplicateAction(Application app, Node target) {
        super(app,target);
        Resources labels = Resources.getBundle("org.jhotdraw.app.Labels");
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
                ((EditableComponent) c).duplicate();
            } else {
                c.getToolkit().beep();
            }
        }
    }*/

    @Override
    public void handle(javafx.event.ActionEvent event) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
}
