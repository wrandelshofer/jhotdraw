/* @(#)CompositeMapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.collection;

import java.util.Map;
import java.util.Set;

/**
 * CompositeMapAccessor.
 *
 * @param <T> the value type
 * @author Werner Randelshofer
 */
public interface CompositeMapAccessor<T> extends MapAccessor<T> {

    long serialVersionUID = 1L;

    @Override
    default boolean containsKey(Map<Key<?>, Object> map) {
        for (MapAccessor<?> sub : getSubAccessors()) {
            if (!sub.containsKey(map)) {
                return false;
            }
        }
        return true;
    }

    public Set<MapAccessor<?>> getSubAccessors();

}
