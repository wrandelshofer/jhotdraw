/*
 * @(#)NonNullProperty.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.SimpleObjectProperty;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * A {@code NonNullProperty} throws an {@code IllegalArgumentException} when
 * attempting to set its value to null.
 *
 * @param <T> the value type
 * @author Werner Randelshofer
 */
public class NonNullObjectProperty<T> extends SimpleObjectProperty<T> {

    /**
     * Creates a new instance.
     *
     * @param bean         The bean which holds this property
     * @param name         The name of the property
     * @param initialValue The initial value. NonNull.
     */
    public NonNullObjectProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    protected void fireValueChangedEvent() {
        super.fireValueChangedEvent();
    }

    /**
     * Sets a new value if it is not null.
     */
    @Override
    public void set(@Nullable T newValue) {
        if (newValue != null) {
            super.set(newValue);
        }
    }

    public T getNonNull() {
        return super.get();
    }

    public void setNonNull(@NonNull T newValue) {
        Objects.requireNonNull(newValue, "newValue is null");
        super.set(newValue);
    }

}
