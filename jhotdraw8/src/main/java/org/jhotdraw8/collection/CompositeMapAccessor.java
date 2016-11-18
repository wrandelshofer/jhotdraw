/* @(#)CompositeMapAccessor.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.collection;

import java.util.Set;

/**
 * CompositeMapAccessor.
 * 
 * @param <T> the value type
 * @author Werner Randelshofer
 */
public interface CompositeMapAccessor<T> extends MapAccessor<T> {
    public Set<MapAccessor<?>> getSubAccessors();
}
