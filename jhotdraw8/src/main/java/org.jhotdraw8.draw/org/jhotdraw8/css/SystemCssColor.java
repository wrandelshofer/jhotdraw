/*
 * @(#)SystemCssColor.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableMap;
import org.jhotdraw8.collection.ImmutableMaps;
import org.jhotdraw8.draw.render.RenderContext;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Represents a system color in a cascading stylesheet.
 * <p>
 * References:
 * <dl>
 *     <dt>CSS Color Module Level 4, System Colors</dt>
 *     <dd><a href="https://www.w3.org/TR/css-color-4/#css-system-colors">w3.org/<a></a></a></dd>
 * </dl>
 */
public class SystemCssColor extends CssColor {

    public static final @NonNull SystemCssColor CANVAS = new SystemCssColor(SystemColorName.CANVAS, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor CANVAS_TEXT = new SystemCssColor(SystemColorName.CANVAS_TEXT, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor LINK_TEXT = new SystemCssColor(SystemColorName.LINK_TEXT, Color.rgb(7, 0, 248));
    public static final @NonNull SystemCssColor VISITED_TEXT = new SystemCssColor(SystemColorName.VISITED_TEXT, Color.rgb(93, 19, 144));
    public static final @NonNull SystemCssColor ACTIVE_TEXT = new SystemCssColor(SystemColorName.ACTIVE_TEXT, Color.rgb(255, 0, 0));
    public static final @NonNull SystemCssColor BUTTON_FACE = new SystemCssColor(SystemColorName.BUTTON_FACE, Color.rgb(240, 240, 240));
    public static final @NonNull SystemCssColor BUTTON_TEXT = new SystemCssColor(SystemColorName.BUTTON_TEXT, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor BUTTON_BORDER = new SystemCssColor(SystemColorName.BUTTON_BORDER, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor FIELD = new SystemCssColor(SystemColorName.FIELD, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor FIELD_TEXT = new SystemCssColor(SystemColorName.FIELD_TEXT, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor HIGHLIGHT = new SystemCssColor(SystemColorName.HIGHLIGHT, Color.rgb(0, 101, 233));
    public static final @NonNull SystemCssColor HIGHLIGHT_TEXT = new SystemCssColor(SystemColorName.HIGHLIGHT_TEXT, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor MARK = new SystemCssColor(SystemColorName.MARK, Color.rgb(255, 255, 0));
    public static final @NonNull SystemCssColor MARK_TEXT = new SystemCssColor(SystemColorName.MARK_TEXT, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor GRAY_TEXT = new SystemCssColor(SystemColorName.GRAY_TEXT, Color.rgb(192, 192, 192));

    // deprecated system colors
    public static final @NonNull SystemCssColor ACTIVE_BORDER = new SystemCssColor(SystemColorName.ACTIVE_BORDER, Color.rgb(0, 105, 253));
    public static final @NonNull SystemCssColor ACTIVE_CAPTION = new SystemCssColor(SystemColorName.ACTIVE_CAPTION, Color.rgb(204, 204, 204));
    public static final @NonNull SystemCssColor APP_WORKSPACE = new SystemCssColor(SystemColorName.APP_WORKSPACE, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor BACKGROUND = new SystemCssColor(SystemColorName.BACKGROUND, Color.rgb(99, 99, 213));
    public static final @NonNull SystemCssColor BUTTON_HIGHLIGHT = new SystemCssColor(SystemColorName.BUTTON_HIGHLIGHT, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor BUTTON_SHADOW = new SystemCssColor(SystemColorName.BUTTON_SHADOW, Color.rgb(220, 220, 220));
    public static final @NonNull SystemCssColor CAPTION_TEXT = new SystemCssColor(SystemColorName.CAPTION_TEXT, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor INACTIVE_BORDER = new SystemCssColor(SystemColorName.INACTIVE_BORDER, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor INACTIVE_CAPTION = new SystemCssColor(SystemColorName.INACTIVE_CAPTION, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor INACTIVE_CAPTION_TEXT = new SystemCssColor(SystemColorName.INACTIVE_CAPTION_TEXT, Color.rgb(69, 69, 69));
    public static final @NonNull SystemCssColor INFO_BACKGROUND = new SystemCssColor(SystemColorName.INFO_BACKGROUND, Color.rgb(255, 255, 192));
    public static final @NonNull SystemCssColor INFO_TEXT = new SystemCssColor(SystemColorName.INFO_TEXT, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor MENU = new SystemCssColor(SystemColorName.MENU, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor MENU_TEXT = new SystemCssColor(SystemColorName.MENU_TEXT, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor SCROLLBAR = new SystemCssColor(SystemColorName.SCROLLBAR, Color.rgb(170, 170, 170));
    public static final @NonNull SystemCssColor THREE_D_DARK_SHADOW = new SystemCssColor(SystemColorName.THREE_D_DARK_SHADOW, Color.rgb(220, 220, 220));
    public static final @NonNull SystemCssColor THREE_D_FACE = new SystemCssColor(SystemColorName.THREE_D_FACE, Color.rgb(240, 240, 240));
    public static final @NonNull SystemCssColor THREE_D_HIGHLIGHT = new SystemCssColor(SystemColorName.THREE_D_HIGHLIGHT, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor THREE_D_LIGHT_SHADOW = new SystemCssColor(SystemColorName.THREE_D_LIGHT_SHADOW, Color.rgb(218, 218, 218));
    public static final @NonNull SystemCssColor THREE_D_SHADOW = new SystemCssColor(SystemColorName.THREE_D_SHADOW, Color.rgb(0, 0, 0));
    public static final @NonNull SystemCssColor WINDOW = new SystemCssColor(SystemColorName.WINDOW, Color.rgb(255, 255, 255));
    public static final @NonNull SystemCssColor WINDOW_FRAME = new SystemCssColor(SystemColorName.WINDOW_FRAME, Color.rgb(204, 204, 204));
    public static final @NonNull SystemCssColor WINDOW_TEXT = new SystemCssColor(SystemColorName.WINDOW_TEXT, Color.rgb(0, 0, 0));


    public SystemCssColor(@NonNull String name, @NonNull Color color) {
        super(name, color);
    }

    @Override
    public @Nullable Paint getPaint(RenderContext ctx) {
        return ctx == null ? getPaint()
                : ctx.getNonNull(RenderContext.SYSTEM_COLOR_CONVERTER_KEY)
                .convert(this);
    }

    private static final @NonNull ImmutableMap<String, SystemCssColor> SYSTEM_COLORS;

    static {
        // Workaround for Java SE 8: javac hangs if ImmutableMap.ofEntries() has many entries.
        Map<String, SystemCssColor> m = new LinkedHashMap<>();


        m.put(CANVAS.getName(), CANVAS);
        m.put(CANVAS_TEXT.getName(), CANVAS_TEXT);
        m.put(LINK_TEXT.getName(), LINK_TEXT);
        m.put(VISITED_TEXT.getName(), VISITED_TEXT);
        m.put(ACTIVE_TEXT.getName(), ACTIVE_TEXT);
        m.put(BUTTON_FACE.getName(), BUTTON_FACE);
        m.put(BUTTON_TEXT.getName(), BUTTON_TEXT);
        m.put(BUTTON_BORDER.getName(), BUTTON_BORDER);
        m.put(FIELD.getName(), FIELD);
        m.put(FIELD_TEXT.getName(), FIELD_TEXT);
        m.put(HIGHLIGHT.getName(), HIGHLIGHT);
        m.put(HIGHLIGHT_TEXT.getName(), HIGHLIGHT_TEXT);
        m.put(MARK.getName(), MARK);
        m.put(MARK_TEXT.getName(), MARK_TEXT);
        m.put(GRAY_TEXT.getName(), GRAY_TEXT);

        m.put(ACTIVE_BORDER.getName(), ACTIVE_BORDER);
        m.put(ACTIVE_CAPTION.getName(), ACTIVE_CAPTION);
        m.put(APP_WORKSPACE.getName(), APP_WORKSPACE);
        m.put(BACKGROUND.getName(), BACKGROUND);
        m.put(BUTTON_HIGHLIGHT.getName(), BUTTON_HIGHLIGHT);
        m.put(BUTTON_SHADOW.getName(), BUTTON_SHADOW);
        m.put(CAPTION_TEXT.getName(), CAPTION_TEXT);
        m.put(INACTIVE_BORDER.getName(), INACTIVE_BORDER);
        m.put(INACTIVE_CAPTION.getName(), INACTIVE_CAPTION);
        m.put(INACTIVE_CAPTION_TEXT.getName(), INACTIVE_CAPTION_TEXT);
        m.put(INFO_BACKGROUND.getName(), INFO_BACKGROUND);
        m.put(INFO_TEXT.getName(), INFO_TEXT);
        m.put(MENU.getName(), MENU);
        m.put(MENU_TEXT.getName(), MENU_TEXT);
        m.put(SCROLLBAR.getName(), SCROLLBAR);
        m.put(THREE_D_DARK_SHADOW.getName(), THREE_D_DARK_SHADOW);
        m.put(THREE_D_FACE.getName(), THREE_D_FACE);
        m.put(THREE_D_HIGHLIGHT.getName(), THREE_D_HIGHLIGHT);
        m.put(THREE_D_LIGHT_SHADOW.getName(), THREE_D_LIGHT_SHADOW);
        m.put(THREE_D_SHADOW.getName(), THREE_D_SHADOW);
        m.put(WINDOW.getName(), WINDOW);
        m.put(WINDOW_FRAME.getName(), WINDOW_FRAME);
        m.put(WINDOW_TEXT.getName(), WINDOW_TEXT);

        SYSTEM_COLORS = ImmutableMaps.ofMap(m);
    }

    /**
     * Creates a system color for the given name.
     * <p>
     * The name is not case sensitive.
     * <p>
     * If the name is unknown, then null is returned.
     *
     * @param name the name of the system color
     * @return the system color or null
     */
    public static @NonNull SystemCssColor of(@NonNull String name) {
        return SYSTEM_COLORS.get(name.toLowerCase());
    }

    /**
     * Returns true if the given name is a known system color.
     * <p>
     * The name is not case sensitive.
     *
     * @param name a name
     * @return true if known
     */
    public static boolean isSystemColor(@NonNull String name) {
        return SYSTEM_COLORS.containsKey(name.toLowerCase());
    }

}
