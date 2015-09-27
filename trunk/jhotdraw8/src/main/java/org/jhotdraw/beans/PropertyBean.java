/* @(#)PropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.Observable;
import javafx.beans.property.MapProperty;
import javafx.beans.property.ReadOnlyMapProperty;
import javafx.css.CssMetaData;
import org.jhotdraw.collection.Key;

/**
 * Interface for beans which support an open number of properties.
 * <p>
 * A property is accessed using a type safe {@link Key}.
 * </p>
 * <p>
 * To implement this interface, you need to implement the
 * {@code valuesProperty()} method as shown below.
 * </p>
 *
 * <pre><code>
 * public class MyBean implements PropertyBean {
 *     private final ReadOnlyMapWrappery{@literal <Key<?>, Object>} properties
 *         = new ReadOnlyMapWrapper{@literal <>}(this, "properties", FXCollections.observableHashMap());
 *
 *     {@literal @}Override
 *     public final MapProperty{@literal <Key<?>, Object>} properties() {
 *          return properties;
 *     }
 * }
 * }</code></pre>
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface PropertyBean  {
    // ---
    // constant declarations
    // ---
    /**
     * The name of the "properties" property.
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
    ReadOnlyMapProperty<Key<?>, Object> properties();

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
        return key.put(properties(), newValue);
    }

    /**
     * Gets a property value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the value
     */
    default <T> T get(Key<T> key) {
        return key.get(properties());
    }
    
    /**
     * Removes a property value.
     *
     * @param <T> the value type
     * @param key the key
     * @return the removed value
     */
    default <T> T remove(Key<T> key) {
        return (T) properties().remove(key);
    }
}
