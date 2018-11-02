/* @(#)DrawingInspector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.text.StringConverterAdapter;
import org.jhotdraw8.xml.text.XmlDoubleConverter;
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
    private ColorPicker backgroundColorPicker;

    @Nonnull
    private Property<CssColor> myBackgroundProperty=new SimpleObjectProperty<>();
    @Nullable
    private Property<CssColor> boundBackgroundProperty;

    @Nonnull
    private InvalidationListener commitHandler = o -> commitEdits();
    @FXML
    private TextField heightField;
    @Nullable
    private Property<Double> heightProperty;

    private Node node;
    @FXML
    private TextField widthField;
    @Nullable
    private Property<Double> widthProperty;

    public DrawingInspector() {
        this(LayersInspector.class.getResource("DrawingInspector.fxml"));
    }

    public DrawingInspector(@Nonnull URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void commitEdits() {
        drawingView.getModel().fireTreeModelEvent(TreeModelEvent.nodeInvalidated(drawingView.getModel(), drawingView.getDrawing()));
    }

    @Override
    public Node getNode() {
        return node;
    }

    private void init(@Nonnull URL fxmlUrl) {
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
            
            CustomBinding.bindBidirectionalAndConvert(//
                    myBackgroundProperty,//
                    backgroundColorPicker.valueProperty(),//
                    (CssColor c) -> c == null ? null : c.getColor(), //
                    CssColor::new//
            );
            backgroundColorField.textProperty().bindBidirectional(myBackgroundProperty,
                    new StringConverterAdapter<>(new CssColorConverter(false)));
            myBackgroundProperty.addListener(commitHandler);
            
        });
        
        
    }

    @Override
    protected void onDrawingChanged(@Nullable Drawing oldValue, @Nullable Drawing newValue) {
        if (widthProperty != null) {
            widthField.textProperty().unbindBidirectional(widthProperty);
            widthProperty.removeListener(commitHandler);
        }
        if (heightProperty != null) {
            heightField.textProperty().unbindBidirectional(heightProperty);
            heightProperty.removeListener(commitHandler);
        }
        widthProperty = null;
        heightProperty = null;
        if (oldValue!=null) {
            myBackgroundProperty.unbindBidirectional(boundBackgroundProperty);
            boundBackgroundProperty=null;
        }
        if (newValue != null) {
            widthProperty = Drawing.WIDTH.propertyAt(newValue.getProperties());
            heightProperty = Drawing.HEIGHT.propertyAt(newValue.getProperties());
            boundBackgroundProperty = Drawing.BACKGROUND.propertyAt(newValue.getProperties());
            widthProperty.addListener(commitHandler);
            heightProperty.addListener(commitHandler);
            myBackgroundProperty.bindBidirectional(boundBackgroundProperty);

            // FIXME binding to figure properties bypasses the DrawingModel!
            widthField.textProperty().bindBidirectional(widthProperty, new StringConverterAdapter<>(new XmlDoubleConverter()));
            heightField.textProperty().bindBidirectional(heightProperty, new StringConverterAdapter<>(new XmlDoubleConverter()));

        }
    }

}
