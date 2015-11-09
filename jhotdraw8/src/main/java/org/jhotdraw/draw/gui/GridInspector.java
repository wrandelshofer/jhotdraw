/* @(#)GridInspector.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.beans.InvalidationListener;
import javafx.beans.property.Property;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.ColorPicker;
import javafx.scene.control.TextField;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.model.DrawingModelEvent;
import org.jhotdraw.gui.PlatformUtil;
import org.jhotdraw.text.CssPaintConverter;
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
    protected void onDrawingChanged(Drawing oldValue, Drawing newValue) {
        // empty, because this is not actually a drawing inspector
    }
}
