/*
 * @(#)SystemColorName.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import org.jhotdraw8.annotation.NonNull;

/**
 * Specifies the name of a system color in a cascading stylesheet.
 * <p>
 * All names are given in lower case. System color names are not case sensitive.
 * <p>
 * References:
 * <dl>
 *     <dt>CSS Color Module Level 4, System Color</dt>
 *     <dd><a href="https://www.w3.org/TR/css-color-4/#css-system-colors">w3.org/<a></a></a></dd>
 * </dl>
 */
public interface SystemColorName {
    @NonNull String CANVAS = "canvas";
    @NonNull String CANVAS_TEXT = "canvastext";
    @NonNull String LINK_TEXT = "linktext";
    @NonNull String VISITED_TEXT = "visitedtext";
    @NonNull String ACTIVE_TEXT = "activetext";
    @NonNull String BUTTON_FACE = "buttonface";
    @NonNull String BUTTON_TEXT = "buttontext";
    @NonNull String BUTTON_BORDER = "buttonborder";
    @NonNull String FIELD = "field";
    @NonNull String FIELD_TEXT = "fieldtext";
    @NonNull String HIGHLIGHT = "highlight";
    @NonNull String HIGHLIGHT_TEXT = "highlighttext";
    @NonNull String MARK = "mark";
    @NonNull String MARK_TEXT = "marktext";
    @NonNull String GRAY_TEXT = "graytext";


}
