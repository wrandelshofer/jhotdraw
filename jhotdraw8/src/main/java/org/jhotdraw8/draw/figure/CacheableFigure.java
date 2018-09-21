/* @(#)TransformCachingFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javax.annotation.Nullable;
import org.jhotdraw8.collection.Key;

/**
 * Provides a cache for computed values.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface CacheableFigure extends Figure {

    @Nullable
    <T> T setCachedValue( Key<T> key, @Nullable T value);

    @Nullable
    <T> T getCachedValue( Key<T> key);

}
