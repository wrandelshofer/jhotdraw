/*
 * @(#)DeleteAction.java
 *
 * Copyright (c) 1996-2010 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.app.action.edit;

import java.util.Optional;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractFocusOwnerAction;
import org.jhotdraw.util.ResourceBundleUtil;

/**
 * Deletes the region at (or after) the caret position.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: DeleteAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class DeleteAction extends AbstractFocusOwnerAction {
    private static final long serialVersionUID = 1L;
    /** The ID for this action. */
    public static final String ID = "edit.delete";

    /** Creates a new instance which acts on the currently focused component.
     * @param app the app
    */
    public DeleteAction(Application app) {
        super(app);
        ResourceBundleUtil labels = ResourceBundleUtil.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
    @Override
    public void handle(javafx.event.ActionEvent event) {
        Optional<View> v = app.getActiveView();
        if (v.isPresent() && !v.get().isDisabled()) {
            Node n = v.get().getNode().getScene().getFocusOwner();
            if (n instanceof TextInputControl) {
                TextInputControl tic=(TextInputControl)n;
                tic.deleteNextChar();
            }
        }
    }
}

