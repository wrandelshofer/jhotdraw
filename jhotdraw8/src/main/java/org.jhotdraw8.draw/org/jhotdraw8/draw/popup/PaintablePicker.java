/*
 * @(#)PaintablePicker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;

import java.util.function.BiConsumer;

public class PaintablePicker extends AbstractPicker<Paintable> {
    // FIXME create CssPaintableDialog
    private CssColorDialog dialog;

    private void update(Node anchor, CssColor initialValue, BiConsumer<Boolean, CssColor> callback) {
        if (dialog == null) {
            dialog = new CssColorDialog(anchor.getScene().getWindow());
        }

        dialog.setOnUse(() -> callback.accept(true, dialog.getCurrentColor()));
        dialog.setOnSave(() -> callback.accept(true, dialog.getCurrentColor()));
        dialog.setCurrentColor(initialValue);

    }

    @Override
    public void show(Node anchor, double screenX, double screenY, Paintable initial, BiConsumer<Boolean, Paintable> callback) {
        update(anchor,
                (initial instanceof CssColor) ? ((CssColor) initial) : null,
                callback::accept
        );
        dialog.show();
    }
}
