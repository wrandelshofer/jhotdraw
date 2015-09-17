/*
 * @(#)Key.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.collection;

import java.io.Serializable;
import java.util.Map;
import java.util.Objects;
import javafx.beans.binding.Binding;
import javafx.beans.binding.MapExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.MapChangeListener;

/**
 * An <em>name</em> which provides typesafe access to a map entry.
 * <p>
 * A Key has a name, a type and a default value.
 * <p>
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
 * Note that {@code Key} is not a value type. Thus using two distinct instances
 * of a Key will result in two distinct entries in the hash map, even if both
 * keys have the same name.
 *
 * @author Werner Randelshofer
 * @version $Id: Key.java 788 2014-03-22 07:56:28Z rawcoder $
 * @param <T> The value type.
 */
public class Key<T> implements Serializable {

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
    private final String typeParameters;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     */
    public Key(String key, Class<T> clazz) {
        this(key, clazz, "", null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param defaultValue The default value.
     */
    public Key(String key, Class<T> clazz, T defaultValue) {
        this(key, clazz, "", defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param key The name of the name.
     * @param clazz The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     * type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public Key(String key, Class<T> clazz, String typeParameters, T defaultValue) {
        if (key == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        if (typeParameters == null) {
            throw new IllegalArgumentException("type parameters is null");
        }
        if (typeParameters.length() > 0) {
            if (!typeParameters.startsWith("<") || !typeParameters.endsWith(">")) {
                throw new IllegalArgumentException("type parameters does not have arrow brackets:"
                        + typeParameters);
            }
        }
        this.name = key;
        this.clazz = clazz;
        this.typeParameters = typeParameters;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    public String getName() {
        return name;
    }

    public Class<?> getValueType() {
        return clazz;
    }

    public String getValueTypeParameters() {
        return typeParameters;
    }

    public String getFullValueType() {
        return clazz.getName() + typeParameters;
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
     * Gets the value of the attribute denoted by this Key from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    public T get(Map<? super Key<?>, Object> a) {
        T value = a.containsKey(this) ? (T) a.get(this) : defaultValue;
        assert isAssignable(value);
        return value;
    }

    /**
     * Gets the value of the attribute denoted by this Key from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    public ObjectProperty<T> getValueProperty(Map<? super Key<?>, ObjectProperty<?>> a) {
        if (!a.containsKey(this)) {
            a.put(this, new SimpleObjectProperty<T>(defaultValue));
        }
        SimpleObjectProperty<T> value = (SimpleObjectProperty<T>) a.get(this);
        return value;
    }

    /**
     * Gets the value of the attribute denoted by this Key from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    public T getValue(Map<? super Key<?>, ObjectProperty<?>> a) {
        if (!a.containsKey(this)) {
            a.put(this, new SimpleObjectProperty<T>(defaultValue));
        }
        SimpleObjectProperty<T> value = (SimpleObjectProperty<T>) a.get(this);
        return value.get();
    }

    /**
     * Use this method to perform a type-safe put operation of an attribute into
     * a Map.
     *
     * @param a An attribute map.
     * @param value The new value.
     * @return The old value.
     */
    public T put(Map<? super Key<?>, Object> a, T value) {
        if (!isAssignable(value)) {
            throw new IllegalArgumentException("Value is not assignable to key. key="
                    + this + ", value=" + value);
        }
        return (T) a.put(this, value);
    }

    /**
     * Use this method to perform a type-safe put operation of an attribute into
     * a Map.
     *
     * @param a An attribute map.
     * @param value The new value.
     * @return The old value.
     */
    public T putValue(Map<? super Key<?>, ObjectProperty<?>> a, T value) {
        if (!isAssignable(value)) {
            throw new IllegalArgumentException("Value is not assignable to key. key="
                    + this + ", value=" + value);
        }
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
        return value == null || clazz.isInstance(value);
    }

    /**
     * Returns true if the specified value is the default value of this key.
     *
     * @param value The object to be verified for assignability.
     * @return True if assignable.
     */
    public boolean isDefault(Object value) {
        return (defaultValue == null)
                ? value == null : defaultValue.equals(value);
    }

    /**
     * Returns the name string.
     */
    @Override
    public String toString() {
        return name;
    }

    /**
     * Creates a new binding for the map entry specified by this key.
     *
     * @param map a map
     * @return a binding for the map entry
     */
    public Binding<T> valueAt(MapExpression<Key<?>, Object> map) {
        ObjectBinding<Object> value = map.valueAt(this);
        return (ObjectBinding<T>) value;
    }

    /**
     * This property is bound to a value in the map.
     */
    private static class PropertyAt<T> extends ReadOnlyObjectWrapper<T> {

        private MapExpression<Key<?>, Object> map;
        private Key<T> key;
        private MapChangeListener<Key<?>, Object> mapListener;

        private PropertyAt(MapExpression<Key<?>, Object> map, Key<T> key) {
            this.map = map;
            this.key = key;

            this.mapListener = (MapChangeListener.Change<? extends Key<?>, ? extends Object> change) -> {
                if (this.key.equals(change.getKey())) {
                    if (super.get() != change.getValueAdded()) {
                        set((T) change.getValueAdded());
                    }
                }
            };
            map.addListener(mapListener);
        }

        @Override
        public T get() {
            return key.get(map);
        }

        @Override
        public void setValue(T value) {
            super.setValue(value);
            if (value != key.get(map)) {
                map.put(key, value);
            }
        }

        @Override
        public void unbind() {
            if (map != null) {
                map.removeListener(mapListener);
                mapListener = null;
                map = null;
                key = null;
            }
        }
    }

    /**
     * Creates a new property for the map entry specified by this key.
     *
     * @param map a map
     * @return a property for the map entry
     */
    public Property<T> propertyAt(final MapExpression<Key<?>, Object> map) {
        ObjectBinding<Object> value = map.valueAt(this);
        return new PropertyAt<>(map, this);
    }

    /**
     * Creates a new read-only property for the map entry specified by this key.
     *
     * @param map a map
     * @return a property for the map entry
     */
    public ReadOnlyProperty<T> readOnlyPropertyAt(final MapExpression<Key<?>, Object> map) {
        ObjectBinding<Object> value = map.valueAt(this);
        return new PropertyAt<>(map, this).getReadOnlyProperty();
    }

}
