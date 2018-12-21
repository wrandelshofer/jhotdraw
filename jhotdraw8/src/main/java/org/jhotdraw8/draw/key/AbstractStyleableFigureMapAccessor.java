/* @(#)AbstractStyleableFigureMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.styleable.ReadableStyleableMapAccessor;
import org.jhotdraw8.styleable.WriteableStyleableMapAccessor;

/**
 * AbstractStyleableFigureMapAccessor.
 *
 * @author Werner Randelshofer
 * @param <T> the value type
 */
public abstract class AbstractStyleableFigureMapAccessor<T>
        implements WriteableStyleableMapAccessor<T>, CompositeMapAccessor<T>, FigureKey<T> {

    @Nonnull
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
    @Nonnull
    private final List<Class<?>> typeParameters;

    @Nonnull
    private final Set<MapAccessor<?>> subAccessors;

    @Nonnull
    private final DirtyMask dirtyMask;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param subAccessors sub accessors which are used by this accessor
     * @param defaultValue The default value.
     */
    public AbstractStyleableFigureMapAccessor(String name, Class<T> clazz, @Nonnull MapAccessor<?>[] subAccessors, T defaultValue) {
        this(name, clazz, null, subAccessors, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     * type parameters are given. Otherwise specify them in arrow brackets.
     * @param subAccessors sub accessors which are used by this accessor
     * @param defaultValue The default value.
     */
    public AbstractStyleableFigureMapAccessor(@Nullable String name, @Nullable Class<?> clazz, @Nullable Class<?>[] typeParameters,
                                              @Nonnull MapAccessor<?>[] subAccessors, @Nullable T defaultValue) {
        if (name == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        if (defaultValue == null) {
            throw new IllegalArgumentException("defaultValue is null");
        }

        this.name = name;
        this.clazz = clazz;
        this.typeParameters = typeParameters == null ? Collections.emptyList() :Arrays.asList(typeParameters.clone());
        this.defaultValue = defaultValue;
        this.subAccessors = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(subAccessors)));

        DirtyMask m = DirtyMask.EMPTY;
        for (MapAccessor<?> sub : subAccessors) {
            if (sub instanceof FigureKey<?>) {
                m = m.add(((FigureKey<?>) sub).getDirtyMask());
            }
        }
        dirtyMask = m;
        cssName = ReadableStyleableMapAccessor.toCssName(name);
    }

    @Override
    public boolean containsKey(@Nonnull Map<Key<?>, Object> map) {
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

    @Nonnull
    @Override
    public Class<T> getValueType() {
        @SuppressWarnings("unchecked")
        Class<T> ret = (Class<T>) clazz;
        return ret;
    }

    @Nonnull
    @Override
    public List<Class<?>> getValueTypeParameters() {
        return typeParameters;
    }

    @Nonnull
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

    /**
     * Returns the name string.
     */
    @Nonnull
    @Override
    public String toString() {
        String keyClass = getClass().getName();
        return keyClass.substring(keyClass.lastIndexOf('.') + 1) + "{name:" + name + " type:" + getFullValueType() + "}";
    }

    @Nonnull
    @Override
    public Set<MapAccessor<?>> getSubAccessors() {
        return subAccessors;
    }

    @Nonnull
    @Override
    public DirtyMask getDirtyMask() {
        return dirtyMask;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

    @Nonnull
    public String getCssName() {
        return cssName;
    }
}
