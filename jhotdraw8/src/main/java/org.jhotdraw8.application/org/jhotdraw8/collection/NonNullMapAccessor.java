/*
 * @(#)NonNullMapAccessor.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import org.jhotdraw8.annotation.NonNull;

import java.util.Map;

/**
 * NonNullMapAccessor.
 *
 * @author Werner Randelshofer
 */
public interface NonNullMapAccessor<T> extends MapAccessor<T> {
    long serialVersionUID = 1L;

    /**
     * Gets the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    @NonNull
    default T getNonNull(@NonNull Map<? super Key<?>, Object> a) {
        T t = get(a);
        assert t != null;
        return t;
    }

    /**
     * Puts the value of the attribute denoted by this accessor from a Map.
     *
     * @param a     A map.
     * @param value The new value.
     * @return The old value.
     */
    @NonNull
    default T putNonNull(@NonNull Map<? super Key<?>, Object> a, @NonNull T value) {
        T t = put(a, value);
        assert t != null;
        return t;
    }

    @NonNull
    default T getDefaultValueNonNull() {
        T v = getDefaultValue();
        if (v == null) {
            throw new NullPointerException();
        }
        return v;
    }
}
