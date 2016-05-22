/* @(#)SimpleDragTracker.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.HashSet;
import java.util.Set;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.model.DrawingModel;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.TransformableFigure;

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
public class SimpleDragTracker extends AbstractTracker implements DragTracker {

    private static final long serialVersionUID = 1L;
    private Set<Figure> groupReshapeableFigures;
    private Figure anchorFigure;
    private Point2D oldPoint;
    private Point2D anchor;

    // --- 
    // Behaviors
    // ---
    @Override
    public void setDraggedFigure(Figure anchor, DrawingView view) {
        this.anchorFigure = anchor;

        // determine which figures can be reshaped together as a group
        Set<Figure> selectedFigures = view.getSelectedFigures();
        groupReshapeableFigures = new HashSet<>();
        for (Figure f : selectedFigures) {
            if (f.isGroupReshapeableWith(selectedFigures)) {
                groupReshapeableFigures.add(f);
            }
        }
    }

    @Override
    public void trackMousePressed(MouseEvent event, DrawingView view) {
        oldPoint = anchor = view.getConstrainer().constrainPoint(anchorFigure, view.viewToWorld(new Point2D(event.getX(), event.getY())));
    }

    @Override
    public void trackMouseReleased(MouseEvent event, DrawingView dv) {
// FIXME fire undoable edit
        //  fireToolDone();
    }

    @Override
    public void trackMouseDragged(MouseEvent event, DrawingView view) {
        Point2D newPoint = view.viewToWorld(new Point2D(event.getX(), event.getY()));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(anchorFigure, newPoint);
        }

        if (event.isMetaDown()) {
            // meta snaps the top left corner of the anchor figure to the grid
            Bounds bounds = anchorFigure.getBoundsInLocal();
            Point2D loc = new Point2D(bounds.getMinX(), bounds.getMinY());
            oldPoint = anchorFigure.localToWorld(loc);
        }

        if (newPoint.equals(oldPoint)) {
            return;
        }

        DrawingModel dm = view.getModel();
        if (event.isShiftDown()) {
            Figure f = anchorFigure;
            Point2D npl = f.worldToParent(newPoint);
            Point2D opl = f.worldToParent(oldPoint);
            if (f instanceof TransformableFigure) {
                Transform tt = ((TransformableFigure) f).getInverseTransform();
                npl = tt.transform(npl);
                opl = tt.transform(opl);
            }
            Transform tx = Transform.translate(npl.getX() - opl.getX(), npl.getY() - opl.getY());
            dm.reshape(f, tx);
        } else {
            for (Figure f : groupReshapeableFigures) {
                Point2D npl = f.worldToParent(newPoint);
                Point2D opl = f.worldToParent(oldPoint);
                if (f instanceof TransformableFigure) {
                    Transform tt = ((TransformableFigure) f).getInverseTransform();
                    npl = tt.transform(npl);
                    opl = tt.transform(opl);
                }
                Transform tx = Transform.translate(npl.getX() - opl.getX(), npl.getY() - opl.getY());
                dm.reshape(f, tx);
            }
        }
        oldPoint = newPoint;
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
