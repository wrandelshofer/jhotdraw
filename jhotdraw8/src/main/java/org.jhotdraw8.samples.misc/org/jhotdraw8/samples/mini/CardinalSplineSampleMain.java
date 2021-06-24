/*
 * @(#)CardinalSplineSampleMain.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polyline;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.CardinalSplines;
import org.jhotdraw8.geom.Geom;

import java.util.ArrayList;

/**
 * CardinalSplineSampleMain.
 *
 * @author Werner Randelshofer
 */
public class CardinalSplineSampleMain extends Application {
    private Polyline polyline = new Polyline(
            10, 100,
            60, 80,
            110, 20,
            160, 80,
            210, 100);
    private Path path = new Path();
    StackPane canvas = new StackPane();
    private DoubleProperty tension = new SimpleDoubleProperty(0.5);
    private BooleanProperty closed = new SimpleBooleanProperty();

    @Override
    public void start(@NonNull Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        HBox hbox = new HBox();
        Slider slider = new Slider();
        slider.valueProperty().bindBidirectional(tension);
        slider.setMin(0.0);
        slider.setMax(1.0);
        CheckBox checkBox = new CheckBox("Closed");
        checkBox.selectedProperty().bindBidirectional(closed);
        hbox.getChildren().add(slider);
        hbox.getChildren().add(checkBox);

        borderPane.setTop(hbox);
        canvas = new StackPane();
        borderPane.setCenter(canvas);
        polyline.setManaged(false);
        path.setManaged(false);
        polyline.setStroke(Color.LIGHTGRAY);
        canvas.getChildren().add(path);
        canvas.getChildren().add(polyline);
        tension.addListener(this::onPropertyChanged);
        closed.addListener(this::onPropertyChanged);

        canvas.setOnMouseClicked(this::onMouseClicked);
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        Parent root = borderPane;
        Scene scene = new Scene(root, 300, 250);
        updatePath();
        primaryStage.setTitle("JHotDraw: Cardinal Spline Sample");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void onPropertyChanged(Observable observable) {
        updatePath();
    }

    private Integer activePolypoint = null;

    private void onMouseDragged(MouseEvent mouseEvent) {
        if (activePolypoint != null) {
            polyline.getPoints().set(activePolypoint, mouseEvent.getX());
            polyline.getPoints().set(activePolypoint + 1, mouseEvent.getY());
            updatePath();
        }
    }

    private void onMousePressed(MouseEvent mouseEvent) {
        activePolypoint = findPolyPoint(mouseEvent.getX(), mouseEvent.getY());
    }

    private void onMouseClicked(MouseEvent mouseEvent) {
        if (mouseEvent.getClickCount() == 2) {
            if (activePolypoint == null) {
                polyline.getPoints().add(mouseEvent.getX());
                polyline.getPoints().add(mouseEvent.getY());
            } else {
                polyline.getPoints().remove(activePolypoint, activePolypoint + 2);
            }
            updatePath();
        }
    }

    private void updatePath() {
        ObservableList<Double> pp = polyline.getPoints();
        ArrayList<Double> ps = new ArrayList<>();
        if (!pp.isEmpty()) {
            if (closed.get()) {
                ps.add(pp.get(pp.size() - 2));
                ps.add(pp.get(pp.size() - 1));
                ps.addAll(pp);
                ps.add(pp.get(0));
                ps.add(pp.get(1));
                ps.add(pp.get(2));
                ps.add(pp.get(3));
            } else {
                ps.add(pp.get(0));
                ps.add(pp.get(1));
                ps.addAll(pp);
                ps.add(pp.get(pp.size() - 2));
                ps.add(pp.get(pp.size() - 1));
            }
        }
        Point2D[] points = CardinalSplines.cardinalSplineToBezier(ps, tension.get());
        ObservableList<PathElement> elements = path.getElements();
        elements.clear();
        for (int i = 0, n = points.length; i < n; ) {
            if (i == 0) {
                elements.add(new MoveTo(points[i].getX(), points[i].getY()));
                i++;
            } else {
                elements.add(new CubicCurveTo(
                        points[i].getX(), points[i].getY(),
                        points[i + 1].getX(), points[i + 1].getY(),
                        points[i + 2].getX(), points[i + 2].getY()
                ));
                i += 3;
            }
        }
        if (closed.get()) {
            elements.add(new ClosePath());
        }
    }

    private Integer findPolyPoint(double x, double y) {
        ObservableList<Double> points = polyline.getPoints();
        Integer index = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (int i = 0, n = points.size(); i < n; i += 2) {
            double px = points.get(i);
            double py = points.get(i + 1);
            double sq = Geom.squaredDistance(x, y, px, py);
            if (sq < 25 && sq < bestDistance) {
                bestDistance = sq;
                index = i;
            }
        }
        return index;
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
