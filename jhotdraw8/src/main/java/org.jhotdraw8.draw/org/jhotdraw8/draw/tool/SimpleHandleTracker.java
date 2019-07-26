/*
 * @(#)SimpleHandleTracker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.handle.Handle;

import java.util.Collection;

/**
 * {@code SimpleHandleTracker} implements interactions with the handles of a
 * Figure.
 * <p>
 * The {@code SimpleHandleTracker} handles one of the three states of the
 * {@code SelectionTool}. Iz comes into action, when the user presses the mouse
 * button over a {@code Figure}.
 * <p>
 * Design pattern:<br>
 * Name: Chain of Responsibility.<br>
 * Role: Handler.<br>
 * Partners: {@link SelectionTool} as Handler, {@link SelectAreaTracker} as
 * Handler, {@link DragTracker} as Handler, {@link HandleTracker} as Handler.
 * <p>
 * Design pattern:<br>
 * Name: State.<br>
 * Role: State.<br>
 * Partners: {@link SelectAreaTracker} as State, {@link DragTracker} as State,
 * {@link SelectionTool} as Context.
 *
 * @author Werner Randelshofer
 * @see SelectionTool
 */
public class SimpleHandleTracker extends AbstractTracker implements HandleTracker {

    private Handle handle;
    private Collection<Figure> compatibleFigures;

    @Override
    public void setHandles(Handle handle, Collection<Figure> compatibleFigures) {
        this.handle = handle;
        this.compatibleFigures = compatibleFigures;
    }

    @Override
    public void trackMousePressed(MouseEvent event, DrawingView dv) {
        handle.handleMousePressed(event, dv);
        node.setCursor(handle.getCursor());
    }

    @Override
    public void trackMouseClicked(MouseEvent event, DrawingView dv) {
        handle.handleMouseClicked(event, dv);
    }

    @Override
    public void trackMouseReleased(MouseEvent event, DrawingView dv) {
        handle.handleMouseReleased(event, dv);
        node.setCursor(handle.getCursor());
    }

    @Override
    public void trackMouseDragged(MouseEvent event, DrawingView dv) {
        handle.handleMouseDragged(event, dv);
        node.setCursor(handle.getCursor());
    }

    @Override
    public void trackKeyPressed(KeyEvent event, DrawingView view) {
    }

    @Override
    public void trackKeyReleased(KeyEvent event, DrawingView view) {
    }

    @Override
    public void trackKeyTyped(KeyEvent event, DrawingView view) {
    }
}
