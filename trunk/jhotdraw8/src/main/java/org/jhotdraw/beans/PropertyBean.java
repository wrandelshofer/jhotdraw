/* @(#)PropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.property.ReadOnlyMapProperty;
import javafx.collections.ObservableMap;
import org.jhotdraw.collection.Key;

/**
 * Interface for beans which support an open number of getProperties in a property
 map.
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
 *</code></pre>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface PropertyBean {

    // ---
    // constant declarations
    // ---

    /**
     * The name of the "getProperties" property.
     */
    public final String PROPERTIES_PROPERTY = "properties";

    // ---
    // property methods
    // ---
    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> propertiesProperty();
    /**
     * Returns an observable map of property keys and their values.
     *
     * @return the map
     */
    default ObservableMap<Key<?>, Object> getProperties() {
        return propertiesProperty().get();
    }

    // ---
    // convenience methods
    // ---
    /**
     * Sets a property value.
     *
     * @param <T> the value type
     * @param key the key
     * @param newValue the value
     * @return the old value
     */
    default <T> T set(Key<T> key, T newValue) {
        return key.put(getProperties(), newValue);
    }

    /**
     * Gets a property value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the value
     */
    default <T> T get(Key<T> key) {
        return key.get(getProperties());
    }

    /**
     * Removes a property value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the removed value
     */
    default <T> T remove(Key<T> key) {
        return (T) getProperties().remove(key);
    }
}
