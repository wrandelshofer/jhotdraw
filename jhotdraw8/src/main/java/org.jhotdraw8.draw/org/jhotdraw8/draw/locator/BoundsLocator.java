/*
 * @(#)BoundsLocator.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.locator;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.Geom;

/**
 * A locator that specifies a point that is relative to the bounds of a figure.
 * <p>
 * The locator has the following parameters:
 * <dl>
 *     <dt>{@code relativeX}</dt><dd>Defines a position on the X-axis
 *     of the figure, relative to its width.
 *     Where {@code 0.0} lies at the left edge of the
 *     figure and {@code 1.0} at the right edge.</dd>
 *     <dt>{@code relativeY}</dt><dd>Defines a position on the Y-axis
 *     of the figure, relative to its height.
 *     Where {@code 0.0} lies at the top edge of the
 *     figure and {@code 1.0} at the bottom edge.</dd>
 * </dl>
 *
 * @author Werner Randelshofer
 */
public class BoundsLocator extends AbstractLocator {

    public static final BoundsLocator CENTER = new BoundsLocator(0.5, 0.5);

    public static final BoundsLocator EAST = new BoundsLocator(1.0, 0.5);
    public static final BoundsLocator NORTH = new BoundsLocator(0.5, 0.0);
    public static final BoundsLocator NORTH_EAST = new BoundsLocator(1.0, 0.0);
    public static final BoundsLocator NORTH_WEST = new BoundsLocator(0.0, 0.0);
    public static final BoundsLocator SOUTH = new BoundsLocator(0.5, 1.0);
    public static final BoundsLocator SOUTH_EAST = new BoundsLocator(1.0, 1.0);
    public static final BoundsLocator SOUTH_WEST = new BoundsLocator(0.0, 1.0);
    public static final BoundsLocator WEST = new BoundsLocator(0.0, 0.5);

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
    public BoundsLocator() {
        this(0, 0);
    }

    /**
     * Creates a new instance.
     *
     * @param relativeX x-position relative to bounds expressed as a value
     *                  between 0 and 1.
     * @param relativeY y-position relative to bounds expressed as a value
     *                  between 0 and 1.
     */
    public BoundsLocator(double relativeX, double relativeY) {
        this.relativeX = relativeX;
        this.relativeY = relativeY;
    }

    /**
     * Creates a new instance.
     *
     * @param bounds current local bounds of a figure
     * @param p      a local coordinate on the figure
     */
    public BoundsLocator(@NonNull Bounds bounds, @NonNull Point2D p) {
        this(bounds, p.getX(), p.getY());
    }

    /**
     * Creates a new instance.
     *
     * @param bounds current local bounds of a figure
     * @param x      a local coordinate on the figure
     * @param y      a local coordinate on the figre
     */
    public BoundsLocator(@NonNull Bounds bounds, double x, double y) {
        this(Geom.clamp((x - bounds.getMinX()) / bounds.getWidth(), 0, 1),
                Geom.clamp((y - bounds.getMinY()) / bounds.getHeight(), 0, 1));
    }

    public double getRelativeX() {
        return relativeX;
    }

    public double getRelativeY() {
        return relativeY;
    }

    @NonNull
    @Override
    public Point2D locate(@NonNull Figure owner) {
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
        final BoundsLocator other = (BoundsLocator) obj;
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
