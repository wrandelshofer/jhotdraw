/*
 * @(#)CssSize.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

import java.util.Objects;

/**
 * Represents a size specified in a particular unit.
 * <p>
 * A CssSize can be used to hold the value of a CSS {@code number-token},
 * {@code percentage-token} or {@code dimension-token}.
 * <p>
 * Unlike {@link javafx.css.Size} this class supports an open ended
 * set of units.
 * <p>
 * References:
 * <dl>
 * <dt>CSS Syntax Module Level 3, Chapter 4. Tokenization</dt>
 * <dd><a href="https://www.w3.org/TR/2019/CR-css-syntax-3-20190716/#tokenization">w3.org</a></dd>
 * </dl>
 *
 * @author Werner Randelshofer
 */
public class CssSize {

    public static final @Nullable CssSize ZERO = new CssSize(0);
    public static final CssSize ONE = new CssSize(1);
    private final @NonNull String units;
    private final double value;

    public CssSize(double value) {
        this(value, UnitConverter.DEFAULT);
    }

    public CssSize(double value, @Nullable String units) {
        this.value = value;
        this.units = units == null ? UnitConverter.DEFAULT : units;
    }

    public static @NonNull CssSize max(@NonNull CssSize a, @NonNull CssSize b) {
        return (a.getConvertedValue() >= b.getConvertedValue()) ? a : b;
    }

    public static @NonNull CssSize min(@NonNull CssSize a, @NonNull CssSize b) {
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

    public @NonNull String getUnits() {
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
    public @NonNull String toString() {
        return "CssSize{" + value + "\"" + units + "\"" + '}';
    }

    public @NonNull CssSize subtract(@NonNull CssSize that) {
        return subtract(that, DefaultUnitConverter.getInstance());
    }

    public @NonNull CssSize add(@NonNull CssSize that) {
        return add(that, DefaultUnitConverter.getInstance());
    }

    public @NonNull CssSize subtract(@NonNull CssSize that, @NonNull UnitConverter unitConverter) {
        return new CssSize(this.value - unitConverter.convert(that, this.units), this.units);
    }

    public @NonNull CssSize add(@NonNull CssSize that, @NonNull UnitConverter unitConverter) {
        return new CssSize(this.value + unitConverter.convert(that, this.units), this.units);
    }

    public @NonNull CssSize abs() {
        return value >= 0 ? this : new CssSize(Math.abs(value), units);
    }

    public @NonNull CssSize multiply(double factor) {
        return new CssSize(value * factor, units);
    }

    public @NonNull CssSize divide(double divisor) {
        return new CssSize(value / divisor, units);
    }
}
