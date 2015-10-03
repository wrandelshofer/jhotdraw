/*
 * @(#)ClearSelectionAction.java
 *
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.edit;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.EditableComponent;
import org.jhotdraw.app.View;
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

    /**
     * Creates a new instance which acts on the currently focused component.
     *
     * @param app the application
     */
    public ClearSelectionAction(Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param app the application
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public ClearSelectionAction(Application app, Node target) {
        super(app, target);
        Resources labels = Resources.getResources("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }

    @Override
    public void handle(javafx.event.ActionEvent event) {
        View v = app.getActiveView();
        if (v != null && !v.isDisabled()) {
            Node n = v.getNode().getScene().getFocusOwner();
            if (n instanceof TextInputControl) {
                TextInputControl tic = (TextInputControl) n;
                tic.deselect();
            } else if (n instanceof EditableComponent) {
                EditableComponent tic = (EditableComponent) n;
                tic.clearSelection();
            }
        }
    }
}
