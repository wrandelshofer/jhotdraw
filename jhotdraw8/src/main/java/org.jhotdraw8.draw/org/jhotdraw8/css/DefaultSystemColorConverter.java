/*
 * @(#)DefaultSystemColorConverter.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.css;

import javafx.scene.paint.Color;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

/**
 * The default color converter provides a fixed set of system colors.
 */
public class DefaultSystemColorConverter extends MappedSystemColorConverter {
    public final static Map<String, Color> LIGHT_SYSTEM_COLORS;

    static {
        Map<String, Color> map = new HashMap<>();
        map.put(CANVAS, Color.WHITE);
        map.put(CANVAS_TEXT, Color.BLACK);
        map.put(LINK_TEXT, Color.NAVY);
        map.put(VISITED_TEXT, Color.PURPLE);
        map.put(ACTIVE_TEXT, Color.RED);
        map.put(BUTTON_FACE, Color.SILVER);
        map.put(BUTTON_TEXT, Color.BLACK);
        map.put(FIELD, Color.WHITE);
        map.put(FIELD_TEXT, Color.BLACK);
        map.put(HIGHLIGHT, Color.CORNFLOWERBLUE);
        map.put(HIGHLIGHT_TEXT, Color.BLACK);
        map.put(GRAY_TEXT, Color.GRAY);
        LIGHT_SYSTEM_COLORS = Collections.unmodifiableMap(map);
    }

    public DefaultSystemColorConverter() {
        super(LIGHT_SYSTEM_COLORS);
    }

}
