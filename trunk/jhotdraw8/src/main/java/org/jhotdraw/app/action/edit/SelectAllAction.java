/*
 * @(#)SelectAllAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.edit;

import edu.umd.cs.findbugs.annotations.Nullable;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractFocusOwnerAction;
import org.jhotdraw.util.*;
//import org.jhotdraw.gui.EditableComponent;

/**
 * Selects all items.
 *
 * @author Werner Randelshofer.
 * @version $Id: SelectAllAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class SelectAllAction extends AbstractFocusOwnerAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.selectAll";

    /** Creates a new instance which acts on the currently focused component.
     * @param app the application */
    public SelectAllAction(Application app) {
        this(app,null);
    }

    /** Creates a new instance which acts on the specified component.
     *
     * @param app the application 
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public SelectAllAction(Application app, @Nullable Node target) {
        super(app,target);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }


    @Override
    public void handle(javafx.event.ActionEvent event) {
        View v = app.getActiveView();
        if (v!=null && !v.isDisabled()) {
            Node n = v.getNode().getScene().getFocusOwner();
            if (n instanceof TextInputControl) {
                TextInputControl tic=(TextInputControl)n;
                tic.selectAll();
            }
        }
    }
}
