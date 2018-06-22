/* @(#)AbstractDrawingInspector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.model.DrawingModel;

/**
 * AbstractDrawingInspector.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractDrawingInspector implements Inspector {

    @Nullable
    protected DrawingView drawingView;

    private final ChangeListener<Drawing> drawingListener = (ObservableValue<? extends Drawing> o, Drawing oldValue, Drawing newValue) -> {
        onDrawingChanged(oldValue, newValue);
    };

    @Override
    public void setDrawingView(@Nullable DrawingView newValue) {
        DrawingView oldValue = drawingView;
        Drawing oldDrawing = null;
        if (oldValue != null) {
            oldValue.drawingProperty().removeListener(drawingListener);
            oldDrawing = oldValue.getDrawing();
        }
        this.drawingView = newValue;
        Drawing newDrawing = null;
        if (newValue != null) {
            newValue.drawingProperty().addListener(drawingListener);
            newDrawing = newValue.getDrawing();
        }
        onDrawingViewChanged(oldValue, newValue);
        onDrawingChanged(oldDrawing, newDrawing);
    }

    protected DrawingModel getDrawingModel() {
        return drawingView.getModel();
    }

    protected Drawing getDrawing() {
        return drawingView.getDrawing();
    }

    /**
     * Can be overridden by subclasses. This implementation is empty.
     *
     * @param oldValue the old drawing view
     * @param newValue the new drawing view
     */
    protected void onDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {

    }

    /**
     * Must be implemented by subclasses.
     *
     * @param oldValue the old drawing
     * @param newValue the new drawing
     */
    protected abstract void onDrawingChanged(Drawing oldValue, Drawing newValue);
}
