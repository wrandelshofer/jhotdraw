package org.jhotdraw.draw.action;

import org.jhotdraw.app.Application;
import org.jhotdraw.app.View;
import org.jhotdraw.app.action.AbstractViewAction;
import org.jhotdraw.draw.DrawingEditor;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.EditorView;

/* @(#)AbstractSelectedAction.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
/**
 * This abstract class can be extended to implement an {@code Action} that acts
 * on behalf of the selected figures of a {@link org.jhotdraw.draw.DrawingView}.
 * <p>
 * By default the disabled state of this action reflects the disabled state of
 * the active {@code DrawingView}. If no drawing view is active, this action is
 * disabled.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSelectedAction extends AbstractViewAction {

    private DrawingEditor editor;

    /**
     * Creates an action which acts on the selected figures on the current view
     * of the specified editor.
     *
     * @param app the application
     * @param editor the drawing editor
     */
    public AbstractSelectedAction(Application app, DrawingEditor editor) {
        super(app, null);
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
    protected DrawingView getView() {
        if (editor == null) {
            View v = getActiveView();
            if (v instanceof EditorView) {
                EditorView ev = (EditorView) v;
                return ev.getEditor() != null ? ev.getEditor().getActiveDrawingView() : null;
            }
        }

        return (editor == null) ? null : editor.getActiveDrawingView();
    }
}
