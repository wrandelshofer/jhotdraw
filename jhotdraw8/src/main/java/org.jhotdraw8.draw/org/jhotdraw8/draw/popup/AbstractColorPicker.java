package org.jhotdraw8.draw.popup;

import com.sun.javafx.scene.control.skin.CustomColorDialog;
import javafx.scene.Node;
import javafx.scene.paint.Color;

public abstract class AbstractColorPicker<T> extends AbstractPicker<T> {
    private CustomColorDialog dialog;

    private void update(Node anchor) {
        if (dialog == null) {
            dialog = new CustomColorDialog(anchor.getScene().getWindow());
            dialog.setOnUse(() -> applyColor(dialog.getCustomColor()));
            dialog.setOnSave(() -> applyColor(dialog.getCustomColor()));
        }
        dialog.setCurrentColor(getCurrentColor());
    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        update(anchor);
        dialog.show();
    }

    protected abstract Color getCurrentColor();

    protected abstract void applyColor(Color customColor);
}
