/*
 * @(#)Figures.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.Iterators;
import org.jhotdraw8.geom.BoundsCalculator;

import static java.lang.Double.max;
import static java.lang.Math.min;

/**
 * Figures.
 *
 * @author Werner Randelshofer
 */
public class Figures {

    private Figures() {

    }

    @NonNull
    public static Bounds getBoundsInWorld(Iterable<? extends Figure> figures) {
        Bounds b3 = Iterators.toList(figures).stream().parallel().map(Figure::getLayoutBoundsInWorld)
                .filter(b -> Double.isFinite(b.getMaxX()) && Double.isFinite(b.getMaxY()))
                .collect(BoundsCalculator::new, BoundsCalculator::accept,
                        BoundsCalculator::combine).getBounds();
        return b3;
    }

    @NonNull
    public static Bounds getCenterBounds(@NonNull Iterable<? extends Figure> figures) {
        double minx = Double.MAX_VALUE, miny = Double.MAX_VALUE,
                maxx = Double.MIN_VALUE, maxy = Double.MIN_VALUE;
        for (Figure f : figures) {
            Bounds b = f.localToWorld(f.getLayoutBounds());
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
