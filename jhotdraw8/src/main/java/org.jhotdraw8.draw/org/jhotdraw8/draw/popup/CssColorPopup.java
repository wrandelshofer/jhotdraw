/*
 * @(#)CssColorPopup.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.popup;

import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.util.Resources;

import java.util.function.BiConsumer;

public class CssColorPopup {
    private CssColorDialog dialog;
    private ContextMenu contextMenu;
    private MenuItem noneItem;
    private ColorPicker colorPicker;
    private BiConsumer<Boolean, CssColor> callback;

    @NonNull
    private ObjectProperty<CssColor> currentColor = new SimpleObjectProperty<>(CssColor.WHITE);

    public CssColorPopup() {
        Resources labels = DrawLabels.getResources();
        contextMenu = new ContextMenu();

        colorPicker = new ColorPicker();
        MenuItem colorPickerItem = new MenuItem(null, colorPicker);
        contextMenu.getItems().add(colorPickerItem);
        colorPicker.setOnAction(event -> callback.accept(true, CssColor.ofColor(colorPicker.getValue())));

        MenuItem unsetItem = new MenuItem();
        unsetItem.setOnAction(e -> callback.accept(false, null));
        noneItem = new MenuItem();
        noneItem.setOnAction(e -> callback.accept(true, null));
        labels.configureMenuItem(unsetItem, "value.unset");
        labels.configureMenuItem(noneItem, "value.none");
        contextMenu.getItems().addAll(
                new SeparatorMenuItem(),
                unsetItem, noneItem
        );

        CustomBinding.bindBidirectionalAndConvert(
                colorPicker.valueProperty(),
                currentColor,
                CssColor::ofColor,
                CssColor::toColor);
    }


    public BiConsumer<Boolean, CssColor> getCallback() {
        return callback;
    }

    public void setCallback(BiConsumer<Boolean, CssColor> callback) {
        this.callback = callback;
    }

    public CssColor getCurrentColor() {
        return currentColor.get();
    }

    @Nullable
    public ObjectProperty<CssColor> currentColorProperty() {
        return currentColor;
    }

    public void setCurrentColor(CssColor currentColor) {
        this.currentColor.set(currentColor);
    }


    public void show(@NonNull Node anchor, double screenX, double screenY) {
        contextMenu.show(anchor, screenX, screenY);
    }

}
