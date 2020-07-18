/*
 * @(#)CardinalSplineSampleMain.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.*;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.geom.BezierCurves;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.bezier2biarc.Bezier2BiArc;
import org.jhotdraw8.geom.bezier2biarc.BiArc;

import java.awt.geom.CubicCurve2D;
import java.util.List;

/**
 * CardinalSplineSampleMain.
 *
 * @author Werner Randelshofer
 */
public class BiArcApproSampleMain extends Application {
    private javafx.scene.shape.Polyline polyline = new javafx.scene.shape.Polyline(
            110, 200,
            160, 180,
            210, 120,
            260, 180,
            310, 200);
    private Path bezierPath = new Path();
    private Path approxPath = new Path();
    private Path inflectionPointsPath = new Path();
    private Path inflectionPointsPath2 = new Path();
    StackPane canvas = new StackPane();

    @Override
    public void start(@NonNull Stage primaryStage) {
        BorderPane borderPane = new BorderPane();
        canvas = new StackPane();
        borderPane.setCenter(canvas);
        polyline.setManaged(false);
        bezierPath.setManaged(false);
        inflectionPointsPath.setManaged(false);
        inflectionPointsPath2.setManaged(false);
        approxPath.setManaged(false);
        approxPath.setStroke(Color.BLUE);
        approxPath.setStrokeWidth(3);
        approxPath.getStrokeDashArray().addAll(8.0, 8.0);
        polyline.setStroke(Color.LIGHTGRAY);
        canvas.getChildren().add(polyline);
        canvas.getChildren().add(bezierPath);
        canvas.getChildren().add(approxPath);
        canvas.getChildren().add(inflectionPointsPath2);
        canvas.getChildren().add(inflectionPointsPath);
        inflectionPointsPath.setFill(Color.RED);
        inflectionPointsPath2.setStroke(Color.YELLOW);

        canvas.setOnMouseClicked(this::onMouseClicked);
        canvas.setOnMousePressed(this::onMousePressed);
        canvas.setOnMouseDragged(this::onMouseDragged);
        Parent root = borderPane;
        Scene scene = new Scene(root, 400, 300);
        updatePath();
        primaryStage.setTitle("JHotDraw: BiArc Approximation Sample");
        primaryStage.setScene(scene);
        primaryStage.show();
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
        ObservableList<PathElement> inf1 = inflectionPointsPath.getElements();
        ObservableList<PathElement> inf2 = inflectionPointsPath2.getElements();
        ObservableList<PathElement> appr = approxPath.getElements();
        inf1.clear();
        inf2.clear();
        appr.clear();

        ObservableList<PathElement> elements = bezierPath.getElements();
        elements.clear();
        ObservableList<Double> points = polyline.getPoints();
        for (int i = 0, n = points.size(); i < n; ) {
            if (i == 0) {
                elements.add(new MoveTo(points.get(i++), points.get(i++)));
            } else if (i <= n - 6) {
                elements.add(new CubicCurveTo(points.get(i++), points.get(i++), points.get(i++), points.get(i++), points.get(i++), points.get(i++)));
            } else if (i <= n - 4) {
                elements.add(new QuadCurveTo(points.get(i++), points.get(i++), points.get(i++), points.get(i++)));
            } else {
                elements.add(new LineTo(points.get(i++), points.get(i++)));
            }
        }

        double x = 0, y = 0;
        for (PathElement e : elements) {
            if (e instanceof MoveTo) {
                x = ((MoveTo) e).getX();
                y = ((MoveTo) e).getY();
            } else if (e instanceof LineTo) {
                x = ((LineTo) e).getX();
                y = ((LineTo) e).getY();
            } else if (e instanceof QuadCurveTo) {
                QuadCurveTo c = (QuadCurveTo) e;
                double x1 = x + (c.getControlX() - x) * 2.0 / 3.0;
                double y1 = y + (c.getControlY() - y) * 2.0 / 3.0;
                double x3 = c.getX();
                double y3 = c.getY();
                double x2 = x3 + (c.getControlX() - x3) * 2.0 / 3.0;
                double y2 = y3 + (c.getControlY() - y3) * 2.0 / 3.0;

                approxBezierCurve(inf1, x, y, x1, y1, x2, y2, x3, y3);
                x = x3;
                y = y3;

            } else if (e instanceof CubicCurveTo) {
                CubicCurveTo c = (CubicCurveTo) e;

                double x1 = c.getControlX1();
                double y1 = c.getControlY1();
                double x2 = c.getControlX2();
                double y2 = c.getControlY2();
                double x3 = c.getX();
                double y3 = c.getY();
                approxBezierCurve(inf1, x, y, x1, y1, x2, y2, x3, y3);

                x = x3;
                y = y3;
            }
        }


    }

    private void approxBezierCurve(ObservableList<PathElement> inf1, double x, double y, double x1, double y1, double x2, double y2, double x3, double y3) {
        ObservableList<PathElement> inf2 = inflectionPointsPath2.getElements();
        ObservableList<PathElement> appr = approxPath.getElements();
        for (double t : BezierCurves.inflectionPoints(
                x, y, x1, y1, x2, y2, x3, y3)) {
            double r = 2;
            Point2D p = BezierCurves.evalCubicCurve(x, y, x1, y1, x2, y2, x3, y3, t);

            inf1.add(new MoveTo(p.getX() - r, p.getY()));
            inf1.add(new ArcTo(r, r, 0, p.getX() + r, p.getY(), false, false));
            inf1.add(new ArcTo(r, r, 0, p.getX() - r, p.getY(), false, false));
            inf1.add(new ClosePath());
        }
        CubicCurve2D.Double cubicBezier = new CubicCurve2D.Double(
                x, y, x1, y1,
                x2, y2, x3, y3
        );
        List<BiArc> biArcs = Bezier2BiArc.approxCubicBezier(cubicBezier, 3, 0.25);

        for (BiArc biArc : biArcs) {
            appr.add(appr.isEmpty() ? new MoveTo(biArc.a1.p1.getX(), biArc.a1.p1.getY()) : new LineTo(biArc.a1.p1.getX(), biArc.a1.p1.getY()));
            appr.add(new ArcTo(biArc.a1.r, biArc.a1.r, 0,
                    biArc.a1.p2.getX(), biArc.a1.p2.getY(),
                    Math.abs(biArc.a1.sweepAngle) > Math.PI, biArc.a1.isClockwise()
            ));
            appr.add(new ArcTo(biArc.a2.r, biArc.a2.r, 0,
                    biArc.a2.p2.getX(), biArc.a2.p2.getY(),
                    Math.abs(biArc.a2.sweepAngle) > Math.PI, biArc.a2.isClockwise()
            ));
        }
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }
}
