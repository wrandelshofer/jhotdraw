/*
 * @(#)SvgElementFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.figure;

import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.styleable.StringStyleableKey;

public interface SvgElementFigure extends Figure {
    /**
     * title.
     * <p>
     * References:
     * <dl>
     *     <dt>SVG Tiny 1.2</dt>
     *     <dd><a href="https://www.w3.org/TR/SVGTiny12/struct.html#TitleAndDescriptionElements">w3.org</a></dd>
     *     <dt>SVG 2: title element</dt>
     *     <dd><a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/struct.html#TitleElement">w3.org</a></dd>
     * </dl>
     */
    StringStyleableKey TITLE_KEY = new StringStyleableKey("title");
    /**
     * desc.
     * <p>
     * References:
     * <dl>
     *     <dt>SVG Tiny 1.2</dt>
     *     <dd><a href="https://www.w3.org/TR/SVGTiny12/struct.html#TitleAndDescriptionElements">w3.org</a></dd>
     *     <dt>SVG 2: desc element</dt>
     *     <dd><a href="https://www.w3.org/TR/2018/CR-SVG2-20181004/struct.html#DescElement">w3.org</a></dd>
     * </dl>
     */
    StringStyleableKey DESC_KEY = new StringStyleableKey("desc");
}
