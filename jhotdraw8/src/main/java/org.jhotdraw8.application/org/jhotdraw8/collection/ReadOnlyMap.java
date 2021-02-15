/*
 * @(#)ReadOnlyMap.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Iterator;
import java.util.Map;
import java.util.Objects;

/**
 * Provides query methods to a map. The state of the map
 * may change.
 * <p>
 * This interface does not guarantee 'read-only', it actually guarantees
 * 'readable'. We use the prefix 'ReadOnly' because this is the naming
 * convention in JavaFX for APIs that provide read methods but no write methods.
 *
 * @param <K>
 * @param <V>
 */
public interface ReadOnlyMap<K, V> {
    boolean isEmpty();

    int size();

    V get(K key);

    Iterator<Map.Entry<K, V>> entries();

    Iterator<K> keys();

    boolean containsKey(K key);

    default @NonNull ReadOnlySet<Map.Entry<K, V>> entrySet() {
        return new ReadOnlySet<Map.Entry<K, V>>() {

            @Override
            public @NonNull Iterator<Map.Entry<K, V>> iterator() {
                return ReadOnlyMap.this.entries();
            }

            @Override
            public int size() {
                return ReadOnlyMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry<?, ?>)) {
                    return false;
                }
                @SuppressWarnings("unchecked") Map.Entry<K, V> e = (Map.Entry<K, V>) o;
                K key = e.getKey();
                V value = ReadOnlyMap.this.get(key);
                return Objects.equals(value, e.getValue());
            }
        };
    }

    default @NonNull ReadOnlySet<K> keySet() {
        return new ReadOnlySet<K>() {

            @Override
            public @NonNull Iterator<K> iterator() {
                return ReadOnlyMap.this.keys();
            }

            @Override
            public int size() {
                return ReadOnlyMap.this.size();
            }

            @Override
            public boolean contains(Object o) {
                if (!(o instanceof Map.Entry)) {
                    return false;
                }
                Map.Entry<?, ?> e = (Map.Entry<?, ?>) o;
                @SuppressWarnings("unchecked") K key = (K) e.getKey();
                V value = ReadOnlyMap.this.get(key);
                return Objects.equals(value, e.getValue());
            }
        };
    }

    /**
     * Wraps this map in the Map API - without copying.
     *
     * @return the wrapped map
     */
    default @NonNull Map<K, V> asMap() {
        return new MapWrapper<>(this);
    }
}
