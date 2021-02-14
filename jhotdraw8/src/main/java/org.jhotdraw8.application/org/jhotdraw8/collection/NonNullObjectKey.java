/*
 * @(#)NonNullObjectKey.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.lang.reflect.Type;
import java.util.Objects;

/**
 * A <em>name</em> which provides typesafe access to a map entry.
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
 */
public class NonNullObjectKey<@NonNull T> implements NonNullKey<@NonNull T> {

    private static final long serialVersionUID = 1L;

    /**
     * Holds a String representation of the name.
     */
    private final @NonNull String name;
    /**
     * Holds the default value.
     */
    private final @NonNull T defaultValue;
    /**
     * This variable is used as a "type token" so that we can check for
     * assignability of attribute values at runtime.
     */
    private final @NonNull Type type;

    private final boolean isTransient;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name         The name of the key.
     * @param type         The type of the value.
     * @param defaultValue The default value.
     */
    public NonNullObjectKey(@NonNull String name, Class<T> type, @NonNull T defaultValue) {
        this(name, type, null, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name           The name of the key.
     * @param type           The type of the value.
     * @param typeParameters The type parameters of the class. Specify null if
     *                       no type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue   The default value.
     */
    public NonNullObjectKey(@NonNull String name, @NonNull Class<?> type, @Nullable Class<?>[] typeParameters, @NonNull T defaultValue) {
        this(name, type, typeParameters, false, defaultValue);
    }

    public NonNullObjectKey(@NonNull String name, @NonNull Class<?> type, @Nullable Class<?>[] typeParameters, boolean isTransient, @NonNull T defaultValue) {
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(type, "clazz is null");
        Objects.requireNonNull(defaultValue, "defaultValue may not be null if isNullable==false");

        this.name = name;
        this.type = type;
        this.isTransient = isTransient;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    @Override
    public @NonNull String getName() {
        return name;
    }

    @Override
    public @NonNull Type getValueType() {
        return type;
    }

    /**
     * Returns the default value of the attribute.
     *
     * @return the default value.
     */
    @Override
    public @NonNull T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isNullable() {
        return false;
    }

    public boolean isTransient() {
        return isTransient;
    }

    /**
     * Returns the name string.
     */
    @Override
    public @NonNull String toString() {
        String keyClass = getClass().getName();
        return keyClass.substring(keyClass.lastIndexOf('.') + 1) + "@" + System.identityHashCode(this) + " {\"" + name + "\"}";
    }
}
