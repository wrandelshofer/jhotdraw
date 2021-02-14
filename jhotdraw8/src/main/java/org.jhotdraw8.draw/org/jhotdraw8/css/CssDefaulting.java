/*
 * @(#)CssDefaulting.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

/**
 * CSS Defaulting keywords can be applied to all CSS properties
 * in HTML and SVG.
 * <p>
 * References:
 * <dl>
 *     <dt>CSS Cascading and Inheritance Level 4, Chapter 7. Defaulting</dt>
 *     <dd><a href="https://www.w3.org/TR/2020/WD-css-cascade-4-20200818/#defaulting">w3.org</a></dd>
 * </dl>
 */
public enum CssDefaulting {
    /**
     * If the cascaded value of a property is the "initial" keyword,
     * the property's specified value is its initial value.
     */
    INITIAL,
    /**
     * If the cascaded value of a property is the "inherit" keyword,
     * the property's specified and computed values are the inherited value.
     */
    INHERIT,
    /**
     * If the cascaded value of a property is the "unset" keyword, then if it is
     * an inherited property, this is treated as inherit, and if it is not, this
     * is treated as initial.
     * <p>
     * This keyword effectively erases all declared values occurring earlier in
     * the cascade, correctly inheriting or not as appropriate for the property
     * (or all longhands of a shorthand).
     */
    UNSET,
    /**
     * If the cascaded value of a property is the revert keyword, the behavior
     * depends on the origin to which the declaration belongs:
     * <dl>
     *     <dt>user-agent origin</dt>
     *     <dd>Equivalent to unset</dd>
     *
     *     <dt>user origin</dt>
     *     <dd>Rolls back the cascaded value to the user-agent level, so that
     *     the specified value is calculated as if no author-level or user-level
     *     rules were specified for this property on this element.</dd>
     *
     *     <dt>author origin</dt>
     *     <dd>Rolls back the cascaded value to the user level, so that the
     *     specified value is calculated as if no author-level rules were
     *     specified for this property on this element. For the purpose of
     *     revert, this origin includes the Animation origin.</dd>
     * </dl>
     */
    REVERT

}
