/*
 * @(#)CssPoint2D.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * Represents a point with x, y values specified as {@link CssSize}s.
 *
 * @author Werner Randelshofer
 */
public class CssPoint2D {

    public static final CssPoint2D ZERO = new CssPoint2D();

    private final @NonNull CssSize x;
    private final @NonNull CssSize y;

    public CssPoint2D(@NonNull CssSize x, @NonNull CssSize y) {
        this.x = x;
        this.y = y;
    }

    public CssPoint2D(double x, double y, @NonNull String units) {
        this(new CssSize(x, units), new CssSize(y, units));
    }

    public CssPoint2D() {
        this(CssSize.ZERO, CssSize.ZERO);
    }

    public CssPoint2D(double x, double y) {
        this(x, y, UnitConverter.DEFAULT);
    }

    public CssPoint2D(@NonNull Point2D p) {
        this(p.getX(), p.getY());
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CssPoint2D other = (CssPoint2D) obj;
        if (!Objects.equals(this.x, other.x)) {
            return false;
        }
        return Objects.equals(this.y, other.y);
    }

    public @NonNull CssSize getX() {
        return x;
    }

    public @NonNull CssSize getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.x);
        hash = 89 * hash + Objects.hashCode(this.y);
        return hash;
    }

    @Override
    public @NonNull String toString() {
        return "CssPoint2D{" +
                "" + x +
                ", " + y +
                '}';
    }

    public @NonNull Point2D getConvertedValue() {
        return new Point2D(x.getConvertedValue(), y.getConvertedValue());

    }

    public @NonNull CssPoint2D subtract(@NonNull CssPoint2D that) {
        return new CssPoint2D(x.subtract(that.x), y.subtract(that.y));
    }

    public @NonNull CssPoint2D add(@NonNull CssPoint2D that) {
        return new CssPoint2D(x.add(that.x), y.add(that.y));
    }

    /**
     * Gets a point that was given in relative coordinates to a bounds.
     * <p>
     * If the x- or y-coordinate of the point is given as a percentage,
     * then the returned point is {@code bounds.minX + p.x/100 * bounds.width},
     * {@code bounds.minY + p.y/100 * bounds.height}.
     * <p>
     * If the x- or y-coordinate of the point is given with default units,
     * then the returned point is {@code bounds.minX + p.x * bounds.width},
     * {@code bounds.minY + p.y * bounds.height}.
     * <p>
     * Otherwise the returned point is {@code bounds.minX + p.x},
     * {@code bounds.minY + p.y}.
     *
     * @param p      point in relative coordinates
     * @param bounds the bounds
     * @return point in absolute coordinates
     */
    public static Point2D getPointInBounds(CssPoint2D p, Bounds bounds) {
        final double x, y;
        final CssSize px = p.getX();
        final CssSize py = p.getY();
        switch (px.getUnits()) {
            case UnitConverter.PERCENTAGE:
                x = Math.fma(bounds.getWidth(), px.getValue() / 100.0, bounds.getMinX());
                break;
            case UnitConverter.DEFAULT:
                x = Math.fma(bounds.getWidth(), px.getValue(), bounds.getMinX());
                break;
            default:
                x = bounds.getMinX() + px.getConvertedValue();
                break;
        }
        switch (py.getUnits()) {
            case UnitConverter.PERCENTAGE:
                y = Math.fma(bounds.getHeight(), py.getValue() / 100.0, bounds.getMinY());
                break;
            case UnitConverter.DEFAULT:
                y = Math.fma(bounds.getHeight(), py.getValue(), bounds.getMinY());
                break;
            default:
                y = bounds.getMinY() + py.getConvertedValue();
                break;
        }
        return new Point2D(x, y);
    }
}
