/* @(#)LayerCell.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.Property;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListCell;
import javafx.scene.control.ListView;
import javafx.scene.control.cell.CheckBoxListCell;
import javafx.scene.layout.BorderPane;
import javafx.util.Callback;
import javafx.util.StringConverter;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class LayerCell extends ListCell<Figure> {

    private Node node;

    @FXML
    private CheckBox visibleCheckBox;

    @FXML
    private CheckBox disabledCheckBox;

    @FXML
    private Label selectionLabel;

    private DrawingView drawingView;

    private boolean isUpdating;

    private Figure item;

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

        visibleCheckBox.selectedProperty().addListener(o -> updateLayerVisible());
        disabledCheckBox.selectedProperty().addListener(o -> updateLayerLocked());
    }

    protected void updateItem(Figure item, boolean empty) {
        super.updateItem(item, empty);

        if (empty || item == null) {
            setText(null);
            setGraphic(null);
            this.item = null;
        } else {
            this.item = item;
            isUpdating = true;
            setText(item.get(Figure.STYLE_ID));
            //idLabel.setText(item.get(Figure.STYLE_ID));
            setGraphic(node);
            Integer count=item.get(LayersInspector.SELECTION_COUNT);
            selectionLabel.setText(count==null?"":"("+count.toString()+")");
            
            // FIXME - we must listen to these properties!
            

            visibleCheckBox.setSelected(item.get(Figure.VISIBLE));
            disabledCheckBox.setSelected(item.get(Figure.DISABLED));
            isUpdating = false;
        }
    }

    public static Callback<ListView<Figure>, ListCell<Figure>> forListView(DrawingView drawingView) {
        return list -> new LayerCell(drawingView);
    }

    private void updateLayerVisible() {
        if (!isUpdating) {
            drawingView.getModel().set(item, Figure.VISIBLE, visibleCheckBox.isSelected());
        }
    }

    private void updateLayerLocked() {
        if (!isUpdating) {
            drawingView.getModel().set(item, Figure.DISABLED, disabledCheckBox.isSelected());
        }
    }
    
    public Label getSelectionLabel() {return selectionLabel;}
}
