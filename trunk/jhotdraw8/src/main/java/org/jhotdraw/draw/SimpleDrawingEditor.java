/* @(#)SimpleDrawingEditor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.HashSet;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SetProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleSetProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.SetChangeListener;
import org.jhotdraw.draw.tool.Tool;

/**
 * SimpleDrawingEditor.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawingEditor implements DrawingEditor {

    private final ChangeListener<Boolean> focusListener = (ObservableValue<? extends Boolean> observable, Boolean oldValue, Boolean newValue) -> {
        if (newValue) {
            setActiveDrawingView((DrawingView) ((ReadOnlyProperty) observable).getBean());
        }
    };

    private final SetProperty<DrawingView> drawingViews = new SimpleSetProperty<>(FXCollections.observableSet(new HashSet<>()));

    {
        drawingViews.addListener((SetChangeListener.Change<? extends DrawingView> change) -> {
            if (change.wasRemoved()) {
                change.getElementRemoved().focusedProperty().removeListener(focusListener);
                if (getActiveDrawingView()!=null) {
                    if (getActiveDrawingView() == change.getElementRemoved()) {
                        setActiveDrawingView(drawingViews.isEmpty() ? null : drawingViews.get().iterator().next());
                    }
                }
            } else if (change.wasAdded()) {
                change.getElementAdded().focusedProperty().addListener(focusListener);
                if (drawingViews.size() == 1) {
                    setActiveDrawingView(change.getElementAdded());
                }
            }

        });
    }

    private final ObjectProperty<DrawingView> activeDrawingView = new SimpleObjectProperty<>(this,"activeDrawingView");
    private final ObjectProperty<Tool> activeTool = new SimpleObjectProperty<Tool>(this,"activeTool") {

        @Override
        protected void fireValueChangedEvent() {
            super.fireValueChangedEvent();
            if (getActiveDrawingView()!=null) {
                getActiveDrawingView().setTool(get());
            }
        }
    };

    @Override
    public SetProperty<DrawingView> drawingViewsProperty() {
        return drawingViews;
    }

    @Override
    public ObjectProperty<DrawingView> activeDrawingViewProperty() {
        return activeDrawingView;
    }

    @Override
    public ObjectProperty<Tool> activeToolProperty() {
        return activeTool;
    }
}
