/*
 * @(#)CssStop.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;

import java.util.Objects;

/**
 * CssStop.
 *
 * @author Werner Randelshofer
 */
public class CssStop {

    final Double offset;
    final CssColor color;

    public CssStop(Double offset, CssColor color) {
        this.offset = offset;
        this.color = color;
    }

    public Double getOffset() {
        return offset;
    }

    public CssColor getColor() {
        return color;
    }

    @Override
    public int hashCode() {
        int hash = 5;
        return hash;
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
        final CssStop other = (CssStop) obj;
        if (!Objects.equals(this.offset, other.offset)) {
            return false;
        }
        return Objects.equals(this.color, other.color);
    }

    @NonNull
    @Override
    public String toString() {
        return "CssStop{" + "offset=" + offset + ", " + color + '}';
    }

}
