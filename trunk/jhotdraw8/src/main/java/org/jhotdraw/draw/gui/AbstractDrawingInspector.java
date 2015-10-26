/* @(#)AbstractDrawingInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.gui;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.scene.Node;
import javafx.scene.layout.BorderPane;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;

/**
 * AbstractDrawingInspector.
 * @author Werner Randelshofer
 */
public abstract class AbstractDrawingInspector extends BorderPane implements Inspector {
    protected DrawingView drawingView;

    private final ChangeListener<Drawing> drawingListener = (ObservableValue<? extends Drawing> o, Drawing oldValue, Drawing newValue) -> {
        onDrawingChanged(oldValue, newValue);
    };
    @Override
    public void setDrawingView(DrawingView newValue) {
        DrawingView oldValue = drawingView;
        if (oldValue != null) {
            oldValue.drawingProperty().removeListener(drawingListener);
        }
        this.drawingView = newValue;
        if (newValue != null) {
            newValue.drawingProperty().addListener(drawingListener);
        }
    }

    @Override
    public Node getNode() {
        return this;
    }

    protected abstract void onDrawingChanged(Drawing oldValue, Drawing newValue);
}
