/*
 * @(#)DrawingExportOptionsPane.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.gui;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.GridPane;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.app.ApplicationLabels;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.text.CssNumberConverter;
import org.jhotdraw8.draw.DrawLabels;
import org.jhotdraw8.draw.io.BitmapExportOutputFormat;
import org.jhotdraw8.gui.InputDialog;
import org.jhotdraw8.text.StringConverterAdapter;
import org.jhotdraw8.util.Resources;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.prefs.Preferences;

import static org.jhotdraw8.draw.io.ExportOutputFormat.EXPORT_DRAWING_DPI_KEY;
import static org.jhotdraw8.draw.io.ExportOutputFormat.EXPORT_DRAWING_KEY;
import static org.jhotdraw8.draw.io.ExportOutputFormat.EXPORT_PAGES_DPI_KEY;
import static org.jhotdraw8.draw.io.ExportOutputFormat.EXPORT_PAGES_KEY;
import static org.jhotdraw8.draw.io.ExportOutputFormat.EXPORT_SLICES_DPI_KEY;
import static org.jhotdraw8.draw.io.ExportOutputFormat.EXPORT_SLICES_KEY;
import static org.jhotdraw8.draw.io.ExportOutputFormat.EXPORT_SLICES_RESOLUTION_2X_KEY;
import static org.jhotdraw8.draw.io.ExportOutputFormat.EXPORT_SLICES_RESOLUTION_3X_KEY;
import static org.jhotdraw8.io.DataFormats.registerDataFormat;

public class DrawingExportOptionsPane extends GridPane {

    public static Dialog<Map<? super Key<?>, Object>> createDialog(DataFormat format) {
        Resources labels = ApplicationLabels.getResources();
        final DrawingExportOptionsPane pane = new DrawingExportOptionsPane();
        pane.setFormat(format);
        return new InputDialog<>(labels.getString("export.dialog.title"), labels.getString("export.dialog.headerText"), pane, pane::getExportOptions);
    }

    @FXML // fx:id="drawingDpiField"
    private TextField drawingDpiField; // Value injected by FXMLLoader

    private final TextFormatter<Number> drawingDpiFormatter = new TextFormatter<>(new StringConverterAdapter<>(new CssNumberConverter(false)));
    @FXML // fx:id="drawingDpiLabel"
    private Label drawingDpiLabel; // Value injected by FXMLLoader

    @FXML // fx:id="exportDrawingCheckBox"
    private CheckBox exportDrawingCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="exportPagesCheckBox"
    private CheckBox exportPagesCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="exportSlicesCheckBox"
    private CheckBox exportSlicesCheckBox; // Value injected by FXMLLoader
    @FXML // URL location of the FXML file that was given to the FXMLLoader
    private URL location;
    @FXML // fx:id="pagesDpiField"
    private TextField pagesDpiField; // Value injected by FXMLLoader
    private final TextFormatter<Number> pagesDpiFormatter = new TextFormatter<>(new StringConverterAdapter<>(new CssNumberConverter(false)));
    @FXML // fx:id="pagesDpiLabel"
    private Label pagesDpiLabel; // Value injected by FXMLLoader
    @FXML // ResourceBundle that was given to the FXMLLoader
    private ResourceBundle resources;

    @FXML // fx:id="slicesDpiField"
    private TextField slicesDpiField; // Value injected by FXMLLoader
    private final TextFormatter<Number> slicesDpiFormatter = new TextFormatter<>(new StringConverterAdapter<>(new CssNumberConverter(false)));
    @FXML // fx:id="slicesDpiLabel"
    private Label slicesDpiLabel; // Value injected by FXMLLoader

    @FXML // fx:id="slicesResolution2xCheckBox"
    private CheckBox slicesResolution2xCheckBox; // Value injected by FXMLLoader

    @FXML // fx:id="slicesResolution3xCheckBox"
    private CheckBox slicesResolution3xCheckBox; // Value injected by FXMLLoader
    private DataFormat format;

    public DrawingExportOptionsPane() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);
        loader.setRoot(this);
        loader.setResources(DrawLabels.getResources().asResourceBundle());
        try {
            loader.load(getClass().getResourceAsStream("DrawingExportOptionsPane.fxml"));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }
    }

    /**
     * Returns a new map.
     *
     * @return the export options
     */
    @Nonnull
    public Map<? super Key<?>, Object> getExportOptions() {
        Map<? super Key<?>, Object> map = new HashMap<>();
        EXPORT_DRAWING_KEY.put(map, exportDrawingCheckBox.isSelected());
        EXPORT_PAGES_KEY.put(map, exportPagesCheckBox.isSelected());
        EXPORT_SLICES_KEY.put(map, exportSlicesCheckBox.isSelected());
        EXPORT_DRAWING_DPI_KEY.put(map, drawingDpiFormatter.getValue().doubleValue());
        EXPORT_PAGES_DPI_KEY.put(map, pagesDpiFormatter.getValue().doubleValue());
        EXPORT_SLICES_DPI_KEY.put(map, slicesDpiFormatter.getValue().doubleValue());
        EXPORT_SLICES_RESOLUTION_2X_KEY.put(map, slicesResolution2xCheckBox.isSelected());
        EXPORT_SLICES_RESOLUTION_3X_KEY.put(map, slicesResolution3xCheckBox.isSelected());
        return map;
    }

    public DataFormat getFormat() {
        return format;
    }

    public void setFormat(DataFormat format) {
        this.format = format;
        updateFormat();
    }

    @FXML
        // This method is called by the FXMLLoader when initialization is complete
    void initialize() {
        assert exportDrawingCheckBox != null : "fx:id=\"exportDrawingCheckBox\" was not injected: check your FXML file 'ExportDialog.fxml'.";
        assert exportPagesCheckBox != null : "fx:id=\"exportPagesCheckBox\" was not injected: check your FXML file 'ExportDialog.fxml'.";
        assert exportSlicesCheckBox != null : "fx:id=\"exportSlicesCheckBox\" was not injected: check your FXML file 'ExportDialog.fxml'.";
        assert drawingDpiField != null : "fx:id=\"drawingDpiField\" was not injected: check your FXML file 'ExportDialog.fxml'.";
        assert pagesDpiField != null : "fx:id=\"pagesDpiField\" was not injected: check your FXML file 'ExportDialog.fxml'.";
        assert slicesDpiField != null : "fx:id=\"slicesDpiField\" was not injected: check your FXML file 'ExportDialog.fxml'.";
        assert slicesResolution2xCheckBox != null : "fx:id=\"slicesResolution2xCheckBox\" was not injected: check your FXML file 'ExportDialog.fxml'.";
        assert slicesResolution3xCheckBox != null : "fx:id=\"slicesResolution3xCheckBox\" was not injected: check your FXML file 'ExportDialog.fxml'.";
        assert drawingDpiLabel != null : "fx:id=\"drawingDpiLabel\" was not injected: check your FXML file 'DrawingExportOptionsPane.fxml'.";
        assert pagesDpiLabel != null : "fx:id=\"pagesDpiLabel\" was not injected: check your FXML file 'DrawingExportOptionsPane.fxml'.";
        assert slicesDpiLabel != null : "fx:id=\"slicesDpiLabel\" was not injected: check your FXML file 'DrawingExportOptionsPane.fxml'.";

        drawingDpiField.setTextFormatter(drawingDpiFormatter);
        pagesDpiField.setTextFormatter(pagesDpiFormatter);
        slicesDpiField.setTextFormatter(slicesDpiFormatter);

        Preferences prefs = Preferences.userNodeForPackage(getClass());
        exportDrawingCheckBox.setSelected(prefs.getBoolean("exportDrawing", true));
        exportPagesCheckBox.setSelected(prefs.getBoolean("exportPages", true));
        exportSlicesCheckBox.setSelected(prefs.getBoolean("exportSlices", true));
        drawingDpiFormatter.setValue(prefs.getDouble("exportDrawingDpi", 72.0));
        pagesDpiFormatter.setValue(prefs.getDouble("exporPagesDpi", 300.0));
        slicesDpiFormatter.setValue(prefs.getDouble("exportSlicesDpi", 72.0));
        slicesResolution2xCheckBox.setSelected(prefs.getBoolean("exporSlicesResolution2x", false));
        slicesResolution3xCheckBox.setSelected(prefs.getBoolean("exporSlicesResolution3x", false));

        exportDrawingCheckBox.selectedProperty().addListener((o, oldv, newv) -> prefs.putBoolean("exportDrawing", newv));
        exportPagesCheckBox.selectedProperty().addListener((o, oldv, newv) -> prefs.putBoolean("exportPages", newv));
        exportSlicesCheckBox.selectedProperty().addListener((o, oldv, newv) -> prefs.putBoolean("exportSlices", newv));
        slicesResolution2xCheckBox.selectedProperty().addListener((o, oldv, newv) -> prefs.putBoolean("exporSlicesResolution2x", newv));
        slicesResolution3xCheckBox.selectedProperty().addListener((o, oldv, newv) -> prefs.putBoolean("exporSlicesResolution3x", newv));

        drawingDpiFormatter.valueProperty().addListener((o, oldv, newv) -> prefs.putDouble("exportDrawingDpi", newv == null ? 72.0 : newv.doubleValue()));
        pagesDpiFormatter.valueProperty().addListener((o, oldv, newv) -> prefs.putDouble("exporPagesDpi", newv == null ? 72.0 : newv.doubleValue()));
        slicesDpiFormatter.valueProperty().addListener((o, oldv, newv) -> prefs.putDouble("exportSlicesDpi", newv == null ? 72.0 : newv.doubleValue()));

        drawingDpiLabel.disableProperty().bind(exportDrawingCheckBox.selectedProperty().not());
        drawingDpiField.disableProperty().bind(exportDrawingCheckBox.selectedProperty().not());
        pagesDpiLabel.disableProperty().bind(exportPagesCheckBox.selectedProperty().not());
        pagesDpiField.disableProperty().bind(exportPagesCheckBox.selectedProperty().not());
        slicesDpiLabel.disableProperty().bind(exportSlicesCheckBox.selectedProperty().not());
        slicesDpiField.disableProperty().bind(exportSlicesCheckBox.selectedProperty().not());
        slicesResolution2xCheckBox.disableProperty().bind(exportSlicesCheckBox.selectedProperty().not());
        slicesResolution3xCheckBox.disableProperty().bind(exportSlicesCheckBox.selectedProperty().not());
    }

    private void updateFormat() {
        boolean dpi = supportsDpi(format);

        drawingDpiLabel.setVisible(dpi);
        drawingDpiField.setVisible(dpi);
        pagesDpiLabel.setVisible(dpi);
        pagesDpiField.setVisible(dpi);
        slicesDpiLabel.setVisible(dpi);
        slicesDpiField.setVisible(dpi);
        slicesResolution2xCheckBox.setVisible(dpi);
        slicesResolution3xCheckBox.setVisible(dpi);
    }

    private final static Set<DataFormat> dpiFormats = new HashSet<>();

    static {
        dpiFormats.add(DataFormat.IMAGE);
        dpiFormats.add(registerDataFormat(BitmapExportOutputFormat.PNG_MIME_TYPE));
        dpiFormats.add(registerDataFormat(BitmapExportOutputFormat.JPEG_MIME_TYPE));
    }

    private boolean supportsDpi(DataFormat format) {
        return dpiFormats.contains(format);
    }
}
