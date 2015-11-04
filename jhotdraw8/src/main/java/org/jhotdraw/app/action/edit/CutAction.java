/*
 * @(#)CutAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.edit;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.EditableComponent;
import org.jhotdraw.app.View;
//import org.jhotdraw.gui.datatransfer.ClipboardUtil;
import org.jhotdraw.util.*;

/**
 * Cuts the selected region and places its contents into the system clipboard.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: CutAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class CutAction extends AbstractSelectionAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.cut";

    /**
     * Creates a new instance which acts on the currently focused component.
     *
     * @param app the application
     */
    public CutAction(Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param app the application
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public CutAction(Application app, Node target) {
        super(app, target);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(javafx.event.ActionEvent event) {
            View v = app.getActiveView();
            if (v != null && !v.isDisabled()) {
                Node n = v.getNode().getScene().getFocusOwner();
                if (n instanceof TextInputControl) {
                    TextInputControl tic = (TextInputControl) n;
                    tic.cut();
                } else if (n instanceof EditableComponent) {
                    EditableComponent tic = (EditableComponent) n;
                    tic.cut();
                }
            }
    }
}
