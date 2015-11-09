/* @(#)GridInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.text.Format;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;
import javafx.util.StringConverter;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.DrawingView;
import org.jhotdraw.draw.constrain.GridConstrainer;
import org.jhotdraw.gui.PlatformUtil;
import org.jhotdraw.text.FormatConverterWrapper;
import org.jhotdraw.text.StringConverterConverterWrapper;
import org.jhotdraw.text.XmlDoubleConverter;
import org.jhotdraw.util.Resources;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class GridInspector extends AbstractDrawingInspector {

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
                    = new StringConverterConverterWrapper<>(new XmlDoubleConverter());
            heightField.textProperty().bindBidirectional(gc.heightProperty(), cc);
            widthField.textProperty().bindBidirectional(gc.widthProperty(), cc);
            xField.textProperty().bindBidirectional(gc.xProperty(), cc);
            yField.textProperty().bindBidirectional(gc.yProperty(), cc);
            angleField.textProperty().bindBidirectional(gc.angleProperty(), cc);
            drawGridCheckBox.selectedProperty().bindBidirectional(gc.drawGridProperty());
            snapToGridCheckBox.selectedProperty().bindBidirectional(gc.snapToGridProperty());
        }
    }

    @Override
    protected void onDrawingChanged(Drawing oldValue, Drawing newValue) {
        // empty, because this is not actually a drawing inspector
    }
}
