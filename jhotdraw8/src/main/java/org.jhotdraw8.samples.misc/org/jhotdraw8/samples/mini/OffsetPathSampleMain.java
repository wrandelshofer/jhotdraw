/*
 * @(#)CardinalSplineSampleMain.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.beans.Observable;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.DoubleProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleDoubleProperty;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.Slider;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.OffsetPathBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.offsetline.ContourBuilder;
import org.jhotdraw8.geom.offsetline.PlineVertex;
import org.jhotdraw8.geom.offsetline.PolyArcPath;

import java.util.List;

/**
 * CardinalSplineSampleMain.
 *
 * @author Werner Randelshofer
 */
public class OffsetPathSampleMain extends Application {
    private javafx.scene.shape.Polyline polyline = new javafx.scene.shape.Polyline(
            110, 200,
            160, 180,
            210, 120,
            260, 180,
            310, 200);
    private Path offsetPath1 = new Path();
    private Path offsetPath2 = new Path();
    private Path offsetPath3 = new Path();
    StackPane canvas = new StackPane();
    private DoubleProperty offset = new SimpleDoubleProperty(0.0);
    private BooleanProperty closed = new SimpleBooleanProperty();

    @Override
    public void start(@NonNull Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        HBox hbox = new HBox();
        Slider slider = new Slider();
        slider.valueProperty().bindBidirectional(offset);
        slider.setMin(-100.0);
        slider.setMax(100.0);
        slider.setMajorTickUnit(10.0);
        slider.setSnapToTicks(true);
        CheckBox checkBox = new CheckBox("Closed");
        checkBox.selectedProperty().bindBidirectional(closed);
        checkBox.selectedProperty().addListener((v, o, n) -> updatePath());
        Button button = new Button("Create Test");
        button.setOnAction(this::createTest);
        hbox.getChildren().add(slider);
        hbox.getChildren().add(checkBox);
        hbox.getChildren().add(button);
        hbox.setSpacing(10.0);

        borderPane.setTop(hbox);
        canvas = new StackPane();
        borderPane.setCenter(canvas);
        polyline.setManaged(false);
        offsetPath1.setManaged(false);
        offsetPath2.setManaged(false);
        offsetPath3.setManaged(false);
        offsetPath1.setMouseTransparent(true);
        offsetPath2.setMouseTransparent(true);
        offsetPath3.setMouseTransparent(true);
        polyline.setMouseTransparent(true);
        offsetPath2.setStroke(Color.BLUE);
        offsetPath2.getStrokeDashArray().addAll(3.0, 3.0);
        offsetPath3.setStroke(Color.PINK);
        polyline.setStroke(Color.LIGHTGRAY);
        canvas.getChildren().add(offsetPath1);
        canvas.getChildren().add(offsetPath2);
        canvas.getChildren().add(offsetPath3);
        canvas.getChildren().add(polyline);
        offset.addListener(this::onPropertyChanged);
        closed.addListener(this::onPropertyChanged);

        canvas.setOnMouseClicked(this::onMouseClicked);
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        Parent root = borderPane;
        Scene scene = new Scene(root, 400, 300);
        updatePath();
        primaryStage.setTitle("JHotDraw: Offset Path Sample");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    private void createTest(ActionEvent actionEvent) {
        PolyArcPath pline = createPline(polyline);
        ContourBuilder papb = new ContourBuilder();
        List<PolyArcPath> offsetPlines = papb.parallelOffset(pline, offset.get());
        StringBuilder buf = new StringBuilder();
        buf.append("dynamicTest(\"1\", () -> doTest(\n");
        dumpPline(pline, buf);
        buf.append(",\n");
        buf.append(offset.get());
        buf.append(",\n");
        buf.append("Arrays.asList(");
        boolean first = true;
        for (PolyArcPath opl : offsetPlines) {
            if (first) {
                first = false;
            } else {
                buf.append(",\n");
            }
            dumpPline(opl, buf);
        }

        buf.append(")\n");
        buf.append(")),\n");
        System.out.println(buf);
    }

    private void dumpPline(PolyArcPath pline, StringBuilder buf) {
        buf.append("polylineOf(").append(pline.isClosed()).append(",new double[][]{");

        boolean first = true;
        for (PlineVertex v : pline) {
            if (first) {
                first = false;
            } else {
                buf.append(", ");
            }
            buf.append('{').append(v.getX()).append(", ").append(v.getY()).append(", ").append(v.bulge()).append('}');
        }
        buf.append("})");
    }

    private void onPropertyChanged(Observable observable) {
        updatePath();
    }

    private Integer activePolypoint = null;
    private double grid = 10.0;

    private void onMouseDragged(MouseEvent mouseEvent) {
        if (activePolypoint != null) {
            polyline.getPoints().set(activePolypoint, Math.floor(mouseEvent.getX() / grid) * grid);
            polyline.getPoints().set(activePolypoint + 1, Math.floor(mouseEvent.getY() / grid) * grid);
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

        doOffsetPath(polyline, offsetPath1, offset.get());
        doOffsetPathWithOldAlgo(polyline, offsetPath3, -offset.get());
        // doRawOffsetPath(polyline,offsetPath2,offset.get());
        doOffsetPath(polyline, offsetPath2, -offset.get());
        offsetPath1.setStrokeWidth(3.0);
        offsetPath2.setVisible(true);
    }

    private void doOffsetPathWithOldAlgo(javafx.scene.shape.Polyline polyline, Path path, double offset) {
        ObservableList<PathElement> elements = path.getElements();
        elements.clear();
        if (closed.get()) {
            Polygon poly = new Polygon();
            poly.getPoints().addAll(polyline.getPoints());
            Shapes.buildFromPathIterator(
                    new OffsetPathBuilder(new FXPathBuilder(elements), offset),
                    Shapes.awtShapeFromFX(poly).getPathIterator(null)
            );
        } else {
            Shapes.buildFromPathIterator(
                    new OffsetPathBuilder(new FXPathBuilder(elements), offset),
                    Shapes.awtShapeFromFX(polyline).getPathIterator(null)
            );
        }
    }

    private void doOffsetPath(javafx.scene.shape.Polyline polyline, Path path, double offset) {
        PolyArcPath pap = createPline(polyline);
        ContourBuilder papb = new ContourBuilder();
        List<PolyArcPath> offsetPlines = papb.parallelOffset(pap, offset);

        ObservableList<PathElement> elements = path.getElements();
        elements.clear();
        for (var offPap : offsetPlines) {
            elements.addAll(
                    Shapes.fxPathElementsFromAWT(offPap.getPathIterator(null)));
        }
    }

    @NonNull
    private PolyArcPath createPline(javafx.scene.shape.Polyline polyline) {
        PolyArcPath pap = new PolyArcPath();
        ObservableList<Double> points = polyline.getPoints();
        for (int i = 0, n = points.size(); i < n; i += 2) {
            pap.addVertex(points.get(i), points.get(i + 1));
        }
        pap.isClosed(closed.get());
        return pap;
    }

    private void doRawOffsetPath(javafx.scene.shape.Polyline polyline, Path path, double offset) {
        PolyArcPath pap = createPline(polyline);
        pap.isClosed(closed.get());
        ContourBuilder papb = new ContourBuilder();
        ObservableList<PathElement> elements = path.getElements();
        elements.clear();
        PolyArcPath offPap = papb.createRawOffsetPline(pap, offset);
        elements.addAll(
                Shapes.fxPathElementsFromAWT(offPap.getPathIterator(null)));

        offPap = papb.createRawOffsetPline(pap, -offset);
        elements.addAll(
                Shapes.fxPathElementsFromAWT(offPap.getPathIterator(null)));
    }


    private Integer findPolyPoint(double x, double y) {
        ObservableList<Double> points = polyline.getPoints();
        Integer index = null;
        double bestDistance = Double.POSITIVE_INFINITY;
        for (int i = 0, n = points.size(); i < n; i += 2) {
            double px = points.get(i);
            double py = points.get(i + 1);
            double sq = Geom.distanceSq(x, y, px, py);
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
