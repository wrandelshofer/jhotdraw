/* @(#)Paintable.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.scene.paint.Paint;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Paintable.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Paintable {

    @Nullable
    public Paint getPaint();

    @Nullable
    public static Paint getPaint(@Nullable Paintable p) {
        return p == null ? null : p.getPaint();
    }
}
