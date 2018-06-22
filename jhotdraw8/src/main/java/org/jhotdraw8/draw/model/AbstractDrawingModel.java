/* @(#)AbstractDrawingModel.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.model;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.tree.AbstractTreeModel;
import java.util.concurrent.CopyOnWriteArrayList;
import javafx.beans.InvalidationListener;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.event.Listener;

/**
 * AbstractDrawingModel.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractDrawingModel extends AbstractTreeModel<Figure> implements DrawingModel {

    private final CopyOnWriteArrayList<Listener<DrawingModelEvent>> drawingModelListeners = new CopyOnWriteArrayList<>();

    @NonNull
    @Override
    final public CopyOnWriteArrayList<Listener<DrawingModelEvent>> getDrawingModelListeners() {
        return drawingModelListeners;
    }
}
