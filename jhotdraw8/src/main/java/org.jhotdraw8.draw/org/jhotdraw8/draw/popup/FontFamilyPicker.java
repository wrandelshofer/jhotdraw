package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import org.jhotdraw8.gui.fontchooser.FontDialog;

import java.util.Optional;

public class FontFamilyPicker extends AbstractPicker<String> {
    private FontDialog dialog;

    private void update(Node anchor) {
        if (dialog == null) {
            dialog = new FontDialog();
        }
    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        update(anchor);
        Optional<String> s = dialog.showAndWait(getCurrentValue());
        s.ifPresent(this::applyValue);
    }


}
