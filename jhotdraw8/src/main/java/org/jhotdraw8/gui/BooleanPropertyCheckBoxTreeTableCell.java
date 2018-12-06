/* @(#)BooleanPropertyCheckBoxTreeTableCell.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui;

import javafx.beans.Observable;
import javafx.beans.property.Property;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TreeTableCell;
import javafx.scene.control.TreeTableColumn;
import javafx.util.Callback;
import javax.annotation.Nullable;

/**
 * BooleanPropertyCheckBoxTreeTableCell.
 *
 * @author Werner Randelshofer
 * @version $Id$
 * @param <S> the row type
 */
public class BooleanPropertyCheckBoxTreeTableCell<S> extends TreeTableCell<S, Boolean> {

    private final CheckBox checkBox = new CheckBox();

    public BooleanPropertyCheckBoxTreeTableCell() {
        checkBox.selectedProperty().addListener(this::commitSelectionChange);
    }

    @Override
    public void updateItem(@Nullable Boolean item, boolean empty) {
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
        return list -> new BooleanPropertyCheckBoxTreeTableCell<>();
    }

    private void commitSelectionChange(Observable o) {
        Property<Boolean> p = (Property<Boolean>) getTableColumn().getCellObservableValue(getIndex());
        p.setValue(checkBox.isSelected());
    }
}
