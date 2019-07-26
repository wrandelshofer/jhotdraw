/*
 * @(#)CssFontPicker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.gui.fontchooser.FontDialog;

import java.util.Optional;
import java.util.function.BiConsumer;

public class CssFontPicker extends AbstractPicker<CssFont> {
    private FontDialog dialog;

    private void update(Node anchor) {
        if (dialog == null) {
            dialog = new FontDialog();
        }
    }

    @Override
    public void show(Node anchor, double screenX, double screenY,
                     CssFont initialValue, BiConsumer<Boolean, CssFont> callback) {
        update(anchor);
        Optional<String> s = dialog.showAndWait(initialValue == null ? null : initialValue.getFamily());
        if (initialValue == null) {
            s.ifPresent(v -> callback.accept(true, new CssFont(v, FontWeight.NORMAL, FontPosture.REGULAR, new CssSize(13))));
        } else {
            s.ifPresent(v -> callback.accept(true, new CssFont(v, FontWeight.NORMAL, FontPosture.REGULAR, initialValue.getSize())));
        }
    }
}
