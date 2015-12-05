/* @(#)KeyMapEntryProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.collection;

import javafx.beans.binding.MapExpression;
import org.jhotdraw.beans.MapEntryProperty;

/**
 * KeyMapEntryProperty.
 * <p>
 * FIXME currently only works fully if the provided MapAccessor is an instance
 * of Key.
 *
 * @author Werner Randelshofer
 */
public class KeyMapEntryProperty<V> extends MapEntryProperty<Key<?>, Object, V> {
private final MapAccessor<V> accessor;
    public KeyMapEntryProperty(MapExpression<Key<?>, Object> map, MapAccessor<V> key) {
        super(map, (key instanceof Key<?>)?(Key<?>)key:null, key.getValueType());
        this.accessor=key;
    }

    @Override
    public V getValue() {
        @SuppressWarnings("unchecked")
        V ret = accessor.get(map);
        return ret;
    }
    @Override
    public void setValue(V value) {
        if (value != null && !tClazz.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("value is not assignable " + value);
        }
       accessor.put(map, value);

            // Note: super must be called after "put", so that listeners
        //       can be properly informed.
        super.setValue(value);
    }
}
