package org.jhotdraw8.draw.popup;

import javafx.scene.paint.Color;
import org.jhotdraw8.css.CssColor;

public class CssColorPicker extends AbstractColorPicker<CssColor> {


    @Override
    protected Color getCurrentColor() {
        CssColor currentValue = getCurrentValue();
        return currentValue == null ? null : currentValue.getColor();
    }

    protected void applyColor(Color customColor) {
        applyValue(new CssColor(customColor));
    }
}
