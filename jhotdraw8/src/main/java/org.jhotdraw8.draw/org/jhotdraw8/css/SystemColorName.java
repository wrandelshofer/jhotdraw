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

    // deprecated system colors

    /**
     * Active window border. Same as ButtonBorder.
     */
    @NonNull String ACTIVE_BORDER = "activeborder";
    /**
     * Active window caption. Same as CanvasText.
     */
    @NonNull String ACTIVE_CAPTION = "activecaption";
    /**
     * Background color of multiple document interface. Same as Canvas.
     */
    @NonNull String APP_WORKSPACE = "appworkspace";
    /**
     * Desktop background. Same as Canvas.
     */
    @NonNull String BACKGROUND = "background";
    /**
     * The color of the border facing the light source for 3-D elements that appear 3-D due to one layer of surrounding border. Same as ButtonFace.
     */
    @NonNull String BUTTON_HIGHLIGHT = "buttonhighlight";
    /**
     * The color of the border away from the light source for 3-D elements that appear 3-D due to one layer of surrounding border. Same as ButtonFace.
     */
    @NonNull String BUTTON_SHADOW = "buttonshadow";
    /**
     * Text in caption, size box, and scrollbar arrow box. Same as CanvasText.
     */
    @NonNull String CAPTION_TEXT = "captiontext";
    /**
     * Inactive window border. Same as ButtonBorder.
     */
    @NonNull String INACTIVE_BORDER = "inactiveborder";
    /**
     * Inactive window caption. Same as Canvas.
     */
    @NonNull String INACTIVE_CAPTION = "inactivecaption";
    /**
     * Color of text in an inactive caption. Same as GrayText.
     */
    @NonNull String INACTIVE_CAPTION_TEXT = "inactivecaptiontext";
    /**
     * Background color for tooltip controls. Same as Canvas.
     */
    @NonNull String INFO_BACKGROUND = "infobackground";
    /**
     * Text color for tooltip controls. Same as CanvasText.
     */
    @NonNull String INFO_TEXT = "infotext";
    /**
     * Menu background. Same as Canvas.
     */
    @NonNull String MENU = "menu";
    /**
     * Text in menus. Same as CanvasText.
     */
    @NonNull String MENU_TEXT = "menutext";
    /**
     * Scroll bar gray area. Same as Canvas.
     */
    @NonNull String SCROLLBAR = "scrollbar";
    /**
     * The color of the darker (generally outer) of the two borders away from the light source for 3-D elements that appear 3-D due to two concentric layers of surrounding border. Same as ButtonBorder.
     */
    @NonNull String THREE_D_DARK_SHADOW = "threeddarkshadow";
    /**
     * The face background color for 3-D elements that appear 3-D due to two concentric layers of surrounding border. Same as ButtonFace.
     */
    @NonNull String THREE_D_FACE = "threedface";
    /**
     * The color of the lighter (generally outer) of the two borders facing the light source for 3-D elements that appear 3-D due to two concentric layers of surrounding border. Same as ButtonBorder.
     */
    @NonNull String THREE_D_HIGHLIGHT = "threedhighlight";
    /**
     * The color of the darker (generally inner) of the two borders facing the light source for 3-D elements that appear 3-D due to two concentric layers of surrounding border. Same as ButtonBorder.
     */
    @NonNull String THREE_D_LIGHT_SHADOW = "threedlightshadow";
    /**
     * The color of the lighter (generally inner) of the two borders away from the light source for 3-D elements that appear 3-D due to two concentric layers of surrounding border. Same as ButtonBorder.
     */
    @NonNull String THREE_D_SHADOW = "threedshadow";
    /**
     * Window background. Same as Canvas.
     */
    @NonNull String WINDOW = "window";

    /**
     * Window frame. Same as ButtonBorder.
     */
    @NonNull String WINDOW_FRAME = "windowframe";
    /**
     * Text in windows. Same as CanvasText.
     */
    @NonNull String WINDOW_TEXT = "windowtext";

}
