/* @(#)MapEntryProperty.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import javafx.beans.binding.MapExpression;
import javafx.beans.property.ReadOnlyObjectWrapper;
import javafx.collections.MapChangeListener;
import javafx.collections.ObservableMap;
import javafx.collections.WeakMapChangeListener;

/**
 * This property is weakly bound to an entry in a map.
 *
 * @author Werner Randelshofer
 * @param <K> key type
 * @param <V> map value type
 * @param <T> entry value type
 */
public class MapEntryProperty<K, V, T extends V> extends ReadOnlyObjectWrapper<T> {

    protected ObservableMap<K, V> map;
    protected K key;
    protected MapChangeListener<K, V> mapListener;
    protected Class<T> tClazz;
    private WeakMapChangeListener<K,V> weakListener;

    public MapEntryProperty(ObservableMap<K, V> map, K key, Class<T> tClazz) {
        this.map = map;
        this.key = key;
        this.tClazz = tClazz;

        if (key != null) {
            this.mapListener = (MapChangeListener.Change<? extends K, ? extends V> change) -> {
                if (this.key.equals(change.getKey())) {
                    @SuppressWarnings("unchecked")
                    T valueAdded = (T) change.getValueAdded();
                    if (super.get() != valueAdded) {
                        set(valueAdded);
                    }
                }
            };
            map.addListener(weakListener=new WeakMapChangeListener<>(mapListener));
        }
    }

    @Override
    public T getValue() {
        @SuppressWarnings("unchecked")
        T temp = (T) map.get(key);
        return temp;
    }

    @Override
    public void setValue(T value) {
        if (value != null && !tClazz.isAssignableFrom(value.getClass())) {
            throw new IllegalArgumentException("value is not assignable " + value);
        }
        V temp = (V) value;
        map.put(key, temp);

        // Note: super must be called after "put", so that listeners
        //       can be properly informed.
        super.setValue(value);
    }

    @Override
    public void unbind() {
        super.unbind();
        if (map != null) {
            map.removeListener(weakListener);
            mapListener = null;
            map = null;
            key = null;
        }
    }
}
