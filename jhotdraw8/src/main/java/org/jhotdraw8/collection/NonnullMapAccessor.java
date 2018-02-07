/* @(#)NonnullMapAccessor.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.collection;

import java.util.Map;

/**
 * NonnullMapAccessor.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface NonnullMapAccessor<T> extends MapAccessor<T> {
   final static long serialVersionUID = 1L;
    /**
     * Gets the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A Map.
     * @return The value of the attribute.
     */
        default T getNonnull( Map<? super Key<?>, Object> a) {
        T t = get(a);
        assert t != null;
        return t;
    }

    /**
     * Puts the value of the attribute denoted by this accessor from a Map.
     *
     * @param a A map.
     * @param value The new value.
     * @return The old value.
     */
        default T putNonnull( Map<? super Key<?>, Object> a,  T value) {
        T t = put(a, value);
        assert t != null;
        return t;
    }

}
