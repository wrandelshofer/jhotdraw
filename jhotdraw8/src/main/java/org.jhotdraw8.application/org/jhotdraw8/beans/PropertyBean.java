/*
 * @(#)PropertyBean.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.MapEntryProperty;
import org.jhotdraw8.collection.NonNullMapAccessor;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * Interface for beans which support an open number of properties in a
 * property map.
 * <p>
 * A property can be accessed using a {@link Key}. The type parameter
 * of the key is used to ensure that property accesses are type safe.
 * </p>
 * <p>
 * To implement this interface, you need to implement method
 * {@link #getProperties()} as shown below.
 * </p>
 *
 * <pre><code>{@literal
 * public class MyBean implements PropertyBean {
 *      protected final ObservableMap<Key<?>, Object> properties = FXCollections.observableMap(new LinkedHashMap<>());
 *
 *     {@literal @}Override
 *     public ObservableMap{@literal <Key<?>, Object>} getProperties() {
 *        return properties;
 *      }
 * }
 * }</code></pre>
 *
 * @author Werner Randelshofer
 */
public interface PropertyBean {

    // ---
    // Properties
    // ---

    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    @Nullable ObservableMap<Key<?>, Object> getProperties();

    @NonNull
    default <T> ObjectProperty<T> getProperty(@NonNull Key<T> key) {
        return new MapEntryProperty<>(getProperties(), key, key.getValueType());
    }

    // ---
    // convenience methods
    // ---

    /**
     * Sets a property value.
     *
     * @param <T>      the value type
     * @param key      the key
     * @param newValue the value
     */
    @Nullable
    default <T> void set(@NonNull MapAccessor<T> key, @Nullable T newValue) {
        key.set(getProperties(), newValue);
    }

    /**
     * Puts a property value.
     *
     * @param <T>      the value type
     * @param key      the key
     * @param newValue the value
     * @return the old value
     */
    @Nullable
    default <T> T put(@NonNull MapAccessor<T> key, @Nullable T newValue) {
        return key.put(getProperties(), newValue);
    }

    /**
     * Gets a property value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the value
     */
    @Nullable
    default <T> T get(@NonNull MapAccessor<T> key) {
        return key.get(getProperties());
    }

    /**
     * Gets a nonnull property value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the value
     */
    @NonNull
    default <T> T getNonNull(@NonNull NonNullMapAccessor<T> key) {
        T value = key.get(getProperties());
        return Objects.requireNonNull(value);
    }

    /**
     * Removes a property value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the removed value
     */
    @Nullable
    default <T> T remove(Key<T> key) {
        @SuppressWarnings("unchecked")
        T removedValue = (T) getProperties().remove(key);
        return removedValue;
    }

    /**
     * Gets all values with the specified keys from the map.
     *
     * @param keys the desired keys
     * @return the map
     */
    @NonNull
    default Map<Key<?>, Object> getAll(Key<?>... keys) {
        return getAll(Arrays.asList(keys));
    }

    /**
     * Gets all values with the specified keys from the map.
     *
     * @param keys the desired keys
     * @return the map
     */
    @NonNull
    default Map<Key<?>, Object> getAll(@NonNull List<Key<?>> keys) {
        Map<Key<?>, Object> map = getProperties();
        Map<Key<?>, Object> result = new LinkedHashMap<>();
        for (Key<?> k : keys) {
            result.put(k, k.get(map));
        }
        return result;
    }

    @NonNull
    default <T> ObjectProperty<T> propertyAt(@NonNull Key<T> key) {
        return new MapEntryProperty<>(getProperties(), key, key.getValueType());
    }

    @NonNull
    @SuppressWarnings("unchecked")
    default <T> ObservableValue<T> valueAt(Key<T> key) {
        return (ObservableValue<T>) Bindings.valueAt(getProperties(), key);
    }
}
