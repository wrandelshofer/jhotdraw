package org.jhotdraw8.css;

import javafx.scene.paint.Color;
import org.jhotdraw8.annotation.NonNull;

import java.util.Map;

public class MappedSystemColorConverter implements SystemColorConverter {
    private final Map<String, Color> systemColors;

    public MappedSystemColorConverter(Map<String, Color> systemColors) {
        this.systemColors = systemColors;
    }

    @Override
    public Color convert(@NonNull CssColor value) {
        Color systemColor = systemColors.get(value.getName());
        return systemColor != null ? systemColor : value.getColor();
    }

}
