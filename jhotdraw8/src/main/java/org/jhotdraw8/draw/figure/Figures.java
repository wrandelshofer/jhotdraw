/* @(#)Figures.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import static java.lang.Double.max;
import static java.lang.Math.min;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.jhotdraw8.collection.Iterators;
import org.jhotdraw8.geom.BoundsCalculator;

/**
 * Figures.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Figures {

    private Figures() {

    }

    public static Bounds getBoundsInWorld(Iterable<? extends Figure> figures) {
        Bounds b3= Iterators.toList(figures).stream().parallel().map(Figure::getBoundsInWorld)
                .filter(b->Double.isFinite(b.getMaxX())&&Double.isFinite(b.getMaxY()))
                .collect(BoundsCalculator::new, BoundsCalculator::accept,
                        BoundsCalculator::combine).getBounds();
        return b3;
    }

    public static Bounds getBoundsOLD(Iterable<? extends Figure> figures) {
        double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE,
                maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE;
        for (Figure f : figures) {
            Bounds b = f.localToWorld(f.getBoundsInLocal());
            final double bmaxx = b.getMaxX();
            final double bmaxy = b.getMaxY();
            if (Double.isNaN(bmaxx)||Double.isNaN(bmaxy)) continue;
            minx = min(minx, b.getMinX());
            maxx = max(maxx, bmaxx);
            miny = min(miny, b.getMinY());
            maxy = max(maxy, bmaxy);
        }
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }

    public static Bounds getCenterBounds(Iterable<? extends Figure> figures) {
        double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE,
                maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE;
        for (Figure f : figures) {
            Bounds b = f.localToWorld(f.getBoundsInLocal());
            double cx = b.getMinX() + b.getWidth() * 0.5;
            double cy = b.getMinY() + b.getHeight() * 0.5;
            minx = min(minx, cx);
            maxx = max(maxx, cx);
            miny = min(miny, cy);
            maxy = max(maxy, cy);
        }
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }
}
