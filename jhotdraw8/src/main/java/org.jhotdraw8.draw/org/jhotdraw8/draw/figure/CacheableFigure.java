/*
 * @(#)CacheableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.Key;

/**
 * Provides a cache for computed values.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @design.pattern Figure Mixin, Traits.
 */
public interface CacheableFigure extends Figure {

    @Nullable <T> T setCachedValue(Key<T> key, @Nullable T value);

    @Nullable <T> T getCachedValue(Key<T> key);

}
