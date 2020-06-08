/*
 * @(#)Paintable.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.paint.Paint;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * Paintable.
 *
 * @author Werner Randelshofer
 */
public interface Paintable {

    @Nullable Paint getPaint();

    default @Nullable Paint getPaint(RenderContext ctx) {
        return getPaint();
    }

    @Nullable
    static Paint getPaint(@Nullable Paintable p) {
        return p == null ? null : p.getPaint();
    }

    @Nullable
    static Paint getPaint(@Nullable Paintable p, RenderContext ctx) {
        return p == null ? null : p.getPaint(ctx);
    }
}
