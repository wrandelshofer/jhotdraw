/* @(#)BooleanPropertyCheckBoxTreeTableCell.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.gui;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import org.jhotdraw8.beans.PropertyBean;
import org.jhotdraw8.collection.Key;

/**
 * BooleanPropertyCheckBoxTreeTableCell.
 *
 * @author Werner Randelshofer
 * @version $$Id: BooleanPropertyCheckBoxTreeTableCell.java 1235 2016-12-19
 * 23:35:25Z rawcoder $$
 * @param <S> the row typei
 */
public class BooleanPropertyCheckBoxTreeTableCell<S> extends TreeTableCell<S, Boolean> {

    private final CheckBox checkBox = new CheckBox();

    public BooleanPropertyCheckBoxTreeTableCell() {
        checkBox.selectedProperty().addListener(this::commitSelectionChange);
    }

    @Override
    public void updateItem(Boolean item, boolean empty) {
        super.updateItem(item, empty);
        if (empty || item == null) {
            setText(null);
            setGraphic(null);
        } else {
            setGraphic(checkBox);
            checkBox.setSelected(item);
        }
    }

    public static <S> Callback<TreeTableColumn<S, Boolean>, TreeTableCell<S, Boolean>> forTreeTableColumn() {
        return list -> new BooleanPropertyCheckBoxTreeTableCell<S>();
    }

    private void commitSelectionChange(Observable o) {
        Property<Boolean> p = (Property<Boolean>) getTableColumn().getCellObservableValue(getIndex());
        p.setValue(checkBox.isSelected());
    }
}
