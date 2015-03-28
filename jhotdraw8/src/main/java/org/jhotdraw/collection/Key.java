/* @(#)Key.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.collection;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import javafx.beans.binding.MapExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;

/**
 * An <em>key</em> which provides typesafe access to a map entry.
 * <p>
 * A Key has a name, a type and a default value. 
 <p>
 * The following code example shows how to set and get a value from a map.
 * <pre>
 * {@code
 * String value = "Werner";
 * Key<String> stringKey = new Key("name",String.class,null);
 * Map<Key<?>,Object> map = new HashMap<>();
 * stringKey.put(map, value);
 * }
 * </pre>
 * <p>
 * 
 * @author Werner Randelshofer
 * @version $Id: Key.java 788 2014-03-22 07:56:28Z rawcoder $
 * @param <T> The value type.
 */
public class Key<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Holds a String representation of the key.
     */
    private String key;
    /**
     * Holds the default value.
     */
    private T defaultValue;
    /** This variable is used as a "type token" so that we can check for
     * assignability of attribute values at runtime.
     */
    private Class<?> clazz;

    /** The hashcode is computed upon creation. */
    private final int hashcode;

    /** Creates a new instance with the specified key, type token class,
     * default value null, and allowing null values.
     * @param key The name of the key.
     * @param clazz The type of the value.
     */
    public Key(String key, Class<?> clazz) {
        this(key, clazz, null);
    }
    
    /** Creates a new instance with the specified key, type token class,
     * default value, and allowing or disallowing null values. 
     * @param key The name of the key.
     * @param clazz The type of the value.
     * @param defaultValue The default value.
     */
    public Key(String key, Class<?> clazz, T defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        this.key = key;
        this.clazz = clazz;
        this.defaultValue = defaultValue;

        // compute hashcode
        int hash = 7;
        hash = 17 * hash + Objects.hashCode(this.key);
        hash = 17 * hash + Objects.hashCode(this.defaultValue);
        hash = 17 * hash + Objects.hashCode(this.clazz);
        hashcode = hash;
    }

    /**
     * Returns the key string.
     * @return key string.
     */
    public String getKey() {
        return key;
    }

    /**
     * Returns the default value of the attribute.
     *
     * @return the default value.
     */
    public T getDefaultValue() {
        return defaultValue;
    }

    /**
     * Gets the value of the attribute denoted by this Key from
     a Map.
     * 
     * @param a A Map.
     * @return The value of the attribute.
     */
    public T get(Map<Key<?>, Object> a) {
        T value = a.containsKey(this) ? (T) a.get(this) : defaultValue;
        assert isAssignable(value);
        return value;
    }
    /**
     * Gets the value of the attribute denoted by this Key from
     a Map.
     * 
     * @param a A Map.
     * @return The value of the attribute.
     */
    public ObjectProperty<T> getValueProperty(Map<Key<?>, ObjectProperty<?>> a) {
        if (!a.containsKey(this)) {
            a.put(this, new SimpleObjectProperty<T>(defaultValue));
        }
        SimpleObjectProperty<T> value = (SimpleObjectProperty<T>) a.get(this);
        return value;
    }
    /**
     * Gets the value of the attribute denoted by this Key from
     a Map.
     * 
     * @param a A Map.
     * @return The value of the attribute.
     */
    public T getValue(Map<Key<?>, ObjectProperty<?>> a) {
        if (!a.containsKey(this)) {
            a.put(this, new SimpleObjectProperty<T>(defaultValue));
        }
        SimpleObjectProperty<T> value = (SimpleObjectProperty<T>) a.get(this);
        return value.get();
    }

    /**
     * Use this method to perform a type-safe put operation of an attribute
     * into a Map.
     *
     * @param a An attribute map.
     * @param value The new value.
     * @return The old value.
     */
    public T put(Map<Key<?>, Object> a, T value) {
        assert isAssignable(value);
        return (T) a.put(this, value);
    }

    /**
     * Use this method to perform a type-safe put operation of an attribute
     * into a Map.
     *
     * @param a An attribute map.
     * @param value The new value.
     * @return The old value.
     */
    public T putValue(Map<Key<?>, ObjectProperty<?>> a, T value) {
        assert isAssignable(value);
        if (a.containsKey(this)) {
            ObjectProperty<T> p = (ObjectProperty<T>) a.get(this);
            T oldValue = p.get();
            p.set(value);
            return oldValue;
        } else {
            a.put(this, new SimpleObjectProperty<>(value));
            return null;
        }
    }

    /**
     * Returns true if the specified value is assignable with this key.
     *
     * @param value The object to be verified for assignability.
     * @return True if assignable.
     */
    public boolean isAssignable(Object value) {
        return clazz.isInstance(value);
    }

    /** Returns the key string. */
    @Override
    public String toString() {
        return key;
    }

    @Override
    public int hashCode() {
        return hashcode;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Key<?> other = (Key<?>) obj;
        if (!Objects.equals(this.key, other.key)) {
            return false;
        }
        if (!Objects.equals(this.defaultValue, other.defaultValue)) {
            return false;
        }
        if (!Objects.equals(this.clazz, other.clazz)) {
            return false;
        }
        return true;
    }

    public ObjectBinding<T> valueAt(MapExpression<Key<?>, Object> map) {
        ObjectBinding<Object> value = map.valueAt(this);
        return (ObjectBinding<T>) value;
    }
}
