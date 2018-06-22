/* @(#)ZoomToolbar.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import static java.lang.Math.*;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.jhotdraw8.draw.DrawingView;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ZoomToolbar extends BorderPane {

    private final double LOG2 = log(2.0);

    @FXML
    private Slider zoomSlider;
    private final DoubleProperty zoomPower = new SimpleDoubleProperty(this, "zoomPower", 0.0);
    private int isUpdating = 0;
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, "zoomFactor", 1.0);

    {
        zoomFactor.addListener((o, oldv, newv) -> {
            if (isUpdating++ == 0) {
                zoomPower.set(log(newv.doubleValue()) / LOG2);
            }
            isUpdating--;
        });
        zoomPower.addListener((o, oldv, newv) -> {      
            if (isUpdating++ == 0) {
                zoomFactor.set(pow(2, newv.doubleValue()));
            }
            isUpdating--;
        });

    }

    public ZoomToolbar() {
        this(ZoomToolbar.class.getResource("ZoomToolbar.fxml"));
    }

    public ZoomToolbar(@NonNull URL fxmlUrl) {
        init(fxmlUrl);
    }

    private void init(URL fxmlUrl) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try (InputStream in = fxmlUrl.openStream()) {
            setCenter(loader.load(in));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        zoomSlider.valueProperty().bindBidirectional(zoomPower);

        zoomSlider.setLabelFormatter(new StringConverter<Double>() {
            private final String[] labels = {"⅛", "¼", "½", "1", "2", "4", "8"};

            @NonNull
            @Override
            public String toString(@NonNull Double object) {
                int index = object.intValue() + labels.length / 2;
                return (index >= 0 && index < labels.length) ? labels[index] : "";
            }

            @NonNull
            @Override
            public Double fromString(String string) {
                return 0.0;
            }
        });

    }

    /**
     * Defines the factor by which the drawing view should be zoomed.
     *
     * @return zoom factor
     */
    @NonNull
    public DoubleProperty zoomFactorProperty() {
        return zoomFactor;
    }

    public void setZoomFactor(double newValue) {
        zoomFactor.set(newValue);
    }

    public double getZoomFactor() {
        return zoomFactor.get();
    }
}
