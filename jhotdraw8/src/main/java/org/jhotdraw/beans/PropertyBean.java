/* @(#)PropertyBean.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.beans;

import javafx.beans.property.MapProperty;
import org.jhotdraw.collection.Key;

/**
 * Interface for beans which support an open number of properties.
 * <p>
 * A property is typically accessed using a type safe {@link Key}.
 * <p>
 * To implement this interface, you need to implement the {@code valuesProperty()}
 * method as shown below.
 *
 * <pre>{@code 
 * public class MyBean implements PropertyBean {
 * private MapProperty<Key<?>, Object> properties;*
 *
 *  @Override
 *  public final MapProperty<Key<?>, Object> properties() {
 *      if (properties == null) {
 *          properties = new SimpleMapProperty<>(FXCollections.observableMap(new HashMap<Key<?>, Object>()));
 *      }
 *      return properties;
 *  }
 * }
 * }</pre>
 * 
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface PropertyBean {

    /** Returns an observable map of properties.
     * @return the map 
     */
    MapProperty<Key<?>,Object> properties();

    /** Sets a property value. */
    default <T> void set(Key<T> key, T value) {
       key.put(properties(), value);
    }
    /** Gets a property value. */
    default <T> T get(Key<T> key) {
       return key.get(properties());
    }
}
