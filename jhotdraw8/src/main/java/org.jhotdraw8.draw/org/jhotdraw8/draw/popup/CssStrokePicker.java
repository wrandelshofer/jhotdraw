/*
 * @(#)CssStrokePicker.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssStroke;

import java.util.function.BiConsumer;

public class CssStrokePicker extends AbstractPicker<CssStroke> {
    // FIXME create CssStrokeDialog
    private CssColorDialog dialog;

    private void update(@NonNull Node anchor, CssColor initialValue, @NonNull BiConsumer<Boolean, CssColor> callback) {
        if (dialog == null) {
            dialog = new CssColorDialog(anchor.getScene().getWindow());
        }
        dialog.setOnUse(() -> callback.accept(true, dialog.getCustomColor()));
        dialog.setOnSave(() -> callback.accept(true, dialog.getCustomColor()));
        dialog.setCurrentColor(initialValue);
    }

    @Override
    public void show(@NonNull Node anchor, double screenX, double screenY, @Nullable CssStroke initialValue, @NonNull BiConsumer<Boolean, CssStroke> callback) {
        CssStroke initial = (initialValue == null) ? new CssStroke(CssColor.BLACK) : initialValue;

        update(anchor,
                (initial.getPaint() instanceof CssColor) ? ((CssColor) initial.getPaint()) : null,
                (b, v) -> callback.accept(b, v == null ? null :
                        new CssStroke(initial.getWidth(), v,
                                initial.getType(),
                                initial.getLineCap(), initial.getLineJoin(),
                                initial.getMiterLimit(),
                                initial.getDashOffset(),
                                initial.getDashArray())
                )
        );
        dialog.show();
    }
}
