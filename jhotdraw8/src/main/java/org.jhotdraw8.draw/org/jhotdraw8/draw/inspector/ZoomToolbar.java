/*
 * @(#)ZoomToolbar.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.inspector;

import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Slider;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.jhotdraw8.annotation.NonNull;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import static java.lang.Math.log;
import static java.lang.Math.pow;

/**
 * FXML Controller class
 *
 * @author Werner Randelshofer
 */
public class ZoomToolbar extends BorderPane {

    private final double LOG2 = log(2.0);

    private final double zoomPowerIncrement = 0.5;

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

    private void init(@NonNull URL fxmlUrl) {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try (InputStream in = fxmlUrl.openStream()) {
            setCenter(loader.load(in));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        zoomSlider.valueProperty().bindBidirectional(zoomPower);

        zoomSlider.setLabelFormatter(new StringConverter<Double>() {
            @NonNull
            @Override
            public String toString(@NonNull Double object) {
                return Integer.toString(object.intValue());
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


    @FXML
    void zoomMinus(ActionEvent event) {
        zoomPower.set(Math.round((zoomPower.get() - zoomPowerIncrement) / zoomPowerIncrement) * zoomPowerIncrement);
    }

    @FXML
    void zoomPlus(ActionEvent event) {
        zoomPower.set(Math.round((zoomPower.get() + zoomPowerIncrement) / zoomPowerIncrement) * zoomPowerIncrement);
    }

    @NonNull
    public DoubleProperty minProperty() {
        return zoomSlider.minProperty();
    }

    @NonNull
    public DoubleProperty maxProperty() {
        return zoomSlider.maxProperty();
    }

    public double getMin() {
        return minProperty().get();
    }

    public double getMax() {
        return maxProperty().get();
    }

    public void setMin(double value) {
        minProperty().set(value);
    }

    public void setMax(double value) {
        maxProperty().set(value);
    }


}
