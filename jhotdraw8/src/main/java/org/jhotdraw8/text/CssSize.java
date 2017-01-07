/* @(#)CssSize.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.util.Objects;
import org.jhotdraw8.io.DefaultUnitConverter;

/**
 * CssSize.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CssSize {

    public final static CssSize ZERO = new CssSize(0, null);
    private final String units;
    private final double value;

    public CssSize(double value, String units) {
        this.value = value;
        this.units = units;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final CssSize other = (CssSize) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        if (!Objects.equals(this.units, other.units)) {
            return false;
        }
        return true;
    }

    public double getDefaultConvertedValue() {
        return DefaultUnitConverter.getInstance().convert(this, null);
    }

    public String getUnits() {
        return units;
    }

    public double getValue() {
        return value;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 97 * hash + Objects.hashCode(this.units);
        hash = 97 * hash + (int) (Double.doubleToLongBits(this.value) ^ (Double.doubleToLongBits(this.value) >>> 32));
        return hash;
    }

    @Override
    public String toString() {
        return "CssSize{" + value + units + '}';
    }

}