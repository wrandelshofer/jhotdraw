/*
 * @(#)FontChooserController.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.gui.fontchooser;


import javafx.beans.binding.Bindings;
import javafx.fxml.FXML;
import javafx.scene.control.ListView;
import javafx.scene.control.Slider;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import javafx.util.converter.NumberStringConverter;
import org.jhotdraw8.binding.CustomBinding;

public class FontChooserController extends FontFamilyChooserController {

    @FXML
    private ListView<Integer> fontSizeList;
    @FXML
    private Slider fontSizeSlider;
    @FXML
    private TextField fontSizeField;


    @FXML
    void initialize() {
        super.initialize();
        assert fontSizeField != null : "fx:id=\"fontSizeField\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert fontSizeList != null : "fx:id=\"fontSizeList\" was not injected: check your FXML file 'FontChooser.fxml'.";
        assert fontSizeSlider != null : "fx:id=\"fontSizeSlider\" was not injected: check your FXML file 'FontChooser.fxml'.";

        initFontSizeControls();
    }

    private void initFontSizeControls() {
        fontSizeList.getItems().addAll(9, 10, 11, 12, 13, 14, 18, 24, 36, 48, 64, 72, 96, 144, 288);
        //fontSizeSlider.valueProperty().bindBidirectional(fontSize);
        CustomBinding.bindBidirectionalAndConvert(
                fontSize,
                fontSizeSlider.valueProperty(),
                a -> fontSizeSlider.getMax() - a.doubleValue(),
                b -> fontSizeSlider.getMax() - b.doubleValue());
        StringConverter<Number> converter = new NumberStringConverter();
        Bindings.bindBidirectional(fontSizeField.textProperty(), fontSize, converter);
        fontSizeList.setOnMouseClicked(event -> {
            Integer selectedItem = fontSizeList.getSelectionModel().getSelectedItem();
            if (selectedItem != null) {
                setFontSize(selectedItem.doubleValue());
            }
            fontSizeList.getSelectionModel().clearSelection();
        });
        fontSize.addListener(o -> updatePreviewTextArea());
    }


}
