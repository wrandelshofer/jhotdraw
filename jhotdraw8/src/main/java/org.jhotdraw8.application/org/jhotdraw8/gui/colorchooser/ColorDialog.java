/*
 * @(#)ColorDialog.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
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
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;

public class ColorDialog extends HBox {
    private final Stage dialog = new Stage();
    private Runnable onSave;
    private Runnable onUse;
    private Runnable onCancel;
    private @NonNull ObjectProperty<ColorSpaceColor> currentColor = new SimpleObjectProperty<>(ColorSpaceColor.WHITE);
    private @NonNull ObjectProperty<ColorSpaceColor> customColor = new SimpleObjectProperty<>(ColorSpaceColor.TRANSPARENT);

    public ColorDialog() {
    }

    public ColorDialog(@Nullable Window owner) {
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
            break;
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

    public @NonNull ObjectProperty<ColorSpaceColor> currentColorProperty() {
        return currentColor;
    }

    public void setCurrentColor(ColorSpaceColor currentColor) {
        this.currentColor.set(currentColor);
    }

    public ColorSpaceColor getCustomColor() {
        return customColor.get();
    }

    public @NonNull ObjectProperty<ColorSpaceColor> customColorProperty() {
        return customColor;
    }

    public void setCustomColor(ColorSpaceColor customColor) {
        this.customColor.set(customColor);
    }

    public void show() {
        dialog.show();
    }
}
