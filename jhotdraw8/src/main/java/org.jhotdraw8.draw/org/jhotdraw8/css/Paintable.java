/*
 * @(#)Paintable.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.paint.Paint;
import org.jhotdraw8.annotation.Nullable;

/**
 * Paintable.
 *
 * @author Werner Randelshofer
 */
public interface Paintable {

    @Nullable
    public Paint getPaint();

    @Nullable
    public static Paint getPaint(@Nullable Paintable p) {
        return p == null ? null : p.getPaint();
    }
}
