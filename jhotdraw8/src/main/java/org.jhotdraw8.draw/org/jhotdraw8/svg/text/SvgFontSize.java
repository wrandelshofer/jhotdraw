/*
 * @(#)SvgFontSize.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.text;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.figure.Figure;

/**
 * SVG font size.
 * <p>
 *     References:
 * <br><a href="https://www.w3.org/TR/css-fonts-3/#font-size-prop">CSS-Fonts-3</a>
 *
 */
public class SvgFontSize {
    public enum SizeKeyword {
        XX_SMALL, X_SMALL, SMALL, MEDIUM, LARGE, X_LARGE, XX_LARGE,
        SMALLER, LARGER
    }

    private final @Nullable SizeKeyword keyword;
    private final @Nullable CssSize length;

    public SvgFontSize(@Nullable SizeKeyword keyword, @Nullable CssSize length) {
        this.keyword = keyword;
        this.length = length;
    }

    public @Nullable SizeKeyword getKeyword() {
        return keyword;
    }

    public @Nullable CssSize getLength() {
        return length;
    }

    public double getConvertedValue(Figure figure, UnitConverter converter) {
        if (keyword!=null) {
            double value=12;
            switch (keyword){
            case XX_SMALL:
                return value*3d/5d;
            case X_SMALL:
                return value*3d/4d;
            case SMALL:
            case SMALLER:// FIXME should use size of parent element
                return value*8d/9d;
            case MEDIUM:
            default:
                return value;
            case LARGE:
            case LARGER:// FIXME should use size of parent element
                return value*6d/5d;
            case X_LARGE:
                return value*3d/2d;
            case XX_LARGE:
                return value*2d/1d;
            }
        } else if (length!=null) {
            return length.getConvertedValue();
        } else {
            return 12;
        }
    }
}
