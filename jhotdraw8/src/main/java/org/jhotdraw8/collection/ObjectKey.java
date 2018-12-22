/* @(#)ObjectKey.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.annotation.Nonnull;

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
 * @version $Id$
 */
public class ObjectKey<T> implements Key<T> {

    private static final long serialVersionUID = 1L;

    /**
     * Holds a String representation of the name.
     */
    @Nonnull
    private final String name;
    /**
     * Holds the default value.
     */
    @Nullable
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
    @Nonnull
    private final List<Class<?>> typeParameters;

    /**
     * Whether the value may be set to null.
     */
    private final boolean isNullable;
    private final boolean isTransient;

    /**
     * Creates a new instance with the specified name, type token class, default
     * value null, and allowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     */
    public ObjectKey( String name,  Class<T> clazz) {
        this(name, clazz, null, null);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param defaultValue The default value.
     */
    public ObjectKey( String name,  Class<T> clazz,  T defaultValue) {
        this(name, clazz, null, defaultValue);
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
    public ObjectKey( String name,  Class<?> clazz, @Nullable Class<?>[] typeParameters, @Nullable T defaultValue) {
        this(name, clazz, typeParameters, true, defaultValue);
    }

    /**
     * Creates a new instance with the specified name, type token class, default
     * value, and allowing or disallowing null values.
     *
     * @param name The name of the key.
     * @param clazz The type of the value.
     * @param typeParameters The type parameters of the class. Specify null if
     * no type parameters are given. Otherwise specify them in arrow brackets.
     * @param isNullable Whether the value may be set to null
     * @param defaultValue The default value.
     */
    public ObjectKey(@Nonnull String name, @Nonnull Class<?> clazz, @Nullable Class<?>[] typeParameters, boolean isNullable, @Nullable T defaultValue) {
        this(name,clazz,typeParameters,isNullable,defaultValue==null,defaultValue);
    }
    public ObjectKey(@Nullable String name, @Nonnull Class<?> clazz, @Nullable Class<?>[] typeParameters, boolean isNullable, boolean isTransient, @Nullable T defaultValue) {
        if (name == null) {
            throw new IllegalArgumentException("key is null");
        }
        if (clazz == null) {
            throw new IllegalArgumentException("clazz is null");
        }
        if (!isNullable && defaultValue == null) {
            throw new IllegalArgumentException("defaultValue may not be null if isNullable==false");
        }

        this.name = name;
        this.clazz = clazz;
        this.typeParameters = typeParameters == null ? Collections.emptyList() : Collections.unmodifiableList(Arrays.asList(typeParameters.clone()));
        this.isNullable = isNullable;
        this.isTransient=isTransient;
        this.defaultValue = defaultValue;
    }

    /**
     * Returns the name string.
     *
     * @return name string.
     */
    @Nonnull
    @Override
    public String getName() {
        return name;
    }

    @Nonnull
    @Override
    public Class<T> getValueType() {
        @SuppressWarnings("unchecked")
        Class<T> ret = (Class<T>) clazz;
        return ret;
    }

    @Nonnull
    @Override
    public List<Class<?>> getValueTypeParameters() {
        return typeParameters;
    }

    @Nonnull
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
    @Nullable
    @Override
    public T getDefaultValue() {
        return defaultValue;
    }

    @Override
    public boolean isNullable() {
        return isNullable;
    }

    public boolean isTransient() {
        return isTransient;
    }

    /**
     * Returns the name string.
     */
    @Nonnull
    @Override
    public String toString() {
        String keyClass = getClass().getName();
        return keyClass.substring(keyClass.lastIndexOf('.') + 1) +"@"+System.identityHashCode(this)+ " {\"" + name + "\"}";
    }
}
