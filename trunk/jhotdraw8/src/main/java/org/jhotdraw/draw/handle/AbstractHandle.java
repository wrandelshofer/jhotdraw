/* @(#)AbstractHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw.handle;

import java.util.LinkedList;
import javafx.beans.InvalidationListener;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;
import org.jhotdraw.draw.DrawingModel;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.SimpleDrawingModel;
import org.jhotdraw.event.Listener;

/**
 * AbstractHandle.
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractHandle implements Handle {
    // ---
    // Fields
    // ---
    protected final Figure figure;
    protected final DrawingView dv;
    private DrawingModel dm;
    private InvalidationListener handler = o -> updateNode();
    /** Listeners. */
    private final LinkedList<Listener<org.jhotdraw.draw.handle.HandleEvent>> handleListeners = new LinkedList<>();

    // ---
    // Constructors
    // ---
    public AbstractHandle(Figure figure, DrawingView dv) {
        this.figure = figure;
        this.dv = dv;
        dm = new SimpleDrawingModel(figure);
        dm.addListener(handler);
        dv.drawingToViewProperty().addListener(handler);
        updateNode();
    }

    // ---
    // Behavior
    // ---
    @Override
    public final void dispose() {
        dm.removeListener(handler);
        dm.setRoot(null);
        dv.drawingToViewProperty().removeListener(handler);
    }

    @Override
    public Figure getFigure() {
       return figure;
    }

    /**
     * Returns true, if the given handle is an instance of the same
     * class or of a subclass of this handle.
     * @param handle
     */
    @Override
    public boolean isCombinableWith(Handle handle) {
        return getClass().isAssignableFrom(handle.getClass());
    }

    protected abstract void updateNode();
    
    // ---
    // Listeners
    // ---
    @Override
    public void addHandleListener(Listener<org.jhotdraw.draw.handle.HandleEvent> listener) {
        handleListeners.add(listener);
    }

    @Override
    public void removeHandleListener(Listener<org.jhotdraw.draw.handle.HandleEvent> listener) {
        handleListeners.remove(listener);
    }
    private void fire(HandleEvent event) {
        for (Listener<HandleEvent> l : handleListeners) {
            l.handle(event);
        }
    }
}
