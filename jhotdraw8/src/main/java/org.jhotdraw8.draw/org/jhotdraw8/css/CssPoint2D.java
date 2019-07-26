/*
 * @(#)CssPoint2D.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * Represents a point with x, y values specified as {@link CssSize}s.
 *
 * @author Werner Randelshofer
 */
public class CssPoint2D {

    public final static CssPoint2D ZERO = new CssPoint2D();

    private final CssSize x;
    private final CssSize y;

    public CssPoint2D(CssSize x, CssSize y) {
        this.x = x;
        this.y = y;
    }

    public CssPoint2D(double x, double y, String units) {
        this(new CssSize(x, units), new CssSize(y, units));
    }

    public CssPoint2D() {
        this(CssSize.ZERO, CssSize.ZERO);
    }

    public CssPoint2D(double x, double y) {
        this(x, y, null);
    }

    public CssPoint2D(Point2D p) {
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
        if (!Objects.equals(this.y, other.y)) {
            return false;
        }
        return true;
    }

    public CssSize getX() {
        return x;
    }

    public CssSize getY() {
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
    public String toString() {
        return "CssPoint2D{" +
                "" + x +
                ", " + y +
                '}';
    }

    public Point2D getConvertedValue() {
        return new Point2D(x.getConvertedValue(), y.getConvertedValue());

    }

    public CssPoint2D subtract(CssPoint2D that) {
        return new CssPoint2D(x.subtract(that.x), y.subtract(that.y));
    }

    public CssPoint2D add(CssPoint2D that) {
        return new CssPoint2D(x.add(that.x), y.add(that.y));
    }
}
