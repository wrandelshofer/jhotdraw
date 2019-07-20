/* @(#)HandlesInspector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.binding.Bindings;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.value.ObservableValue;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.converter.NumberStringConverter;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.beans.NonnullProperty;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.text.StringConverterAdapter;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ResourceBundle;

public class HandlesInspector extends AbstractDrawingViewInspector {

    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;

    @FXML // fx:id="handleColorField"
    private TextField handleColorField; // Value injected by FXMLLoader

    @FXML // fx:id="handleColorPicker"
    private ColorPicker handleColorPicker; // Value injected by FXMLLoader

    @FXML // fx:id="handleSizeField"
    private TextField handleSizeField; // Value injected by FXMLLoader

    @FXML // fx:id="handleSizeSlider"
    private Slider handleSizeSlider; // Value injected by FXMLLoader

    @FXML
    private TextField handleStrokeWidthField;

    @FXML
    private Slider handleStrokeWidthSlider;
    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert handleColorField != null : "fx:id=\"handleColorField\" was not injected.";
        assert handleColorPicker != null : "fx:id=\"handleColorPicker\" was not injected.";
        assert handleSizeField != null : "fx:id=\"handleSizeField\" was not injected.";
        assert handleSizeSlider != null : "fx:id=\"handleSizeSlider\" was not injected.";
        assert handleStrokeWidthField != null : "fx:id=\"handleStrokeWidthField\" was not injected: check your FXML file 'HandlesInspector.fxml'.";
        assert handleStrokeWidthSlider != null : "fx:id=\"handleStrokeWidthSlider\" was not injected: check your FXML file 'HandlesInspector.fxml'.";

        handleColorPicker.setValue(handleColorProperty.getValue().getColor());
        CustomBinding.bindBidirectionalAndConvert(//
                handleColorPicker.valueProperty(),//
                handleColorProperty,//
                CssColor::new,//
                (CssColor c) -> c == null ? null : c.getColor() //
        );
        handleColorField.textProperty().bindBidirectional(handleColorProperty, new StringConverterAdapter<>(
                new CssColorConverter(false)));


        handleSizeSlider.valueProperty().bindBidirectional(handleSizeProperty);
        Bindings.bindBidirectional(
                handleSizeField.textProperty(),
                handleSizeProperty,
                new NumberStringConverter());

        handleStrokeWidthSlider.valueProperty().bindBidirectional(handleStrokeWidthProperty);
        Bindings.bindBidirectional(
                handleStrokeWidthField.textProperty(),
                handleStrokeWidthProperty,
                new NumberStringConverter());
    }

    @Nonnull
    private NonnullProperty<CssColor> handleColorProperty = new NonnullProperty<>(this, "handleColor", CssColor.valueOf("blue"));

    @Nonnull
    private IntegerProperty handleSizeProperty = new SimpleIntegerProperty(this, "handleSize", 11);

    @Nonnull
    private IntegerProperty handleStrokeWidthProperty = new SimpleIntegerProperty(this, "handleStrokeWidth", 1);

    private Node node;

    public HandlesInspector() {
        this(GridInspector.class.getResource("HandlesInspector.fxml"));
    }

    public HandlesInspector(@Nonnull URL fxmlUrl) {
        init(fxmlUrl);
    }


    private void init(@Nonnull URL fxmlUrl) {
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
        });
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    protected void handleDrawingViewChanged(ObservableValue<? extends DrawingView> observable, DrawingView oldValue, DrawingView newValue) {
        if (oldValue != null) {
            handleColorProperty.unbindBidirectional(oldValue.getEditor().handleColorProperty());
            handleSizeProperty.unbindBidirectional(oldValue.getEditor().handleSizeProperty());
            handleStrokeWidthProperty.unbindBidirectional(oldValue.getEditor().handleStrokeWidthProperty());
        }
        try {
            if (newValue != null) {
                handleColorProperty.bindBidirectional(newValue.getEditor().handleColorProperty());
                handleSizeProperty.bindBidirectional(newValue.getEditor().handleSizeProperty());
                handleStrokeWidthProperty.bindBidirectional(newValue.getEditor().handleStrokeWidthProperty());
            }
        } catch (Throwable t) {
            t.printStackTrace();
        }
    }
}
