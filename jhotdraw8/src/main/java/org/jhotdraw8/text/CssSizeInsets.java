/* @(#)CssSizeInsets.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.text;

import java.util.Objects;
import javafx.geometry.Insets;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.UnitConverter;

/**
 * CssSizeInsets.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSizeInsets {

    public final static CssSizeInsets ZERO = new CssSizeInsets();

    private final CssSize bottom;
    private final CssSize left;
    private final CssSize right;
    private final CssSize top;

    public CssSizeInsets(CssSize top, CssSize right, CssSize bottom, CssSize left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public CssSizeInsets(double top, double right, double bottom, double left, String units) {
        this(new CssSize(top, units), new CssSize(right, units), new CssSize(bottom, units), new CssSize(left, units));
    }

    public CssSizeInsets() {
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
        final CssSizeInsets other = (CssSizeInsets) obj;
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
    @NonNull
    public Insets getConvertedValue(double width, double height) {
        final UnitConverter heightConverter = new DefaultUnitConverter(72.0, height);
        final UnitConverter widthConverter = new DefaultUnitConverter(72.0, width);
        return new Insets(heightConverter.convert(top, null), widthConverter.convert(right, null),
                heightConverter.convert(bottom, null), widthConverter.convert(left, null));
    }

    @NonNull
    public Insets getDefaultConvertedValue() {
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

}
