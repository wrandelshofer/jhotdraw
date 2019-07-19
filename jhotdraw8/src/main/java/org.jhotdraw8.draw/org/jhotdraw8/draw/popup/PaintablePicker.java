package org.jhotdraw8.draw.popup;

import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;

public class PaintablePicker extends AbstractColorPicker<Paintable> {

    @Override
    protected Color getCurrentColor() {
        Paintable currentValue = getCurrentValue();
        Paint paint = currentValue == null ? null : currentValue.getPaint();
        return (paint instanceof Color) ? (Color) paint : null;
    }

    protected void applyColor(Color customColor) {
        applyValue(new CssColor(customColor));
    }
}
