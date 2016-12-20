/* @(#)ReadOnlyNonnullWrapper.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.beans;

import javafx.beans.property.ReadOnlyObjectWrapper;

/**
 * ReadOnlyNonnullWrapper.
 *
 * @author Werner Randelshofer
 * @version $Id: ReadOnlyNonnullWrapper.java 1149 2016-11-18 11:00:10Z rawcoder
 * $
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
