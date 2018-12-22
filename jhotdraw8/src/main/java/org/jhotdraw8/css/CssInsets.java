/* @(#)CssInsets.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import java.util.Objects;
import javafx.geometry.Insets;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.UnitConverter;

/**
 * Represents a set of inside offsets specified as {@link CssSize}s.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssInsets {

    public final static CssInsets ZERO = new CssInsets();

    private final CssSize bottom;
    private final CssSize left;
    private final CssSize right;
    private final CssSize top;

    public CssInsets(CssSize top, CssSize right, CssSize bottom, CssSize left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public CssInsets(double top, double right, double bottom, double left, String units) {
        this(new CssSize(top, units), new CssSize(right, units), new CssSize(bottom, units), new CssSize(left, units));
    }

    public CssInsets() {
        this(CssSize.ZERO, CssSize.ZERO, CssSize.ZERO, CssSize.ZERO);
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
        final CssInsets other = (CssInsets) obj;
        if (!Objects.equals(this.top, other.top)) {
            return false;
        }
        if (!Objects.equals(this.right, other.right)) {
            return false;
        }
        if (!Objects.equals(this.bottom, other.bottom)) {
            return false;
        }
        if (!Objects.equals(this.left, other.left)) {
            return false;
        }
        return true;
    }

    public CssSize getBottom() {
        return bottom;
    }

    /**
     * Converts values using the specified width and heights for converting
     * percentages in the insets.
     *
     * @param width the width for computing percentages for left and right
     * insets
     * @param height the height for computing percentages for top and bottom
     * insets
     * @return the converted value
     */
    @Nonnull
    public Insets getConvertedValue(double width, double height) {
        final UnitConverter heightConverter = new DefaultUnitConverter(72.0, height);
        final UnitConverter widthConverter = new DefaultUnitConverter(72.0, width);
        return new Insets(heightConverter.convert(top, UnitConverter.DEFAULT), widthConverter.convert(right, UnitConverter.DEFAULT),
                heightConverter.convert(bottom, UnitConverter.DEFAULT), widthConverter.convert(left, UnitConverter.DEFAULT));
    }

    @Nonnull
    public Insets getConvertedValue() {
        return new Insets(top.getConvertedValue(), right.getConvertedValue(),
                bottom.getConvertedValue(), left.getConvertedValue());
    }

    public CssSize getLeft() {
        return left;
    }

    public CssSize getRight() {
        return right;
    }

    public CssSize getTop() {
        return top;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.top);
        hash = 89 * hash + Objects.hashCode(this.right);
        hash = 89 * hash + Objects.hashCode(this.bottom);
        hash = 89 * hash + Objects.hashCode(this.left);
        return hash;
    }

    @Override
    public String toString() {
        return "CssInsets{" +
                "" + bottom +
                ", " + left +
                ", " + right +
                ", " + top +
                '}';
    }
}
