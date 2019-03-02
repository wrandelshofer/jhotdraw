/* @(#)DeleteAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.app.EditableComponent;

/**
 * Deletes the region at (or after) the caret position.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DeleteAction extends AbstractSelectionAction {

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
    public DeleteAction(Application app) {
        super(app);
        ApplicationLabels.getResources().configureAction(this, ID);
    }

    @Override
    protected void handleActionPerformed(ActionEvent event, @Nonnull EditableComponent c) {
        c.deleteSelection();
    }

}
