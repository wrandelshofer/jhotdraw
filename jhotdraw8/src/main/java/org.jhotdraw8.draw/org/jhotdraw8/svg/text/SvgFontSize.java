/*
 * @(#)SvgFontSize.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.text;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;

/**
 * SVG font size.
 * <p>
 * <a href="https://www.w3.org/TR/css-fonts-3/#font-size-prop">link</a>
 */
public class SvgFontSize {
    public enum SizeKeyword {
        XX_SMALL, X_SMALL, SMALL, MEDIUM, LARGE, X_LARGE, XX_LARGE,
        SMALLER, LARGER
    }

    @Nullable
    private final SizeKeyword keyword;
    @Nullable
    private final CssSize length;

    public SvgFontSize(@Nullable SizeKeyword keyword, @Nullable CssSize length) {
        this.keyword = keyword;
        this.length = length;
    }

    public SizeKeyword getKeyword() {
        return keyword;
    }

    public CssSize getLength() {
        return length;
    }
}
