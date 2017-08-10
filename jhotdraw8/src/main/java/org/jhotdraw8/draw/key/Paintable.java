/* @(#)Paintable.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.key;

import javafx.scene.paint.Paint;

/**
 * Paintable.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface Paintable {

    public Paint getPaint();

    public static Paint getPaint(Paintable p) {
        return p == null ? null : p.getPaint();
    }
}
