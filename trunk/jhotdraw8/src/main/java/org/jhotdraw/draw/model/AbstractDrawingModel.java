/* @(#)AbstractDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.LinkedList;
import javafx.beans.InvalidationListener;
import org.jhotdraw.beans.ListenerSupport;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.event.Listener;

/**
 * AbstractDrawingModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractDrawingModel implements DrawingModel {

    private final ListenerSupport<Listener<DrawingModelEvent>> listeners = new ListenerSupport<>();
    private final ListenerSupport<InvalidationListener> invalidationListeners = new ListenerSupport<>();
    /**
     * This is the set of figures which are out of sync with their stylesheet.
     */
    private final HashSet<Figure> dirtyStyles = new HashSet<>();
    /**
     * This is the set of figures which are out of sync with their layout.
     */
    private final HashSet<Figure> dirtyLayouts = new HashSet<>();
    /**
     * This is a list of figures which are out of sync with their layout. This
     * list contains the same figures as {@code dirtyLayouts}, but the figures
     * are ordered in the sequence that the layout needs to be performed.
     * This list is required to handle transitive layout dependencies.
     */
    private final ArrayList<Figure> dirtyLayoutList = new ArrayList<>();
    private boolean isValidating = false;
    protected Drawing root;

    @Override
    public Drawing getRoot() {
        return root;
    }

    @Override
    public void addDrawingModelListener(Listener<DrawingModelEvent> listener) {
        listeners.add(listener);
    }

    @Override
    public void removeDrawingModelListener(Listener<DrawingModelEvent> listener) {
        listeners.remove(listener);
    }

    @Override
    public void addListener(InvalidationListener l) {
        invalidationListeners.add(l);
    }

    @Override
    public void removeListener(InvalidationListener l) {
        invalidationListeners.remove(l);
    }

    @Override
    public void fire(DrawingModelEvent event) {
        listeners.fire(l -> l.handle(event));
        invalidationListeners.fire(l -> l.invalidated(this));
        handle(event);
    }

    protected void handle(DrawingModelEvent event) {
        if (isValidating) {
            return;
        }
        switch (event.getEventType()) {
        case FIGURE_ADDED_TO_PARENT:
            invalidateStyle(event.getFigure());
            break;
        case FIGURE_ADDED_TO_DRAWING:
            invokeAddNotify(event.getFigure(), event.getDrawing());
            break;
        case FIGURE_REMOVED_FROM_DRAWING:
            invokeRemoveNotify(event.getFigure(), event.getDrawing());
            if (dirtyLayouts.remove(event.getFigure())) {
                dirtyLayoutList.remove(event.getFigure());
            }
            dirtyStyles.remove(event.getFigure());
            break;
        case CONNECTION_CHANGED:
            invokeConnectNotify(event.getFigure());
            break;
        case TRANSFORM_CHANGED:
            invokeTransformNotify(event.getFigure());
            break;

        case FIGURE_REMOVED_FROM_PARENT:
            if (dirtyLayouts.remove(event.getFigure())) {
                dirtyLayoutList.remove(event.getFigure());
            }
            dirtyStyles.remove(event.getFigure());
            break;
        case NODE_INVALIDATED:
        case ROOT_CHANGED:
        case SUBTREE_NODES_INVALIDATED:
            // not my business
            break;
        case LAYOUT_INVALIDATED:
            invalidateLayout(event.getFigure());
            break;
        case STYLE_INVALIDATED:
            invalidateStyle(event.getFigure());
            break;
        case SUBTREE_STRUCTURE_CHANGED:
            invalidateLayout(event.getFigure());
            break;
        default:
            throw new UnsupportedOperationException(event.getEventType()
                    + "not supported");
        }
    }

    protected void invalidateLayout(Figure figure) {
        if (dirtyLayouts.add(figure)) {
            dirtyLayoutList.add(figure);
        }
    }

    protected void invalidateStyle(Figure figure) {
        dirtyStyles.add(figure);
    }

    @Override
    public void validate() {
        if (!dirtyStyles.isEmpty()) {
            isValidating = true;
            LinkedList<Figure> fs = new LinkedList<>(dirtyStyles);
            dirtyStyles.clear();
            for (Figure f : fs) {
                invokeStylesheetNotify(f);
            }
            isValidating = false;
        }
        if (!dirtyLayouts.isEmpty()) {
            isValidating = true;
            ArrayList<Figure> fs = new ArrayList<>(dirtyLayoutList);
            dirtyLayouts.clear();
            dirtyLayoutList.clear();
            for (Figure f : fs) {
                invokeLayoutNotify(f);
            }
            isValidating = false;
        }
    }

    /**
     * Fires {@code LayoutInvalidated} for all figure which are transitively
     * connected to the figures in the {@code todo} list, and which are not in
     * the {@code done} list. Handles connection cycles.
     *
     * @param todo the todo list
     * @param done the done list
     */
    private void fireLayoutInvalidatedForFiguresConnectedWithTodo(Collection<Figure> todo, HashSet<Figure> done) {
        HashSet<Figure> todoNext = new HashSet<>();
        for (Figure figure : todo) {
            for (Figure c : figure.getConnectedFigures()) {
                if (done.add(c)) {
                    fire(DrawingModelEvent.layoutInvalidated(this, c));
                    todoNext.add(c);
                }
            }
        }
        if (!todoNext.isEmpty()) {
            fireLayoutInvalidatedForFiguresConnectedWithTodo(todoNext, done);
        }
    }

    /**
     * Fires {@code LayoutInvalidated} for all figure which are transitively
     * connected to the specified figure. Handles connection cycles.
     *
     * @param figure the figure
     */
    protected void fireLayoutInvalidatedConnectedFigures(Figure figure) {
        LinkedList<Figure> todo = new LinkedList<>();
        todo.add(figure);
        fireLayoutInvalidatedForFiguresConnectedWithTodo(todo, new HashSet<Figure>());
    }

    /**
     * Fires {@code LayoutInvalidated} for all figure which are transitively
     * connected to the subtree starting at the specified figure. Handles
     * connection cycles.
     *
     * @param subtreeRoot the figure
     */
    protected void fireLayoutInvalidatedForFiguresConnectedWithSubtree(Figure subtreeRoot) {
        LinkedList<Figure> todo = new LinkedList<>();
        for (Figure f : subtreeRoot.preorderIterable()) {
            todo.add(f);
        }
        fireLayoutInvalidatedForFiguresConnectedWithTodo(todo, new HashSet<Figure>());
    }

    /**
     * Invokes {@code removeNotify} on the figure.
     *
     * @param figure the figure
     * @param drawing the drawing
     */
    private void invokeRemoveNotify(Figure figure, Drawing drawing) {
        figure.removeNotify(drawing);
    }

    /**
     * Invokes {@code addNotify} on the figure.
     *
     * @param figure the figure
     * @param drawing the drawing
     */
    private void invokeAddNotify(Figure figure, Drawing drawing) {
        figure.addNotify(drawing);
    }

    /**
     * * Invokes {@code connectNotify} on the figure.
     *
     * @param figure the figure
     * @param drawing the drawing
     */
    private void invokeConnectNotify(Figure figure) {
        figure.connectNotify();
    }

    /**
     * Invokes {@code transformNotify} on the figure and all its descendants.
     *
     * @param figure the figure
     * @param drawing the drawing
     */
    private void invokeTransformNotify(Figure figure) {
        for (Figure f : figure.preorderIterable()) {
            figure.transformNotify();
        }
    }

    public void invokeLayoutNotify(Figure figure) {
        figure.layoutNotify();
        fire(DrawingModelEvent.transformChanged(this, figure));
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }

    public void invokeStylesheetNotify(Figure figure) {
        figure.stylesheetNotify();
        fire(DrawingModelEvent.subtreeNodesInvalidated(this, figure));
        fireLayoutInvalidatedForFiguresConnectedWithSubtree(figure);
    }
}
