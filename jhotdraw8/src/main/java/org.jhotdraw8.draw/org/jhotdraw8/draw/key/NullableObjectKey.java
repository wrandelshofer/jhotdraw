/*
 * @(#)NullableObjectKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ObjectKey;

/**
 * NullableObjectKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NullableObjectKey<T> extends ObjectKey<T> {

    final static long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *  @param key       The name of the name.
     * @param clazz     The type of the value.
     */
    public NullableObjectKey(@Nonnull String key, @Nonnull Class<T> clazz) {
        this(key, clazz, null, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *  @param key          The name of the name.
     * @param clazz        The type of the value.
     * @param defaultValue The default value.
     */
    public NullableObjectKey(@Nonnull String key, @Nonnull Class<T> clazz, @Nullable T defaultValue) {
        this(key, clazz, null, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing null values.
     *  @param name           The name of the key.
     * @param clazz          The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     *                       type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue   The default value.
     */
    public NullableObjectKey(@Nonnull String name, @Nonnull Class<?> clazz, @Nullable Class<?>[] typeParameters, @Nullable T defaultValue) {
        super(name, clazz, typeParameters, true, false, defaultValue);
    }


}
