/* @(#)GridInspector.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.ParseException;
import java.util.prefs.Preferences;
import javafx.beans.property.Property;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.value.ChangeListener;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.util.StringConverter;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.binding.CustomBinding;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.constrain.GridConstrainer;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.gui.PlatformUtil;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.text.StringConverterAdapter;
import org.jhotdraw8.xml.text.XmlNumberConverter;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class GridInspector extends AbstractDrawingViewInspector {

    @FXML
    private TextField angleField;
    @FXML
    private CheckBox drawGridCheckBox;
    private GridConstrainer gridConstrainer;
    @FXML
    private TextField heightField;
    @FXML
    private TextField gridColorField;

    @FXML
    private ColorPicker gridColorPicker;
    @FXML
    private TextField majorXField;
    @FXML
    private TextField majorYField;

    @Nonnull
    private Property<CssColor> gridColorProperty = new SimpleObjectProperty<>();
    private Node node;

    @FXML
    private CheckBox snapToGridCheckBox;
    @FXML
    private TextField widthField;
    @FXML
    private TextField xField;
    @FXML
    private TextField yField;

    public GridInspector() {
        this(GridInspector.class.getResource("GridInspector.fxml"));
    }

    public GridInspector(@Nonnull URL fxmlUrl) {
        init(fxmlUrl);
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
        });

        Preferences prefs = Preferences.userNodeForPackage(GridInspector.class);
        snapToGridCheckBox.setSelected(prefs.getBoolean("snapToGrid", true));
        snapToGridCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("snapToGrid", newValue));
        drawGridCheckBox.setSelected(prefs.getBoolean("drawGrid", true));
        drawGridCheckBox.selectedProperty().addListener((o, oldValue, newValue)
                -> prefs.putBoolean("drawGrid", newValue));

        CustomBinding.bindBidirectionalAndConvert(//
                gridColorProperty,//
                gridColorPicker.valueProperty(),//
                (CssColor c) -> c == null ? null : c.getColor(), //
                CssColor::new//
        );
        gridColorField.textProperty().bindBidirectional(gridColorProperty, new StringConverterAdapter<>(
                new CssColorConverter(false)));
    }

    @Override
    protected void onDrawingViewChanged(@Nullable DrawingView oldValue, @Nullable DrawingView newValue) {
        Preferences prefs = Preferences.userNodeForPackage(GridInspector.class);
        ChangeListener<CssSize> prefsGridX = (o, oldv, newv) -> {
            prefs.putDouble("gridX", newv.getValue());
            String units = newv.getUnits();
            prefs.put("gridXUnits",  units==null?"":units);
        };
        ChangeListener<CssSize> prefsGridY = (o, oldv, newv) -> {
            prefs.putDouble("gridY", newv.getValue());
            String units = newv.getUnits();
            prefs.put("gridYUnits",  units==null?"":units);
        };
        ChangeListener<CssSize> prefsGridWidth = (o, oldv, newv) -> {
            prefs.putDouble("gridWidth", newv.getValue());
            String units = newv.getUnits();
            prefs.put("gridWidthUnits", units==null?"":units);
        };
        ChangeListener<CssSize> prefsGridHeight = (o, oldv, newv) -> {
            prefs.putDouble("gridHeight", newv.getValue());
            String units = newv.getUnits();
            prefs.put("gridHeightUnits",  units==null?"":units);
        };
        ChangeListener<Number> prefsGridAngle = (o, oldv, newv) -> prefs.putDouble("gridAngle", newv.doubleValue());
        ChangeListener<Number> prefsGridMajorX = (o, oldv, newv) -> prefs.putInt("gridMajorX", newv.intValue());
        ChangeListener<Number> prefsGridMajorY = (o, oldv, newv) -> prefs.putInt("gridMajorY", newv.intValue());
        ChangeListener<CssColor> prefsGridColor = (o, oldv, newv) -> prefs.put("gridColor", newv.getName());

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
            if (oldValue instanceof GridConstrainer) {
                GridConstrainer gc = (GridConstrainer) oldValue;
                gridColorProperty.unbindBidirectional(gc.gridColorProperty());
            }
        }
        if (newValue != null) {
            if (false && (newValue.getConstrainer() instanceof GridConstrainer)) {
                gridConstrainer = (GridConstrainer) newValue.getConstrainer();
            } else {

                gridConstrainer = new GridConstrainer(
                        new CssSize(prefs.getDouble("gridX", 0),   prefs.get("gridXUnits",null)),
                        new CssSize(prefs.getDouble("gridY", 0),   prefs.get("gridYUnits",null)),
                        new CssSize(prefs.getDouble("gridWidth", 0),   prefs.get("gridWidthUnits",null)),
                        new CssSize(prefs.getDouble("gridHeight", 0),   prefs.get("gridHeightUnits",null)),
                        prefs.getDouble("gridAngle", 11.25), prefs.getInt("gridMajorX", 5), prefs.getInt("gridMajorY", 5));
               Converter<CssColor> converter=new CssColorConverter(true);
                try {
                    gridConstrainer.setGridColor(converter.fromString(prefs.get("gridColor",gridConstrainer.getGridColor().getName())));
                } catch (@Nonnull ParseException|IOException ex) {
                    // don't set color if preferences is bogus
                }
                newValue.setConstrainer(gridConstrainer);
            }
            StringConverter<CssSize> sc
                    = new StringConverterAdapter<>(new CssSizeConverter(false));
            StringConverter<Number> nc
                    = new StringConverterAdapter<>(new XmlNumberConverter());
            heightField.textProperty().bindBidirectional(gridConstrainer.heightProperty(), sc);
            widthField.textProperty().bindBidirectional(gridConstrainer.widthProperty(), sc);
            xField.textProperty().bindBidirectional(gridConstrainer.xProperty(), sc);
            yField.textProperty().bindBidirectional(gridConstrainer.yProperty(), sc);
            majorXField.textProperty().bindBidirectional(gridConstrainer.majorXProperty(), nc);
            majorYField.textProperty().bindBidirectional(gridConstrainer.majorYProperty(), nc);
            angleField.textProperty().bindBidirectional(gridConstrainer.angleProperty(), nc);
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
                gridConstrainer.gridColorProperty().addListener(prefsGridColor);

                gridColorProperty.bindBidirectional(gridConstrainer.gridColorProperty());
            }

        }
    }
}
