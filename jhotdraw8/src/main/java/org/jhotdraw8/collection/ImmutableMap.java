package org.jhotdraw8.collection;

import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Collection;
import java.util.Map;

public interface ImmutableMap<K, V> extends ReadOnlyMap<K, V> {

    static <K, V> ImmutableMap<K, V> of(K k1, V v1) {
        return new ImmutableHashMap<>(k1, v1);
    }

    static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2) {
        return new ImmutableHashMap<>(k1, v1, k2, v2);
    }

    static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3) {
        return new ImmutableHashMap<>(k1, v1, k2, v2, k3, v3);
    }

    static <K, V> ImmutableMap<K, V> of(K k1, V v1, K k2, V v2, K k3, V v3, K k4, V v4) {
        return new ImmutableHashMap<>(k1, v1, k2, v2, k3, v3, k4, v4);
    }

    static <K, V> Map.Entry<K, V> entry(K k, V v) {
        return new AbstractMap.SimpleImmutableEntry<>(k, v);
    }

    @SafeVarargs
    static <K, V> ImmutableMap<K, V> ofEntries(Map.Entry<K, V>... entries) {
        @SuppressWarnings("varargs")
        ImmutableHashMap<K, V> result = new ImmutableHashMap<>(Arrays.asList(entries));
        return result;
    }

    static <K, V> ImmutableMap<K, V> of(Map<K, V> map) {
        @SuppressWarnings("unchecked")
        ImmutableMap<K, V> kvImmutableMap = map instanceof ImmutableMap<?, ?> ? (ImmutableMap<K, V>) map : new ImmutableHashMap<K, V>(map);
        return kvImmutableMap;
    }

    static <K, V> ImmutableMap<K, V> of(ReadOnlyMap<K, V> map) {

        return map instanceof ImmutableMap<?, ?> ? (ImmutableMap<K, V>) map : new ImmutableHashMap<K, V>(map);
    }

    static <K, V> ImmutableMap<K, V> of(ReadOnlyCollection<Map.Entry<K, V>> entrySet) {
        return new ImmutableHashMap<>(entrySet);
    }

    static <K, V> ImmutableMap<V, K> inverseOf(Iterable<Map.Entry<K, V>> entrySet) {
        return new ImmutableHashMap<>(entrySet, entry -> new AbstractMap.SimpleImmutableEntry<>(entry.getValue(), entry.getKey()));
    }

    static <K, V> ImmutableMap<K, V> of(Collection<Map.Entry<K, V>> entrySet) {
        return new ImmutableHashMap<>(entrySet);
    }

    static <K, V> ImmutableMap<K, V> ofMap(Map<? extends K, ? extends V> map) {
        return new ImmutableHashMap<>(map);
    }

    static <K, V> ImmutableMap<K, V> emptyMap() {
        @SuppressWarnings("unchecked")
        ImmutableMap<K, V> map = (ImmutableMap<K, V>)ImmutableHashMap.EMPTY_MAP;
        return map;
    }


}
