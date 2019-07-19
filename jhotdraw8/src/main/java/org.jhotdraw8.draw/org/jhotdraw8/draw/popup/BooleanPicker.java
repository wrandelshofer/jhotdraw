package org.jhotdraw8.draw.popup;

import javafx.event.ActionEvent;
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
public class BooleanPicker extends AbstractPicker<Boolean> {
    private ContextMenu contextMenu;
    private MenuItem initialItem;
    private MenuItem noneItem;
    private MenuItem trueItem;
    private MenuItem falseItemm;

    public BooleanPicker() {

    }


    private void init() {
        Resources labels = DrawLabels.getResources();
        contextMenu = new ContextMenu();
        initialItem = new MenuItem();
        noneItem = new MenuItem();
        trueItem = new MenuItem();
        falseItemm = new MenuItem();
        initialItem.setOnAction(this::setToInitialize);
        noneItem.setOnAction(this::setToNone);
        trueItem.setOnAction(this::setToTrue);
        falseItemm.setOnAction(this::setToFalse);
        labels.configureMenuItem(initialItem, "value.initial");
        labels.configureMenuItem(noneItem, "value.none");
        labels.configureMenuItem(trueItem, "value.true");
        labels.configureMenuItem(falseItemm, "value.false");
        contextMenu.getItems().addAll(
                trueItem, falseItemm,
                new SeparatorMenuItem(),
                initialItem, noneItem
        );
    }

    private void update() {
        if (contextMenu == null) {
            init();
        }
        Converter<Boolean> converter = getMapAccessor().getConverter();
        if (converter instanceof CssConverter<?>) {
            CssConverter<?> cssConverter = (CssConverter<?>) converter;
            noneItem.setVisible(cssConverter.isNullable());
        }
    }

    private void setToNone(ActionEvent actionEvent) {
        applyValue(null);
    }
    private void setToTrue(ActionEvent actionEvent) {
        applyValue(true);
    }

    private void setToFalse(ActionEvent actionEvent) {
        applyValue(false);
    }


    private void setToInitialize(ActionEvent actionEvent) {
        applyInitialValue();

    }


    public void show(Node anchor, double screenX, double screenY) {
        update();
        contextMenu.show(anchor, screenX, screenY);
    }
}
