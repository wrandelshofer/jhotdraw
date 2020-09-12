/*
 * @(#)KeyMapEntryProperty.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javafx.collections.ObservableMap;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

/**
 * KeyMapEntryProperty.
 * <p>
 * FIXME currently only works fully if the provided MapAccessor is an instance
 * of Key.
 *
 * @author Werner Randelshofer
 */
public class KeyMapEntryProperty<V> extends MapEntryProperty<Key<?>, Object, V> {
    @NonNull
    private final MapAccessor<V> accessor;

    public KeyMapEntryProperty(@NonNull ObservableMap<Key<?>, Object> map, MapAccessor<V> key) {
        super(map, (key instanceof Key<?>) ? (Key<?>) key : null, key.getRawValueType());
        this.accessor = key;
    }

    @Nullable
    @Override
    public V get() {
        @SuppressWarnings("unchecked")
        V ret = accessor.get(map);
        return ret;
    }

    @Override
    public void set(V value) {
        accessor.put(map, value);

        // Note: super must be called after "put", so that listeners
        //       can be properly informed.
        super.set(value);
    }

    @Nullable
    @Override
    public Object getBean() {
        return map;
    }

    @Override
    public String getName() {
        return accessor.toString();
    }
}
