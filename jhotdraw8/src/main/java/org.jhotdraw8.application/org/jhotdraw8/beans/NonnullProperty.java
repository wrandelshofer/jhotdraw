/* @(#)NonnullProperty.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.SimpleObjectProperty;

/**
 * A {@code NonnullProperty} throws an {@code IllegalArgumentException} when
 * attempting to set its value to null.
 *
 * @param <T> the value type
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NonnullProperty<T> extends SimpleObjectProperty<T> {

    /**
     * Creates a new instance.
     *
     * @param bean         The bean which holds this property
     * @param name         The name of the property
     * @param initialValue The initial value. Nonnull.
     */
    public NonnullProperty(Object bean, String name, T initialValue) {
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
    public void set(T newValue) {
        if (newValue != null) {
            super.set(newValue);
        }
    }

    public T getNonnull() {
        return super.get();
    }

    public void setNonnull(T newValue) {
        if (newValue == null) {
            throw new NullPointerException("newValue");
        }
        super.set(newValue);
    }

}
