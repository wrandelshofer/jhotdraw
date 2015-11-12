/* @(#)KeyMapEntryProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.collection;

import javafx.beans.binding.MapExpression;
import org.jhotdraw.beans.MapEntryProperty;

/**
 * KeyMapEntryProperty.
 *
 * @author Werner Randelshofer
 */
public class KeyMapEntryProperty<T> extends MapEntryProperty<Key<?>, Object, T> {

    public KeyMapEntryProperty(MapExpression<Key<?>, Object> map, Key<T> key) {
        super(map, key, key.getValueType());
    }

    @Override
    public T getValue() {
        @SuppressWarnings("unchecked")
        T ret = (T) key.get(map);
        return ret;
    }

}
