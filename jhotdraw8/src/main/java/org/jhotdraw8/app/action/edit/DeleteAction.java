/*
 * @(#)DeleteAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw8.app.action.edit;

import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.app.action.AbstractFocusOwnerAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.ProjectView;

/**
 * Deletes the region at (or after) the caret position.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DeleteAction<V extends ProjectView<V>> extends AbstractFocusOwnerAction<V> {

    private static final long serialVersionUID = 1L;
    /**
     * The ID for this action.
     */
    public static final String ID = "edit.delete";

    /**
     * Creates a new instance which acts on the currently focused component.
     *
     * @param app the app
     */
    public DeleteAction(Application<V> app) {
        super(app);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(javafx.event.ActionEvent event) {
        event.consume();
        V v = app.getActiveView();
        if (v != null && !v.isDisabled()) {
            Node n = v.getNode().getScene().getFocusOwner();
            if (n instanceof TextInputControl) {
                TextInputControl tic = (TextInputControl) n;
                tic.deleteNextChar();
            }
            if (n instanceof EditableComponent) {
                EditableComponent tic = (EditableComponent) n;
                tic.deleteSelection();
            }
        }
    }
}
