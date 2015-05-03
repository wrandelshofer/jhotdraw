/* @(#)SelectionTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.tool;

import java.awt.event.InputEvent;
import java.awt.geom.Point2D;
import java.util.HashSet;
import java.util.Locale;
import java.util.Optional;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.handle.Handle;
import org.jhotdraw.event.Listener;
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
 * <code>SimpleSelectAreaTracker</code>, the <code>SimpleDragTracker</code> and the
 * <code>SimpleHandleTracker</code>.
 * <p>
 * A Figure can be selected by clicking at it. Holding the alt key or the
 * ctrl key down, selects the Figure behind it.
 * <hr>
 * <b>Design Patterns</b>
 *
 * <p><em>Strategy</em><br>
 * The different behavior states of the selection tool are implemented by
 * trackers.<br>
 * Context: {@link SelectionTool}; State: {@link DragTracker},
 * {@link HandleTracker}, {@link SelectAreaTracker}.
 *
 * <p><em>Chain of responsibility</em><br>
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

    // ---
    // Constructors
    // ---
    public SelectionTool() {
        this("selectionTool", Resources.getBundle("org.jhotdraw.draw.Labels"));
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
        Bounds b = getNode().getBoundsInParent();
        Drawing drawing = view.getDrawing();
        double vx = evt.getX();
        double vy = evt.getY();
        Optional<Figure> figure = view.findFigure(vx, vy);
        if (isSelectBehindEnabled() && (evt.isAltDown() || evt.isControlDown())) {
            // Select a figure behind the current selection
            // FIXME implement me - this is just a stub and selects just the figure
            //         behind the front most figure
            figure = view.findFigureBehind(vx, vy, figure.get());
        }

        if (figure.isPresent()) {
            DragTracker t = getDragTracker(figure.get());
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
        fireToolDone();
    }

    /**
     * Method to get a {@code HandleTracker} which handles user interaction
     * for the specified handle.
     */
    protected HandleTracker getHandleTracker(Handle handle) {
        if (handleTracker == null) {
            handleTracker = new SimpleHandleTracker();
        }
        handleTracker.setHandles(handle, getDrawingView().get().getCompatibleHandles(handle));
        return handleTracker;
    }

    /**
     * Method to get a {@code DragTracker} which handles user interaction
     * for dragging the specified figure.
     */
    protected DragTracker getDragTracker(Figure f) {
        if (dragTracker == null) {
            dragTracker = new SimpleDragTracker();
        }
        dragTracker.setDraggedFigure(f);
        return dragTracker;
    }

    /**
     * Method to get a {@code SelectAreaTracker} which handles user interaction
     * for selecting an area on the drawing.
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
     */
    public void setHandleTracker(HandleTracker newValue) {
        handleTracker = newValue;
    }

    /**
     * Method to set a {@code SelectAreaTracker}. If you specify null, the
     * {@code SelectionTool} uses the {@code DefaultSelectAreaTracker}.
     */
    public void setSelectAreaTracker(SelectAreaTracker newValue) {
        selectAreaTracker = newValue;
    }

    /**
     * Method to set a {@code DragTracker}. If you specify null, the
     * {@code SelectionTool} uses the {@code DefaultDragTracker}.
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
