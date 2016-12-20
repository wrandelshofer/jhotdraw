/* @(#)GridInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.inspector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.prefs.Preferences;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.constrain.GridConstrainer;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.text.StringConverterConverterWrapper;
import org.jhotdraw8.text.XmlNumberConverter;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class GridInspector extends AbstractDrawingViewInspector {

    @FXML
    private TextField heightField;

    @FXML
    private TextField widthField;
    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    @FXML
    private TextField majorXField;
    @FXML
    private TextField majorYField;

    @FXML
    private TextField angleField;

    @FXML
    private CheckBox snapToGridCheckBox;
    @FXML
    private CheckBox drawGridCheckBox;

    private Node node;

    public GridInspector() {
        this(LayersInspector.class.getResource("GridInspector.fxml"));
    }

    public GridInspector(URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene. 
        PlatformUtil.invokeAndWait(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(Labels.getBundle());
            loader.setController(this);

            try (InputStream in = fxmlUrl.openStream()) {
                node = loader.load(in);
            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        });

        Preferences prefs = Preferences.userNodeForPackage(GridInspector.class);
        snapToGridCheckBox.setSelected(prefs.getBoolean("snapToGrid", true));
        snapToGridCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("snapToGrid", newValue));
        drawGridCheckBox.setSelected(prefs.getBoolean("drawGrid", true));
        drawGridCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("drawGrid", newValue));
    }
    private GridConstrainer gridConstrainer;

    @Override
    protected void onDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {
        Preferences prefs = Preferences.userNodeForPackage(GridInspector.class);
        ChangeListener<Number> prefsGridX = (o, oldv, newv) -> prefs.putDouble("gridX", newv.doubleValue());
        ChangeListener<Number> prefsGridY = (o, oldv, newv) -> prefs.putDouble("gridY", newv.doubleValue());
        ChangeListener<Number> prefsGridWidth = (o, oldv, newv) -> prefs.putDouble("gridWidth", newv.doubleValue());
        ChangeListener<Number> prefsGridAngle = (o, oldv, newv) -> prefs.putDouble("gridAngle", newv.doubleValue());
        ChangeListener<Number> prefsGridHeight = (o, oldv, newv) -> prefs.putDouble("gridHeight", newv.doubleValue());
        ChangeListener<Number> prefsGridMajorX = (o, oldv, newv) -> prefs.putInt("gridMajorX", newv.intValue());
        ChangeListener<Number> prefsGridMajorY = (o, oldv, newv) -> prefs.putInt("gridMajorY", newv.intValue());

        if (oldValue != null) {
            heightField.textProperty().unbind();
            widthField.textProperty().unbind();
            xField.textProperty().unbind();
            yField.textProperty().unbind();
            majorXField.textProperty().unbind();
            majorYField.textProperty().unbind();
            angleField.textProperty().unbind();
            drawGridCheckBox.selectedProperty().unbind();
            snapToGridCheckBox.selectedProperty().unbind();
        }
        if (gridConstrainer != null) {
            gridConstrainer.xProperty().removeListener(prefsGridX);
            gridConstrainer.yProperty().removeListener(prefsGridY);
            gridConstrainer.widthProperty().removeListener(prefsGridWidth);
            gridConstrainer.heightProperty().removeListener(prefsGridHeight);
            gridConstrainer.angleProperty().removeListener(prefsGridAngle);
            gridConstrainer.majorXProperty().removeListener(prefsGridMajorX);
            gridConstrainer.majorYProperty().removeListener(prefsGridMajorY);
        }
        if (newValue != null) {
            if (false && (newValue.getConstrainer() instanceof GridConstrainer)) {
                gridConstrainer = (GridConstrainer) newValue.getConstrainer();
            } else {

                gridConstrainer = new GridConstrainer(prefs.getDouble("gridX", 0), prefs.getDouble("gridY", 0), prefs.getDouble("gridWidth", 10), prefs.getDouble("gridHeight", 10),
                        prefs.getDouble("gridAngle", 11.25), prefs.getInt("gridMajorX", 5), prefs.getInt("gridMajorY", 5));
                newValue.setConstrainer(gridConstrainer);
            }
            StringConverter<Number> cc
                    = new StringConverterConverterWrapper<>(new XmlNumberConverter());
            heightField.textProperty().bindBidirectional(gridConstrainer.heightProperty(), cc);
            widthField.textProperty().bindBidirectional(gridConstrainer.widthProperty(), cc);
            xField.textProperty().bindBidirectional(gridConstrainer.xProperty(), cc);
            yField.textProperty().bindBidirectional(gridConstrainer.yProperty(), cc);
            majorXField.textProperty().bindBidirectional(gridConstrainer.majorXProperty(), cc);
            majorYField.textProperty().bindBidirectional(gridConstrainer.majorYProperty(), cc);
            angleField.textProperty().bindBidirectional(gridConstrainer.angleProperty(), cc);
            gridConstrainer.drawGridProperty().set(drawGridCheckBox.isSelected());
            drawGridCheckBox.selectedProperty().bindBidirectional(gridConstrainer.drawGridProperty());
            gridConstrainer.snapToGridProperty().set(snapToGridCheckBox.isSelected());
            snapToGridCheckBox.selectedProperty().bindBidirectional(gridConstrainer.snapToGridProperty());

            if (gridConstrainer != null) {
                gridConstrainer.xProperty().addListener(prefsGridX);
                gridConstrainer.yProperty().addListener(prefsGridY);
                gridConstrainer.widthProperty().addListener(prefsGridWidth);
                gridConstrainer.heightProperty().addListener(prefsGridHeight);
                gridConstrainer.angleProperty().addListener(prefsGridAngle);
                gridConstrainer.majorXProperty().addListener(prefsGridMajorX);
                gridConstrainer.majorYProperty().addListener(prefsGridMajorY);
            }
        }
    }

    @Override
    public Node getNode() {
        return node;
    }

}
