/*
 * @(#)FontFamilyPicker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import org.jhotdraw8.gui.fontchooser.FontDialog;

import java.util.Optional;
import java.util.function.BiConsumer;

public class FontFamilyPicker extends AbstractPicker<String> {
    private FontDialog dialog;

    private void update(Node anchor) {
        if (dialog == null) {
            dialog = new FontDialog();
        }
    }

    @Override
    public void show(Node anchor, double screenX, double screenY,
                     String initialValue, BiConsumer<Boolean, String> callback) {
        update(anchor);
        Optional<String> s = dialog.showAndWait(initialValue);
        s.ifPresent(v -> callback.accept(true, v));
    }


}
