/* @(#)BezierPointLocator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.locator;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.NonnullMapAccessor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A {@link Locator} which locates a node on a point of a Figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PointLocator extends AbstractLocator {

    private static final long serialVersionUID = 1L;
    private NonnullMapAccessor<CssPoint2D> key;

    public PointLocator(NonnullMapAccessor<CssPoint2D> key) {
        this.key = key;
    }

    @Override
    public Point2D locate(@Nonnull Figure owner) {
        return owner.getNonnull(key).getConvertedValue();
    }
}
