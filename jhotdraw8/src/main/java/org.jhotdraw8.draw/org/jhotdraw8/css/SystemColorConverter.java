/*
 * @(#)SystemColorConverter.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;

/**
 * The CssColorConverter converts system colors into color values.
 * <p>
 * References:<br>
 *     <ul>
 *         <li><a href="https://www.w3.org/TR/2020/WD-css-color-4-20201112/">CSS System colors</a></li>
 *     </ul>
 * </p>
 */
public interface SystemColorConverter {
    /**
     * Background of application content or documents.
     */
    String CANVAS = "canvas";
    /**
     * Text in application content or documents.
     */
    String CANVAS_TEXT = "canvastext";
    /**
     * Text in non-active, non-visited links.
     */
    String LINK_TEXT = "linktext";
    /**
     * Text in visited links.
     */
    String VISITED_TEXT = "visitedtext";
    /**
     * Text in active links.
     */
    String ACTIVE_TEXT = "activetext";
    /**
     * The face background color for push buttons.
     */
    String BUTTON_FACE = "buttonface";
    /**
     * Text on push buttons.
     */
    String BUTTON_TEXT = "Buttontext";
    /**
     * Background of input fields.
     */
    String FIELD = "field";
    /**
     * Text in input fields.
     */
    String FIELD_TEXT = "fieldtext";
    /**
     * Background of item(s) selected in a control.
     */
    String HIGHLIGHT = "highlight";
    /**
     * Text of item(s) selected in a control.
     */
    String HIGHLIGHT_TEXT = "HighlightText";
    /**
     * Disabled text. (Often, but not necessarily, gray.)
     */
    String GRAY_TEXT = "graytext";
    ;

    /**
     * Converts the specified value from input unit to a Color value.
     *
     * @param value a value
     * @return converted value
     */
    default Color convert(@NonNull CssColor value) {
        return value.getColor();
    }

}
