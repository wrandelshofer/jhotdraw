/* @(#)CanvasInspector.java
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
import org.jhotdraw.gui.PlatformUtil;
import org.jhotdraw.text.CssPaintConverter;
import org.jhotdraw.text.FormatConverterWrapper;
import org.jhotdraw.text.StringConverterConverterWrapper;
import org.jhotdraw.text.XmlDoubleConverter;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class CanvasInspector extends AbstractDrawingInspector {

    @FXML
    private TextField backgroundColorField;

    @FXML
    private TextField heightField;

    @FXML
    private ColorPicker backgroundColorPicker;

    @FXML
    private TextField widthField;

    private Property<Double> widthProperty;
    private Property<Double> heightProperty;
    private Property<Paint> backgroundProperty;

    private InvalidationListener commitHandler = o -> commitEdits();

    public CanvasInspector() {
        this(LayersInspector.class.getResource("CanvasInspector.fxml"));
    }

    public CanvasInspector(URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene. 
        PlatformUtil.invokeAndWait(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(Resources.getBundle("org.jhotdraw.draw.gui.Labels"));
            loader.setController(this);

            try (InputStream in = fxmlUrl.openStream()) {
                setCenter(loader.load(in));
            } catch (IOException ex) {
                throw new InternalError(ex);
            }
        });
    }

    @Override
    protected void onDrawingChanged(Drawing oldValue, Drawing newValue) {
        if (widthProperty != null) {
            widthField.textProperty().unbindBidirectional(widthProperty);
            widthProperty.removeListener(commitHandler);
        }
        if (heightProperty != null) {
            heightField.textProperty().unbindBidirectional(heightProperty);
            heightProperty.removeListener(commitHandler);
        }
        if (backgroundProperty != null) {
            backgroundColorField.textProperty().unbindBidirectional(backgroundProperty);
            backgroundProperty.removeListener(commitHandler);
        }
        widthProperty = null;
        heightProperty = null;
        backgroundProperty = null;
        if (newValue != null) {
            widthProperty = Drawing.WIDTH.propertyAt(newValue.propertiesProperty());
            heightProperty = Drawing.HEIGHT.propertyAt(newValue.propertiesProperty());
            backgroundProperty = Drawing.BACKGROUND.propertyAt(newValue.propertiesProperty());
            widthProperty.addListener(commitHandler);
            heightProperty.addListener(commitHandler);
            backgroundProperty.addListener(commitHandler);

            // FIXME binding to figure properties bypasses the DrawingModel!
            widthField.textProperty().bindBidirectional(widthProperty, new StringConverterConverterWrapper(new XmlDoubleConverter()));
            heightField.textProperty().bindBidirectional(heightProperty, new StringConverterConverterWrapper(new XmlDoubleConverter()));
            backgroundColorField.textProperty().bindBidirectional(backgroundProperty, new StringConverterConverterWrapper(new CssPaintConverter()));
            @SuppressWarnings("unchecked")
            Property<Color> colorProperty = (Property<Color>) (Property<?>) backgroundProperty;
            backgroundColorPicker.valueProperty().bindBidirectional(colorProperty);
        }
    }

    private void commitEdits() {
        drawingView.getModel().fire(DrawingModelEvent.nodeInvalidated(drawingView.getModel(), drawingView.getDrawing()));
    }

}
