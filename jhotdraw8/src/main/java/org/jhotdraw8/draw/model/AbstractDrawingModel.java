/* @(#)AbstractDrawingModel.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.model;

import javax.annotation.Nonnull;
import org.jhotdraw8.tree.AbstractTreeModel;
import java.util.concurrent.CopyOnWriteArrayList;

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

    @Nonnull
    @Override
    final public CopyOnWriteArrayList<Listener<DrawingModelEvent>> getDrawingModelListeners() {
        return drawingModelListeners;
    }
}
