/*
 * @(#)AbstractStyleableMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.ReadOnlyStyleableMapAccessor;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * AbstractStyleableMapAccessor.
 *
 * @param <T> the value type
 * @author Werner Randelshofer
 */
public abstract class AbstractStyleableMapAccessor<T>
        implements WriteableStyleableMapAccessor<T>, CompositeMapAccessor<T> {

    @NonNull
    private final String cssName;
    private static final long serialVersionUID = 1L;

    /**
     * Holds a String representation of the name.
     */
    @Nullable
    private final String name;
    /**
     * Holds the default value.
     */
    @Nullable
    private final T defaultValue;
    /**
     * This variable is used as a "type token" so that we can check for
     * assignability of attribute values at runtime.
     */
    @Nullable
    private final Class<?> clazz;
    /**
     * The type token is not sufficient, if the type is parameterized. We allow
     * to specify the type parameters as a string.
     */
    @NonNull
    private final List<Class<?>> typeParameters;

    @NonNull
    private final Set<MapAccessor<?>> subAccessors;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name         The name of the key.
     * @param clazz        The type of the value.
     * @param subAccessors sub accessors which are used by this accessor
     * @param defaultValue The default value.
     */
    public AbstractStyleableMapAccessor(String name, Class<T> clazz, @NonNull MapAccessor<?>[] subAccessors, T defaultValue) {
        this(name, clazz, null, subAccessors, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name           The name of the key.
     * @param clazz          The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     *                       type parameters are given. Otherwise specify them in arrow brackets.
     * @param subAccessors   sub accessors which are used by this accessor
     * @param defaultValue   The default value.
     */
    public AbstractStyleableMapAccessor(@Nullable String name, @Nullable Class<?> clazz, @Nullable Class<?>[] typeParameters,
                                        @NonNull MapAccessor<?>[] subAccessors, @Nullable T defaultValue) {
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(defaultValue, "defaultValue is null");

        this.name = name;
        this.clazz = clazz;
        this.typeParameters = typeParameters == null ? Collections.emptyList() : Arrays.asList(typeParameters.clone());
        this.defaultValue = defaultValue;
        this.subAccessors = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(subAccessors)));

        cssName = ReadOnlyStyleableMapAccessor.toCssName(name);
    }

    @Override
    public boolean containsKey(@NonNull Map<Key<?>, Object> map) {
        return CompositeMapAccessor.super.containsKey(map);
    }

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    @Nullable
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public Class<T> getValueType() {
        @SuppressWarnings("unchecked")
        Class<T> ret = (Class<T>) clazz;
        return ret;
    }

    @NonNull
    @Override
    public Class<?> getComponentValueType() {
        return getValueType();
    }

    @NonNull
    @Override
    public List<Class<?>> getValueTypeParameters() {
        return typeParameters;
    }

    @NonNull
    public String getFullValueType() {
        StringBuilder buf = new StringBuilder();
        buf.append(clazz.getName());
        if (!typeParameters.isEmpty()) {
            buf.append('<');
            boolean first = true;
            for (Class<?> tp : typeParameters) {
                if (first) {
                    first = false;
                } else {
                    buf.append(',');
                }
                buf.append(tp.getName());
            }
            buf.append('>');
        }
        return buf.toString();
    }

    /**
     * Returns the default value of the attribute.
     *
     * @return the default value.
     */
    @Nullable
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Nullable
    @Override
    public final T put(@NonNull Map<? super Key<?>, Object> a, @Nullable T value) {
        T oldValue = get(a);
        set(a, value);
        return oldValue;
    }


    /**
     * Returns the name string.
     */
    @NonNull
    @Override
    public String toString() {
        String keyClass = getClass().getName();
        return keyClass.substring(keyClass.lastIndexOf('.') + 1) + "{name:" + name + " type:" + getFullValueType() + "}";
    }

    @NonNull
    @Override
    public Set<MapAccessor<?>> getSubAccessors() {
        return subAccessors;
    }


    @Override
    public boolean isTransient() {
        return false;
    }

    @NonNull
    public String getCssName() {
        return cssName;
    }
}
