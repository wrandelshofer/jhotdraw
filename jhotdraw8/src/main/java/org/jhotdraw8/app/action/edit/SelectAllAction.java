/* @(#)SelectAllAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.app.action.AbstractFocusOwnerAction;
import org.jhotdraw8.util.Resources;
import org.jhotdraw8.app.Project;

/**
 * Selects all items.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class SelectAllAction extends AbstractSelectionAction {

    private static final long serialVersionUID = 1L;

    public static final String ID = "edit.selectAll";

    /**
     * Creates a new instance which acts on the currently focused component.
     *
     * @param app the application
     */
    public SelectAllAction(Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param app the application
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public SelectAllAction(Application app, Node target) {
        super(app, target);
        Resources.getResources("org.jhotdraw8.app.Labels").configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, EditableComponent c) {
        c.selectAll();
    }

}
