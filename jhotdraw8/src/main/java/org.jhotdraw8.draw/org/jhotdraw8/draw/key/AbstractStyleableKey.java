/* @(#)NullableObjectKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.styleable.ReadOnlyStyleableMapAccessor;

/**
 * AbstractStyleableKey.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AbstractStyleableKey<T> extends ObjectKey<T> {

    final static long serialVersionUID = 1L;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key   The name of the name.
     * @param clazz The type of the value.
     */
    public AbstractStyleableKey(String key, Class<T> clazz) {
        this(key, clazz, null, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value.
     *
     * @param key          The name of the name.
     * @param clazz        The type of the value.
     * @param defaultValue The default value.
     */
    public AbstractStyleableKey(String key, Class<T> clazz, T defaultValue) {
        this(key, clazz, null, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing null values.
     *
     * @param name           The name of the key.
     * @param clazz          The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     *                       type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue   The default value.
     */
    public AbstractStyleableKey(String name, Class<?> clazz, Class<?>[] typeParameters, T defaultValue) {
        this(null, name, clazz, typeParameters, true, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param namespace    The namespace
     * @param name         The name of the key.
     * @param clazz        The type of the value.
     * @param isNullable   Whether the value may be set to null
     * @param defaultValue The default value.
     */
    public AbstractStyleableKey(@Nullable String namespace, String name, Class<?> clazz, boolean isNullable, T defaultValue) {
        this(namespace, name, clazz, null, isNullable, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param namespace      The namespace
     * @param name           The name of the key.
     * @param clazz          The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     *                       type parameters are given. Otherwise specify them in arrow brackets.
     * @param isNullable     Whether the value may be set to null
     * @param defaultValue   The default value.
     */
    public AbstractStyleableKey(@Nullable String namespace, String name, Class<?> clazz, Class<?>[] typeParameters, boolean isNullable, T defaultValue) {
        super(name, clazz, typeParameters, isNullable, defaultValue);
        this.cssName = ReadOnlyStyleableMapAccessor.toCssName(name);
        this.namespace = namespace;
    }

    @Nonnull
    private final String cssName;
    @Nullable
    private final String namespace;

    @Nonnull
    public String getCssName() {
        return cssName;
    }

    @Nullable
    public String getCssNamespace() {
        return namespace;
    }
}
