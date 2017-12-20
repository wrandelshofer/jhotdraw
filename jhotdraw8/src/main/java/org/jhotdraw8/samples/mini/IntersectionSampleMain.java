/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.samples.mini;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.function.Consumer;
import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.geometry.Point2D;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ChoiceBox;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.stage.Stage;
import org.jhotdraw8.geom.Intersection;
import org.jhotdraw8.geom.Intersections;

/**
 * IntersectionSampleMain.
 *
 * @author Werner Randelshofer
 */
public class IntersectionSampleMain extends Application {

    StackPane canvas;
    ChoiceBox<String> choice1;
    ChoiceBox<String> choice2;
    List<Shape> shapes;
    Path isectPath = new Path();

    private Shape createShapeAndHandles(final String shapeName, Color shapeColor, Color handleColor) {
        Shape shape = null;
        if (shapeName != null) {
            switch (shapeName) {
                case "Ellipse": {
                    Ellipse ellipse = new Ellipse(100, 100, 80, 60);
                    shape = ellipse;
                    ellipse.setFill(null);
                    ellipse.setStroke(shapeColor);
                    ellipse.setStrokeWidth(2);
                    ellipse.setManaged(false);
                    ellipse.setMouseTransparent(true);
                    Handle centerHandle = new Handle(handleColor);
                    centerHandle.updateHandle = () -> centerHandle.setPosition(ellipse.getCenterX(), ellipse.getCenterY());
                    centerHandle.updateShape = (p) -> {
                        ellipse.setCenterX(p.getX());
                        ellipse.setCenterY(p.getY());
                    };
                    Handle rxHandle = new Handle(handleColor);
                    rxHandle.updateHandle = () -> rxHandle.setPosition(ellipse.getCenterX() + ellipse.getRadiusX(), ellipse.getCenterY());
                    rxHandle.updateShape = (p) -> {
                        ellipse.setRadiusX(p.getX() - ellipse.getCenterX());
                    };
                    Handle ryHandle = new Handle(handleColor);
                    ryHandle.updateHandle = () -> ryHandle.setPosition(ellipse.getCenterX(), ellipse.getCenterY() - ellipse.getRadiusY());
                    ryHandle.updateShape = (p) -> {
                        ellipse.setRadiusY(-p.getY() + ellipse.getCenterY());
                    };
                    canvas.getChildren().addAll(ellipse);
                    handles.add(centerHandle);
                    handles.add(rxHandle);
                    handles.add(ryHandle);
                    break;
                }
                case "Line": {
                    Line line = new Line(20, 20, 180, 160);
                    shape = line;
                    line.setFill(null);
                    line.setStrokeWidth(2);
                    line.setManaged(false);
                    line.setMouseTransparent(true);
                    Handle startHandle = new Handle(handleColor);
                    startHandle.updateHandle = () -> startHandle.setPosition(line.getStartX(), line.getStartY());
                    startHandle.updateShape = (p) -> {
                        line.setStartX(p.getX());
                        line.setStartY(p.getY());
                    };
                    Handle endHandle = new Handle(handleColor);
                    endHandle.updateHandle = () -> endHandle.setPosition(line.getEndX(), line.getEndY());
                    endHandle.updateShape = (p) -> {
                        line.setEndX(p.getX());
                        line.setEndY(p.getY());
                    };
                    canvas.getChildren().addAll(line);
                    handles.add(startHandle);
                    handles.add(endHandle);
                    break;
                }
                case "QuadCurve": {
                    QuadCurve curve = new QuadCurve(20, 20, 80, 160, 200, 40);
                    shape = curve;
                    curve.setFill(null);
                    curve.setStrokeWidth(2);
                    curve.setManaged(false);
                    curve.setMouseTransparent(true);
                    Handle startHandle = new Handle(handleColor);
                    startHandle.updateHandle = () -> startHandle.setPosition(curve.getStartX(), curve.getStartY());
                    startHandle.updateShape = (p) -> {
                        curve.setStartX(p.getX());
                        curve.setStartY(p.getY());
                    };
                    Handle ctrlHandle = new Handle(handleColor);
                    ctrlHandle.updateHandle = () -> ctrlHandle.setPosition(curve.getControlX(), curve.getControlY());
                    ctrlHandle.updateShape = (p) -> {
                        curve.setControlX(p.getX());
                        curve.setControlY(p.getY());
                    };
                    Handle endHandle = new Handle(handleColor);
                    endHandle.updateHandle = () -> endHandle.setPosition(curve.getEndX(), curve.getEndY());
                    endHandle.updateShape = (p) -> {
                        curve.setEndX(p.getX());
                        curve.setEndY(p.getY());
                    };
                    canvas.getChildren().addAll(curve);
                    handles.add(startHandle);
                    handles.add(ctrlHandle);
                    handles.add(endHandle);
                    break;
                }
                case "CubicCurve": {
                    CubicCurve curve = new CubicCurve(20, 20, 40, 160,80,170, 200, 40);
                    shape = curve;
                    curve.setFill(null);
                    curve.setStrokeWidth(2);
                    curve.setManaged(false);
                    curve.setMouseTransparent(true);
                    Handle startHandle = new Handle(handleColor);
                    startHandle.updateHandle = () -> startHandle.setPosition(curve.getStartX(), curve.getStartY());
                    startHandle.updateShape = (p) -> {
                        curve.setStartX(p.getX());
                        curve.setStartY(p.getY());
                    };
                    Handle ctrl1Handle = new Handle(handleColor);
                    ctrl1Handle.updateHandle = () -> ctrl1Handle.setPosition(curve.getControlX1(), curve.getControlY1());
                    ctrl1Handle.updateShape = (p) -> {
                        curve.setControlX1(p.getX());
                        curve.setControlY1(p.getY());
                    };
                    Handle ctrl2Handle = new Handle(handleColor);
                    ctrl2Handle.updateHandle = () -> ctrl2Handle.setPosition(curve.getControlX2(), curve.getControlY2());
                    ctrl2Handle.updateShape = (p) -> {
                        curve.setControlX2(p.getX());
                        curve.setControlY2(p.getY());
                    };
                    Handle endHandle = new Handle(handleColor);
                    endHandle.updateHandle = () -> endHandle.setPosition(curve.getEndX(), curve.getEndY());
                    endHandle.updateShape = (p) -> {
                        curve.setEndX(p.getX());
                        curve.setEndY(p.getY());
                    };
                    canvas.getChildren().addAll(curve);
                    handles.add(startHandle);
                    handles.add(ctrl1Handle);
                    handles.add(ctrl2Handle);
                    handles.add(endHandle);
                    break;
                }
            }
        }
        if (shape != null) {
            shape.setStroke(shapeColor);
            shapes.add(shape);
        }
        return shape;
    }

