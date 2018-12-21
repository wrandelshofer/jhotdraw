/* @(#)NonnullMapAccessor.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import java.util.Map;

/**
 * NonnullMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface NonnullMapAccessor<T> extends MapAccessor<T> {
    final static long serialVersionUID = 1L;

    /**
     * Gets the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
    @Nonnull
    default T getNonnull(@Nonnull Map<? super Key<?>, Object> a) {
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
    @Nonnull
    default T putNonnull(@Nonnull Map<? super Key<?>, Object> a, @Nonnull T value) {
        T t = put(a, value);
        assert t != null;
        return t;
    }

    @Nonnull
    default T getDefaultValueNonnull() {
        T v = getDefaultValue();
        if (v==null)throw new NullPointerException();
        return v;
    }
}
