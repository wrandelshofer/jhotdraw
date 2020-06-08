/*
 * @(#)ImmutableMaps.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public class ImmutableMaps {


    @NonNull
    @SafeVarargs
    public static <K, V> ImmutableMap<K, V> ofEntries(Map.Entry<K, V>... entries) {
        @SuppressWarnings("varargs")
        ImmutableHashMap<K, V> result = new ImmutableHashMap<>(Arrays.asList(entries));
        return result;
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> of(Map<K, V> map) {
        @SuppressWarnings("unchecked")
        ImmutableMap<K, V> kvImmutableMap = map instanceof ImmutableMap<?, ?> ? (ImmutableMap<K, V>) map : new ImmutableHashMap<K, V>(map);
        return kvImmutableMap;
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> of(ReadOnlyMap<K, V> map) {

        return map instanceof ImmutableMap<?, ?> ? (ImmutableMap<K, V>) map : new ImmutableHashMap<K, V>(map);
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> of(@NonNull ReadOnlyCollection<Map.Entry<K, V>> entrySet) {
        return new ImmutableHashMap<>(entrySet);
    }

    @NonNull
    public static <K, V> ImmutableMap<V, K> inverseOf(@NonNull Iterable<Map.Entry<K, V>> entrySet) {
        return new ImmutableHashMap<>(entrySet, entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getValue(), entry.getKey()));
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> of(@NonNull Collection<Map.Entry<K, V>> entrySet) {
        return new ImmutableHashMap<>(entrySet);
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> ofMap(@NonNull Map<? extends K, ? extends V> map) {
        return new ImmutableHashMap<>(map);
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> emptyMap() {
        @SuppressWarnings("unchecked")
        ImmutableMap<K, V> map = (ImmutableMap<K, V>) ImmutableHashMap.EMPTY_MAP;
        return map;
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> of(K k1, V v1) {
        return new ImmutableHashMap<>(k1, v1);
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2) {
        return new ImmutableHashMap<>(k1, v1, k2, v2);
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return new ImmutableHashMap<>(k1, v1, k2, v2, k3, v3);
    }

    @NonNull
    public static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return new ImmutableHashMap<>(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    @NonNull
    public static <K, V> Map.Entry<K, V> entry(K k, V v) {
        return new AbstractMap.SimpleImmutableEntry<>(k, v);
    }
}
