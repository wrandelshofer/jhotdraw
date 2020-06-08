/*
 * @(#)AbstractDrawingModel.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.model;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.tree.AbstractTreeModel;

import java.util.concurrent.CopyOnWriteArrayList;

/**
 * AbstractDrawingModel.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractDrawingModel extends AbstractTreeModel<Figure> implements DrawingModel {

    private final CopyOnWriteArrayList<Listener<DrawingModelEvent>> drawingModelListeners = new CopyOnWriteArrayList<>();

    @NonNull
    @Override
    final public CopyOnWriteArrayList<Listener<DrawingModelEvent>> getDrawingModelListeners() {
        return drawingModelListeners;
    }
}
