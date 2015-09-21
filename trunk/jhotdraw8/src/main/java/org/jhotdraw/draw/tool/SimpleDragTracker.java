/* @(#)SimpleDragTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.DrawingModel;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

/**
 * |@code SimpleDragTracker} implements interactions with the content area of a
 * {@code Figure}.
 * <p>
 * The {@code DefaultDragTracker} handles one of the three states of the
 * {@code SelectionTool}. It comes into action, when the user presses the mouse
 * button over the content area of a {@code Figure}.
 * <p>
 * Design pattern:<br>
 * Name: Chain of Responsibility.<br>
 * Role: Handler.<br>
 * Partners: {@link SelectionTool} as Handler, {@link SelectAreaTracker} as
 * Handler, {@link HandleTracker} as Handler.
 * <p>
 * Design pattern:<br>
 * Name: State.<br>
 * Role: State.<br>
 * Partners: {@link SelectAreaTracker} as State, {@link SelectionTool} as
 * Context, {@link HandleTracker} as State.
 *
 * @see SelectionTool
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDragTracker extends AbstractTool implements DragTracker {

    private static final long serialVersionUID = 1L;
    protected Figure anchorFigure;
    private Point2D previous;
    private Point2D start;

    // --- 
    // Behaviors
    // ---
    @Override
    public void setDraggedFigure(Figure f, DrawingView view) {
        anchorFigure = f;
    }

    @Override
    public void trackMousePressed(MouseEvent evt, DrawingView view) {
        // FIXME implement me properly
        previous = start = view.viewToDrawing(evt.getX(), evt.getY());
    }

    @Override
    public void trackMouseReleased(MouseEvent event, DrawingView dv) {
        fireToolDone();
    }

    @Override
    public void trackMouseDragged(MouseEvent evt, DrawingView dv) {
        // FIXME implement me properly
        Point2D current = dv.viewToDrawing(evt.getX(), evt.getY());
        if (evt.isControlDown()) {
            if (Math.abs(current.getX() - start.getX()) < Math.abs(current.getY() - start.getY())) {
                current = new Point2D(start.getX(), current.getY());
            } else {
                current = new Point2D(current.getX(), start.getY());
            }
        }
        // Convert point into drawing coordinates
        Point2D dp = current.subtract(previous);
        Transform t = Transform.translate(dp.getX(), dp.getY());
        DrawingModel dm = dv.getDrawingModel();
        for (Figure f : dv.getSelectedFigures()) {
            dm.reshape(f, t);
        }

        previous = current;
    }

}
