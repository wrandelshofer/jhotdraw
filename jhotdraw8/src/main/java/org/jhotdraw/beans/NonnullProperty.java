/* @(#)NonnullProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.beans;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

/**
 * NonnullProperty.
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> The value type
 */
public class NonnullProperty<T> extends SimpleObjectProperty<T> {

    public NonnullProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
    }

    @Override
    public void set(T newValue) {
        if (newValue == null) {
            throw new IllegalArgumentException("newValue is null");
        }
        super.set(newValue);
    }


}
