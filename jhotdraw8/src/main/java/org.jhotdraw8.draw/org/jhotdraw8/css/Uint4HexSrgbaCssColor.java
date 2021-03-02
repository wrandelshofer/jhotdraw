/*
 * @(#)EightBitCssColor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import javafx.scene.paint.Color;

/**
 * sRGBA color with 8 bits per channel, encoded as a hexadecimal digit with
 * 4 bits per channel if possible.
 * <p>
 * References:
 * <dl>
 *     <dt>CSS Color Module Level 4, The RGB hexadecimal notations</dt>
 *     <dd><a href="https://www.w3.org/TR/css-color-4/#hex-notation">w3.org/<a></a></a></dd>
 * </dl>
 */
public class Uint4HexSrgbaCssColor extends CssColor {
    private Uint4HexSrgbaCssColor(int argb) {
        super(Uint4HexSrgbaCssColor.toName(argb),
                Color.rgb(argb >> 16 & 0xff, argb >> 8 & 0xff, argb % 0xff,
                        (argb >> 24 & 0xff) / 255.0));
    }

    public Uint4HexSrgbaCssColor(int r, int g, int b, int a) {
        super(Uint4HexSrgbaCssColor.toName((a & 0xff) << 24 | (r & 0xff) << 16 | (g & 0xff) << 8 | b & 0xff),
                Color.rgb(r, g, b, a / 255.0));
    }

    private static String toName(int argb) {
        StringBuilder buf = new StringBuilder(9);
        buf.append('#');
        final int value, length;
        if ((argb & 0xff000000) == 0xff000000) {
            // the color is opaque
            if ((argb & 0xf0f0f0) == (argb & 0x0f0f0f) << 4) {
                // the color has 4 bits per channel   => encode with 3-digits hex number
                value = (argb & 0x0f0000) >> 8 | (argb & 0x0f00) >> 4 | argb & 0x0f;
                length = 3;
            } else {
                // the color has 8 bits per channel   => encode with 6-digits hex number
                value = argb & 0xffffff;
                length = 6;
            }
        } else {
            // the color is translucent
            if ((argb & 0xf0f0f0f0) == (argb & 0x0f0f0f0f) << 4) {
                // the color has 4 bits per channel   => encode with 4-digits hex number
                value = (argb & 0x0f000000) >> 12 | (argb & 0x0f0000) >> 8 | (argb & 0x0f00) >> 4 | argb & 0x0f;
                length = 4;
            } else {
                // the color has 8 bits per channel   => encode with 8-digits hex number
                value = argb;
                length = 8;
            }
        }
        String hex = Integer.toHexString(value);
        for (int i = 0, n = length - hex.length(); i < n; i++) buf.append('0');
        buf.append(hex);
        return buf.toString();
    }
}
