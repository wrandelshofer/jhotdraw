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

    public static final @NonNull SystemCssColor CANVAS = new SystemCssColor(SystemColorName.CANVAS, new Color(1.0, 1.0, 1.0, 1.0));
    public static final @NonNull SystemCssColor CANVAS_TEXT = new SystemCssColor(SystemColorName.CANVAS_TEXT, new Color(0.0, 0.0, 0.0, 1.0));
    public static final @NonNull SystemCssColor LINK_TEXT = new SystemCssColor(SystemColorName.LINK_TEXT, new Color(0.0, 0.0, 0.933, 1.0));
    public static final @NonNull SystemCssColor VISITED_TEXT = new SystemCssColor(SystemColorName.VISITED_TEXT, new Color(0.333, 0.102, 0.545, 1.0));
    public static final @NonNull SystemCssColor ACTIVE_TEXT = new SystemCssColor(SystemColorName.ACTIVE_TEXT, new Color(1.0, 0.0, 0.0, 1.0));
    public static final @NonNull SystemCssColor BUTTON_FACE = new SystemCssColor(SystemColorName.BUTTON_FACE, new Color(0.867, 0.867, 0.867, 1.0));
    public static final @NonNull SystemCssColor BUTTON_TEXT = new SystemCssColor(SystemColorName.BUTTON_TEXT, new Color(0.0, 0.0, 0.0, 1.0));
    public static final @NonNull SystemCssColor BUTTON_BORDER = new SystemCssColor(SystemColorName.BUTTON_BORDER, new Color(1.0, 1.0, 1.0, 1.0));
    public static final @NonNull SystemCssColor FIELD = new SystemCssColor(SystemColorName.FIELD, new Color(1.0, 1.0, 1.0, 1.0));
    public static final @NonNull SystemCssColor FIELD_TEXT = new SystemCssColor(SystemColorName.FIELD_TEXT, new Color(0.0, 0.0, 0.0, 1.0));
    public static final @NonNull SystemCssColor HIGHLIGHT = new SystemCssColor(SystemColorName.HIGHLIGHT, new Color(0.71, 0.835, 1.0, 1.0));
    public static final @NonNull SystemCssColor HIGHLIGHT_TEXT = new SystemCssColor(SystemColorName.HIGHLIGHT_TEXT, new Color(0.0, 0.0, 0.0, 1.0));
    public static final @NonNull SystemCssColor MARK = new SystemCssColor(SystemColorName.MARK, new Color(1.0, 1.0, 0.0, 1.0));
    public static final @NonNull SystemCssColor MARK_TEXT = new SystemCssColor(SystemColorName.MARK_TEXT, new Color(1.0, 1.0, 1.0, 1.0));
    public static final @NonNull SystemCssColor GRAY_TEXT = new SystemCssColor(SystemColorName.GRAY_TEXT, new Color(0.502, 0.502, 0.502, 1.0));

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
        SYSTEM_COLORS = ImmutableMaps.ofMap(m);
    }
    /**
     * Creates a system color for the given name.
     * <p>
     * If the name is unknown, then a black system color with the given
     * name is created.
     *
     * @param name the name of the system color
     * @return a new instance
     */
    public static @NonNull SystemCssColor of(@NonNull String name) {
        SystemCssColor color = SYSTEM_COLORS.get(name.toLowerCase());
        return color == null ? new SystemCssColor(name, Color.BLACK) : color;
    }

    /**
     * Returns true if the given name is a known system color.
     *
     * @param name a name
     * @return true if known
     */
    public static boolean isSystemColor(@NonNull String name) {
        return SYSTEM_COLORS.containsKey(name.toLowerCase());
    }

}
