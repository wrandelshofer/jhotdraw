/*
 * @(#)PaintablePicker.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;

import java.util.function.BiConsumer;

public class PaintablePicker extends AbstractPicker<Paintable> {
    // FIXME create CssPaintableDialog
    private CssColorPopup dialog;

    private void update(@NonNull Node anchor, CssColor initialValue, @NonNull BiConsumer<Boolean, CssColor> callback) {
        if (dialog == null) {
            dialog = new CssColorPopup();
        }

        dialog.setCallback(callback);
        dialog.setCurrentColor(initialValue);

    }

    @Override
    public void show(@NonNull Node anchor, double screenX, double screenY, Paintable initial, @NonNull BiConsumer<Boolean, Paintable> callback) {
        update(anchor,
                (initial instanceof CssColor) ? ((CssColor) initial) : null,
                callback::accept
        );
        dialog.show(anchor, screenX, screenY);
    }
}
