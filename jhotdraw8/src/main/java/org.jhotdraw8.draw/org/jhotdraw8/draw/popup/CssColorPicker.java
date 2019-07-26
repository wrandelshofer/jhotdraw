/*
 * @(#)CssColorPicker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import org.jhotdraw8.css.CssColor;

import java.util.function.BiConsumer;

public class CssColorPicker extends AbstractColorPicker<CssColor> {

    @Override
    public void show(Node anchor, double screenX, double screenY, CssColor initialValue, BiConsumer<Boolean, CssColor> callback) {
        abstractShow(anchor, screenX, screenY,
                initialValue == null ? null : initialValue.getColor(),
                (b, v) -> callback.accept(b, v == null ? null : new CssColor(v))
        );
    }
}
