/*
 * @(#)NonNullObjectKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
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
public class NonNullObjectKey<T> implements NonNullKey<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Holds a String representation of the name.
     */
    @NonNull
    private final String name;
    /**
     * Holds the default value.
     */
    @NonNull
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
    @NonNull
    private final List<Class<?>> typeParameters;

    private final boolean isTransient;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name         The name of the key.
     * @param clazz        The type of the value.
     * @param defaultValue The default value.
     */
    public NonNullObjectKey(String name, Class<T> clazz, @NonNull T defaultValue) {
        this(name, clazz, null, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name           The name of the key.
     * @param clazz          The type of the value.
     * @param typeParameters The type parameters of the class. Specify null if
     *                       no type parameters are given. Otherwise specify them in arrow brackets.
     * @param defaultValue   The default value.
     */
    public NonNullObjectKey(String name, Class<?> clazz, @Nullable Class<?>[] typeParameters, @NonNull T defaultValue) {
        this(name, clazz, typeParameters, false, defaultValue);
    }

    public NonNullObjectKey(@Nullable String name, @Nullable Class<?> clazz, @Nullable Class<?>[] typeParameters, boolean isTransient, @NonNull T defaultValue) {
        Objects.requireNonNull(name, "name is null");
        Objects.requireNonNull(clazz, "clazz is null");
        Objects.requireNonNull(defaultValue, "defaultValue may not be null if isNullable==false");

        this.name = name;
        this.clazz = clazz;
        this.typeParameters = typeParameters == null ? Collections.emptyList() : Collections.unmodifiableList(Arrays.asList(typeParameters.clone()));
        this.isTransient = isTransient;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    @NonNull
    @Override
    public String getName() {
        return name;
    }

    @NonNull
    @Override
    public Class<T> getValueType() {
        @SuppressWarnings("unchecked")
        Class<T> ret = (Class<T>) clazz;
        return ret;
    }

    @NonNull
    @Override
    public Class<?> getComponentValueType() {
        return typeParameters.size() == 0 ? getValueType() : typeParameters.get(0);
    }

    @NonNull
    @Override
    public List<Class<?>> getValueTypeParameters() {
        return typeParameters;
    }

    @NonNull
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
    @NonNull
    @Override
    public T getDefaultValue() {
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
    @NonNull
    @Override
    public String toString() {
        String keyClass = getClass().getName();
        return keyClass.substring(keyClass.lastIndexOf('.') + 1) + "@" + System.identityHashCode(this) + " {\"" + name + "\"}";
    }
}
