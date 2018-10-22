/* @(#)CssSizeInsets.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.util.Objects;
import javafx.geometry.Insets;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
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

    private final CssDimension bottom;
    private final CssDimension left;
    private final CssDimension right;
    private final CssDimension top;

    public CssSizeInsets(CssDimension top, CssDimension right, CssDimension bottom, CssDimension left) {
        this.top = top;
        this.right = right;
        this.bottom = bottom;
        this.left = left;
    }

    public CssSizeInsets(double top, double right, double bottom, double left, String units) {
        this(new CssDimension(top, units), new CssDimension(right, units), new CssDimension(bottom, units), new CssDimension(left, units));
    }

    public CssSizeInsets() {
        this(CssDimension.ZERO, CssDimension.ZERO, CssDimension.ZERO, CssDimension.ZERO);
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

    public CssDimension getBottom() {
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
        return new Insets(heightConverter.convert(top, null), widthConverter.convert(right, null),
                heightConverter.convert(bottom, null), widthConverter.convert(left, null));
    }

    @Nonnull
    public Insets getDefaultConvertedValue() {
        return new Insets(top.getConvertedValue(), right.getConvertedValue(),
                bottom.getConvertedValue(), left.getConvertedValue());
    }

    public CssDimension getLeft() {
        return left;
    }

    public CssDimension getRight() {
        return right;
    }

    public CssDimension getTop() {
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
