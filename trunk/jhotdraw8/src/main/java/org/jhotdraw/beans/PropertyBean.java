/* @(#)PropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.beans;

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
public interface PropertyBean {
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
     * Returns an observable map of properties.
     *
     * @return the map
     */
    ReadOnlyMapProperty<Key<?>, Object> properties();

    /**
     * Sets a property value.
     *
     * @param <T> the value type
     * @param key the key
     * @param value the value
     */
    default <T> void set(Key<T> key, T value) {
        key.put(properties(), value);
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
}
