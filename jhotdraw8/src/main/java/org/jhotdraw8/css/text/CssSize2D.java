/* @(#)CssSize2D.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import java.util.Objects;

import javafx.geometry.Point2D;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * CssSize2D.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSize2D {

    private final CssSize x;
    private final CssSize y;

    public CssSize2D(double x, double y, String units) {
        this(new CssSize(x, units), new CssSize(y, units));
    }

    public CssSize2D(CssSize x, CssSize y) {
        this.x = x;
        this.y = y;
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
        final CssSize2D other = (CssSize2D) obj;
        if (!Objects.equals(this.x, other.x)) {
            return false;
        }
        if (!Objects.equals(this.y, other.y)) {
            return false;
        }
        return true;
    }

    @Nonnull
    public Point2D getConvertedValue() {
        return new Point2D(x.getConvertedValue(), y.getConvertedValue());
    }

    public CssSize getX() {
        return x;
    }

    public CssSize getY() {
        return y;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 11 * hash + Objects.hashCode(this.x);
        hash = 11 * hash + Objects.hashCode(this.y);
        return hash;
    }

    @Nonnull
    @Override
    public String toString() {
        return "CssSize2D{" + x.getValue() + (x.getUnits()==null? "" : x.getUnits())
                + ", " + y.getValue() + "" +  (y.getUnits()==null? "" : y.getUnits()) + '}';
    }

}
