/* @(#)MapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.collection;

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
 */
public interface MapAccessor <T> extends Serializable {

    final static long serialVersionUID = 1L;
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
    
    /** Returns the value type. */
    Class<T> getValueType();

    /** Returns the type parameters of the value type. */
    List<Class<?>> getValueTypeParameters();

    /** Returns the default value of this map accessor. */
    public T getDefaultValue();

    /** Returns a string representation of the value type and its type parameters. */
    public String getFullValueType();


}
