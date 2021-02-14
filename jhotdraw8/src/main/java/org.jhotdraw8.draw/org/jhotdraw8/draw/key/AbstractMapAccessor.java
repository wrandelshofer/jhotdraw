/*
 * @(#)AbstractMapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.MapAccessor;

import java.lang.reflect.Type;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Objects;

/**
 * AbstractMapAccessor.
 *
 * @param <T> the value type
 * @author Werner Randelshofer
 */
public abstract class AbstractMapAccessor<T> implements CompositeMapAccessor<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Holds a String representation of the name.
     */
    private final @Nullable String name;
    /**
     * Holds the default value.
     */
    private final @Nullable T defaultValue;
    /**
     * This variable is used as a "type token" so that we can check for
     * assignability of attribute values at runtime.
     */
    private final @NonNull Type type;

    private final @NonNull List<MapAccessor<?>> subAccessors;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name         The name of the key.
     * @param type         The type of the value.
     * @param subAccessors sub accessors which are used by this accessor
     * @param defaultValue The default value.
     */
    public AbstractMapAccessor(String name, Class<T> type, @NonNull MapAccessor<?>[] subAccessors, T defaultValue) {
        this(name, type, null, subAccessors, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name           The name of the key.
     * @param type           The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     *                       type parameters are given. Otherwise specify them in arrow brackets.
     * @param subAccessors   sub accessors which are used by this accessor
     * @param defaultValue   The default value.
     */
    public AbstractMapAccessor(@Nullable String name, @Nullable Class<?> type, @Nullable Class<?>[] typeParameters, @NonNull MapAccessor<?>[] subAccessors, @Nullable T defaultValue) {
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(type, "clazz is null");
        Objects.requireNonNull(defaultValue, "default value is null");

        this.name = name;
        this.type = type;
        this.defaultValue = defaultValue;
        this.subAccessors = Arrays.asList(subAccessors.clone());
    }

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    @Override
    public @Nullable String getName() {
        return name;
    }

    @Override
    public @NonNull Type getValueType() {
        return type;
    }


    /**
     * Returns the default value of the attribute.
     *
     * @return the default value.
     */
    @Override
    public @Nullable T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the name string.
     */
    @Override
    public @NonNull String toString() {
        String keyClass = getClass().getName();
        return keyClass.substring(keyClass.lastIndexOf('.') + 1) + "{name:" + name + " type:" + getValueType() + "}";
    }

    @Override
    public @NonNull Collection<MapAccessor<?>> getSubAccessors() {
        return subAccessors;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

}
