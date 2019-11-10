/*
 * @(#)MapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A type safe accessor for maps.
 * <p>
 * Design pattern: Strategy
 *
 * @param <T> The value type.
 * @author Werner Randelshofer
 * @design.pattern MapAccessor Strategy, Strategy. Encapsulates a strategy for
 * accessing property values of a map.
 */
public interface MapAccessor<T> extends Serializable {

    long serialVersionUID = 1L;

    /**
     * Whether the map contains all keys required by this map accessor.
     *
     * @param map a map
     * @return true if map contains all keys required by this map accessor.
     */
    boolean containsKey(Map<Key<?>, Object> map);

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    @Nullable
    String getName();

    /**
     * Gets the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    @Nullable
    T get(@NonNull Map<? super Key<?>, Object> a);

    /**
     * Puts the value of the attribute denoted by this accessor from a Map.
     *
     * @param a     A map.
     * @param value The new value. Subclasses may require that the value is non-null.
     * @return The old value.
     */
    @Nullable
    T put(@NonNull Map<? super Key<?>, Object> a, @Nullable T value);

    /**
     * Removes the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A map.
     * @return The old value.
     */
    @Nullable
    T remove(@NonNull Map<? super Key<?>, Object> a);

    /**
     * Returns the value type.
     * If this is a {@code {@literal List<String>}} then the value type
     * is {@code List}.
     *
     * @return the value type
     */
    @NonNull Class<T> getValueType();

    /**
     * Returns the component type of the value type.
     * <p>
     * If this is a {@code {@literal List<String>}} then the component value type
     * is {@code String}.
     *
     * @return the component value type
     */
    Class<?> getComponentValueType();

    /**
     * Returns the type parameters of the value type.
     *
     * @return an unmodifiable list with the type parameters
     */
    @NonNull List<Class<?>> getValueTypeParameters();

    /**
     * Returns the default value of this map accessor.
     *
     * @return the default value
     */
    @Nullable
    T getDefaultValue();


    /**
     * Returns a string representation of the value type and its type
     * parameters.
     *
     * @return the class name of the value type including type parameters
     */
    String getFullValueType();

    /**
     * Whether the value needs to be made persistent.
     *
     * @return true if transient
     */
    boolean isTransient();

}
