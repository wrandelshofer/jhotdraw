/* @(#)CssSize.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.UnitConverter;

import java.util.Objects;

/**
 * Represents a size specified in a particular unit.
 * <p>
 * Unlike {@code javafx.css.Size} this class supports an open ended
 * set of units.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssSize {

    @Nullable
    public final static CssSize ZERO = new CssSize(0);
    public static final CssSize ONE = new CssSize(1);
    @Nonnull
    private final String units;
    private final double value;

    public CssSize(double value) {
        this(value, UnitConverter.DEFAULT);
    }

    public CssSize(double value, @Nullable String units) {
        this.value = value;
        this.units = units == null ? UnitConverter.DEFAULT : units;
    }

    public static CssSize max(CssSize a, CssSize b) {
        return (a.getConvertedValue() >= b.getConvertedValue()) ? a : b;
    }

    public static CssSize min(CssSize a, CssSize b) {
        return (a.getConvertedValue() <= b.getConvertedValue()) ? a : b;
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
        final CssSize other = (CssSize) obj;
        if (Double.doubleToLongBits(this.value) != Double.doubleToLongBits(other.value)) {
            return false;
        }
        if (!Objects.equals(this.units, other.units)) {
            return false;
        }
        return true;
    }

    public double getConvertedValue() {
        return DefaultUnitConverter.getInstance().convert(this, UnitConverter.DEFAULT);
    }

    public double getConvertedValue(UnitConverter converter) {
        return converter.convert(this, UnitConverter.DEFAULT);
    }

    @Nonnull
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

    @Nonnull
    @Override
    public String toString() {
        return "CssSize{" + value + "\"" + units + "\"" + '}';
    }

    public CssSize subtract(CssSize that) {
        return new CssSize(this.value - DefaultUnitConverter.getInstance().convert(that, this.units), this.units);
    }

    public CssSize add(CssSize that) {
        return new CssSize(this.value + DefaultUnitConverter.getInstance().convert(that, this.units), this.units);
    }

    public CssSize abs() {
        return value >= 0 ? this : new CssSize(Math.abs(value), units);
    }

    public CssSize multiply(double factor) {
        return new CssSize(value * factor, units);
    }

    public CssSize divide(double divisor) {
        return new CssSize(value / divisor, units);
    }
}
