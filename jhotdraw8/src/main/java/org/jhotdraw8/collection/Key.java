/* @(#)Key.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Map;
import java.util.Objects;
import javafx.beans.binding.Binding;
import javafx.beans.binding.Bindings;
import javafx.beans.binding.MapExpression;
import javafx.beans.binding.ObjectBinding;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.Property;
import javafx.beans.property.ReadOnlyProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.collections.ObservableMap;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

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
 * @version $Id$
 */
public interface Key<T> extends MapAccessor<T> {

    final static long serialVersionUID = 1L;

    @Override
    public default boolean containsKey(@Nonnull Map<Key<?>, Object> map) {
        return map.containsKey(this);
    }

    @Nonnull
    public String getFullValueType();

    /**
     * Gets the value of the attribute denoted by this Key from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    @Override
    @Nullable
    default T get(@Nonnull Map<? super Key<?>, Object> a) {
        @SuppressWarnings("unchecked")
        T value = (T) a.getOrDefault(this, getDefaultValue());
        //assert isAssignable(value) : value + " is not assignable to " + getValueType();
        return value;
    }

    /**
     * Gets the value of the attribute denoted by this Key from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    @Nonnull
    default ObjectProperty<T> getValueProperty(@Nonnull Map<? super Key<?>, ObjectProperty<?>> a) {
        if (!a.containsKey(this)) {
            a.put(this, new SimpleObjectProperty<T>(getDefaultValue()));
        }
        @SuppressWarnings("unchecked")
        SimpleObjectProperty<T> value = (SimpleObjectProperty<T>) a.get(this);
        return value;
    }

    /**
     * Gets the value of the attribute denoted by this Key from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    @Nullable
    default T getValue(@Nonnull Map<? super Key<?>, ObjectProperty<?>> a) {
        if (!a.containsKey(this)) {
            a.put(this, new SimpleObjectProperty<T>(getDefaultValue()));
        }
        @SuppressWarnings("unchecked")
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
    @Nonnull
    @Override
    default T put(@Nonnull Map<? super Key<?>, Object> a, @Nullable T value) {
        if (isDefault(value) && !a.containsKey(this)) {
            return value;
        }

        @SuppressWarnings("unchecked")
        T oldValue = (T) a.put(this, value);
        return oldValue;
    }

    /**
     * Use this method to perform a type-safe remove operation of an attribute
     * on a Map.
     *
     * @param a An attribute map.
     * @return The old value.
     */
    @Override
    @Nullable
    default T remove(@Nonnull Map<? super Key<?>, Object> a) {
        @SuppressWarnings("unchecked")
        T oldValue = (T) a.remove(this);
        return oldValue;
    }

    /**
     * Use this method to perform a type-safe put operation of an attribute into
     * a Map.
     *
     * @param a An attribute map.
     * @param value The new value.
     * @return The old value.
     */
    @Nullable
    default T putValue(@Nonnull Map<? super Key<?>, ObjectProperty<?>> a, @Nullable T value) {
        if (!isAssignable(value)) {
            throw new IllegalArgumentException("Value is not assignable to key. key="
                    + this + ", value=" + value);
        }
        if (a.containsKey(this)) {
            @SuppressWarnings("unchecked")
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
     * Whether the value may be set to null.
     *
     * @return true if nullable
     */
    boolean isNullable();

    /**
     * Returns true if the specified value is assignable with this key.
     *
     * @param value The object to be verified for assignability.
     * @return True if assignable.
     */
    default boolean isAssignable(@Nullable Object value) {
        return value == null && isNullable() || getValueType().isInstance(value);
    }

    /**
     * Returns true if the specified value is the default value of this key.
     *
     * @param value The object to be verified for assignability.
     * @return True if assignable.
     */
    default boolean isDefault(@Nullable Object value) {
        return Objects.equals(getDefaultValue(), value);
    }

    /**
     * Creates a new binding for the map entry specified by this key.
     *
     * @param map a map
     * @return a binding for the map entry
     */
    @Nonnull
    default Binding<T> valueAt(@Nonnull ObservableMap<Key<?>, Object> map) {
        ObjectBinding<Object> value = Bindings.valueAt(map, this);
        @SuppressWarnings("unchecked")
        Binding<T> binding = (ObjectBinding<T>) value;
        return binding;
    }

    /**
     * Creates a new property for the map entry specified by this key.
     *
     * @param map a map
     * @return a property for the map entry
     */
    @Nonnull
    default ObjectProperty<T> propertyAt(@Nonnull final ObservableMap<Key<?>, Object> map) {
        return new KeyMapEntryProperty<>(map, this);
    }

    /**
     * Creates a new read-only property for the map entry specified by this key.
     *
     * @param map a map
     * @return a property for the map entry
     */
    @Nonnull
    default ReadOnlyProperty<T> readOnlyPropertyAt(@Nonnull final MapExpression<Key<?>, Object> map) {
        ObjectBinding<Object> value = map.valueAt(this);
        return new KeyMapEntryProperty<>(map, this);
    }

}
