/*
 * @(#)CssSize.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * Represents a size specified in a particular unit.
 * <p>
 * Unlike {@code javafx.css.Size} this class supports an open ended
 * set of units.
 *
 * @author Werner Randelshofer
 */
public class CssSize {

    @Nullable
    public final static CssSize ZERO = new CssSize(0);
    public static final CssSize ONE = new CssSize(1);
    @NonNull
    private final String units;
    private final double value;

    public CssSize(double value) {
        this(value, UnitConverter.DEFAULT);
    }

    public CssSize(double value, @Nullable String units) {
        this.value = value;
        this.units = units == null ? UnitConverter.DEFAULT : units;
    }

    @NonNull
    public static CssSize max(@NonNull CssSize a, @NonNull CssSize b) {
        return (a.getConvertedValue() >= b.getConvertedValue()) ? a : b;
    }

    @NonNull
    public static CssSize min(@NonNull CssSize a, @NonNull CssSize b) {
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
        return Objects.equals(this.units, other.units);
    }

    public double getConvertedValue() {
        return DefaultUnitConverter.getInstance().convert(this, UnitConverter.DEFAULT);
    }

    public double getConvertedValue(@NonNull UnitConverter converter) {
        return converter.convert(this, UnitConverter.DEFAULT);
    }

    public double getConvertedValue(@NonNull UnitConverter converter, @NonNull String units) {
        return converter.convert(this, units);
    }

    @NonNull
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

    @NonNull
    @Override
    public String toString() {
        return "CssSize{" + value + "\"" + units + "\"" + '}';
    }

    @NonNull
    public CssSize subtract(@NonNull CssSize that) {
        return new CssSize(this.value - DefaultUnitConverter.getInstance().convert(that, this.units), this.units);
    }

    @NonNull
    public CssSize add(@NonNull CssSize that) {
        return new CssSize(this.value + DefaultUnitConverter.getInstance().convert(that, this.units), this.units);
    }

    @NonNull
    public CssSize abs() {
        return value >= 0 ? this : new CssSize(Math.abs(value), units);
    }

    @NonNull
    public CssSize multiply(double factor) {
        return new CssSize(value * factor, units);
    }

    @NonNull
    public CssSize divide(double divisor) {
        return new CssSize(value / divisor, units);
    }
}
