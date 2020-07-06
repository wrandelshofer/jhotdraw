/*
 * @(#)AbstractSelectionAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.TextInputControl;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.app.action.AbstractApplicationAction;

/**
 * {@code AbstractSelectionAction} acts on the selection of a target component
 * or of the currently focused component in the application.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSelectionAction extends AbstractApplicationAction {

    @Nullable
    private final Node target;


    /**
     * Creates a new instance.
     *
     * @param application the application
     */
    public AbstractSelectionAction(@NonNull Application application) {
        this(application, null);
    }

    /**
     * Creates a new instance.
     *
     * @param application the application
     * @param target      the target node
     */
    public AbstractSelectionAction(@NonNull Application application, @Nullable Node target) {
        super(application);
        this.target = target;
    }

    @Nullable
    public EditableComponent getEditableComponent() {
        if (target != null) {
            return tryAsEditableComponent(target);
        }

        Activity v = app.getActiveActivity();
        if (v != null && !v.isDisabled()) {
            Node n = v.getNode().getScene().getFocusOwner();
            while (n != null) {
                EditableComponent editableComponent = tryAsEditableComponent(n);
                if (editableComponent != null) {
                    return editableComponent;
                }
                n = n.getParent();
            }
        }
        return null;
    }

    @Nullable
    private EditableComponent tryAsEditableComponent(Node n) {
        if (n instanceof TextInputControl) {
            TextInputControl tic = (TextInputControl) n;
            return new TextInputControlAdapter(tic);
        } else if (n instanceof EditableComponent) {
            EditableComponent tic = (EditableComponent) n;
            return tic;
        } else if (n.getProperties().get(EditableComponent.EDITABLE_COMPONENT) instanceof EditableComponent) {
            EditableComponent tic = (EditableComponent) n.getProperties().get(EditableComponent.EDITABLE_COMPONENT);
            return tic;
        } else {
            return null;
        }
    }

    @Override
    protected final void onActionPerformed(@NonNull ActionEvent event, @NonNull Application application) {
        EditableComponent ec = getEditableComponent();
        if (ec != null) {
            onActionPerformed(event, ec);
        }
    }

    protected abstract void onActionPerformed(ActionEvent event, EditableComponent ec);

    private static class TextInputControlAdapter implements EditableComponent {

        final TextInputControl control;

        public TextInputControlAdapter(TextInputControl control) {
            this.control = control;
        }

        @Override
        public void clearSelection() {
            control.selectRange(control.getCaretPosition(), control.getCaretPosition());
        }

        @Override
        public void copy() {
            control.copy();
        }

        @Override
        public void cut() {
            control.cut();
        }

        @Override
        public void deleteSelection() {
            control.deleteText(control.getSelection());
        }

        @Override
        public void duplicateSelection() {
            control.insertText(control.getCaretPosition(), control.getSelectedText());
        }

        @Override
        public void paste() {
            control.paste();
        }

        @Override
        public void selectAll() {
            control.selectAll();
        }

        @NonNull
        @Override
        public ReadOnlyBooleanProperty selectionEmptyProperty() {
            throw new UnsupportedOperationException("unsupported");
        }

    }
}
