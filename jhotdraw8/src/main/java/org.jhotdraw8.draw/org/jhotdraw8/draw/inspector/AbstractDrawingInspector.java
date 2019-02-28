/* @(#)AbstractDrawingInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Drawing;
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
    @Nullable
    protected DrawingModel drawingModel;
    @Nullable
    protected Drawing drawing;

    private final ChangeListener<Drawing> drawingListener = (ObservableValue<? extends Drawing> o, Drawing oldValue, Drawing newValue) -> {
        onDrawingChanged(oldValue, newValue);
    };
    private final ChangeListener<DrawingModel> modelListener = (ObservableValue<? extends DrawingModel> o, DrawingModel oldValue, DrawingModel newValue) -> {
        onDrawingModelChanged(oldValue, newValue);
    };

    @Override
    public void setDrawingView(@Nullable DrawingView newValue) {
        DrawingView oldValue = drawingView;
        Drawing oldDrawing = drawing;
        DrawingModel oldModel = drawingModel;
        if (oldValue != null) {
            oldValue.modelProperty().removeListener(modelListener);
            oldValue.drawingProperty().removeListener(drawingListener);
            oldDrawing = oldValue.getDrawing();
        }
        this.drawingView = newValue;
        Drawing newDrawing = null;
        if (newValue != null) {
            newValue.drawingProperty().addListener(drawingListener);
            newValue.modelProperty().addListener(modelListener);
            newDrawing = newValue.getDrawing();
            drawingModel = newValue.getModel();
        }
        onDrawingViewChanged(oldValue, newValue);
        onDrawingModelChanged(oldModel, drawingModel);
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

    /**
     * Can be overriden by subclasses.
     * This implementation is empty.
     *
     * @param oldValue the old drawing model
     * @param newValue the new drawing model
     */
    protected void onDrawingModelChanged(DrawingModel oldValue, DrawingModel newValue) {

    }
}
