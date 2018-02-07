/* @(#)NonnullProperty.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.SimpleObjectProperty;

/**
 * A {@code NonnullProperty} throws an {@code IllegalArgumentException} when
 * attempting to set its value to null.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> the value type
 */
public class NonnullProperty<T> extends SimpleObjectProperty<T> {

    /**
     * Creates a new instance.
     *
     * @param bean The bean which holds this property
     * @param name The name of the property
     * @param initialValue The initial value. Nonnull.
     */
    public NonnullProperty( Object bean,  String name, T initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    protected void fireValueChangedEvent() {
        if (get() == null) {
            throw new NullPointerException("newValue is null");
        }
        super.fireValueChangedEvent();
    }

        public T getNonnull() {
        return super.get();
    }

    public void setNonnull( T newValue) {
        super.set(newValue);
    }

}
