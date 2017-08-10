/* @(#)PathConnector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.connector;

import java.awt.geom.PathIterator;
import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE_TYPE;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.geom.Intersection;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE;

/**
 * PathConnector. The target of the connection must implement {@link PathIterableFigure}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 $$
 */
public class PathConnector extends LocatorConnector {

    public PathConnector() {
        super(new RelativeLocator(0.5, 0.5));
    }

    public PathConnector(Locator locator) {
        super(locator);
    }


    @Override
    public Double intersect(Figure connection, Figure target, Point2D start, Point2D end) {
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
            switch (target.getStyled(STROKE_TYPE)) {
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

        Intersection i = Intersection.intersectLinePathIterator(s, e, pit);
        return i.isEmpty() ? null : i.getIntersections().lastKey();
    }
}
