/* @(#)LayerCell.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.figure.HideableFigure;
import org.jhotdraw.draw.figure.LockableFigure;
import org.jhotdraw.draw.figure.StyleableFigure;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class.
 * 
 * XXX all keys must be customizable
 *
 * @author werni
 */
public class LayerCell extends ListCell<Figure> {

    private HBox node;

    @FXML
    private CheckBox visibleCheckBox;

    @FXML
    private CheckBox lockedCheckBox;

    @FXML
    private Label selectionLabel;

    private DrawingView drawingView;

    private boolean isUpdating;

    private Figure item;

    private TextField textField;

    public LayerCell(DrawingView drawingView) {
        this(LayersInspector.class.getResource("LayerCell.fxml"), drawingView);
    }

    public LayerCell(URL fxmlUrl, DrawingView drawingView) {
        this.drawingView = drawingView;
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setResources(Resources.getBundle("org.jhotdraw.draw.gui.Labels"));

        try (InputStream in = fxmlUrl.openStream()) {
            node = loader.load(in);
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        visibleCheckBox.selectedProperty().addListener(o -> commitLayerVisible());
        lockedCheckBox.selectedProperty().addListener(o -> commitLayerLocked());
    }

    @Override
    protected void updateItem(Figure item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            this.item = null;
        } else {
            isUpdating = true;
            this.item = item;
            if (isEditing()) {
                if (textField == null) {
                    textField = createTextField();
                }
                if (textField != null) {
                    textField.setText(getItemText());
                }
                setText(null);

                if (textField.getParent() == null) {
                    node.getChildren().add(textField);
                }
            } else {
                setText(getItemText());
                if (textField!=null&&textField.getParent() != null) {
                    node.getChildren().remove(textField);
                }
            }
            setGraphic(node);
            Integer count = item.get(LayersInspector.SELECTION_COUNT);
            selectionLabel.setText(count == null ? "" : "(" + count.toString() + ")");

            visibleCheckBox.setSelected(item.get(HideableFigure.VISIBLE));
            lockedCheckBox.setSelected(item.get(LockableFigure.LOCKED));
            isUpdating = false;
        }
    }

    public static Callback<ListView<Figure>, ListCell<Figure>> forListView(DrawingView drawingView) {
        return list -> new LayerCell(drawingView);
    }

    private void commitLayerVisible() {
        if (!isUpdating) {
            drawingView.getModel().set(item, HideableFigure.VISIBLE, visibleCheckBox.isSelected());
        }
    }

    private void commitLayerLocked() {
        if (!isUpdating) {
            drawingView.getModel().set(item, LockableFigure.LOCKED, lockedCheckBox.isSelected());
        }
    }

    public Label getSelectionLabel() {
        return selectionLabel;
    }

    /**
     * Returns the {@link StringConverter} used in this cell.
     * @return the converter
     */
    public final StringConverter<Figure> getConverter() {
        return null;//converterProperty().get(); 
    }

    @Override
    public void startEdit() {
        if (!isEditable() || !getListView().isEditable()) {
            return;
        }
        super.startEdit();
        updateItem(getItem(),false);
        textField.selectAll();
        textField.requestFocus();
    }

    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItemText());
        if (textField != null) {
            node.getChildren().remove(textField);
        }
    }

    private String getItemText() {
        return getItem() == null ? "" : getItem().get(StyleableFigure.STYLE_ID);
    }

    private TextField createTextField() {
        final TextField textField = new TextField();

        // Use onAction here rather than onKeyReleased (with check for Enter),
        // as otherwise we encounter RT-34685
        textField.setOnAction(event -> {
            commitEdit(item);
            event.consume();
        });
        textField.setOnKeyReleased(t -> {
            if (t.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
                t.consume();
            }
        });
        return textField;
    }

    @Override
    public void commitEdit(Figure newValue) {
        if (textField != null && isEditing()) {
            drawingView.getModel().set(
                    item, StyleableFigure.STYLE_ID, textField.getText());
        }
        super.commitEdit(newValue);
    }
}
