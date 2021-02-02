/*
 * @(#)MapAccessor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.io.Serializable;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
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
     * Sets the value of the attribute denoted by this accessor from a Map.
     *
     * @param a     A map.
     * @param value The new value. Subclasses may require that the value is non-null.
     */
    default void set(@NonNull Map<? super Key<?>, Object> a, @Nullable T value) {
        put(a, value);
    }

    /**
     * Removes the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A map.
     * @return The old value.
     */
    @Nullable
    T remove(@NonNull Map<? super Key<?>, Object> a);

    /**
     * Returns the value type of this map accessor.
     * <p>
     * If the value type has type parameters, make sure to create it using
     * {@link org.jhotdraw8.reflect.TypeToken}.
     */
    @NonNull Type getValueType();

    /**
     * Returns the raw value type of this map accessor.
     */
    @SuppressWarnings("unchecked")
    default @NonNull Class<T> getRawValueType() {
        Type t = getValueType();
        return (Class<T>) ((t instanceof ParameterizedType) ? ((ParameterizedType) t).getRawType() : t);
    }

    /**
     * Returns the default value of this map accessor.
     * <p>
     * The default value of an attribute or property is
     * the value used when that attribute or property is not
     * specified.
     *
     * @return the default value
     */
    T getDefaultValue();

    /**
     * Whether the value needs to be made persistent.
     *
     * @return true if transient
     */
    boolean isTransient();

}
