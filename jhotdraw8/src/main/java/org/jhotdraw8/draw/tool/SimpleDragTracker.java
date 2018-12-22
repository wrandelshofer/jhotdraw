/* @(#)SimpleDragTracker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.AnchorableFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;
import org.jhotdraw8.geom.Geom;

import org.jhotdraw8.annotation.Nonnull;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import static org.jhotdraw8.draw.handle.MoveHandle.translateFigure;

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
 * @author Werner Randelshofer
 * @version $Id$
 * @see SelectionTool
 */
public class SimpleDragTracker extends AbstractTracker implements DragTracker {

    private static final long serialVersionUID = 1L;
    private Set<Figure> groupReshapeableFigures;
    private Figure anchorFigure;
    private CssPoint2D oldPoint;
    private CssPoint2D anchor;

    // --- 
    // Behaviors
    // ---
    @Override
    public void setDraggedFigure(Figure anchor, @Nonnull DrawingView view) {
        this.anchorFigure = anchor;

        // determine which figures can be reshaped together as a group
        Set<Figure> selectedFigures = view.getSelectedFigures();
        groupReshapeableFigures = new HashSet<>();
        for (Figure f : selectedFigures) {
            if (f.isGroupReshapeableWith(selectedFigures)) {
                groupReshapeableFigures.add(f);
            }
        }

        // if the layout of the anchor figure does not depend on the layout of other figures,
        // remove all figures that do depend from the group
        if (anchor.getLayoutSubjects().isEmpty()) {
            for (Iterator<Figure> i = groupReshapeableFigures.iterator();i.hasNext();) {
                Figure f=i.next();
                if (!f.getLayoutSubjects().isEmpty()) {
                    i.remove();
                }
            }
        }
    }

    @Override
    public void trackMousePressed(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        oldPoint = anchor = view.getConstrainer().constrainPoint(anchorFigure,
                new CssPoint2D(view.viewToWorld(new Point2D(event.getX(), event.getY()))));
    }

    @Override
    public void trackMouseReleased(MouseEvent event, DrawingView dv) {
// FIXME fire undoable edit
        dv.recreateHandles();
        //  fireToolDone();
    }

    @Override
    public void trackMouseClicked(MouseEvent event, DrawingView dv) {
    }

    @Override
    public void trackMouseDragged(@Nonnull MouseEvent event, @Nonnull DrawingView view) {
        CssPoint2D newPoint = new CssPoint2D(view.viewToWorld(new Point2D(event.getX(), event.getY())));

        if (!event.isAltDown() && !event.isControlDown()) {
            // alt or control turns the constrainer off
            newPoint = view.getConstrainer().constrainPoint(anchorFigure, newPoint);
        }

        if (event.isMetaDown()) {
            // meta snaps the top left corner of the anchor figure to the grid
            // or whatever corner is specified in the anchor
            Bounds bounds = anchorFigure.getBoundsInLocal();

            double anchorX = Geom.clamp(anchorFigure.getNonnull(AnchorableFigure.ANCHOR_X), 0, 1);
            double anchorY = Geom.clamp(anchorFigure.getNonnull(AnchorableFigure.ANCHOR_Y), 0, 1);

            Point2D loc = new Point2D(bounds.getMinX() + anchorX * bounds.getWidth(),
                    bounds.getMinY() + anchorY * bounds.getHeight());
            oldPoint = new CssPoint2D(anchorFigure.localToWorld(loc));
        }

        if (newPoint.equals(oldPoint)) {
            return;
        }

        DrawingModel model = view.getModel();
        if (event.isShiftDown()) {
            // shift transforms only the anchor figure
            Figure f = anchorFigure;
            translateFigure(f, oldPoint, newPoint, model);
        } else {
            for (Figure f : groupReshapeableFigures) {
                translateFigure(f, oldPoint, newPoint, model);
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
