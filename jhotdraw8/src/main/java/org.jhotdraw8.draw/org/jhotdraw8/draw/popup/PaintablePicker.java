package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import javafx.scene.paint.Color;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;

import java.util.function.BiConsumer;

public class PaintablePicker extends AbstractColorPicker<Paintable> {

    @Override
    public void show(Node anchor, double screenX, double screenY, Paintable initialValue, BiConsumer<Boolean, Paintable> callback) {
        abstractShow(anchor, screenX, screenY,
                initialValue instanceof CssColor ? (Color) initialValue.getPaint() : null,
                (b, v) -> callback.accept(b, v == null ? null : new CssColor(v))
        );
    }
}
