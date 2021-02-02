/*
 * @(#)NullableObjectKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ObjectKey;

/**
 * NullableObjectKey.
 *
 * @author Werner Randelshofer
 */
public class NullableObjectKey<T> extends ObjectKey<T> {

    final static long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *  @param key       The name of the name.
     * @param clazz     The type of the value.
     */
    public NullableObjectKey(@NonNull String key, @NonNull Class<T> clazz) {
        this(key, clazz, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param name         The name of the key.
     * @param clazz        The type of the value.
     * @param defaultValue The default value.
     */
    public NullableObjectKey(@NonNull String name, @NonNull Class<?> clazz, @Nullable T defaultValue) {
        super(name, clazz, true, false, defaultValue);
    }


}
