/* @(#)TransientKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.collection;

/**
 * TransientKey can be used to store temporary data in an object.
 * 
 * @author Werner Randelshofer
 * @param <T> the value type
 */
public class TransientKey<T> extends SimpleKey<T> {
    private static final long serialVersionUID = 1L;

    public TransientKey(String name, Class<T> clazz) {
        super(name, clazz);
    }

    public TransientKey(String name, Class<T> clazz, T defaultValue) {
        super(name, clazz, defaultValue);
    }

    public TransientKey(String name, Class<?> clazz, Class<?>[] typeParameters, T defaultValue) {
        super(name, clazz, typeParameters, defaultValue);
    }

    public TransientKey(String name, Class<?> clazz, Class<?>[] typeParameters, boolean isNullable, T defaultValue) {
        super(name, clazz, typeParameters, isNullable, defaultValue);
    }

    @Override
    public boolean isTransient() {
        return true;
    }

    
}
