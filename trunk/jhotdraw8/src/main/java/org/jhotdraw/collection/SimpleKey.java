/* @(#)SimpleKey.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
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
public class SimpleKey<T> implements Key<T> {

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
     * @param name The name of the key.
     * @param clazz The type of the value.
     */
    public SimpleKey(String name, Class<T> clazz) {
        this(name, clazz, "", null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param defaultValue The default value.
     */
    public SimpleKey(String name, Class<T> clazz, T defaultValue) {
        this(name, clazz, "", defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param typeParameters The type parameters of the class. Specify "" if no
     * type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue The default value.
     */
    public SimpleKey(String name, Class<?> clazz, String typeParameters, T defaultValue) {
        if (name == null) {
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
        this.name = name;
        this.clazz = clazz;
        this.typeParameters = typeParameters;
        this.defaultValue = defaultValue;
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
    public Class<?> getValueType() {
        return clazz;
    }

    @Override
    public String getValueTypeParameters() {
        return typeParameters;
    }

    @Override
    public String getFullValueType() {
        return clazz.getName() + typeParameters;
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
        return name;
    }
}
