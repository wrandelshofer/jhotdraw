/*
 * @(#)ImmutableHashMap.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.function.Function;

public class ImmutableHashMap<K, V> extends AbstractReadOnlyMap<K, V> implements ImmutableMap<K, V> {
    @NonNull
    private final Map<K, V> backingMap;
    final static ImmutableMap<?, ?> EMPTY_MAP = new ImmutableHashMap<>();


    public ImmutableHashMap(@NonNull Map<? extends K, ? extends V> backingMap) {
        if (backingMap.isEmpty()) {
            this.backingMap = Collections.emptyMap();
        } else {
            this.backingMap = new LinkedHashMap<>(backingMap);
        }
    }

    public ImmutableHashMap(@NonNull ReadOnlyMap<? extends K, ? extends V> backingMap) {
        if (backingMap.isEmpty()) {
            this.backingMap = Collections.emptyMap();
        } else {
            LinkedHashMap<K, V> backingMap1 = new LinkedHashMap<>(backingMap.size());
            this.backingMap = backingMap1;
            for (Map.Entry<? extends K, ? extends V> entry : backingMap.entrySet()) {
                backingMap1.put(entry.getKey(), entry.getValue());
            }

        }
    }

    public ImmutableHashMap() {
        this.backingMap = Collections.emptyMap();
    }

    public ImmutableHashMap(K k1, V v1) {
        HashMap<K, V> backingMap1 = new LinkedHashMap<>(1);
        backingMap1.put(k1, v1);
        this.backingMap = backingMap1;
    }

    public ImmutableHashMap(K k1, V v1, K k2, V v2) {
        HashMap<K, V> backingMap1 = new LinkedHashMap<>(2);
        backingMap1.put(k1, v1);
        backingMap1.put(k2, v2);
        this.backingMap = backingMap1;
    }

    public ImmutableHashMap(K k1, V v1, K k2, V v2, K k3, V v3) {
        HashMap<K, V> backingMap1 = new LinkedHashMap<>(2);
        backingMap1.put(k1, v1);
        backingMap1.put(k2, v2);
        backingMap1.put(k3, v3);
        this.backingMap = backingMap1;
    }

    public ImmutableHashMap(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        HashMap<K, V> backingMap1 = new LinkedHashMap<>(2);
        backingMap1.put(k1, v1);
        backingMap1.put(k2, v2);
        backingMap1.put(k3, v3);
        backingMap1.put(k4, v4);
        this.backingMap = backingMap1;
    }

    public ImmutableHashMap(@NonNull Collection<? extends Map.Entry<K, V>> entries) {
        HashMap<K, V> backingMap1 = new LinkedHashMap<>(entries.size());
        for (Map.Entry<K, V> entry : entries) {
            backingMap1.put(entry.getKey(), entry.getValue());
        }
        this.backingMap = backingMap1;
    }

    public ImmutableHashMap(@NonNull ReadOnlyCollection<Map.Entry<K, V>> entries) {
        HashMap<K, V> backingMap1 = new LinkedHashMap<>(entries.size());
        for (Map.Entry<K, V> entry : entries) {
            backingMap1.put(entry.getKey(), entry.getValue());
        }
        this.backingMap = backingMap1;
    }

    public <X> ImmutableHashMap(@NonNull Iterable<X> entries, @NonNull Function<X, Map.Entry<K, V>> mappingFunction) {
        HashMap<K, V> backingMap1 = new HashMap<>();
        for (X x : entries) {
            Map.Entry<K, V> entry = mappingFunction.apply(x);
            backingMap1.put(entry.getKey(), entry.getValue());
        }

        this.backingMap = backingMap1;
    }

    @Override
    public boolean isEmpty() {
        return backingMap.isEmpty();
    }

    @Override
    public int size() {
        return backingMap.size();
    }

    @Override
    public V get(K key) {
        return backingMap.get(key);
    }

    @NonNull
    @Override
    public Iterator<Map.Entry<K, V>> entries() {
        return backingMap.entrySet().iterator();
    }

    @NonNull
    @Override
    public Iterator<K> keys() {
        return backingMap.keySet().iterator();
    }

    @Override
    public boolean containsKey(K key) {
        return backingMap.containsKey(key);
    }

    @Override
    public String toString() {
        return backingMap.toString();
    }
}
