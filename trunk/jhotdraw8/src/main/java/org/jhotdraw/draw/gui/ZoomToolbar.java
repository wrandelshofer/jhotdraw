/* @(#)ZoomToolbar.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.gui;

import static java.lang.Math.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Logger;
import javafx.beans.binding.DoubleBinding;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.Slider;
import javafx.scene.control.ToggleButton;
import javafx.scene.layout.BorderPane;
import javafx.util.StringConverter;
import org.jhotdraw.draw.DrawingView;

/**
 * FXML Controller class
 *
 * @author werni
 */
public class ZoomToolbar extends BorderPane {

    private final double LOG2 = log(2.0);

    @FXML
    private Slider zoomSlider;
    private final DoubleProperty zoomPower;
    private final DoubleProperty zoomFactor = new SimpleDoubleProperty(this, "zoomFactor") {

        @Override
        public void set(double newValue) {
            double oldValue = get();
            super.set(newValue);
            if (newValue != oldValue) {
                zoomPower.set(log(newValue) / LOG2);
            }
        }

    };

    {
        zoomPower = new SimpleDoubleProperty(this, "zoomPower",0.0) {

            @Override
            public void set(double newValue) {
                double oldValue = get();
                super.set(newValue);
                if (newValue != oldValue) {
                    zoomFactor.set(pow(2, newValue));
                }
            }
        };
    }
    private final ObjectProperty<DrawingView> drawingView = new SimpleObjectProperty<DrawingView>(this,"drawingView") {

        @Override
        public void set(DrawingView newValue) {
            DrawingView oldValue = get();
            super.set(newValue); 
            
            zoomFactor.unbind();
            if (newValue!=null) {
            zoomFactor.bindBidirectional(newValue.zoomFactorProperty());
                newValue.zoomFactorProperty().set(1.0);
            }
        }
    };

    public ZoomToolbar() {
        init();
    }

    public void init() {
        FXMLLoader loader = new FXMLLoader();
        loader.setController(this);

        try {
            setCenter(loader.load(getClass().getResourceAsStream("ZoomToolbar.fxml")));
        } catch (IOException ex) {
            throw new InternalError(ex);
        }

        zoomSlider.valueProperty().bindBidirectional(zoomPower);

        zoomSlider.setLabelFormatter(new StringConverter<Double>() {
private final String[] labels = {"⅛","¼","½","1","2","4","8"};
            @Override
            public String toString(Double object) {
                int index = object.intValue()+labels.length/2;
                return (index>=0&&index<labels.length)?labels[index]:"";
            }

            @Override
            public Double fromString(String string) {
                return 0.0;
            }
        });
        
        /*new DoubleBinding() {{super.bind(zoomFactorProperty);}

         @Override
         protected double computeValue() {
         double v= log((zoomFactorProperty.get()))/LOG2;
         System.out.println("v:"+v);
         return v;
         }
         });*/
    }

    public DoubleProperty zoomFactor() {
        return zoomFactor();
    }

    public void setZoomFactor(double newValue) {
        zoomFactor.set(newValue);
    }

    public double getZoomFactor() {
        return zoomFactor.get();
    }
    
    public void setDrawingView(DrawingView newValue) {
        drawingView().set(newValue);
    }

    private ObjectProperty<DrawingView> drawingView() {
        return drawingView;
    }
}
