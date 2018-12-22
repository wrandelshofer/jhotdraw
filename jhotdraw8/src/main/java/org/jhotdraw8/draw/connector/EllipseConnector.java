/* @(#)EllipseConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.StrokableFigure.STROKE_TYPE;
import static org.jhotdraw8.draw.figure.StrokableFigure.STROKE_WIDTH;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Intersection;
import static org.jhotdraw8.draw.figure.StrokableFigure.STROKE;
import org.jhotdraw8.geom.Intersections;

/**
 * EllipseConnector.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class EllipseConnector extends LocatorConnector {

    public EllipseConnector() {
        super(RelativeLocator.CENTER);
    }

    public EllipseConnector(Locator locator) {
        super(locator);
    }

    @Nullable
    @Override
    public Double intersect(Figure connection, @Nonnull Figure target, @Nonnull Point2D start, @Nonnull Point2D end) {
        Point2D s = target.worldToLocal(start);
        Point2D e = target.worldToLocal(end);
        Bounds bounds = target.getBoundsInLocal();

        // FIXME does not take line join into account
        if (target.getStyled(STROKE) != null) {
            double grow;
            switch (target.getStyledNonnull(STROKE_TYPE)) {
                case CENTERED:
                default:
                    grow = target.getStyledNonnull(STROKE_WIDTH).getConvertedValue() / 2d;
                    break;
                case OUTSIDE:
                    grow = target.getStyledNonnull(STROKE_WIDTH).getConvertedValue();
                    break;
                case INSIDE:
                    grow = 0d;
                    break;
            }
            bounds = Geom.grow(bounds, grow, grow);
        }

        Intersection i = Intersections.intersectLineEllipse(s, e, bounds);
        double maxT = 0;
        for (double t : i.getTs()) {
            if (t > maxT) {
                maxT = t;
            }
        }
        return i.isEmpty() ? null : maxT;
    }
}
