/*
 * @(#)AbstractSelectedAction.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.action;

import javafx.beans.property.SimpleBooleanProperty;
import javafx.event.ActionEvent;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.app.action.AbstractAction;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;

/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on behalf of the selected figures of a
 * {@link org.jhotdraw8.draw.DrawingView}.
 * <p>
 * By default the disabled state of this action reflects the disabled state of
 * the active {@code DrawingView}. If no drawing view is active, this action is
 * disabled.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractDrawingViewAction extends AbstractAction {

    @NonNull
    private final DrawingEditor editor;

    /**
     * Creates an action which acts on the selected figures on the current view
     * of the specified editor.
     *
     * @param editor the drawing editor
     */
    public AbstractDrawingViewAction(@NonNull DrawingEditor editor) {
        this.editor = editor;

        // If the editor has no active drawing view, or the drawing view is disabled,
        // we add the editor as a disabler to this action.
        SimpleBooleanProperty editorHasNoDrawingViewOrDrawingViewIsDisabledProperty = new SimpleBooleanProperty();
        CustomBinding.bind(editorHasNoDrawingViewOrDrawingViewIsDisabledProperty, editor.activeDrawingViewProperty(), drawingView -> drawingView == null ? new SimpleBooleanProperty(true) : drawingView.getNode().disableProperty());
        CustomBinding.bindMembershipToBoolean(disablers(), new Object(), editorHasNoDrawingViewOrDrawingViewIsDisabledProperty);
    }

    /**
     * Gets the drawing editor.
     *
     * @return the drawing editor
     */
    public @NonNull DrawingEditor getEditor() {
        return editor;
    }

    /**
     * Gets the active drawing view of the drawing editor.
     *
     * @return the active drawing view. Returns null if the editor is null no
     * drawing view is active.
     */
    @Nullable
    protected DrawingView getView() {
        return editor.getActiveDrawingView();
    }

    @Override
    protected void onActionPerformed(@NonNull ActionEvent event) {
        DrawingView view = getView();
        if (view != null) {
            onActionPerformed(event, view);
        }
    }

    protected abstract void onActionPerformed(@NonNull ActionEvent even, @NonNull DrawingView view);
}
