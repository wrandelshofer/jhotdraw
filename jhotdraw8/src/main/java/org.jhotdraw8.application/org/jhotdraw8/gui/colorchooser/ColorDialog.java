package org.jhotdraw8.gui.colorchooser;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.EventHandler;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;

public class ColorDialog extends HBox {
    private final Stage dialog = new Stage();
    private Runnable onSave;
    private Runnable onUse;
    private Runnable onCancel;
    private ObjectProperty<ColorSpaceColor> currentColor = new SimpleObjectProperty<>(ColorSpaceColor.WHITE);
    private ObjectProperty<ColorSpaceColor> customColor = new SimpleObjectProperty<>(ColorSpaceColor.TRANSPARENT);

    public ColorDialog() {
    }

    public ColorDialog(Window owner) {
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

    public ColorSpaceColor getCurrentColor() {
        return currentColor.get();
    }

    public ObjectProperty<ColorSpaceColor> currentColorProperty() {
        return currentColor;
    }

    public void setCurrentColor(ColorSpaceColor currentColor) {
        this.currentColor.set(currentColor);
    }

    public ColorSpaceColor getCustomColor() {
        return customColor.get();
    }

    public ObjectProperty<ColorSpaceColor> customColorProperty() {
        return customColor;
    }

    public void setCustomColor(ColorSpaceColor customColor) {
        this.customColor.set(customColor);
    }

    public void show() {
        dialog.show();
    }
}
