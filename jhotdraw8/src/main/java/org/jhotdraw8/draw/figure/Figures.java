/* @(#)Figures.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import static java.lang.Double.max;
import static java.lang.Math.min;
import java.util.Collection;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;

/**
 * Figures.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Figures {

    private Figures() {

    }

    public static Bounds getBounds(Iterable<? extends Figure> figures) {
        double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE,
                maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE;
        for (Figure f : figures) {
            Bounds b = f.localToWorld(f.getBoundsInLocal());
            minx = min(minx, b.getMinX());
            maxx = max(maxx, b.getMaxX());
            miny = min(miny, b.getMinY());
            maxy = max(maxy, b.getMaxY());
        }
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }
}
