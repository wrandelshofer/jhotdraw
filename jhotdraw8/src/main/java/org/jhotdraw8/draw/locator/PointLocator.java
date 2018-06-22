/* @(#)BezierPointLocator.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.locator;

import javafx.geometry.Point2D;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A {@link Locator} which locates a node on a point of a Figure.
 *
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PointLocator extends AbstractLocator {

    private static final long serialVersionUID = 1L;
    private MapAccessor<Point2D> key;

    public PointLocator(MapAccessor<Point2D> key) {
        this.key = key;
    }

    @Override
    public Point2D locate(@NonNull Figure owner) {
        return owner.get(key);
    }
}
