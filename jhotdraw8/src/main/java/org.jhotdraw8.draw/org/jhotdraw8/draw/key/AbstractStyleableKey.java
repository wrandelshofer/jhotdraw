/*
 * @(#)AbstractStyleableKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.reflect.TypeToken;
import org.jhotdraw8.styleable.ReadOnlyStyleableMapAccessor;

import java.lang.reflect.Type;

/**
 * AbstractStyleableKey.
 *
 * @author Werner Randelshofer
 */
public class AbstractStyleableKey<T> extends ObjectKey<T> {

    static final long serialVersionUID = 1L;


    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param key          The name of the name.
     * @param typeToken    The type of the value.
     * @param defaultValue The default value.
     */
    public AbstractStyleableKey(@NonNull String key, @NonNull TypeToken<T> typeToken, T defaultValue) {
        this(null, key, typeToken.getType(), defaultValue == null, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param key          The name of the name.
     * @param type        The type of the value.
     * @param defaultValue The default value.
     */
    public AbstractStyleableKey(@NonNull String key, @NonNull Type type, T defaultValue) {
        this(null, key, ReadOnlyStyleableMapAccessor.toCssName(key), type, defaultValue == null, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param namespace    The namespace
     * @param name         The name of the key.
     * @param type        The type of the value.
     * @param isNullable   Whether the value may be set to null
     * @param defaultValue The default value.
     */
    public AbstractStyleableKey(@Nullable String namespace, @NonNull String name, @NonNull Type type, boolean isNullable, T defaultValue) {
        this(namespace, name, ReadOnlyStyleableMapAccessor.toCssName(name), type, isNullable, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param namespace      The namespace
     * @param name           The name of the key.
     * @param cssName        The name of the as seen by CSS.
     * @param type          The type of the value.
     * @param isNullable     Whether the value may be set to null
     * @param defaultValue   The default value.
     */
    public AbstractStyleableKey(@Nullable String namespace, @NonNull String name, @NonNull String cssName, @NonNull Type type, boolean isNullable, T defaultValue) {
        super(name, type, isNullable, defaultValue);
        this.cssName = cssName;
        this.namespace = namespace;
    }

    private final @NonNull String cssName;
    private final @Nullable String namespace;

    public @NonNull String getCssName() {
        return cssName;
    }

    public @Nullable String getCssNamespace() {
        return namespace;
    }
}
