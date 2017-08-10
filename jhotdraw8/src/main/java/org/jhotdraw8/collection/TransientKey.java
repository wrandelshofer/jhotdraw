/* @(#)TransientKey.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

/**
 * TransientKey can be used to store temporary data in an object.
 *
 * @author Werner Randelshofer
 * @param <T> the value type
 */
public class TransientKey<T> extends ObjectKey<T> {

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
