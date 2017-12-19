/* @(#)RectangleConnector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE_TYPE;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE_WIDTH;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Intersection;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE;
import org.jhotdraw8.geom.Intersections;

/**
 * RectangleConnector.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RectangleConnector extends LocatorConnector {

    public RectangleConnector() {
        super(RelativeLocator.CENTER);
    }

    public RectangleConnector(Locator locator) {
        super(locator);
    }

    @Override
    public Double intersect(Figure connection, Figure target, Point2D start, Point2D end) {
        Point2D s = target.worldToLocal(start);
        Point2D e = target.worldToLocal(end);
        Bounds bounds = target.getBoundsInLocal();

        // FIXME does not take line join into account
        if (target.getStyled(STROKE) != null) {
            double grow;
            switch (target.getStyled(STROKE_TYPE)) {
                case CENTERED:
                default:
                    grow = target.getStyled(STROKE_WIDTH) / 2d;
                    break;
                case OUTSIDE:
                    grow = target.getStyled(STROKE_WIDTH);
                    break;
                case INSIDE:
                    grow = 0d;
                    break;
            }
            bounds = Geom.grow(bounds, grow, grow);
        }

        Intersection i = Intersections.intersectLineRectangle(s, e, bounds);
        double maxT = 0;
        for (double t : i.getTs()) {
            if (t > maxT) {
                maxT = t;
            }
        }
        return i.isEmpty() ? null : maxT;
    }
}
