/* @(#)AbstractDrawingModel.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.model;

import org.jhotdraw.draw.model.DrawingModelEvent;
import org.jhotdraw.draw.model.DrawingModel;
import javafx.beans.InvalidationListener;
import org.jhotdraw.beans.ListenerSupport;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Drawing;
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
    }
}
