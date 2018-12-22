/* @(#)UnitConverter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.io;

import java.util.Objects;

import org.jhotdraw8.annotation.Nonnull;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssSize;

import static java.lang.Math.max;
import static java.lang.Math.min;

/**
 * UnitConverter.
 * <p>
 * References:
 * <ul>
 * <li><a href="https://www.w3.org/TR/css3-values/#absolute-length">
 * Absolute lengths: the cm, mm, Q, in, pt, pc, px units</a>></li>
 * <li><a href="https://www.w3.org/TR/css3-values/#viewport-relative-lengths">
 * Viewport-percentage lengths: the vw, vh, vmin, vmax units</a>></li>
 * <li><a href="https://www.w3.org/TR/css3-values/#font-relative-lengths">
 * Font-relative lengths: the em, ex, ch, rem units</a>></li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface UnitConverter {

    String DEFAULT = "";
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
    String VIEWPORT_WIDTH_PERCENTAGE = "vw";
    String VIEWPORT_HEIGHT_PERCENTAGE = "vh";
    String VIEWPORT_MIN_PERCENTAGE = "vmin";
    String VIEWPORT_MAX_PERCENTAGE = "vmax";

    /**
     * Gets the resolution in dots per inch.
     *
     * @return dpi, default value: 96.0.
     */
    default double getDpi() {
        return 96.0;
    }

    /**
     * Gets the viewport width.
     *
     * @return viewport width, default value: 1024.0.
     */
    default double getViewportWidth() {
        return 1024.0;
    }

    /**
     * Gets the viewport height.
     *
     * @return viewport height, default value: 768.0.
     */
    default double getViewportHeight() {
        return 768.0;
    }

    /**
     * Gets the factor for percentage values.
     *
     * @return percentageFactor, for example 100.
     */
    default double getPercentageFactor() {
        return 100.0;
    }

    default double getFactor(@Nonnull String unit) {
        final double factor;
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
            case VIEWPORT_HEIGHT_PERCENTAGE:
                factor = 100.0 / getViewportHeight();
                break;
            case VIEWPORT_WIDTH_PERCENTAGE:
                factor = 100.0 / getViewportWidth();
                break;
            case VIEWPORT_MIN_PERCENTAGE:
                factor = 100.0 / min(getViewportHeight(), getViewportWidth());
                break;
            case VIEWPORT_MAX_PERCENTAGE:
                factor = 100.0 / max(getViewportHeight(), getViewportWidth());
                break;
            case DEFAULT:
            default:
                factor = 1.0;
                break;
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
     * @param value      a value
     * @param inputUnit  the units of the value
     * @param outputUnit the desired output unit
     * @return converted value
     */
    default double convert(double value, @Nonnull String inputUnit, @Nonnull String outputUnit) {
        if (value == 0.0 || Objects.equals(inputUnit, outputUnit)) {
            return value;
        }

        return value * getFactor(outputUnit) / getFactor(inputUnit);
    }

    default CssSize convertSize(double value, @Nonnull String inputUnit, @Nonnull String outputUnit) {
        return new CssSize(convert(value, inputUnit, outputUnit), outputUnit);
    }

    /**
     * Converts the specified value from input unit to output unit.
     *
     * @param value      a value
     * @param outputUnit the desired output unit
     * @return converted value
     */
    default double convert(@Nonnull CssSize value, @Nonnull String outputUnit) {
        return convert(value.getValue(), value.getUnits(), outputUnit);
    }

    default CssSize convertSize(@Nonnull CssSize value, @Nonnull String outputUnit) {
        return new CssSize(convert(value.getValue(), value.getUnits(), outputUnit), outputUnit);
    }

    default CssPoint2D convertPoint2D(CssPoint2D cssPoint2D, @Nonnull String units) {
        return new CssPoint2D(convertSize(cssPoint2D.getX(), units),
                convertSize(cssPoint2D.getY(), units));
    }

}
