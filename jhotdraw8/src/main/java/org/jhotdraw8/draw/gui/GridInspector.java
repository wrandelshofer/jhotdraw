/* @(#)GridInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.prefs.Preferences;
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
import org.jhotdraw8.util.Resources;

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
            loader.setResources(Resources.getBundle("org.jhotdraw8.draw.gui.Labels"));
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

    @Override
    protected void onDrawingViewChanged(DrawingView oldValue, DrawingView newValue) {
        if (oldValue != null) {
            heightField.textProperty().unbind();
            widthField.textProperty().unbind();
            xField.textProperty().unbind();
            yField.textProperty().unbind();
            angleField.textProperty().unbind();
            drawGridCheckBox.selectedProperty().unbind();
            snapToGridCheckBox.selectedProperty().unbind();
        }
        if (newValue != null) {
            GridConstrainer gc;
            if (newValue.getConstrainer() instanceof GridConstrainer) {
                gc = (GridConstrainer) newValue.getConstrainer();
            } else {
                gc = new GridConstrainer(0, 0, 10, 10, 45);
                newValue.setConstrainer(gc);
            }
            StringConverter<Number> cc
                    = new StringConverterConverterWrapper<>(new XmlNumberConverter());
            heightField.textProperty().bindBidirectional(gc.heightProperty(), cc);
            widthField.textProperty().bindBidirectional(gc.widthProperty(), cc);
            xField.textProperty().bindBidirectional(gc.xProperty(), cc);
            yField.textProperty().bindBidirectional(gc.yProperty(), cc);
            angleField.textProperty().bindBidirectional(gc.angleProperty(), cc);
            gc.drawGridProperty().set(drawGridCheckBox.isSelected());
            drawGridCheckBox.selectedProperty().bindBidirectional(gc.drawGridProperty());
            gc.snapToGridProperty().set(snapToGridCheckBox.isSelected());
            snapToGridCheckBox.selectedProperty().bindBidirectional(gc.snapToGridProperty());
        }
    }

    @Override
    public Node getNode() {
        return node;
    }

}
