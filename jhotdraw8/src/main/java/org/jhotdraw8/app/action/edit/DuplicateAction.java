/* @(#)DuplicateAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javax.annotation.Nonnull;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.app.Labels;
import org.jhotdraw8.util.Resources;

/**
 * Duplicates the selected region.
 * <p>
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class DuplicateAction extends AbstractSelectionAction {

    private static final long serialVersionUID = 1L;
    public static final String ID = "edit.duplicate";

    /**
     * Creates a new instance which acts on the currently focused component.
     *
     * @param app the application
     */
    public DuplicateAction(Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance which acts on the specified component.
     *
     * @param app the application
     * @param target The target of the action. Specify null for the currently
     * focused component.
     */
    public DuplicateAction(Application app, Node target) {
        super(app, target);
        Labels.getLabels().configureAction(this, ID);
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
    protected void handleActionPerformed(ActionEvent event, @Nonnull EditableComponent c) {
        c.duplicateSelection();
    }

}
