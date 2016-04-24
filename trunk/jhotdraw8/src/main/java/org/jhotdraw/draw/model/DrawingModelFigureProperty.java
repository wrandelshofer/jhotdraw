/* @(#)MapEntryProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.model;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.MapChangeListener;
import javafx.collections.WeakMapChangeListener;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.event.Listener;
import org.jhotdraw.event.WeakListener;

/**
 * This property is weakly bound to a property of a figure in the DrawingModel.
 *
 * @author Werner Randelshofer
 */
public class DrawingModelFigureProperty<T> extends ReadOnlyObjectWrapper<T> {

    private DrawingModel model;
    private Figure figure;
    private Key<T> key;
    private Listener<DrawingModelEvent> modelListener;
    private WeakListener<DrawingModelEvent> weakListener;

    public DrawingModelFigureProperty(DrawingModel model, Figure figure, Key<T> key) {
        this.model = model;
        this.key = key;
        this.figure = figure;

        if (key != null) {
            this.modelListener = (event) -> {
                if (event.getEventType() == DrawingModelEvent.EventType.PROPERTY_VALUE_CHANGED
                        && this.figure == event.getFigure() && this.key == event.getKey()) {
                    @SuppressWarnings("unchecked")
                    T newValue = (T) event.getNewValue();
                    if (super.get() != newValue) {
                        set(newValue);
                    }
                }
            };

            model.addDrawingModelListener(weakListener = new WeakListener<DrawingModelEvent>(modelListener, model::removeDrawingModelListener));
        }
    }

    @Override
    public T getValue() {
        @SuppressWarnings("unchecked")
        T temp = figure.get(key);
        return temp;
    }

    @Override
    public void setValue(T value) {
        if (value != null && !key.isAssignable(value)) {
            throw new IllegalArgumentException("value is not assignable " + value);
        }
        model.set(figure, key, value);

        // Note: super must be called after "put", so that listeners
        //       can be properly informed.
        super.setValue(value);
    }

    @Override
    public void unbind() {
        super.unbind();
        if (model != null) {
            model.removeDrawingModelListener(weakListener);
            modelListener = null;
            model = null;
            key = null;
            weakListener = null;
        }
    }
}
