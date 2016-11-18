/* @(#)SelectionTool.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.tool;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import org.jhotdraw8.draw.Drawing;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.SimpleDrawingEditor;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.util.Resources;

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
 * the <code>SimpleHandleTracker</code>.
 * <p>
 * A Figure can be selected by clicking at it. Holding the alt key or the ctrl
 * key down, selects the Figure behind it.
 * <p>
 * Holding down the shift key on mouse pressed, enforces the area selection
 * function.
 * 
 * @design.pattern SelectionTool Strategy, Context.
 * The different behavior states of the selection tool are implemented by
 * trackers.
 * @design.pattern HandleTracker Chain of Responsibility, Handler.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SelectionTool extends AbstractTool {
    public final static String ID = "tool.selectFigure";
    // ---
    // Property names
    // ---
    public final static String SELECT_BEHIND_ENABLED = "selectBehindEnabled";
    // ---
    // Fields
    // ---
    private static final long serialVersionUID = 1L;
    /**
     * Look inside a radius of 2 pixels if the mouse click did not hit
     * something.
     */
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
    private HandleType handleType;

    // ---
    // Constructors
    // ---
    public SelectionTool() {
        this("tool.selectFigure", HandleType.RESIZE, Resources.getResources("org.jhotdraw8.draw.Labels"));
    }

    public SelectionTool(String name, Resources rsrc) {
        this(name, HandleType.RESIZE, rsrc);
    }

    public SelectionTool(String name, HandleType handleType, Resources rsrc) {
        super(name, rsrc);
        this.handleType = handleType;
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
    protected void handleMousePressed(MouseEvent event, DrawingView view) {
        requestFocus();
        mouseDragged = false;
        Bounds b = getNode().getBoundsInParent();
        Drawing drawing = view.getDrawing();
        double vx = event.getX();
        double vy = event.getY();

        if (event.isControlDown()) {
            SelectAreaTracker t = getSelectAreaTracker();
            setTracker(t);
        } else {
            Handle h = view.findHandle(vx, vy);
            if (h != null) {
                node.setCursor(h.getCursor());
                setTracker(getHandleTracker(h));
            } else {
                node.setCursor(Cursor.DEFAULT);

                /*
             if (pressedFigure == null && tolerance != 0) {
             List<Figure> fs = view.findFiguresIntersecting(vx - tolerance, vy
             - tolerance, tolerance * 2, tolerance * 2, false);
             if (!fs.isEmpty()) {
             pressedFigure = fs.get(0);
             }
             }*/
                // "alt" modifier selects figure behind.
                if (isSelectBehindEnabled() && (event.isAltDown())) {
                    // Select a figure behind the current selection
                    pressedFigure = null;
                    boolean selectionFound = false;
                    for (Figure f : view.findFigures(vx, vy, false)) {
                        if (view.selectedFiguresProperty().contains(f)) {
                            selectionFound = true;
                            continue;
                        }
                        if (selectionFound) {
                            pressedFigure = f;
                            break;
                        }
                    }
                } else {
                    // find in selection
                    pressedFigure = view.findFigure(vx, vy, view.getSelectedFigures());
                    // find in entire drawing
                    if (pressedFigure == null) {
                        pressedFigure = view.findFigure(vx, vy);
                    }
                }

                // "shift" without "meta" adds the pressed figure to the selection
                if (event.isShiftDown() && !event.isMetaDown()) {
                    if (pressedFigure != null) {
                        view.getSelectedFigures().add(pressedFigure);
                        return;
                    }
                } else if (!event.isShiftDown() && event.isMetaDown()) {
                    // "meta" without "shift"  toggles the selection for the pressed figure
                    if (pressedFigure != null) {
                        if (view.selectedFiguresProperty().contains(pressedFigure)) {
                            view.selectedFiguresProperty().remove(pressedFigure);
                        } else {
                            view.selectedFiguresProperty().add(pressedFigure);
                        }
                        return;
                    }
                } else if (!event.isShiftDown() && !event.isMetaDown()) {
                    // neither "meta" nor "shift" sets the selection to the pressed figure
                    if (pressedFigure != null && !view.selectedFiguresProperty().contains(pressedFigure)) {
                        view.selectedFiguresProperty().clear();
                        view.selectedFiguresProperty().add(pressedFigure);
                    }
                }

                // "control" modifier enforces the select area tracker
                if (view.selectedFiguresProperty().contains(pressedFigure)) {
                    DragTracker t = getDragTracker(pressedFigure, view);
                    setTracker(t);
                } else {
                    SelectAreaTracker t = getSelectAreaTracker();
                    setTracker(t);
                }
            }
        }
        if (tracker != null) {
            tracker.trackMousePressed(event, view);
        }
        fireToolStarted();
    }

    @Override
    protected void handleMouseDragged(MouseEvent event, DrawingView dv) {
        mouseDragged = true;
        if (tracker != null) {
            tracker.trackMouseDragged(event, dv);
        }
    }

    @Override
    protected void handleMouseReleased(MouseEvent event, DrawingView dv) {
        if (tracker != null) {
            tracker.trackMouseReleased(event, dv);
        }
        setTracker(null);
    }

    @Override
    protected void handleMouseMoved(MouseEvent event, DrawingView view) {
        double vx = event.getX();
        double vy = event.getY();
        Handle h = view.findHandle(vx, vy);
        if (h != null) {
            node.setCursor(h.getCursor());
            setTracker(getHandleTracker(h));
        } else {
            node.setCursor(Cursor.DEFAULT);
        }
    }

    protected void handleKeyPressed(KeyEvent event, DrawingView view) {
        if (tracker != null) {
            tracker.trackKeyPressed(event, view);
        }
    }

    protected void handleKeyReleased(KeyEvent event, DrawingView view) {
        if (tracker != null) {
            tracker.trackKeyReleased(event, view);
        }
    }

    protected void handleKeyTyped(KeyEvent event, DrawingView view) {
        if (tracker != null) {
            tracker.trackKeyTyped(event, view);
        }
    }

    /**
     * Method to get a {@code HandleTracker} which handles user interaction for
     * the specified handle.
     *
     * @param handle a handle
     * @return a handle tracker
     */
    protected HandleTracker getHandleTracker(Handle handle) {
        if (handleTracker == null) {
            handleTracker = new SimpleHandleTracker();
        }
        handleTracker.setHandles(handle, getDrawingView().getFiguresWithCompatibleHandle(
                getDrawingView().getSelectedFigures(), handle));
        return handleTracker;
    }

    /**
     * Method to get a {@code DragTracker} which handles user interaction for
     * dragging the specified figure.
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
            drawPane.setCenter(null);
        }
        tracker = t;
        if (tracker != null) {
            drawPane.setCenter(tracker.getNode());
            drawPane.layout();
        }
    }

    @Override
    public void activate(SimpleDrawingEditor editor) {
        for (DrawingView view : editor.getDrawingViews()) {
            view.setHandleType(handleType);
        }
    }

    // ---
    // Convenience Methods
    // ---
    public boolean isSelectBehindEnabled() {
        return selectBehindEnabled.get();
    }
}
