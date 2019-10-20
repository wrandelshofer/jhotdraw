package org.jhotdraw8.draw.popup;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import org.jhotdraw8.css.CssColor;

public class CssColorDialog extends HBox {
    private final Stage dialog = new Stage();
    private Runnable onSave;
    private Runnable onUse;
    private Runnable onCancel;
    private ObjectProperty<CssColor> currentColor = new SimpleObjectProperty<>(CssColor.WHITE);
    private ObjectProperty<CssColor> customColor = new SimpleObjectProperty<>(CssColor.TRANSPARENT);

    public CssColorDialog() {
    }

    public CssColorDialog(Window owner) {
        if (owner != null) {
            dialog.initOwner(owner);
        }
        dialog.initModality(Modality.WINDOW_MODAL);
        dialog.initStyle(StageStyle.UTILITY);
        dialog.setResizable(false);
        dialog.addEventHandler(KeyEvent.ANY, keyEventListener);
    }

    private final EventHandler<KeyEvent> keyEventListener = e -> {
        switch (e.getCode()) {
            case ESCAPE:
                dialog.setScene(null);
                dialog.close();
            default:
                break;
        }
    };

    public Runnable getOnSave() {
        return onSave;
    }

    public void setOnSave(Runnable onSave) {
        this.onSave = onSave;
    }

    public Runnable getOnUse() {
        return onUse;
    }

    public void setOnUse(Runnable onUse) {
        this.onUse = onUse;
    }

    public Runnable getOnCancel() {
        return onCancel;
    }

    public void setOnCancel(Runnable onCancel) {
        this.onCancel = onCancel;
    }

    public CssColor getCurrentColor() {
        return currentColor.get();
    }

    public ObjectProperty<CssColor> currentColorProperty() {
        return currentColor;
    }

    public void setCurrentColor(CssColor currentColor) {
        this.currentColor.set(currentColor);
    }

    public CssColor getCustomColor() {
        return customColor.get();
    }

    public ObjectProperty<CssColor> customColorProperty() {
        return customColor;
    }

    public void setCustomColor(CssColor customColor) {
        this.customColor.set(customColor);
    }

    public void show() {
        dialog.show();
    }
}
