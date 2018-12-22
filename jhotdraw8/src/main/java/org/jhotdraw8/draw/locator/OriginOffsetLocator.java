/* @(#)OriginOffsetLocator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.locator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;

/**
 * A locator that specifies a point as an offset to the top left corner (origin) of the figure..
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class OriginOffsetLocator extends AbstractLocator {

    private static final long serialVersionUID = 1L;
    /**
     * x-offset to origin.
     */
    protected final double offsetX;
    /**
     * y-offset to origin.
     */
    protected final double offsetY;

    /**
     * Creates a new instance.
     */
    public OriginOffsetLocator() {
        this(0, 0);
    }

    /**
     * Creates a new instance.
     * @param offsetX x-offset to origin
     * @param offsetY y-offset to origin
     */
    public OriginOffsetLocator(double offsetX, double offsetY) {
        this.offsetX = offsetX;
        this.offsetY = offsetY;
    }
    /**
     * Creates a new instance.
     * @param offset offset to origin
     */
    public OriginOffsetLocator(Point2D offset) {
        this(offset.getX(),offset.getY());
    }

    public double getOffsetX() {
        return offsetX;
    }

    public double getOffsetY() {
        return offsetY;
    }

    @Nonnull
    @Override
    public Point2D locate(@Nonnull Figure owner) {
        Bounds bounds = owner.getBoundsInLocal();

        Point2D location = new Point2D(
                bounds.getMinX() +  offsetX,
                bounds.getMinY() +  offsetY
        );
        return location;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final OriginOffsetLocator other = (OriginOffsetLocator) obj;
        if (this.offsetX != other.offsetX) {
            return false;
        }
        if (this.offsetY != other.offsetY) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.offsetX) ^ (Double.doubleToLongBits(this.offsetX) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.offsetY) ^ (Double.doubleToLongBits(this.offsetY) >>> 32));
        return hash;
    }
}
