/* @(#)AbstractDrawingModel.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.model;

import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import org.jhotdraw.event.Listener;

/**
 * AbstractDrawingModel.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public abstract class AbstractDrawingModel implements DrawingModel {
    private final CopyOnWriteArrayList<Listener<DrawingModelEvent>> drawingModelListeners = new CopyOnWriteArrayList<>();
    private final CopyOnWriteArrayList<InvalidationListener> invalidationListeners = new CopyOnWriteArrayList<>();

    @Override
    public CopyOnWriteArrayList<Listener<DrawingModelEvent>> getDrawingModelListeners() {
        return drawingModelListeners;
    }

    @Override
    public CopyOnWriteArrayList<InvalidationListener> getInvalidationListeners() {
        return invalidationListeners;
    }
}
