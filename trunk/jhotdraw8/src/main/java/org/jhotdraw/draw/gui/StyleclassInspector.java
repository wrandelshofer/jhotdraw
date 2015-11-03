/* @(#)StyleclassInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableMap;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.model.DrawingModelEvent;
import org.jhotdraw.text.CssPaintConverter;
import org.jhotdraw.text.StringConverterConverterWrapper;
import org.jhotdraw.text.XmlDoubleConverter;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class StyleclassInspector extends AbstractDrawingInspector {

    @FXML
    private TextField backgroundColorField;

    @FXML
    private TextField heightField;

    @FXML
    private ColorPicker backgroundColorPicker;

    @FXML
    private TextField widthField;

    @FXML
    private TextField opacityField;


    private Property<Double> widthProperty;
    private Property<Double> heightProperty;
    private Property<Paint> backgroundProperty;
    
    private InvalidationListener drawingUpdater = o->onDrawingPropertyValueChanged();
    
    public StyleclassInspector() {
        this(LayersInspector.class.getResource("StyleclassInspector.fxml"));
    }

    public StyleclassInspector(URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        FXMLLoader loader = new FXMLLoader();
        loader.setResources(Resources.getBundle("org.jhotdraw.draw.gui.Labels"));
        loader.setController(this);

        try (InputStream in = fxmlUrl.openStream()) {
            setCenter(loader.load(in));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }

    protected void onDrawingChanged(Drawing oldValue, Drawing newValue) {
        if (widthProperty != null) {
          //  widthField.textProperty().unbindBidirectional(widthProperty);
            widthProperty.removeListener(drawingUpdater);
        }
        if (heightProperty != null) {
          //  heightField.textProperty().unbindBidirectional(heightProperty);
            heightProperty.removeListener(drawingUpdater);
        }
        if (backgroundProperty != null) {
          //  backgroundColorField.textProperty().unbindBidirectional(backgroundProperty);
            backgroundProperty.removeListener(drawingUpdater);
        }
        widthProperty = null;
        heightProperty = null;
        backgroundProperty = null;
        if (newValue != null) {
            widthProperty = Drawing.WIDTH.propertyAt(newValue.propertiesProperty());
            heightProperty = Drawing.HEIGHT.propertyAt(newValue.propertiesProperty());
            backgroundProperty = Drawing.BACKGROUND.propertyAt(newValue.propertiesProperty());
            widthProperty.addListener(drawingUpdater);
            heightProperty.addListener(drawingUpdater);
            backgroundProperty.addListener(drawingUpdater);

          //  widthField.textProperty().bindBidirectional(widthProperty, new StringConverterConverterWrapper<>(new XmlDoubleConverter()));
           // heightField.textProperty().bindBidirectional(heightProperty, new StringConverterConverterWrapper<>(new XmlDoubleConverter()));
          //  backgroundColorField.textProperty().bindBidirectional(backgroundProperty, new StringConverterConverterWrapper<>(new CssPaintConverter()));
            @SuppressWarnings("unchecked")
            Property<Color> colorProperty=(Property<Color>)(Property<?>)backgroundProperty;
            backgroundColorPicker.valueProperty().bindBidirectional(colorProperty);
        }
    }

    private void onDrawingPropertyValueChanged() {
        drawingView.getModel().fire(DrawingModelEvent.nodeInvalidated(drawingView.getModel(), drawingView.getDrawing()));
    }

}
