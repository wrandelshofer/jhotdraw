/* @(#)AbstractSelectionInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.util.Set;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;

/**
 * AbstractSelectionInspector.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractSelectionInspector implements Inspector {

    protected DrawingView drawingView;

    private final SetChangeListener<Figure> selectionListener = change -> {
        onSelectionChanged(drawingView==null?FXCollections.emptyObservableSet():drawingView.getSelectedFigures());
    };

    @Override
    public void setDrawingView(DrawingView newValue) {
        DrawingView oldValue = drawingView;
        if (oldValue != null) {
            oldValue.selectedFiguresProperty().removeListener(selectionListener);
        }
        this.drawingView = newValue;
        if (newValue != null) {
            newValue.selectedFiguresProperty().addListener(selectionListener);
        }
    }

    /**
     * Must be implemented by subclasses.
     *
     * @param oldValue the old selection
     * @param newValue the new selection
     */
    protected abstract void onSelectionChanged(Set<Figure> newValue);
}
