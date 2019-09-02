/*
 * @(#)FontFamilyPicker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import org.jhotdraw8.gui.fontchooser.FontFamilyDialog;

import java.util.Optional;
import java.util.function.BiConsumer;

public class FontFamilyPicker extends AbstractPicker<String> {
    private FontFamilyDialog dialog;

    private void update(Node anchor) {
        if (dialog == null) {
            dialog = new FontFamilyDialog();
        }
    }

    @Override
    public void show(Node anchor, double screenX, double screenY,
                     String initialValue, BiConsumer<Boolean, String> callback) {
        String initial = initialValue == null
                ? "Arial"
                : initialValue;
        update(anchor);
        Optional<String> s = dialog.showAndWait(initial);
        s.ifPresent(v -> callback.accept(true, v));
    }


}
