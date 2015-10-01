/* @(#)AbstractDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.model;

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
        case FIGURE_ADDED:
            invalidateStyle(event.getFigure());
            break;
        case FIGURE_REMOVED:
        case NODE_INVALIDATED:
        case ROOT_CHANGED:
        case SUBTREE_NODES_INVALIDATED:
            // not my business
            break;
        case LAYOUT_INVALIDATED:
            invalidateLayout(event.getFigure());
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
        dirtyLayouts.add(figure);
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
                applyCss(f);
            }
            isValidating = false;
        }
        if (!dirtyLayouts.isEmpty()) {
            isValidating = true;
            LinkedList<Figure> fs = new LinkedList<>(dirtyLayouts);
            dirtyLayouts.clear();
            for (Figure f : fs) {
                layout(f);
            }
            isValidating = false;
        }
    }

    /**
     * Fires {@code LayoutInvalidated} for all figure which are transitively
     * connected to the subtree starting at the specified figure, and which
     * are not in the {@code done} set. Handles connection cycles.
     *
     * @param figure the figure
     */
    private void fireLayoutInvalidatedForConnectionsOf(Collection<Figure> todo, HashSet<Figure> done) {
        HashSet<Figure> todoNext = new HashSet<>();
        for (Figure figure : todo) {
            for (Figure c : figure.getConnectedFigures()) {
                if (done.add(c)) {
                    fire(DrawingModelEvent.layoutInvalidated(this, c));
                } else {
                    todoNext.add(c);
                }
            }
        }
        if (!todoNext.isEmpty()) {
            fireLayoutInvalidatedForConnectionsOf(todoNext, done);
        }
    }

    /**
     * Fires {@code LayoutInvalidated} for all figure which are transitively
     * connected to the specified figure. Handles connection cycles.
     *
     * @param figure the figure
     */
    protected void fireLayoutInvalidatedForConnectionsOfFigure(Figure figure) {
        LinkedList<Figure> todo = new LinkedList();
        todo.add(figure);
        fireLayoutInvalidatedForConnectionsOf(todo, new HashSet<Figure>());
    }

    /**
     * Fires {@code LayoutInvalidated} for all figure which are transitively
     * connected to the subtree starting at the specified figure. Handles
     * connection cycles.
     *
     * @param figure the figure
     */
    protected void fireLayoutInvalidatedForConnectionsOfSubtree(Figure figure) {
        LinkedList<Figure> todo = new LinkedList();
        for (Figure f : figure.preorderIterable()) {
            todo.add(f);
        }
        fireLayoutInvalidatedForConnectionsOf(todo, new HashSet<Figure>());
    }
}
