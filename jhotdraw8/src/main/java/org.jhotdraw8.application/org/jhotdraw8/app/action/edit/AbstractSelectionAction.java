/*
 * @(#)AbstractSelectionAction.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.app.action.edit;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ReadOnlyBooleanProperty;
import javafx.beans.value.ChangeListener;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.TextInputControl;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.Activity;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.EditableComponent;
import org.jhotdraw8.app.action.AbstractApplicationAction;

/**
 * {@code AbstractSelectionAction} acts on the selection of a target component.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSelectionAction extends AbstractApplicationAction {

    private static final long serialVersionUID = 1L;
    private Node target;
    @Nullable
    private final ChangeListener<Activity> activeViewListener = (observable, oldValue, newValue) -> {
        disabled.unbind();
        if (newValue == null || newValue.getNode() == null) {
            disabled.set(true);
        } else {
            Scene s = newValue.getNode().getScene();
            if (target == null) {
                disabled.bind(
                        s.focusOwnerProperty().isNull().or(app.disabledProperty()).or(newValue.disabledProperty()).or(Bindings.isNotEmpty(disablers)));
            } else {
                disabled.bind(
                        s.focusOwnerProperty().isNotEqualTo(target).or(app.disabledProperty()).or(newValue.disabledProperty()).or(Bindings.isNotEmpty(disablers)));
            }
        }
    };

    /**
     * Creates a new instance.
     *
     * @param app the application
     */
    public AbstractSelectionAction(@Nonnull Application app) {
        this(app, null);
    }

    /**
     * Creates a new instance.
     *
     * @param app    the application
     * @param target the target node
     */
    public AbstractSelectionAction(@Nonnull Application app, Node target) {
        super(app);
        this.target = target;

        app.activeViewProperty().addListener(activeViewListener);
        activeViewListener.changed(null, null, app.getActiveView());

    }

    @Nullable
    public EditableComponent getEditableComponent() {
        Activity v = app.getActiveView();
        if (v != null && !v.isDisabled()) {
            Node n = v.getNode().getScene().getFocusOwner();
            while (n != null) {
                if (n instanceof TextInputControl) {
                    TextInputControl tic = (TextInputControl) n;
                    return new TextInputControlAdapter(tic);
                } else if (n instanceof EditableComponent) {
                    EditableComponent tic = (EditableComponent) n;
                    return tic;
                } else if (n.getProperties().get(EditableComponent.EDITABLE_COMPONENT) instanceof EditableComponent) {
                    EditableComponent tic = (EditableComponent) n.getProperties().get(EditableComponent.EDITABLE_COMPONENT);
                    return tic;
                }
                n = n.getParent();
            }
        }
        return null;
    }

    @Override
    protected final void handleActionPerformed(ActionEvent event, Application app) {
        EditableComponent ec = getEditableComponent();
        if (ec != null) {
            handleActionPerformed(event, ec);
        }
    }

    protected abstract void handleActionPerformed(ActionEvent event, EditableComponent ec);

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

        @Nonnull
        @Override
        public ReadOnlyBooleanProperty selectionEmptyProperty() {
            throw new UnsupportedOperationException("unsupported");
        }

    }
}
