package org.jhotdraw8.draw.action;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.app.ActivityViewController;
import org.jhotdraw8.app.Application;
import org.jhotdraw8.app.action.AbstractViewControllerAction;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.EditorView;

/* @(#)AbstractSelectedAction.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
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
 * @version $Id$
 */
public abstract class AbstractSelectedAction extends AbstractViewControllerAction<ActivityViewController> {

    private DrawingEditor editor;

    /**
     * Creates an action which acts on the selected figures on the current view
     * of the specified editor.
     *
     * @param app the application
     * @param editor the drawing editor
     */
    public AbstractSelectedAction(@Nonnull Application app, DrawingEditor editor) {
        super(app, null,null);
        setEditor(editor);
    }

    /**
     * Sets the drawing editor.
     *
     * @param newValue the drawing editor
     */
    public void setEditor(DrawingEditor newValue) {
        // FIXME register and unregister listeners and update disabled state
        DrawingEditor oldValue = editor;
        this.editor = newValue;
    }

    /**
     * Gets the drawing editor.
     *
     * @return the drawing editor
     */
    public DrawingEditor getEditor() {
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
        if (editor == null) {
            ActivityViewController v = getActiveView();
            if (v instanceof EditorView) {
                EditorView ev = (EditorView) v;
                return ev.getEditor() != null ? ev.getEditor().getActiveDrawingView() : null;
            }
        }

        return (editor == null) ? null : editor.getActiveDrawingView();
    }
}
