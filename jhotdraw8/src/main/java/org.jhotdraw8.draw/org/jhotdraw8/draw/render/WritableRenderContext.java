/*
 * @(#)RenderContext.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.render;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;

/**
 * RenderContext.
 *
 * @author Werner Randelshofer
 */
public interface WritableRenderContext extends RenderContext {
     <T> void set(@NonNull MapAccessor<T> key, @Nullable T value);
}
