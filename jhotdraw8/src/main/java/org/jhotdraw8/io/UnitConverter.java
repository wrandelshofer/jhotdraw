/* @(#)UnitConverter.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import java.util.Objects;
import javax.annotation.Nullable;
import javax.annotation.Nonnull;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;

/**
 * UnitConverter.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface UnitConverter {

    String CENTIMETERS = "cm";
    String EM = "em";
    String EX = "ex";
    String INCH = "in";
    String MILLIMETERS = "mm";
    String QUARTER_MILLIMETERS = "Q";
    String PERCENTAGE = "%";
    String PICAS = "pc";
    String PIXELS = "px";
    String POINTS = "pt";

    /**
     * Gets the resolution in dots per inch.
     *
     * @return dpi
     */
    default double getDpi() {
        return 96.0;
    }

    /**
     * Gets the factor for percentage values.
     *
     * @return percentageFactor, for example 100.
     */
    default double getPercentageFactor() {
        return 100.0;
    }

    default double getFactor(@Nullable String unit) {
        double factor = 1.0;
        if (unit != null) {
            switch (unit) {
                case PERCENTAGE:
                    factor = getPercentageFactor();
                    break;
                case PIXELS:
                    factor = 1.0;
                    break;
                case CENTIMETERS:
                    factor = 2.54 / getDpi();
                    break;
                case MILLIMETERS:
                    factor = 25.4 / getDpi();
                    break;
                case QUARTER_MILLIMETERS:
                    factor = 25.4 * 0.25 / getDpi();
                    break;
                case INCH:
                    factor = 1.0 / getDpi();
                    break;
                case POINTS:
                    factor = 72 / getDpi();
                    break;
                case PICAS:
                    factor = 72 * 12.0 / getDpi();
                    break;
                case EM:
                    factor = 1.0 / getFontSize();
                    break;
                case EX:
                    factor = 1.0 / getFontXHeight();
                    break;
            }
        }
        return factor;
    }

    /**
     * Gets the font size;
     *
     * @return em
     */
    default double getFontSize() {
        return 12;
    }

    /**
     * Gets the x-height of the font size.
     *
     * @return ex
     */
    default double getFontXHeight() {
        return 8;
    }

    /**
     * Converts the specified value from input unit to output unit.
     *
     * @param value a value
     * @param inputUnit the units of the value
     * @param outputUnit the desired output unit
     * @return converted value
     */
    default double convert(double value, @Nullable String inputUnit, @Nullable String outputUnit) {
        if (value == 0.0 || Objects.equals(inputUnit, outputUnit)) {
            return value;
        }

        return value * getFactor(outputUnit) / getFactor(inputUnit);
    }
    default CssSize convertSize(double value, @Nullable String inputUnit, @Nullable String outputUnit) {
        return new CssSize(convert(value,inputUnit,outputUnit),outputUnit);
    }

    /**
     * Converts the specified value from input unit to output unit.
     *
     * @param value a value
     * @param outputUnit the desired output unit
     * @return converted value
     */
    default double convert(@Nonnull CssSize value, @Nullable String outputUnit) {
        return convert(value.getValue(), value.getUnits(), outputUnit);
    }
    default CssSize convertSize(@Nonnull CssSize value, @Nullable String outputUnit) {
        return new CssSize(convert(value.getValue(), value.getUnits(), outputUnit),outputUnit);
    }
    default CssPoint2D convertPoint2D(CssPoint2D cssPoint2D, String units) {
        return new CssPoint2D(convertSize(cssPoint2D.getX(),units),
                convertSize(cssPoint2D.getY(),units));
    }

}
