/* @(#)PropertyBean.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.beans;

import javax.annotation.Nonnull;
import org.jhotdraw8.collection.MapEntryProperty;

import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import javafx.beans.binding.Bindings;
import javafx.beans.property.ObjectProperty;
import javafx.beans.value.ObservableValue;
import javafx.collections.ObservableMap;
import javax.annotation.Nullable;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.collection.NonnullMapAccessor;

/**
 * Interface for beans which support an open number of getProperties in a
 * property map.
 * <p>
 * A property is accessed using a type safe {@link Key}.
 * </p>
 * <p>
 * To implement this interface, you need to implement method
 * {@code valuesProperty()} as shown below.
 * </p>
 *
 * <pre><code>
 * public class MyBean implements PropertyBean {
 *     protected final ReadOnlyMapProperty{@literal <Key<?>, Object>} properties//
 *            = new ReadOnlyMapWrapper{@literal <Key<?>, Object>}(//
 *                    this, PROPERTIES_PROPERTY, //
 *                    FXCollections.observableHashMap()).getReadOnlyProperty();
 *
 *     {@literal @}Override
 *     public final MapProperty{@literal <Key<?>, Object>} propertiesProperty() {
 *        return getProperties;
 *    }
 * }
 * </code></pre>
 *
 * @author Werner Randelshofer
 * @version $Id$
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
    ObservableMap<Key<?>, Object> getProperties();

    default <T> ObjectProperty<T> getProperty(Key<T> key) {
       return new MapEntryProperty<>(getProperties(),key,key.getValueType());
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
     * @return the old value
     */
    @Nullable
    default <T> T set(@Nonnull MapAccessor<T> key, @Nullable T newValue) {
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
    default <T> T get(@Nonnull MapAccessor<T> key) {
        return key.get(getProperties());
    }

    /**
     * Gets a nonnull property value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the value
     */
    @Nonnull
    default <T> T getNonnull(@Nonnull NonnullMapAccessor<T> key) {
        T value = key.get(getProperties());
        if (value == null)throw new NullPointerException();
        return value;
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
    @Nonnull
    default Map<Key<?>, Object> getAll(Key<?>... keys) {
        return getAll(Arrays.asList(keys));
    }

    /**
     * Gets all values with the specified keys from the map.
     *
     * @param keys the desired keys
     * @return the map
     */
    @Nonnull
    default Map<Key<?>, Object> getAll(@Nonnull List<Key<?>> keys) {
        Map<Key<?>, Object> map = getProperties();
        Map<Key<?>, Object> result = new LinkedHashMap<>();
        for (Key<?> k : keys) {
            result.put(k, k.get(map));
        }
        return result;
    }

    @Nonnull
    default <T> ObjectProperty<T> propertyAt(@Nonnull Key<T> key) {
        return new MapEntryProperty<>(getProperties(), key, key.getValueType());
    }

    @Nonnull
    @SuppressWarnings("unchecked")
    default <T> ObservableValue<T> valueAt(Key<T> key) {
        return (ObservableValue<T>) (ObservableValue<Object>) Bindings.valueAt(getProperties(), key);
    }
}
