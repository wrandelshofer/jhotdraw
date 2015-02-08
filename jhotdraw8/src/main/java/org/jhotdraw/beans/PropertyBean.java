/* @(#)PropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.property.MapProperty;
import javafx.beans.property.ObjectProperty;
import org.jhotdraw.collection.Key;

/**
 * Interface for beans which support an open number of properties.
 * <p>
 * A property is accessed using a type safe {@link Key}.
 * <p>
 * To implement this interface, you need to implement the {@code valuesProperty()}
 * method as shown below.
 *
 * <pre>{@code 
 * public class MyBean implements PropertyBean {
 *   private final MapProperty<Key<?>, ObjectProperty<?>> values =
 *           new SimpleMapProperty<>(FXCollections.observableHashMap());
 *   }
 *   public MapProperty<Key<?>, ObjectProperty<?>> valuesProperty() {
 *      return values;
 *   }
 * }
 * }</pre>
 * 
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface PropertyBean {

    /** A map for client properties.
     * @return the map 
     */
    public MapProperty<Key<?>, ObjectProperty<?>> valuesProperty();

    /** Sets a client property value.
     * @param <V> the value type
     * @param key the key
     * @param value the value
     */
    default public <V> void putValue(Key<V> key, V value) {
        key.putValue(valuesProperty(), value);
    }

    /** Gets a client property value.
     * @param <V> the value type
     * @param key the key
     * @return the value
     */
    default public <V> V getValue(Key<V> key) {
        return key.getValue(valuesProperty());
    }

    /** Gets a client property.
     * @param <V> the value type
     * @param key the key
     * @return the value
     */
    default public <V> ObjectProperty<V> getValueProperty(Key<V> key) {
        return key.getValueProperty(valuesProperty());
    }
}
