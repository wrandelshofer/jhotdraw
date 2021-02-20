/*
 * @(#)SimpleNullableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.reflect.TypeToken;

import java.lang.reflect.Type;

/**
 * A simple {@link Key} which has a nullable value.
 *
 * @author Werner Randelshofer
 */
public class SimpleNullableKey<T> extends AbstractKey<T> implements NullableKey<T> {

    static final long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name, type token class, and
     * with null as the default value.
     *
     * @param name The name of the name.
     * @param type The type of the value.
     */
    public SimpleNullableKey(@NonNull String name, @NonNull Type type) {
        super(name, type, false, false, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param name         The name of the name.
     * @param type         The type of the value.
     * @param defaultValue The default value.
     */
    public SimpleNullableKey(@NonNull String name, @NonNull Type type, @Nullable T defaultValue) {
        super(name, type, true, false, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, and
     * with null as the default value.
     *
     * @param name The name of the key.
     * @param type The type of the value.
     */
    public SimpleNullableKey(@NonNull String name, @NonNull TypeToken<T> type) {
        this(name, type.getType(), null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param name         The name of the key.
     * @param type         The type of the value.
     * @param defaultValue The default value.
     */
    public SimpleNullableKey(@NonNull String name, @NonNull TypeToken<T> type, @Nullable T defaultValue) {
        this(name, type.getType(), defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, and
     * with null as the default value.
     *
     * @param name  The name of the key.
     * @param clazz The type of the value.
     */
    public SimpleNullableKey(@NonNull String name, @NonNull Class<?> clazz) {
        super(name, clazz, true, false, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param name         The name of the key.
     * @param clazz        The type of the value.
     * @param defaultValue The default value.
     */
    public SimpleNullableKey(@NonNull String name, @NonNull Class<?> clazz, @Nullable T defaultValue) {
        super(name, clazz, true, false, defaultValue);
    }


}
