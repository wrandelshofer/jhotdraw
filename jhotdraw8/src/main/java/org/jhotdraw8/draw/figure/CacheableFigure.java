/* @(#)TransformCachingFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import org.jetbrains.annotations.Nullable;
import org.jhotdraw8.collection.Key;
import static org.jhotdraw8.draw.figure.FigureImplementationDetails.*;
import org.jhotdraw8.geom.Transforms;

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
     <T>T setCachedValue(@Nonnull Key<T> key,@Nullable T value);
     @Nullable <T>T getCachedValue(@Nonnull Key<T> key);

}
