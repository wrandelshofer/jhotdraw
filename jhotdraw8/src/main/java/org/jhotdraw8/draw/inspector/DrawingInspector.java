/* @(#)DrawingInspector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.text.CssColorConverter;
import org.jhotdraw8.text.StringConverterAdapter;
import org.jhotdraw8.text.XmlDoubleConverter;
import org.jhotdraw8.tree.TreeModelEvent;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DrawingInspector extends AbstractDrawingInspector {

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
    private Property<CssColor> backgroundProperty;

    private InvalidationListener commitHandler = o -> commitEdits();

    private Node node;

    public DrawingInspector() {
        this(LayersInspector.class.getResource("DrawingInspector.fxml"));
    }

    public DrawingInspector(URL fxmlUrl) {
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
            widthProperty = Drawing.WIDTH.propertyAt(newValue.getProperties());
            heightProperty = Drawing.HEIGHT.propertyAt(newValue.getProperties());
            backgroundProperty = Drawing.BACKGROUND.propertyAt(newValue.getProperties());
            widthProperty.addListener(commitHandler);
            heightProperty.addListener(commitHandler);
            backgroundProperty.addListener(commitHandler);

            // FIXME binding to figure properties bypasses the DrawingModel!
            widthField.textProperty().bindBidirectional(widthProperty, new StringConverterAdapter<>(new XmlDoubleConverter()));
            heightField.textProperty().bindBidirectional(heightProperty, new StringConverterAdapter<>(new XmlDoubleConverter()));
            backgroundColorField.textProperty().bindBidirectional(backgroundProperty, new StringConverterAdapter<>(new CssColorConverter(false)));

            CustomBinding.bindBidirectional(//
                    backgroundProperty,//
                    backgroundColorPicker.valueProperty(),//
                    (CssColor c) -> c == null ? null : c.getColor(), //
                    (Color c) -> new CssColor(c)//
            );
        }
    }

    private void commitEdits() {
        drawingView.getModel().fireTreeModelEvent(TreeModelEvent.nodeInvalidated(drawingView.getModel(), drawingView.getDrawing()));
    }

    @Override
    public Node getNode() {
        return node;
    }

}
