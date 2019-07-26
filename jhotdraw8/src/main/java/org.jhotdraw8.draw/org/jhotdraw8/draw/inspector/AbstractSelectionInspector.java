/*
 * @(#)AbstractSelectionInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.model.DrawingModel;

import java.util.Collections;
import java.util.Set;

/**
 * AbstractSelectionInspector.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSelectionInspector implements Inspector<DrawingView> {
    final protected ObjectProperty<DrawingView> subject = new SimpleObjectProperty<>();

    {
        subject.addListener(this::handleDrawingViewChanged);
    }

    public ObjectProperty<DrawingView> subjectProperty() {
        return subject;
    }

    protected Drawing getDrawing() {
        return getSubject().getDrawing();
    }

    protected DrawingModel getModel() {
        return getSubject().getModel();
    }


    private final SetChangeListener<Figure> selectionListener = change -> {
        DrawingView drawingView = getSubject();
        handleSelectionChanged(drawingView == null ? FXCollections.emptyObservableSet() : drawingView.getSelectedFigures());
    };


    protected void handleDrawingViewChanged(ObservableValue<? extends DrawingView> observable, @Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        if (oldValue != null) {
            oldValue.selectedFiguresProperty().removeListener(selectionListener);
        }
        if (newValue != null) {
            newValue.selectedFiguresProperty().addListener(selectionListener);
        }
    }

    @Nonnull
    protected Set<Figure> getSelectedFigures() {
        DrawingView drawingView = getSubject();
        return drawingView == null ? Collections.emptySet() : drawingView.getSelectedFigures();
    }

    /**
     * Must be implemented by subclasses.
     *
     * @param newValue the new selection
     */
    protected abstract void handleSelectionChanged(Set<Figure> newValue);
}
