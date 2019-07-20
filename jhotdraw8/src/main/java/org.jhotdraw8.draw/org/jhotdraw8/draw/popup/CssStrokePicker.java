package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssStroke;

import java.util.function.BiConsumer;

public class CssStrokePicker extends AbstractColorPicker<CssStroke> {
    @Override
    public void show(Node anchor, double screenX, double screenY, CssStroke initialValue, BiConsumer<Boolean, CssStroke> callback) {
        CssStroke initial = (initialValue == null) ? new CssStroke(CssColor.BLACK) : initialValue;

        abstractShow(anchor, screenX, screenY,
                (initial.getPaint() instanceof CssColor) ? ((CssColor) initial.getPaint()).getColor() : null,
                (b, v) -> callback.accept(b, v == null ? null :
                        new CssStroke(initial.getWidth(), new CssColor(v),
                                initial.getType(),
                                initial.getLineCap(), initial.getLineJoin(),
                                initial.getMiterLimit(),
                                initial.getDashOffset(),
                                initial.getDashArray()))
        );
    }
}
