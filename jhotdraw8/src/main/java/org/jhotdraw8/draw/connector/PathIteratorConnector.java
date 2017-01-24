/* @(#)RectangleConnector.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.connector;

import java.awt.geom.PathIterator;
import javafx.geometry.Point2D;
import org.jhotdraw8.draw.figure.Figure;
import static org.jhotdraw8.draw.figure.Figure.bounds;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE_COLOR;
import static org.jhotdraw8.draw.figure.StrokeableFigure.STROKE_TYPE;
import org.jhotdraw8.draw.locator.Locator;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Intersection;

/**
 * RectangleConnector.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PathIteratorConnector extends LocatorConnector {
    
    public PathIteratorConnector() {
        super(new RelativeLocator(0.5, 0.5));
    }

    public PathIteratorConnector(Locator locator) {
        super(locator);
    }
    
    @Override
    public Point2D chopStart(Figure connection, Figure target, double sx, double sy, double ex, double ey) {
        return chopStart(connection, target, new Point2D(sx, sy), new Point2D(ex, ey));
    }

    @Override
    public Point2D chopStart(Figure connection, Figure target, Point2D start, Point2D end) {
        Double t = intersect(connection, target, start, end);
        return t == null ? start : Geom.lerp(start, end, t);
    }

    @Override
    public Double intersect(Figure connection, Figure target, Point2D start, Point2D end) {
        if (!(target instanceof PathIterableFigure)) {
            return 0.0;
        }
        PathIterableFigure pif = (PathIterableFigure) target;
        Point2D s = target.worldToLocal(start);
        Point2D e = target.worldToLocal(end);
        PathIterator pit;

        
        // FIXME does not take line join into account
        if (target.getStyled(STROKE_COLOR) != null) {
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
        double maxT = 0;
        for (double t : i.getTs()) {
            if (t > maxT) {
                maxT = t;
            }
        }
        return i.isEmpty() ? null : maxT;
    }
}
