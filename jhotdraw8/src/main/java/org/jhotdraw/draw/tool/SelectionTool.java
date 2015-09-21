/* @(#)SelectionTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.util.List;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.util.Resources;

/**
 * A tool to select and manipulate figures.
 * <p>
 * A selection tool is in one of three states:
 * <ol>
 * <li>area selection</li>
 * <li>figure dragging</li>
 * <li>handle manipulation</li>
 * </ol>
 * The different states are handled by different tracker objects: the
 * <code>SimpleSelectAreaTracker</code>, the <code>SimpleDragTracker</code> and
 * the
 * <code>SimpleHandleTracker</code>.
 * <p>
 * A Figure can be selected by clicking at it. Holding the alt key or the
 * ctrl key down, selects the Figure behind it.
 * <p>
 * Holding down the shift key on mouse pressed, enforces the area selection
 * function.
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p>
 * <em>Strategy</em><br>
 * The different behavior states of the selection tool are implemented by
 * trackers.<br>
 * Context: {@link SelectionTool}; State: {@link DragTracker},
 * {@link HandleTracker}, {@link SelectAreaTracker}.
 *
 * <p>
 * <em>Chain of responsibility</em><br>
 * Mouse and keyboard events of the user occur on the drawing view, and are
 * preprocessed by the {@code DragTracker} of a {@code SelectionTool}. In
 * turn {@code DragTracker} invokes "track" methods on a {@code Handle} which in
 * turn changes an aspect of a figure.<br>
 * Client: {@link SelectionTool}; Handler: {@link DragTracker}, {@link Handle}.
 * <hr>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectionTool extends AbstractTool {

    // ---
    // Property names
    // ---
    public final static String SELECT_BEHIND_ENABLED = "selectBehindEnabled";
    // ---
    // Fields
    // ---
    private static final long serialVersionUID = 1L;
    /** Look inside a radius of 2 pixels if the mouse click did not hit
     * something. */
    private final double tolerance = 2;
    /**
     * The tracker encapsulates the current state of the SelectionTool.
     */
    private Tracker tracker;
    /**
     * The tracker encapsulates the current state of the SelectionTool.
     */
    private HandleTracker handleTracker;
    /**
     * The tracker encapsulates the current state of the SelectionTool.
     */
    private SelectAreaTracker selectAreaTracker;
    /**
     * The tracker encapsulates the current state of the SelectionTool.
     */
    private DragTracker dragTracker;

    private final BooleanProperty selectBehindEnabled = new SimpleBooleanProperty(this, SELECT_BEHIND_ENABLED, true);
    private boolean mouseDragged;
    private Figure pressedFigure;

    // ---
    // Constructors
    // ---
    public SelectionTool() {
        this("selectionTool", Resources.getResources("org.jhotdraw.draw.Labels"));
    }

    public SelectionTool(String name, Resources rsrc) {
        super(name, rsrc);
    }

    // ---
    // Properties
    // ---
    public BooleanProperty selectBehindEnabledProperty() {
        return selectBehindEnabled;
    }

    // ---
    // Behaviors
    // ---
    @Override
    protected void stopEditing() {
        setTracker(null);
    }

    @Override
    protected void onMousePressed(MouseEvent evt, DrawingView view) {
        mouseDragged = false;
        Bounds b = getNode().getBoundsInParent();
        Drawing drawing = view.getDrawing();
        double vx = evt.getX();
        double vy = evt.getY();
        pressedFigure = view.findFigure(vx, vy);
        if (pressedFigure == null) {
            List<Figure> fs = view.findFiguresIntersecting(vx - tolerance, vy
                    - tolerance, tolerance * 2, tolerance * 2);
            if (!fs.isEmpty()) {
                pressedFigure = fs.get(0);
            }
        }
        if (isSelectBehindEnabled() && (evt.isShiftDown())) {
            // Select a figure behind the current selection
            // FIXME implement me - this is just a stub and selects just the figure
            //         behind the front most figure
            pressedFigure = view.findFigureBehind(vx, vy, pressedFigure);
        }
        if (pressedFigure != null
                && (!(evt.isAltDown() || evt.isControlDown())
                || view.getSelectedFigures().contains(pressedFigure))) {
            DragTracker t = getDragTracker(pressedFigure, view);
            setTracker(t);
        } else {
            SelectAreaTracker t = getSelectAreaTracker();
            setTracker(t);
        }
        if (tracker != null) {
            tracker.trackMousePressed(evt, view);
        }
        fireToolStarted();
    }

    @Override
    protected void onMouseDragged(MouseEvent event, DrawingView dv) {
        mouseDragged = true;
        if (tracker != null) {
            tracker.trackMouseDragged(event, dv);
        }
    }

    @Override
    protected void onMouseReleased(MouseEvent event, DrawingView dv) {
        if (tracker != null) {
            tracker.trackMouseReleased(event, dv);
        }
        setTracker(null);

        if (!mouseDragged && pressedFigure != null) {
            dv.selectionProperty().add(pressedFigure);
        }

        fireToolDone();
    }

    /**
     * Method to get a {@code HandleTracker} which handles user interaction
     * for the specified handle.
     *
     * @param handle a handle
     * @return a handle tracker
     */
    protected HandleTracker getHandleTracker(Handle handle) {
        if (handleTracker == null) {
            handleTracker = new SimpleHandleTracker();
        }
        handleTracker.setHandles(handle, getDrawingView().getCompatibleHandles(handle));
        return handleTracker;
    }

    /**
     * Method to get a {@code DragTracker} which handles user interaction
     * for dragging the specified figure.
     *
     * @param f a figure
     * @param dv a drawing view
     * @return a tracker
     */
    protected DragTracker getDragTracker(Figure f, DrawingView dv) {
        if (dragTracker == null) {
            dragTracker = new SimpleDragTracker();
        }
        dragTracker.setDraggedFigure(f, dv);
        return dragTracker;
    }

    /**
     * Method to get a {@code SelectAreaTracker} which handles user interaction
     * for selecting an area on the drawing.
     *
     * @return a tracker
     */
    protected SelectAreaTracker getSelectAreaTracker() {
        if (selectAreaTracker == null) {
            selectAreaTracker = new SimpleSelectAreaTracker();
        }
        return selectAreaTracker;
    }

    /**
     * Method to set a {@code HandleTracker}. If you specify null, the
     * {@code SelectionTool} uses the {@code DefaultHandleTracker}.
     *
     * @param newValue a tracker
     */
    public void setHandleTracker(HandleTracker newValue) {
        handleTracker = newValue;
    }

    /**
     * Method to set a {@code SelectAreaTracker}. If you specify null, the
     * {@code SelectionTool} uses the {@code DefaultSelectAreaTracker}.
     *
     * @param newValue a tracker
     */
    public void setSelectAreaTracker(SelectAreaTracker newValue) {
        selectAreaTracker = newValue;
    }

    /**
     * Method to set a {@code DragTracker}. If you specify null, the
     * {@code SelectionTool} uses the {@code DefaultDragTracker}.
     *
     * @param newValue a tracker
     */
    public void setDragTracker(DragTracker newValue) {
        dragTracker = newValue;
    }

    private void setTracker(Tracker t) {

        if (tracker != null) {
            node.setCenter(null);
        }
        tracker = t;
        if (tracker != null) {
            node.setCenter(tracker.getNode());
            node.layout();
        }
    }

    // ---
    // Convenience Methods
    // ---
    public boolean isSelectBehindEnabled() {
        return selectBehindEnabled.get();
    }
}
