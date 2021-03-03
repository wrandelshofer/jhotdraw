/*
 * @(#)DrawingModelFigureProperty.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.model;

import javafx.beans.property.ReadOnlyObjectWrapper;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
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

    private final @NonNull DrawingModel model;
    protected final @Nullable Figure figure;
    private final @Nullable Key<T> key;
    private final @Nullable Listener<DrawingModelEvent> modelListener;
    private final @Nullable WeakListener<DrawingModelEvent> weakListener;
    private final boolean isDeclaredKey;

    public DrawingModelFigureProperty(@NonNull DrawingModel model, Figure figure, Key<T> key) {
        this(model, figure, key, false);
    }

    public DrawingModelFigureProperty(@NonNull DrawingModel model, @Nullable Figure figure, @Nullable Key<T> key, boolean allKeys) {
        this.model = model;
        this.key = key;
        this.figure = figure;
        this.isDeclaredKey = figure != null && Figure.getDeclaredAndInheritedMapAccessors(figure.getClass()).contains(key);

        if (key != null) {
            this.modelListener = (event) -> {
                if (event.getEventType() == DrawingModelEvent.EventType.PROPERTY_VALUE_CHANGED
                        && this.figure == event.getNode()) {
                    if (this.key == event.getKey()) {
                        @SuppressWarnings("unchecked")
                        T newValue = event.getNewValue();
                        if (super.get() != newValue) {
                            set(newValue);
                        }
                    } else if (allKeys) {
                        updateValue();
                    }
                }
            };

            model.addDrawingModelListener(weakListener = new WeakListener<>(modelListener, model::removeDrawingModelListener));
        } else {
            modelListener = null;
            weakListener = null;
        }
    }

    @Override
    public @Nullable T getValue() {
        @SuppressWarnings("unchecked")
        T temp = isDeclaredKey && figure != null && key != null ? figure.get(key) : null;
        return temp;
    }

    @Override
    public void setValue(@Nullable T value) {
        if (isDeclaredKey && figure != null && key != null) {
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
        model.removeDrawingModelListener(weakListener);
    }

    /**
     * This implementation is empty.
     */
    protected void updateValue() {
    }
}
