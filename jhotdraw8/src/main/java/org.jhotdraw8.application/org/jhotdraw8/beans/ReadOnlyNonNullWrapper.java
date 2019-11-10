/*
 * @(#)ReadOnlyNonNullWrapper.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * ReadOnlyNonNullWrapper.
 *
 * @author Werner Randelshofer
 */
public class ReadOnlyNonNullWrapper<T> extends ReadOnlyObjectWrapper<T> {

    public ReadOnlyNonNullWrapper(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    protected void fireValueChangedEvent() {
        if (get() == null) {
            throw new NullPointerException("newValue is null");
        }
        super.fireValueChangedEvent();
    }

}
