/* @(#)MapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import java.io.Serializable;
import java.util.List;
import java.util.Map;

/**
 * A type safe accessor for maps.
 * <p>
 * Design pattern: Strategy
 *
 * @design.pattern MapAccessor Strategy, Strategy. Encapsulates a strategy for
 * accessing property values of a map.
 *
 * @param <T> The value type.
 * @author Werner Randelshofer
 */
public interface MapAccessor<T> extends Serializable {

    final static long serialVersionUID = 1L;

    /**
     * Whether the map contains all keys required by this map accessor.
     * 
    * @param map
     * @return true if map contains all keys required by this map accessor.
     */
    boolean containsKey(Map<Key<?>, Object> map);

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    String getName();

    /**
     * Gets the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    T get(Map<? super Key<?>, Object> a);

    /**
     * Puts the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A map.
     * @param value The new value.
     * @return The old value.
     */
    T put(Map<? super Key<?>, Object> a, T value);

    /**
     * Removes the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A map.
     * @return The old value.
     */
    T remove(Map<? super Key<?>, Object> a);

    /**
     * Returns the value type.
     *
     * @return the value type
     */
    Class<T> getValueType();

    /**
     * Returns the type parameters of the value type.
     *
     * @return an unmodifiable list with the type parameters
     */
    List<Class<?>> getValueTypeParameters();

    /**
     * Returns the default value of this map accessor.
     *
     * @return the default value
     */
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
