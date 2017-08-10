/* @(#)TransformCachingFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.Key;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.*;
import org.jhotdraw8.geom.Transforms;

/**
 * Provides a cache for computed values.
 *
 * @design.pattern Figure Mixin, Traits.
 *
 * @author Werner Randelshofer
 * @version $Id: TransformCacheableFigure.java 1120 2016-01-15 17:37:49Z
 * rawcoder $
 */
public interface CacheableFigure extends Figure {

     <T>T setCachedValue(Key<T> key, T value);
     <T>T getCachedValue(Key<T> key);

}
