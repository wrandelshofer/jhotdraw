/*
 * @(#)ObjectFigureKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonNullMapAccessor;
import org.jhotdraw8.collection.ObjectKey;

/**
 * ObjectFigureKey.
 *
 * @author Werner Randelshofer
 */
public class ObjectFigureKey<T> extends ObjectKey<T> implements
        NonNullMapAccessor<T> {

    final static long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *  @param key          The name of the name.
     * @param clazz        The type of the value.
     * @param defaultValue The default value.
     */
    public ObjectFigureKey(@NonNull String key, @NonNull Class<T> clazz, @NonNull T defaultValue) {
        this(key, clazz, null, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and disallowing null values.
     *  @param name           The name of the key.
     * @param clazz          The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     *                       type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue   The default value.
     */
    public ObjectFigureKey(@NonNull String name, @NonNull Class<?> clazz, @Nullable Class<?>[] typeParameters, @NonNull T defaultValue) {
        super(name, clazz, typeParameters, false, false, defaultValue);
    }

}
