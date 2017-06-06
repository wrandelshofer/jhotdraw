/* @(#)MapEntryProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.model;

import javafx.beans.property.ReadOnlyObjectWrapper;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.event.Listener;
import org.jhotdraw8.event.WeakListener;

/**
 * This property is weakly bound to a property of a figure in the DrawingModel.
 * <p>
 * If the key is not declared by the figure, then the value will always be null.
 *
 * @author Werner Randelshofer
 */
public class DrawingModelFigureProperty<T> extends ReadOnlyObjectWrapper<T> {

    private final DrawingModel model;
    protected final Figure figure;
    private final Key<T> key;
    private final Listener<DrawingModelEvent> modelListener;
    private final WeakListener<DrawingModelEvent> weakListener;
    private final boolean isDeclaredKey;

    public DrawingModelFigureProperty(DrawingModel model, Figure figure, Key<T> key) {
        this(model, figure, key, false);
    }

    public DrawingModelFigureProperty(DrawingModel model, Figure figure, Key<T> key, boolean allKeys) {
        this.model = model;
        this.key = key;
        this.figure = figure;
        this.isDeclaredKey = Figure.getDeclaredAndInheritedMapAccessors(figure.getClass()).contains(key);

        if (key != null) {
            this.modelListener = (event) -> {
                if (event.getEventType() == DrawingModelEvent.EventType.PROPERTY_VALUE_CHANGED
                        && this.figure == event.getNode()) {
                    if (this.key == event.getKey()) {
                        @SuppressWarnings("unchecked")
                        T newValue = (T) event.getNewValue();
                        if (super.get() != newValue) {
                            set(newValue);
                        }
                    } else if (allKeys) {
                        updateValue();
                    }
                }
            };

            model.addDrawingModelListener(weakListener = new WeakListener<DrawingModelEvent>(modelListener, model::removeDrawingModelListener));
        } else {
            modelListener = null;
            weakListener = null;
        }
    }

    @Override
    public T getValue() {
        @SuppressWarnings("unchecked")
        T temp = isDeclaredKey ? figure.get(key) : null;
        return temp;
    }

    @Override
    public void setValue(T value) {
        if (isDeclaredKey) {
            if (value != null && !key.isAssignable(value)) {
                throw new IllegalArgumentException("value is not assignable " + value);
            }
            model.set(figure, key, value);
        }
        // Note: super must be called after "put", so that listeners
        //       can be properly informed.
        super.setValue(value);
    }

    @Override
    public void unbind() {
        super.unbind();
        if (model != null) {
            model.removeDrawingModelListener(weakListener);
        }
    }

    /**
     * This implementation is empty.
     */
    protected void updateValue() {
    }
}