    private class Handle {

        Rectangle node = new Rectangle(5, 5);
        Runnable updateHandle;
        Consumer<Point2D> updateShape;

        {
            node.setManaged(false);
            node.setOnMousePressed(this::onMousePressed);
            node.setOnMouseDragged(this::onMouseDragged);
        }

        public Handle(Color color) {
            node.setFill(color);
            node.setStroke(Color.BLACK);
        }

        private void onMousePressed(MouseEvent evt) {
            node.requestFocus();
        }

        public void setPosition(double x, double y) {
            node.setX(x - node.getWidth() / 2);
            node.setY(y - node.getHeight() / 2);
        }

        private void onMouseDragged(MouseEvent evt) {
            updateShape.accept(new Point2D(evt.getX() - evt.getX() % 10, evt.getY() - evt.getY() % 10));
            updateHandles();
        }
    }

    @Override
    public void start(Stage primaryStage) {
        choice1 = new ChoiceBox<String>();
        choice1.getItems().setAll("QuadCurve", "CubicCurve", "Ellipse", "Line");
        choice2 = new ChoiceBox<String>();
        choice2.getItems().setAll("QuadCurve", "CubicCurve", "Ellipse", "Line");
        HBox hbox = new HBox(4);
        hbox.getChildren().addAll(choice1, choice2);
        BorderPane borderPane = new BorderPane();
        borderPane.setTop(hbox);
        canvas = new StackPane();
        borderPane.setCenter(canvas);
        isectPath.setFill(Color.RED);
        isectPath.setStroke(Color.WHITE);
        isectPath.setManaged(false);
        isectPath.setMouseTransparent(true);
        Parent root = borderPane;
        shapes = new ArrayList<>();
        Scene scene = new Scene(root, 300, 250);

        choice1.setOnAction(this::updateShapes);
        choice2.setOnAction(this::updateShapes);

        primaryStage.setTitle("JHotDraw: Intersection Sample");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private void updateHandles() {
        for (Handle h : handles) {
            h.updateHandle.run();
        }
        final ObservableList<PathElement> elems = isectPath.getElements();
        elems.clear();
        if (shapes.size() == 2) {
            Shape shape0 = shapes.get(0);
            Shape shape1 = shapes.get(1);
            Intersection isect = null;
            if (shape0 instanceof CubicCurve && shape1 instanceof CubicCurve) {
                CubicCurve l0 = (CubicCurve) shape0;
                CubicCurve l1 = (CubicCurve) shape1;
                isect = Intersections.intersectBezier3Bezier3(l0.getStartX(), l0.getStartY(), l0.getControlX1(), l0.getControlY1(), l0.getControlX2(), l0.getControlY2(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getControlX1(), l1.getControlY1(),  l1.getControlX2(), l1.getControlY2(), l1.getEndX(), l1.getEndY());
                System.out.println("cubiccurve0:" + l0.getStartX() + " " + l0.getStartY() + " " + l0.getControlX1() + " " + l0.getControlY1()  + " " + l0.getControlX2() + " " + l0.getControlY2()+ " "+ l0.getEndX() + " " + l0.getEndY());
                System.out.println("cubiccurve1:" + l1.getStartX() + " " + l1.getStartY() + " " + l1.getControlX1() + " " + l1.getControlY1()+ " " + l1.getControlX2() + " " + l1.getControlY2() + " " + l1.getEndX() + " " + l1.getEndY());
            }   else       if (shape0 instanceof Ellipse && shape1 instanceof Ellipse) {
                Ellipse e0 = (Ellipse) shape0;
                Ellipse e1 = (Ellipse) shape1;
                isect = Intersections.intersectEllipseEllipse(e0.getCenterX(), e0.getCenterY(), e0.getRadiusX(), e0.getRadiusY(),
                        e1.getCenterX(), e1.getCenterY(), e1.getRadiusX(), e1.getRadiusY());
                System.out.println("ellipse0:" + e0.getCenterX() + " " + e0.getCenterY() + " " + e0.getRadiusX() + " " + e0.getRadiusY());
                System.out.println("ellipse1:" + e1.getCenterX() + " " + e1.getCenterY() + " " + e1.getRadiusX() + " " + e1.getRadiusY());
            } else if (shape0 instanceof Ellipse && shape1 instanceof Line) {
                Ellipse e0 = (Ellipse) shape0;
                Line l1 = (Line) shape1;
                isect = Intersections.intersectEllipseLine(e0.getCenterX(), e0.getCenterY(), e0.getRadiusX(), e0.getRadiusY(),
                        l1.getStartX(), l1.getStartY(), l1.getEndX(), l1.getEndY());
                System.out.println("ellipse0:" + e0.getCenterX() + " " + e0.getCenterY() + " " + e0.getRadiusX() + " " + e0.getRadiusY());
                System.out.println("line1:" + l1.getStartX() + " " + l1.getStartY() + " " + l1.getEndX() + " " + l1.getEndY());
            } else if (shape0 instanceof Ellipse && shape1 instanceof QuadCurve) {
                Ellipse e0 = (Ellipse) shape0;
                QuadCurve l1 = (QuadCurve) shape0;
                isect = Intersections.intersectEllipseBezier2(e0.getCenterX(), e0.getCenterY(), e0.getRadiusX(), e0.getRadiusY(),
                        l1.getStartX(), l1.getStartY(), l1.getControlX(), l1.getControlY(), l1.getEndX(), l1.getEndY());
                System.out.println("ellipse0:" + e0.getCenterX() + " " + e0.getCenterY() + " " + e0.getRadiusX() + " " + e0.getRadiusY());
                System.out.println("quardcurve1:" + l1.getStartX() + " " + l1.getStartY() + " " + l1.getControlX() + " " + l1.getControlY() + " " + l1.getEndX() + " " + l1.getEndY());
            } else if (shape0 instanceof Line && shape1 instanceof Ellipse) {
                Line l0 = (Line) shape0;
                Ellipse e1 = (Ellipse) shape1;
                isect = Intersections.intersectLineEllipse(l0.getStartX(), l0.getStartY(), l0.getEndX(), l0.getEndY(),
                        e1.getCenterX(), e1.getCenterY(), e1.getRadiusX(), e1.getRadiusY());
                System.out.println("line0:" + l0.getStartX() + " " + l0.getStartY() + " " + l0.getEndX() + " " + l0.getEndY());
                System.out.println("ellipse1:" + e1.getCenterX() + " " + e1.getCenterY() + " " + e1.getRadiusX() + " " + e1.getRadiusY());
            } else if (shape0 instanceof Line && shape1 instanceof Line) {
                Line l0 = (Line) shape0;
                Line l1 = (Line) shape1;
                isect = Intersections.intersectLineLine(l0.getStartX(), l0.getStartY(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getEndX(), l1.getEndY());
                System.out.println("line0:" + l0.getStartX() + " " + l0.getStartY() + " " + l0.getEndX() + " " + l0.getEndY());
                System.out.println("line1:" + l1.getStartX() + " " + l1.getStartY() + " " + l1.getEndX() + " " + l1.getEndY());
            } else if (shape0 instanceof QuadCurve && shape1 instanceof QuadCurve) {
                QuadCurve l0 = (QuadCurve) shape0;
                QuadCurve l1 = (QuadCurve) shape1;
                isect = Intersections.intersectBezier2Bezier2(l0.getStartX(), l0.getStartY(), l0.getControlX(), l0.getControlY(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getControlX(), l1.getControlY(), l1.getEndX(), l1.getEndY());
                System.out.println("quadcurve0:" + l0.getStartX() + " " + l0.getStartY() + " " + l0.getControlX() + " " + l0.getControlY() + " "+ l0.getEndX() + " " + l0.getEndY());
                System.out.println("quardcurve1:" + l1.getStartX() + " " + l1.getStartY() + " " + l1.getControlX() + " " + l1.getControlY() + " " + l1.getEndX() + " " + l1.getEndY());
            } else if (shape0 instanceof QuadCurve && shape1 instanceof Ellipse) {
                QuadCurve l0 = (QuadCurve) shape0;
                Ellipse e1 = (Ellipse) shape1;
                isect = Intersections.intersectBezier2Ellipse(l0.getStartX(), l0.getStartY(), l0.getControlX(), l0.getControlY(), l0.getEndX(), l0.getEndY(),
                        e1.getCenterX(), e1.getCenterY(), e1.getRadiusX(), e1.getRadiusY());
                System.out.println("quadcurve0:" + l0.getStartX() + " " + l0.getStartY() + " " + l0.getControlX() + " " + l0.getControlY() + " "+ l0.getEndX() + " " + l0.getEndY());
                System.out.println("ellipse1:" + e1.getCenterX() + " " + e1.getCenterY() + " " + e1.getRadiusX() + " " + e1.getRadiusY());
            }
            if (isect !=null){
            double r = 3.5;
            for (Map.Entry<Double, Point2D> entry : isect.getIntersections()) {
                Point2D p = entry.getValue();
                System.out.println("  p:" + p);
                double x = p.getX();
                double y = p.getY();
                elems.add(new MoveTo(x - r, y));
                elems.add(new ArcTo(r, r, 0, x + r, y, true, true));
                elems.add(new ArcTo(r, r, 0, x - r, y, true, true));
                elems.add(new ClosePath());
           }}
        }
    }
    private List<Handle> handles = new ArrayList<>();

    private void updateShapes(ActionEvent evt) {
        handles.clear();
        shapes.clear();
        canvas.getChildren().clear();
        Shape shape;
        shape = createShapeAndHandles(choice1.getValue(), Color.GREEN, Color.GREEN);
        shape = createShapeAndHandles(choice2.getValue(), Color.CYAN, Color.CYAN);
        for (Handle h : handles) {
            canvas.getChildren().add(h.node);
        }
        canvas.getChildren().add(isectPath);
        updateHandles();
    }

}
