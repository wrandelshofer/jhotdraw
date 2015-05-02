/*
 * @(#)CopyAction.java
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
//import org.jhotdraw.gui.datatransfer.ClipboardUtil;
import org.jhotdraw.util.*;

/**
 * Copies the selected region and place its contents into the system clipboard.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id: CopyAction.java 788 2014-03-22 07:56:28Z rawcoder $
 */
public class CopyAction extends AbstractSelectionAction {
    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.copy";

    /** Creates a new instance which acts on the currently focused component.
     * @param app the application */
    public CopyAction(Application app) {
        this(app, Optional.empty());
    }

    /** Creates a new instance which acts on the specified component.
     *
     * @param app the application 
     * @param target The target of the action. Specify empty for the currently
     * focused component.
     */
    public CopyAction(Application app,Optional< Node> target) {
        super(app,target);
        Resources labels = Resources.getBundle("org.jhotdraw.app.Labels");
        labels.configureAction(this, ID);
    }
    @Override
    public void handle(javafx.event.ActionEvent event) {
        Optional<View> v = app.getActiveView();
        if (v.isPresent() && !v.get().isDisabled()) {
            Node n = v.get().getNode().getScene().getFocusOwner();
            if (n instanceof TextInputControl) {
                TextInputControl tic=(TextInputControl)n;
                tic.copy();
            }
        }
    }
}
