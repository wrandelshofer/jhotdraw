/*
 * @(#)CssDimension2D.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * Represents a dimension with width, height values specified as {@link CssSize}s.
 *
 * @author Werner Randelshofer
 */
public class CssDimension2D {

    public static final CssDimension2D ZERO = new CssDimension2D();

    private final @NonNull CssSize width;
    private final @NonNull CssSize height;

    public CssDimension2D(@NonNull CssSize width, @NonNull CssSize height) {
        this.width = width;
        this.height = height;
    }

    public CssDimension2D(double width, double height, @NonNull String units) {
        this(new CssSize(width, units), new CssSize(height, units));
    }

    public CssDimension2D() {
        this(CssSize.ZERO, CssSize.ZERO);
    }

    public CssDimension2D(double width, double height) {
        this(width, height, UnitConverter.DEFAULT);
    }

    public CssDimension2D(@NonNull Point2D p) {
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
        final CssDimension2D other = (CssDimension2D) obj;
        if (!Objects.equals(this.width, other.width)) {
            return false;
        }
        return Objects.equals(this.height, other.height);
    }

    public @NonNull CssSize getWidth() {
        return width;
    }

    public @NonNull CssSize getHeight() {
        return height;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.width);
        hash = 89 * hash + Objects.hashCode(this.height);
        return hash;
    }

    @Override
    public @NonNull String toString() {
        return "CssPoint2D{" +
                "" + width +
                ", " + height +
                '}';
    }

    public @NonNull Point2D getConvertedValue() {
        return new Point2D(width.getConvertedValue(), height.getConvertedValue());

    }

    public @NonNull CssDimension2D subtract(@NonNull CssDimension2D that) {
        return new CssDimension2D(width.subtract(that.width), height.subtract(that.height));
    }

    public @NonNull CssDimension2D add(@NonNull CssDimension2D that) {
        return new CssDimension2D(width.add(that.width), height.add(that.height));
    }
}
