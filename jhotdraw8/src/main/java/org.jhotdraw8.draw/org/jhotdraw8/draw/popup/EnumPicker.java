package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.util.Resources;

import java.util.function.BiConsumer;

/**
 * Picker for boolean values.
 */
public class EnumPicker<T extends Enum<T>> extends AbstractPicker<T> {
    private ContextMenu contextMenu;
    private MenuItem noneItem;
    private BiConsumer<Boolean, T> callback;
    private Converter<T> converter;
    private Class<T> enumClazz;

    public EnumPicker(Class<T> enumClazz, Converter<T> converter) {
        this.enumClazz = enumClazz;
        this.converter = converter;
    }


    private void init() {
        Resources labels = DrawLabels.getResources();
        contextMenu = new ContextMenu();
        for (T enumConstant : enumClazz.getEnumConstants()) {
            String s = converter.toString(enumConstant);
            MenuItem valueItem = new MenuItem(s);
            valueItem.setOnAction(e -> callback.accept(true, enumConstant));
            contextMenu.getItems().add(valueItem);
        }

        MenuItem initialItem = new MenuItem();
        initialItem.setOnAction(e -> callback.accept(false, null));
        noneItem = new MenuItem();
        noneItem.setOnAction(e -> callback.accept(true, null));
        labels.configureMenuItem(initialItem, "value.initial");
        labels.configureMenuItem(noneItem, "value.none");
        contextMenu.getItems().addAll(
                new SeparatorMenuItem(),
                initialItem, noneItem
        );
    }

    private void update(T initialValue) {
        if (contextMenu == null) {
            init();
        }
        if (converter instanceof CssConverter<?>) {
            CssConverter<?> cssConverter = (CssConverter<?>) converter;
            noneItem.setVisible(cssConverter.isNullable());
        }
    }

    public void show(Node anchor, double screenX, double screenY,
                     T initialValue, BiConsumer<Boolean, T> callback) {
        update(initialValue);
        this.callback = callback;
        contextMenu.show(anchor, screenX, screenY);
    }
}
