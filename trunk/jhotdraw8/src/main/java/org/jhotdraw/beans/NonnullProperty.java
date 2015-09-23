/* @(#)NonnullProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.beans;

import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.SimpleObjectProperty;

/**
 * A {@code NonnullProperty} throws an {@code IllegalArgumentException} when
 * attempting to set its value to null.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 * @param <T> The value type
 */
public class NonnullProperty<T> extends SimpleObjectProperty<T> {

    /** Creates a new instance.
     * 
     * @param bean The bean which holds this property
     * @param name The name of the property
     * @param initialValue The initial value. Nonnull.
     */
    public NonnullProperty(Object bean, String name, T initialValue) {
        super(bean, name, initialValue);
    }

    /** Sets a new value.
     * 
     * @param newValue a value
     * @throws NullPointerException if newValue is null.
     */
    @Override
    public void set(T newValue) {
        if (newValue == null) {
            throw new NullPointerException("newValue");
        }
        super.set(newValue);
    }


}
