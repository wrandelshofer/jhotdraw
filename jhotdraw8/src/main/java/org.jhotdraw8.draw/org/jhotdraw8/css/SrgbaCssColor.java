/*
 * @(#)RgbCssColor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.text.NumberConverter;

import static org.jhotdraw8.geom.Geom.clamp;

/**
 * sRGBA color encoded with numbers and/or percentages.
 * <p>
 * <pre>ISO EBNF 14977:
 *
 * sRGBA = "rgb(" red , [ "," ] , green , [ "," ] , blue ,
 *              [ [ ("/" , ",") ] , alpha ] ,
 *              ")"
 *       ;
 * red   = number | percentage ;
 * green = number | percentage ;
 * blue  = number | percentage ;
 * alpha = number | percentage ;
 * percentage = number , "%" ;
 *
 * number = integer | decimal ;
 * integer = digit , {digit} ;
 * decimal = {digit} , '.' , digit , {digit} ;
 *
 * digit = '0' | '1' | '2' | '3' | '4' | '5' | '6' | '7' | '8' | '9' ;
 * </pre>
 * References:
 * <dl>
 *     <dt>CSS-4 RGB functions</dt>
 *     <dd><a href="https://www.w3.org/TR/css-color-4/#rgb-functions">w3.org</a></dd>
 * </dl>
 */
public class SrgbaCssColor extends CssColor {
    private static final NumberConverter num = new NumberConverter();
    public static final SrgbaCssColor BLACK = new SrgbaCssColor(CssSize.ZERO, CssSize.ZERO, CssSize.ZERO, CssSize.ONE);

    private final @NonNull CssSize red, green, blue, opacity;

    public SrgbaCssColor(@NonNull Color color) {
        super(toName(
                new CssSize(color.getRed() * 100, UnitConverter.PERCENTAGE),
                new CssSize(color.getGreen() * 100, UnitConverter.PERCENTAGE),
                new CssSize(color.getBlue() * 100, UnitConverter.PERCENTAGE),
                new CssSize(color.getOpacity())), color);
        this.red = new CssSize(color.getRed() * 100, UnitConverter.PERCENTAGE);
        this.green = new CssSize(color.getGreen() * 100, UnitConverter.PERCENTAGE);
        this.blue = new CssSize(color.getBlue() * 100, UnitConverter.PERCENTAGE);
        this.opacity = new CssSize(color.getOpacity());
    }

    public SrgbaCssColor(@NonNull CssSize red, @NonNull CssSize green, @NonNull CssSize blue, @NonNull CssSize opacity) {
        super(toName(red, green, blue, opacity),
/*
                Color.rgb(
                        (int) Math.round(clamp(UnitConverter.PERCENTAGE.equals(red.getUnits()) ? red.getValue() * 2.55 : red.getValue(), 0, 255)),
                        (int)Math.round(clamp(UnitConverter.PERCENTAGE.equals(green.getUnits()) ? green.getValue()  * 2.55 : green.getValue() , 0, 255)),
                        (int) Math.round(clamp(UnitConverter.PERCENTAGE.equals(blue.getUnits()) ? blue.getValue()  * 2.55 : blue.getValue() , 0, 255)),
                        clamp(UnitConverter.PERCENTAGE.equals(opacity.getUnits()) ? opacity.getValue() / 100 : opacity.getValue(), 0, 1)
                )
*/

                // The following code would be nicer, but JavaFX color internally
                // does not convert double numbers to rgb numbers as we expect it.

                (UnitConverter.PERCENTAGE.equals(red.getUnits())
                        || UnitConverter.PERCENTAGE.equals(green.getUnits())
                        | UnitConverter.PERCENTAGE.equals(blue.getUnits()))
                        ? Color.color(
                        clamp(UnitConverter.PERCENTAGE.equals(red.getUnits()) ? red.getValue() / 100 : red.getValue() / 255, 0, 1),
                        clamp(UnitConverter.PERCENTAGE.equals(green.getUnits()) ? green.getValue() / 100 : green.getValue() / 255, 0, 1),
                        clamp(UnitConverter.PERCENTAGE.equals(blue.getUnits()) ? blue.getValue() / 100 : blue.getValue() / 255, 0, 1),
                        clamp(UnitConverter.PERCENTAGE.equals(opacity.getUnits()) ? opacity.getValue() / 100 : opacity.getValue(), 0, 1)
                )
                        : Color.rgb(
                        (int) Math.round(clamp(UnitConverter.PERCENTAGE.equals(red.getUnits()) ? red.getValue() * 2.55 : red.getValue(), 0, 255)),
                        (int) Math.round(clamp(UnitConverter.PERCENTAGE.equals(green.getUnits()) ? green.getValue() * 2.55 : green.getValue(), 0, 255)),
                        (int) Math.round(clamp(UnitConverter.PERCENTAGE.equals(blue.getUnits()) ? blue.getValue() * 2.55 : blue.getValue(), 0, 255)),
                        clamp(UnitConverter.PERCENTAGE.equals(opacity.getUnits()) ? opacity.getValue() / 100 : opacity.getValue(), 0, 1)
                )

        );
        this.red = red;
        this.green = green;
        this.blue = blue;
        this.opacity = opacity;
    }

    private static String toName(@NonNull CssSize red, @NonNull CssSize green, @NonNull CssSize blue, @NonNull CssSize opacity) {
        StringBuilder buf = new StringBuilder(20);
        if (UnitConverter.PERCENTAGE.equals(opacity.getUnits()) && opacity.getValue() == 100.0
                || opacity.getValue() == 1) {
            buf.append("rgb(");
            buf.append(num.toString(red.getValue()));
            buf.append(red.getUnits());
            buf.append(",");
            buf.append(num.toString(green.getValue()));
            buf.append(green.getUnits());
            buf.append(",");
            buf.append(num.toString(blue.getValue()));
            buf.append(blue.getUnits());
        } else {
            buf.append("rgba(");
            buf.append(num.toString(red.getValue()));
            buf.append(red.getUnits());
            buf.append(",");
            buf.append(num.toString(green.getValue()));
            buf.append(green.getUnits());
            buf.append(",");
            buf.append(num.toString(blue.getValue()));
            buf.append(blue.getUnits());
            buf.append(",");
            buf.append(num.toString(opacity.getValue()));
            buf.append(opacity.getUnits());
        }
        buf.append(')');
        return buf.toString();
    }
}
