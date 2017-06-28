/* @(#)Paintable.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
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
