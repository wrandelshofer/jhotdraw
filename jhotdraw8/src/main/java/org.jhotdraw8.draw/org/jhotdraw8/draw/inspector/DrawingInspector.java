/*
 * @(#)DrawingInspector.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.text.StringConverterAdapter;
import org.jhotdraw8.tree.TreeModelEvent;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 */
public class DrawingInspector extends AbstractDrawingInspector {

    @FXML
    private TextField backgroundColorField;

    @FXML
    private ColorPicker backgroundColorPicker;

    private @NonNull Property<CssColor> myBackgroundProperty = new SimpleObjectProperty<>();
    private @Nullable Property<CssColor> boundBackgroundProperty;

    private @NonNull ChangeListener<CssSize> sizeCommitHandler = (o, oldv, newv) -> commitEdits();
    private @NonNull ChangeListener<CssColor> colorCommitHandler = (o, oldv, newv) -> commitEdits();
    @FXML
    private TextField xField;
    @FXML
    private TextField yField;
    @FXML
    private TextField heightField;
    private @Nullable Property<CssSize> xProperty;
    private @Nullable Property<CssSize> yProperty;
    private @Nullable Property<CssSize> heightProperty;

    private Node node;
    @FXML
    private TextField widthField;
    private @Nullable Property<CssSize> widthProperty;

    public DrawingInspector() {
        this(LayersInspector.class.getResource("DrawingInspector.fxml"));
    }

    public DrawingInspector(@NonNull URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void commitEdits() {
        DrawingView subject = getSubject();
        subject.getModel().fireTreeModelEvent(TreeModelEvent.nodeChanged(subject.getModel(), subject.getDrawing()));
    }

    @Override
    public Node getNode() {
        return node;
    }

    private void init(@NonNull URL fxmlUrl) {
        // We must use invoke and wait here, because we instantiate Tooltips
        // which immediately instanciate a Window and a Scene.
        PlatformUtil.invokeAndWait(() -> {
            FXMLLoader loader = new FXMLLoader();
            loader.setResources(InspectorLabels.getResources().asResourceBundle());
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

        });


    }

    @Override
    protected void onDrawingChanged(ObservableValue<? extends Drawing> observable, @Nullable Drawing oldValue, @Nullable Drawing newValue) {
        if (widthProperty != null) {
            widthField.textProperty().unbindBidirectional(widthProperty);
            widthProperty.removeListener(sizeCommitHandler);
        }
        if (heightProperty != null) {
            heightField.textProperty().unbindBidirectional(heightProperty);
            heightProperty.removeListener(sizeCommitHandler);
        }
        if (xProperty != null) {
            xField.textProperty().unbindBidirectional(xProperty);
            xProperty.removeListener(sizeCommitHandler);
        }
        if (yProperty != null) {
            yField.textProperty().unbindBidirectional(yProperty);
            yProperty.removeListener(sizeCommitHandler);
        }
        xProperty = null;
        yProperty = null;
        widthProperty = null;
        heightProperty = null;
        if (oldValue != null) {
            myBackgroundProperty.unbindBidirectional(boundBackgroundProperty);
            myBackgroundProperty.removeListener(colorCommitHandler);
            boundBackgroundProperty = null;
        }
        if (newValue != null) {
            xProperty = Drawing.X.propertyAt(newValue.getProperties());
            yProperty = Drawing.Y.propertyAt(newValue.getProperties());
            widthProperty = Drawing.WIDTH.propertyAt(newValue.getProperties());
            heightProperty = Drawing.HEIGHT.propertyAt(newValue.getProperties());
            boundBackgroundProperty = Drawing.BACKGROUND.propertyAt(newValue.getProperties());
            xProperty.addListener(sizeCommitHandler);
            yProperty.addListener(sizeCommitHandler);
            widthProperty.addListener(sizeCommitHandler);
            heightProperty.addListener(sizeCommitHandler);
            myBackgroundProperty.bindBidirectional(boundBackgroundProperty);
            myBackgroundProperty.addListener((ChangeListener<? super CssColor>) colorCommitHandler);

            // FIXME binding to figure properties bypasses the DrawingModel!
            xField.textProperty().bindBidirectional(xProperty, new StringConverterAdapter<>(new CssSizeConverter(false)));
            yField.textProperty().bindBidirectional(yProperty, new StringConverterAdapter<>(new CssSizeConverter(false)));
            widthField.textProperty().bindBidirectional(widthProperty, new StringConverterAdapter<>(new CssSizeConverter(false)));
            heightField.textProperty().bindBidirectional(heightProperty, new StringConverterAdapter<>(new CssSizeConverter(false)));
        }
    }

}
