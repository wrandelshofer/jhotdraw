/*
 * @(#)RgbCssColor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;

public class FloatRgbaCssColor extends CssColor {
    public FloatRgbaCssColor(@NonNull Color color) {
        super(toName((float) color.getRed(), (float) color.getGreen(), (float) color.getBlue(), (float) color.getOpacity()), color);
    }

    public FloatRgbaCssColor(float red, float green, float blue, float opacity) {
        super(toName(red, green, blue, opacity), Color.color(red, green, blue, opacity));
    }

    private static String toName(float red, float green, float blue, float opacity) {
        StringBuilder buf = new StringBuilder(20);
        if (opacity == 1.0) {
            buf.append("rgb(");
            buf.append(red * 100);
            buf.append("%,");
            buf.append(green * 100);
            buf.append("%,");
            buf.append(blue * 100);
            buf.append("%");
        } else {
            buf.append("rgba(");
            buf.append(red * 100);
            buf.append("%,");
            buf.append(green * 100);
            buf.append("%,");
            buf.append(blue * 100);
            buf.append("%,");
            buf.append(opacity * 100);
            buf.append("%");
        }
        buf.append(')');
        return buf.toString();
    }
}
