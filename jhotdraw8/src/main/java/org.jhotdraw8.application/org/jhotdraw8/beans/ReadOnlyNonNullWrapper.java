/*
 * @(#)ReadOnlyNonNullWrapper.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.ReadOnlyObjectWrapper;

import java.util.Objects;

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
        Objects.requireNonNull(get(), "new value is null");
        super.fireValueChangedEvent();
    }

}
