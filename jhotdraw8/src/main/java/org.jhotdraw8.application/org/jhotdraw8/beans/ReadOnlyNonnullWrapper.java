/*
 * @(#)ReadOnlyNonnullWrapper.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * ReadOnlyNonnullWrapper.
 *
 * @author Werner Randelshofer
 */
public class ReadOnlyNonnullWrapper<T> extends ReadOnlyObjectWrapper<T> {

    public ReadOnlyNonnullWrapper(Object bean, String name, T initialValue) {
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
