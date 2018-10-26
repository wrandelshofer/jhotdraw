/* @(#)CssDimensionInsets.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import javafx.geometry.Insets;
import javafx.geometry.Rectangle2D;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.UnitConverter;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import java.util.Objects;

/**
 * CssDimensionInsets.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssDimensionRectangle2D {

    public final static CssDimensionRectangle2D ZERO = new CssDimensionRectangle2D();

    private final CssDimension width;
    private final CssDimension height;
    private final CssDimension y;
    private final CssDimension x;

    public CssDimensionRectangle2D(CssDimension x, CssDimension y, CssDimension width, CssDimension height) {
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public CssDimensionRectangle2D(double x, double y, double width, double height, String units) {
        this(new CssDimension(x, units), new CssDimension(y, units), new CssDimension(width, units), new CssDimension(height, units));
    }

    public CssDimensionRectangle2D() {
        this(CssDimension.ZERO, CssDimension.ZERO, CssDimension.ZERO, CssDimension.ZERO);
    }

    public CssDimensionRectangle2D(double x, double y, double width, double height) {
        this(x,y,width,height,null);
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
        final CssDimensionRectangle2D other = (CssDimensionRectangle2D) obj;
        if (!Objects.equals(this.x, other.x)) {
            return false;
        }
        if (!Objects.equals(this.y, other.y)) {
            return false;
        }
        if (!Objects.equals(this.width, other.width)) {
            return false;
        }
        if (!Objects.equals(this.height, other.height)) {
            return false;
        }
        return true;
    }

    public CssDimension getWidth() {
        return width;
    }

    public CssDimension getHeight() {
        return height;
    }

    public CssDimension getMinY() {
        return y;
    }

    public CssDimension getMinX() {
        return x;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 89 * hash + Objects.hashCode(this.x);
        hash = 89 * hash + Objects.hashCode(this.y);
        hash = 89 * hash + Objects.hashCode(this.width);
        hash = 89 * hash + Objects.hashCode(this.height);
        return hash;
    }

    @Override
    public String toString() {
        return "CssDimensionRectangle2D{" +
                "" + x +
                ", " + y +
                ", " + width +
                ", " + height +
                '}';
    }

    public Rectangle2D getConvertedValue() {
        return new Rectangle2D(x.getConvertedValue(),y.getConvertedValue(),width.getConvertedValue(),height.getConvertedValue());

    }
}
