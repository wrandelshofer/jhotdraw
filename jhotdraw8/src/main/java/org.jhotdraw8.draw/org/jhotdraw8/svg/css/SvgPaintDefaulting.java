/*
 * @(#)SvgPaintDefaulting.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.css;

/**
 * CSS Color defaulting keywords can be applied to all color properties
 * in HTML and SVG.
 * <p>
 * References:
 * <dl>
 *     <dt>SVG Tiny 1.2 Specifying Paint</dt>
 *     <dd><a href="https://www.w3.org/TR/SVGTiny12/painting.html#SpecifyingPaint">w3.org</a></dd>
 * </dl>
 */
public enum SvgPaintDefaulting {
    /**
     * Indicates that painting shall be done using the color specified by the current animated value of the 'color' property.
     */
    CURRENT_COLOR,
    /**
     * If the cascaded value of a property is the "inherit" keyword,
     * the property's specified and computed values are the inherited value.
     */
    INHERIT,

}
