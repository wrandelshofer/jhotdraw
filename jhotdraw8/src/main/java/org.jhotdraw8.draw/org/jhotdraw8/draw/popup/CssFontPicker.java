package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.gui.fontchooser.FontDialog;

import java.util.Optional;

public class CssFontPicker extends AbstractPicker<CssFont> {
    private FontDialog dialog;

    private void update(Node anchor) {
        if (dialog == null) {
            dialog = new FontDialog();
        }
    }

    @Override
    public void show(Node anchor, double screenX, double screenY) {
        update(anchor);
        CssFont currentValue = getCurrentValue();
        Optional<String> s = dialog.showAndWait(currentValue == null ? null : currentValue.getFamily());
        if (currentValue == null) {
            s.ifPresent(v -> applyValue(new CssFont(v, FontWeight.NORMAL, FontPosture.REGULAR, new CssSize(13))));
        } else {
            s.ifPresent(v -> applyValue(new CssFont(v, FontWeight.NORMAL, FontPosture.REGULAR, currentValue.getSize())));
        }
    }
}
