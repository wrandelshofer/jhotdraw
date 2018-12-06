/* @(#)AbstractSelectionInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import java.util.Collections;
import java.util.Set;

import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;

/**
 * AbstractSelectionInspector.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public abstract class AbstractSelectionInspector implements Inspector {

    @Nullable
    protected DrawingView drawingView;

    private final SetChangeListener<Figure> selectionListener = change -> {
        handleSelectionChanged(drawingView == null ? FXCollections.emptyObservableSet() : drawingView.getSelectedFigures());
    };

    protected DrawingModel getDrawingModel() {
        return drawingView.getModel();
    }

    @Override
    public void setDrawingView(@Nullable DrawingView newValue) {
        DrawingView oldValue = drawingView;
        if (oldValue != null) {
            oldValue.selectedFiguresProperty().removeListener(selectionListener);
        }
        this.drawingView = newValue;
        if (newValue != null) {
            newValue.selectedFiguresProperty().addListener(selectionListener);
        }
        handleDrawingViewChanged(oldValue, newValue);
    }

    @Nonnull
    protected Set<Figure> getSelectedFigures() {
        return drawingView == null ? Collections.emptySet() : drawingView.getSelectedFigures();
    }

    /**
     * Can be implemented by subclasses.
     *
     * @param oldValue the old selection
     * @param newValue the new selection
     */
    protected void handleDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {

    }

    /**
     * Must be implemented by subclasses.
     *
     * @param newValue the new selection
     */
    protected abstract void handleSelectionChanged(Set<Figure> newValue);
}
