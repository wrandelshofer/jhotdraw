package org.jhotdraw8.draw.popup;

import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.SeparatorMenuItem;
import org.jhotdraw8.css.text.CssConverter;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.util.Resources;

/**
 * Picker for boolean values.
 */
public class EnumPicker<T extends Enum<T>> extends AbstractPicker<T> {
    private ContextMenu contextMenu;
    private MenuItem noneItem;

    public EnumPicker() {

    }


    private void init() {
        Resources labels = DrawLabels.getResources();
        contextMenu = new ContextMenu();
        Converter<T> converter = getMapAccessor().getConverter();
        for (T enumConstant : getMapAccessor().getValueType().getEnumConstants()) {
            String s = converter.toString(enumConstant);
            MenuItem valueItem = new MenuItem(s);
            valueItem.setOnAction(e -> applyValue(enumConstant));
            contextMenu.getItems().add(valueItem);
        }

        MenuItem initialItem = new MenuItem();
        initialItem.setOnAction(e -> applyInitialValue());
        noneItem = new MenuItem();
        noneItem.setOnAction(e -> applyValue(null));
        labels.configureMenuItem(initialItem, "value.initial");
        labels.configureMenuItem(noneItem, "value.none");
        contextMenu.getItems().addAll(
                new SeparatorMenuItem(),
                initialItem, noneItem
        );
    }

    private void update() {
        if (contextMenu == null) {
            init();
        }
        Converter<T> converter = getMapAccessor().getConverter();
        if (converter instanceof CssConverter<?>) {
            CssConverter<?> cssConverter = (CssConverter<?>) converter;
            noneItem.setVisible(cssConverter.isNullable());
        }
    }

    public void show(Node anchor, double screenX, double screenY) {
        update();
        contextMenu.show(anchor, screenX, screenY);
    }
}
