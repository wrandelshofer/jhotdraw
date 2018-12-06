/* @(#)RelativeLocator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.locator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.Geom;

/**
 * A locator that specifies a point that is relative to the bounds of a figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class RelativeLocator extends AbstractLocator {

    public static final RelativeLocator CENTER = new RelativeLocator(0.5, 0.5);

    public static final RelativeLocator EAST = new RelativeLocator(1.0, 0.5);
    public static final RelativeLocator NORTH = new RelativeLocator(0.5, 0.0);
    public static final RelativeLocator NORTH_EAST = new RelativeLocator(1.0, 0.0);
    public static final RelativeLocator NORTH_WEST = new RelativeLocator(0.0, 0.0);
    public static final RelativeLocator SOUTH = new RelativeLocator(0.5, 1.0);
    public static final RelativeLocator SOUTH_EAST = new RelativeLocator(1.0, 1.0);
    public static final RelativeLocator SOUTH_WEST = new RelativeLocator(0.0, 1.0);
    public static final RelativeLocator WEST = new RelativeLocator(0.0, 0.5);

    private static final long serialVersionUID = 1L;
    /**
     * Relative x-coordinate on the bounds of the figure. The value 0 is on the
     * left boundary of the figure, the value 1 on the right boundary.
     */
    protected final double relativeX;
    /**
     * Relative y-coordinate on the bounds of the figure. The value 0 is on the
     * top boundary of the figure, the value 1 on the bottom boundary.
     */
    protected final double relativeY;

    /**
     * Creates a new instance.
     */
    public RelativeLocator() {
        this(0, 0);
    }

    /**
     * Creates a new instance.
     * @param relativeX x-position relative to bounds expressed as a value
     * between 0 and 1.
     * @param relativeY y-position relative to bounds expressed as a value
     * between 0 and 1.
     */
    public RelativeLocator(double relativeX, double relativeY) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }
    /**
     * Creates a new instance.
     * @param bounds current local bounds of a figure 
     * @param p a local coordinate on the figure
     */
    public RelativeLocator(@Nonnull Bounds bounds, Point2D p) {
        this(bounds,p.getX(),p.getY());
    }
    /**
     * Creates a new instance.
     * @param bounds current local bounds of a figure 
     * @param x a local coordinate on the figure
     * @param y a local coordinate on the figre
     */
    public RelativeLocator(Bounds bounds, double x, double y) {
        this(Geom.clamp((x-bounds.getMinX())/ bounds.getWidth(),0,1),
         Geom.clamp((y-bounds.getMinY())/ bounds.getHeight(),0,1));
    }

    public double getRelativeX() {
        return relativeX;
    }

    public double getRelativeY() {
        return relativeY;
    }

    @Nonnull
    @Override
    public Point2D locate(@Nonnull Figure owner) {
        Bounds bounds = owner.getBoundsInLocal();

        Point2D location = new Point2D(
                bounds.getMinX() + bounds.getWidth() * relativeX,
                bounds.getMinY() + bounds.getHeight() * relativeY
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
        final RelativeLocator other = (RelativeLocator) obj;
        if (this.relativeX != other.relativeX) {
            return false;
        }
        if (this.relativeY != other.relativeY) {
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.relativeX) ^ (Double.doubleToLongBits(this.relativeX) >>> 32));
        hash = 71 * hash + (int) (Double.doubleToLongBits(this.relativeY) ^ (Double.doubleToLongBits(this.relativeY) >>> 32));
        return hash;
    }
}
