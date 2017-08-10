/* @(#)AbstractFigureMapAccessor.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import org.jhotdraw8.collection.CompositeMapAccessor;
import org.jhotdraw8.collection.MapAccessor;

/**
 * AbstractFigureMapAccessor.
 *
 * @author Werner Randelshofer
 * @param <T> the value type
 */
public abstract class AbstractFigureMapAccessor<T> implements CompositeMapAccessor<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Holds a String representation of the name.
     */
    private final String name;
    /**
     * Holds the default value.
     */
    private final T defaultValue;
    /**
     * This variable is used as a "type token" so that we can check for
     * assignability of attribute values at runtime.
     */
    private final Class<?> clazz;
    /**
     * The type token is not sufficient, if the type is parameterized. We allow
     * to specify the type parameters as a string.
     */
    private final List<Class<?>> typeParameters;

    private final Set<MapAccessor<?>> subAccessors;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param subAccessors sub accessors which are used by this accessor
     * @param defaultValue The default value.
     */
    public AbstractFigureMapAccessor(String name, Class<T> clazz, MapAccessor<?>[] subAccessors, T defaultValue) {
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
    public AbstractFigureMapAccessor(String name, Class<?> clazz, Class<?>[] typeParameters, MapAccessor<?>[] subAccessors, T defaultValue) {
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
        this.typeParameters = typeParameters == null ? Collections.emptyList() : Collections.unmodifiableList(Arrays.asList(typeParameters.clone()));
        this.defaultValue = defaultValue;
        this.subAccessors = Collections.unmodifiableSet(new LinkedHashSet<>(Arrays.asList(subAccessors)));
    }

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    @Override
    public String getName() {
        return name;
    }

    @Override
    public Class<T> getValueType() {
        @SuppressWarnings("unchecked")
        Class<T> ret = (Class<T>) clazz;
        return ret;
    }

    @Override
    public List<Class<?>> getValueTypeParameters() {
        return typeParameters;
    }

    @Override
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
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Returns the name string.
     */
    @Override
    public String toString() {
        String keyClass = getClass().getName();
        return keyClass.substring(keyClass.lastIndexOf('.') + 1) + "{name:" + name + " type:" + getFullValueType() + "}";
    }

    @Override
    public Set<MapAccessor<?>> getSubAccessors() {
        return subAccessors;
    }

    @Override
    public boolean isTransient() {
        return false;
    }

}
