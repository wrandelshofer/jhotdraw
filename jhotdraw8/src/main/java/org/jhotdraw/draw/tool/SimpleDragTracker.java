/* @(#)SimpleDragTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.NonInvertibleTransformException;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.model.DrawingModel;
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
    private Point2D oldPoint;
    private Point2D anchor;

    // --- 
    // Behaviors
    // ---
    @Override
    public void setDraggedFigure(Figure f, DrawingView view) {
        anchorFigure = f;
    }

    @Override
    public void trackMousePressed(MouseEvent event, DrawingView view) {
         oldPoint = anchor = view.getConstrainer().constrainPoint(anchorFigure,view.viewToDrawing(new Point2D(event.getX(),event.getY())));
   }

    @Override
    public void trackMouseReleased(MouseEvent event, DrawingView dv) {
// FIXME fire undoable edit
        fireToolDone();
    }

    @Override
    public void trackMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToDrawing(new Point2D(event.getX(), event.getY()));
        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(anchorFigure, newPoint);
        }
        if (event.isMetaDown()) {
            // meta snaps the center of the anchor figure to the grid
            Bounds b = anchorFigure.getBoundsInLocal();
            Point2D loc = new Point2D( b.getMinX()+b.getWidth()/2,b.getMinY()+b.getHeight()/2);
            oldPoint = anchorFigure.localToDrawing(loc);
        }

        Transform tx = Transform.translate(newPoint.getX() - oldPoint.getX(), newPoint.getY()- oldPoint.getY());
        DrawingModel dm = view.getModel();
        for (Figure f : view.getSelectedFigures()) {
            dm.reshape(f, f.getDrawingToParent().createConcatenation(tx));
        }

        oldPoint = newPoint;
    }

}
