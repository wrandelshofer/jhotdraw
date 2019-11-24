package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.macos.MacOSPreferences;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class MacOSSystemColorConverter extends MappedSystemColorConverter {
    public final static Map<String, Color> DARK_SYSTEM_COLORS;

    static {
        Map<String, Color> map = new HashMap<>();
        map.put(CANVAS, Color.web("#2b2b2b"));
        map.put(CANVAS_TEXT, Color.web("#bbbbbb"));
        map.put(LINK_TEXT, Color.NAVY);
        map.put(VISITED_TEXT, Color.PURPLE);
        map.put(ACTIVE_TEXT, Color.RED);
        map.put(BUTTON_FACE, Color.web("#666a70"));
        map.put(BUTTON_TEXT, Color.web("#bbbbbb"));
        map.put(FIELD, Color.web("#6c7078"));
        map.put(FIELD_TEXT, Color.web("#bbbbbb"));
        map.put(HIGHLIGHT, Color.web("#3f628a"));
        map.put(HIGHLIGHT_TEXT, Color.web("#bbbbbb"));
        map.put(GRAY_TEXT, Color.web("#646464"));
        DARK_SYSTEM_COLORS = Collections.unmodifiableMap(map);
    }

    public MacOSSystemColorConverter() {
        super(
                "Dark".equals(MacOSPreferences.get(MacOSPreferences.GLOBAL_PREFERENCES, "AppleInterfaceStyle"))
                        ? DARK_SYSTEM_COLORS
                        : DefaultSystemColorConverter.LIGHT_SYSTEM_COLORS

        );
    }
}
