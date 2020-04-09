/*
 * @(#)EllipseConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Intersection;
import org.jhotdraw8.geom.Intersections;

import static org.jhotdraw8.draw.figure.StrokableFigure.STROKE;
import static org.jhotdraw8.draw.figure.StrokableFigure.STROKE_TYPE;
import static org.jhotdraw8.draw.figure.StrokableFigure.STROKE_WIDTH;

/**
 * EllipseConnector.
 *
 * @author Werner Randelshofer
 */
public class EllipseConnector extends LocatorConnector {

    public EllipseConnector() {
        super(BoundsLocator.CENTER);
    }

    public EllipseConnector(Locator locator) {
        super(locator);
    }

    @Nullable
    @Override
    public Intersection.IntersectionPoint intersect(Figure connection, @NonNull Figure target, @NonNull Point2D start, @NonNull Point2D end) {
        Point2D s = target.worldToLocal(start);
        Point2D e = target.worldToLocal(end);
        Bounds bounds = target.getLayoutBounds();

        // FIXME does not take line join into account
        if (target.getStyled(STROKE) != null) {
            double grow;
            switch (target.getStyledNonNull(STROKE_TYPE)) {
                case CENTERED:
                default:
                    grow = target.getStyledNonNull(STROKE_WIDTH).getConvertedValue() / 2d;
                    break;
                case OUTSIDE:
                    grow = target.getStyledNonNull(STROKE_WIDTH).getConvertedValue();
                    break;
                case INSIDE:
                    grow = 0d;
                    break;
            }
            bounds = Geom.grow(bounds, grow, grow);
        }

        Intersection i = Intersections.intersectLineEllipse(s, e, bounds);
        return i.getLastIntersectionPoint();
    }
}
