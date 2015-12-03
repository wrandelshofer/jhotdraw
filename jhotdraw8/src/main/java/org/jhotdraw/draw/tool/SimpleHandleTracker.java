/* @(#)SimpleHandleTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.Collection;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.Handle;

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
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleHandleTracker extends AbstractTracker implements HandleTracker {

    private Handle handle;
    protected Collection<Figure> compatibleFigures;

    @Override
    public void setHandles(Handle handle, Collection<Figure> compatibleFigures) {
        this.handle = handle;
        this.compatibleFigures = compatibleFigures;
    }

    @Override
    public void trackMousePressed(MouseEvent event, DrawingView dv) {
        handle.onMousePressed(event, dv);
    }

    @Override
    public void trackMouseReleased(MouseEvent event, DrawingView dv) {
        handle.onMouseReleased(event, dv);
       // fireToolDone();
    }

    @Override
    public void trackMouseDragged(MouseEvent event, DrawingView dv) {
        handle.onMouseDragged(event, dv);
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
