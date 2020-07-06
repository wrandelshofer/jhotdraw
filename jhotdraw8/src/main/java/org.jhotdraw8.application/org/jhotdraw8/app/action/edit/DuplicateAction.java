/*
 * @(#)DuplicateAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.EditableComponent;

/**
 * Duplicates the selected region.
 * <p>
 *
 * @author Werner Randelshofer.
 */
public class DuplicateAction extends AbstractSelectionAction {

public static final String ID = "edit.duplicate";

    /**
     * Creates a new instance which acts on the currently focused component.
     *
     * @param app the application
     */
    public DuplicateAction(@NonNull Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param app    the application
     * @param target The target of the action. Specify null for the currently
     *               focused component.
     */
    public DuplicateAction(@NonNull Application app, @Nullable Node target) {
        super(app, target);
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    @Override
    protected void onActionPerformed(ActionEvent event, @NonNull EditableComponent c) {
        c.duplicateSelection();
    }

}
