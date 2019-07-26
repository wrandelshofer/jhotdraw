/*
 * @(#)PathConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.geom.Intersection;
import org.jhotdraw8.geom.Intersections;

import java.awt.geom.PathIterator;

import static org.jhotdraw8.draw.figure.StrokableFigure.STROKE;
import static org.jhotdraw8.draw.figure.StrokableFigure.STROKE_TYPE;

/**
 * PathConnector. The target of the connection must implement {@link PathIterableFigure}.
 *
 * @author Werner Randelshofer
 * $$
 */
public class PathConnector extends LocatorConnector {

    public PathConnector() {
        super(BoundsLocator.CENTER);
    }

    public PathConnector(Locator locator) {
        super(locator);
    }


    @Nullable
    @Override
    public Intersection.IntersectionPoint intersect(Figure connection, Figure target, @Nonnull Point2D start, @Nonnull Point2D end) {
        if (!(target instanceof PathIterableFigure)) {
            return super.intersect(connection, target, start, end);
        }
        PathIterableFigure pif = (PathIterableFigure) target;
        Point2D s = target.worldToLocal(start);
        Point2D e = target.worldToLocal(end);
        PathIterator pit;

        // FIXME does not take line join into account
        if (target.getStyled(STROKE) != null) {
            double grow;
            switch (target.getStyledNonnull(STROKE_TYPE)) {
                case CENTERED:
                default:
                    // FIXME must stroke the path
                    pit = pif.getPathIterator(null);
                    break;
                case OUTSIDE:
                    // FIXME must stroke the path
                    pit = pif.getPathIterator(null);
                    break;
                case INSIDE:
                    pit = pif.getPathIterator(null);
                    break;
            }
        } else {
            pit = pif.getPathIterator(null);
        }

        Intersection i = Intersections.intersectLinePathIterator(s, e, pit);
        return i.getLastIntersectionPoint();
    }
}
