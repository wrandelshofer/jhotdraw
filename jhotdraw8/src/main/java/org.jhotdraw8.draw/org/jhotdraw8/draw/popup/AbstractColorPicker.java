/*
 * @(#)AbstractColorPicker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

//import com.sun.javafx.scene.control.skin.CustomColorDialog;
import javafx.scene.Node;
import javafx.scene.paint.Color;

import java.util.function.BiConsumer;

public abstract class AbstractColorPicker<T> extends AbstractPicker<T> {
    //private CustomColorDialog dialog;

    private void update(Node anchor, Color initialValue, BiConsumer<Boolean, Color> callback) {
        /*
        if (dialog == null) {
            dialog = new CustomColorDialog(anchor.getScene().getWindow());
        }
        dialog.setOnUse(() -> callback.accept(true, dialog.getCustomColor()));
        dialog.setOnSave(() -> callback.accept(true, dialog.getCustomColor()));
        dialog.setCurrentColor(initialValue);
        */
    }


    protected void abstractShow(Node anchor, double screenX, double screenY,
                                Color initialValue, BiConsumer<Boolean, Color> callback) {
        update(anchor, initialValue, callback);
        //dialog.show();

    }


}
