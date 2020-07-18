/*
 * @(#)IntersectionSampleMain.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

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
import javafx.scene.shape.*;
import javafx.stage.Stage;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.geom.BezierCurves;
import org.jhotdraw8.geom.Intersection;
import org.jhotdraw8.geom.Intersections;

import java.util.*;
import java.util.function.Consumer;

/**
 * IntersectionSampleMain.
 *
 * @author Werner Randelshofer
 */
public class IntersectionSampleMain extends Application {

    StackPane canvas;
    ChoiceBox<String> choice1;
    ChoiceBox<String> choice2;
    @NonNull
    List<Map.Entry<Shape, List<Handle>>> shapes = new ArrayList<>();

    {
        shapes.add(null);
        shapes.add(null);
    }

    @NonNull
    Path isectPath = new Path();

    private static class Point extends Circle {

        public Point(double centerX, double centerY, double radius) {
            super(centerX, centerY, radius);
        }
    }

    @Nullable
    private Map.Entry<Shape, List<Handle>> createShapeAndHandles(@Nullable final String shapeName, Color shapeColor, Color handleColor) {
        Shape shape = null;
        List<Handle> handles = new ArrayList<>();
        if (shapeName != null) {
            switch (shapeName) {
                case "Circle": {
                    Circle circle = new Circle(100, 100, 60);
                    shape = circle;
                    Handle centerHandle = new Handle(handleColor);
                    centerHandle.updateHandle = () -> centerHandle.setPosition(circle.getCenterX(), circle.getCenterY());
                    centerHandle.updateShape = (p) -> {
                        circle.setCenterX(p.getX());
                        circle.setCenterY(p.getY());
                    };
                    Handle rxHandle = new Handle(handleColor);
                    rxHandle.updateHandle = () -> rxHandle.setPosition(circle.getCenterX() + circle.getRadius(), circle.getCenterY());
                    rxHandle.updateShape = (p) -> {
                        circle.setRadius(p.getX() - circle.getCenterX());
                    };
                    handles.add(centerHandle);
                    handles.add(rxHandle);
                    break;
                }
                case "CubicCurve": {
                    CubicCurve curve = new CubicCurve(20, 20, 40, 160, 80, 170, 200, 40);
                    shape = curve;
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
                    handles.add(startHandle);
                    handles.add(ctrl1Handle);
                    handles.add(ctrl2Handle);
                    handles.add(endHandle);
                    break;
                }

                case "Ellipse": {
                    Ellipse ellipse = new Ellipse(100, 100, 80, 60);
                    shape = ellipse;
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
                    handles.add(centerHandle);
                    handles.add(rxHandle);
                    handles.add(ryHandle);
                    break;
                }
                case "Line": {
                    Line line = new Line(20, 20, 180, 160);
                    shape = line;
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
                    handles.add(startHandle);
                    handles.add(endHandle);
                    break;
                }
                case "Point": {
                    Point point = new Point(100, 100, 60);
                    shape = point;
                    Handle centerHandle = new Handle(handleColor);
                    centerHandle.updateHandle = () -> centerHandle.setPosition(point.getCenterX(), point.getCenterY());
                    centerHandle.updateShape = (p) -> {
                        point.setCenterX(p.getX());
                        point.setCenterY(p.getY());
                    };
                    Handle rxHandle = new Handle(handleColor);
                    rxHandle.updateHandle = () -> rxHandle.setPosition(point.getCenterX() + point.getRadius(), point.getCenterY());
                    rxHandle.updateShape = (p) -> {
                        point.setRadius(p.getX() - point.getCenterX());
                    };
                    handles.add(centerHandle);
                    handles.add(rxHandle);
                    break;
                }
                case "QuadCurve": {
                    QuadCurve curve = new QuadCurve(20, 20, 80, 160, 200, 40);
                    shape = curve;
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
                    handles.add(startHandle);
                    handles.add(ctrlHandle);
                    handles.add(endHandle);
                    break;
                }
                case "Rectangle": {
                    Rectangle rectangle = new Rectangle(40, 40, 80, 60);
                    shape = rectangle;
                    Handle originHandle = new Handle(handleColor);
                    originHandle.updateHandle = () -> originHandle.setPosition(rectangle.getX(), rectangle.getY());
                    originHandle.updateShape = (p) -> {
                        rectangle.setX(p.getX());
                        rectangle.setY(p.getY());
                    };
                    Handle sizeHandle = new Handle(handleColor);
                    sizeHandle.updateHandle = () -> sizeHandle.setPosition(rectangle.getX() + rectangle.getWidth(),
                            rectangle.getY() + rectangle.getHeight());
                    sizeHandle.updateShape = (p) -> {
                        rectangle.setWidth(p.getX() - rectangle.getX());
                        rectangle.setHeight(p.getY() - rectangle.getY());
                    };
                    handles.add(sizeHandle);
                    handles.add(originHandle);
                    break;
                }
            }
        }
        if (shape != null) {
            shape.setFill(null);
            shape.setStroke(shapeColor);
            shape.setStrokeWidth(2);
            shape.setManaged(false);
            shape.setMouseTransparent(true);
            canvas.getChildren().addAll(shape);

        }
        return shape == null ? null : new AbstractMap.SimpleEntry<>(shape, handles);
    }

    private class Handle {

        @NonNull
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

        private void onMouseDragged(@NonNull MouseEvent evt) {
            updateShape.accept(new Point2D(evt.getX() - evt.getX() % 10, evt.getY() - evt.getY() % 10));
            updateHandles();
            updateIntersections();
        }
    }

    @Override
    public void start(@NonNull Stage primaryStage) {
        choice1 = new ChoiceBox<>();
        choice1.getItems().setAll("Circle", "CubicCurve", "Ellipse", "Line", "Rectangle", "QuadCurve");
        choice2 = new ChoiceBox<>();
        choice2.getItems().setAll("Circle", "CubicCurve", "Ellipse", "Line", "Point", "Rectangle", "QuadCurve");
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
        for (Map.Entry<Shape, List<Handle>> entry : shapes) {
            if (entry != null) {
                List<Handle> handles = entry.getValue();
                for (Handle h : handles) {
                    h.updateHandle.run();
                }
            }
        }
    }

    private void print(@NonNull Shape shape, String name) {
        System.out.print(name + " new ");
        if (shape.getClass() == Circle.class) {
            Circle l0 = (Circle) shape;
            System.out.print("Circle(" + l0.getCenterX() + ", " + l0.getCenterY() + ", " + l0.getRadius());
        } else if (shape.getClass() == CubicCurve.class) {
            CubicCurve l0 = (CubicCurve) shape;
            System.out.print("CubicCurve(" + l0.getStartX() + ", " + l0.getStartY() + ", " + l0.getControlX1() + ", " + l0.getControlY1() + ", " + l0.getControlX2() + ", " + l0.getControlY2() + ", " + l0.getEndX() + ", " + l0.getEndY());
        } else if (shape.getClass() == Ellipse.class) {
            Ellipse l0 = (Ellipse) shape;
            System.out.print("Ellipse(" + l0.getCenterX() + ", " + l0.getCenterY() + ", " + l0.getRadiusX() + ", " + l0.getRadiusY());
        } else if (shape.getClass() == Line.class) {
            Line l0 = (Line) shape;
            System.out.print("Line(" + l0.getStartX() + ", " + l0.getStartY() + ", " + l0.getEndX() + ", " + l0.getEndY());
        } else if (shape.getClass() == Point.class) {
            Point l0 = (Point) shape;
            System.out.print("Point(" + l0.getCenterX() + ", " + l0.getCenterY() + ", " + l0.getRadius());
        } else if (shape.getClass() == Rectangle.class) {
            Rectangle l0 = (Rectangle) shape;
            System.out.print("Rectangle(" + l0.getX() + ", " + l0.getY() + ", " + l0.getWidth() + ", " + l0.getHeight());
        } else if (shape.getClass() == QuadCurve.class) {
            QuadCurve l0 = (QuadCurve) shape;
            System.out.print("QuadCurve(" + l0.getStartX() + ", " + l0.getStartY() + ", " + l0.getControlX() + ", " + l0.getControlY() + ", " + l0.getEndX() + ", " + l0.getEndY());
        }
        System.out.println(")");
    }

    private void updateIntersections() {
        final ObservableList<PathElement> elems = isectPath.getElements();
        elems.clear();
        Map.Entry<Shape, List<Handle>> entry0 = shapes.get(0);
        Map.Entry<Shape, List<Handle>> entry1 = shapes.get(1);
        if (entry0 != null && entry1 != null) {
            Shape shape0 = entry0.getKey();
            Shape shape1 = entry1.getKey();
            print(shape0, "0");
            print(shape1, "1");
            Intersection isect = null;

            if (shape0.getClass() == Circle.class && shape1.getClass() == Circle.class) {
                Circle l0 = (Circle) shape0;
                Circle l1 = (Circle) shape1;
                isect = Intersections.intersectCircleCircle(l0.getCenterX(), l0.getCenterY(), l0.getRadius(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadius());
            } else if (shape0.getClass() == Circle.class && shape1.getClass() == Ellipse.class) {
                Circle l0 = (Circle) shape0;
                Ellipse l1 = (Ellipse) shape1;
                isect = Intersections.intersectCircleEllipse(l0.getCenterX(), l0.getCenterY(), l0.getRadius(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadiusX(), l1.getRadiusY());
            } else if (shape0.getClass() == Circle.class && shape1.getClass() == Line.class) {
                Circle l0 = (Circle) shape0;
                Line l1 = (Line) shape1;
                isect = Intersections.intersectCircleLine(l0.getCenterX(), l0.getCenterY(), l0.getRadius(),
                        l1.getStartX(), l1.getStartY(), l1.getEndX(), l1.getEndY());
            } else if (shape0.getClass() == Circle.class && shape1.getClass() == Point.class) {
                Circle l0 = (Circle) shape0;
                Point l1 = (Point) shape1;
                isect = Intersections.intersectCirclePoint(l0.getCenterX(), l0.getCenterY(), l0.getRadius(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadius());
            } else if (shape0.getClass() == Circle.class && shape1.getClass() == Rectangle.class) {
                Circle l0 = (Circle) shape0;
                Rectangle l1 = (Rectangle) shape1;
                isect = Intersections.intersectCircleRectangle(l0.getCenterX(), l0.getCenterY(), l0.getRadius(),
                        l1.getX(), l1.getY(), l1.getWidth(), l1.getHeight()
                );
                //
            } else if (shape0.getClass() == CubicCurve.class && shape1.getClass() == Circle.class) {
                CubicCurve l0 = (CubicCurve) shape0;
                Circle l1 = (Circle) shape1;
                isect = Intersections.intersectCubicCurveCircle(
                        l0.getStartX(), l0.getStartY(), l0.getControlX1(), l0.getControlY1(), l0.getControlX2(), l0.getControlY2(), l0.getEndX(), l0.getEndY(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadius());
            } else if (shape0.getClass() == CubicCurve.class && shape1.getClass() == CubicCurve.class) {
                CubicCurve l0 = (CubicCurve) shape0;
                CubicCurve l1 = (CubicCurve) shape1;
                isect = Intersections.intersectCubicCurveCubicCurve(
                        l0.getStartX(), l0.getStartY(), l0.getControlX1(), l0.getControlY1(), l0.getControlX2(), l0.getControlY2(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getControlX1(), l1.getControlY1(), l1.getControlX2(), l1.getControlY2(), l1.getEndX(), l1.getEndY()
                );
            } else if (shape0.getClass() == CubicCurve.class && shape1.getClass() == Ellipse.class) {
                CubicCurve l0 = (CubicCurve) shape0;
                Ellipse l1 = (Ellipse) shape1;
                isect = Intersections.intersectCubicCurveEllipse(
                        l0.getStartX(), l0.getStartY(), l0.getControlX1(), l0.getControlY1(), l0.getControlX2(), l0.getControlY2(), l0.getEndX(), l0.getEndY(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadiusX(), l1.getRadiusY());
            } else if (shape0.getClass() == CubicCurve.class && shape1.getClass() == Line.class) {
                CubicCurve l0 = (CubicCurve) shape0;
                Line l1 = (Line) shape1;
                isect = Intersections.intersectCubicCurveLine(
                        l0.getStartX(), l0.getStartY(), l0.getControlX1(), l0.getControlY1(), l0.getControlX2(), l0.getControlY2(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getEndX(), l1.getEndY());
            } else if (shape0.getClass() == CubicCurve.class && shape1.getClass() == Point.class) {
                CubicCurve l0 = (CubicCurve) shape0;
                Point l1 = (Point) shape1;
                isect = Intersections.intersectCubicCurvePoint(
                        l0.getStartX(), l0.getStartY(), l0.getControlX1(), l0.getControlY1(), l0.getControlX2(), l0.getControlY2(), l0.getEndX(), l0.getEndY(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadius());

                if (isect.size() == 1) {
                    System.out.println("  t:" + isect.getFirstT() + " ctrlPoint:" + isect.getLastPoint());
                    double[] left = new double[6];
                    double[] right = new double[6];
                    BezierCurves.splitCubicCurve(l0.getStartX(), l0.getStartY(), l0.getControlX1(), l0.getControlY1(),
                            l0.getControlX2(), l0.getControlY2(), l0.getEndX(), l0.getEndY(), isect.getFirstT(),
                            left, right);
                    System.out.println("  left:" + Arrays.toString(left));
                    System.out.println("  right:" + Arrays.toString(right));
                    double[] newControlPoint = BezierCurves.mergeCubicCurve(
                            l0.getStartX(), l0.getStartY(), left[0], left[1], left[2], left[3], left[4], left[5], right[0], right[1], right[2], right[3], right[4], right[5], 1.0);
                    System.out.println("  new ctrlPoint:" + Arrays.toString(newControlPoint));
                }

            } else if (shape0.getClass() == CubicCurve.class && shape1.getClass() == QuadCurve.class) {
                CubicCurve l0 = (CubicCurve) shape0;
                QuadCurve l1 = (QuadCurve) shape1;
                isect = Intersections.intersectCubicCurveQuadraticCurve(
                        l0.getStartX(), l0.getStartY(), l0.getControlX1(), l0.getControlY1(), l0.getControlX2(), l0.getControlY2(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getControlX(), l1.getControlY(), l1.getEndX(), l1.getEndY());
                //
            } else if (shape0.getClass() == Ellipse.class && shape1.getClass() == Circle.class) {
                Ellipse e0 = (Ellipse) shape0;
                Circle l1 = (Circle) shape1;
                isect = Intersections.intersectEllipseCircle(e0.getCenterX(), e0.getCenterY(), e0.getRadiusX(), e0.getRadiusY(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadius());
            } else if (shape0.getClass() == Ellipse.class && shape1.getClass() == Ellipse.class) {
                Ellipse e0 = (Ellipse) shape0;
                Ellipse e1 = (Ellipse) shape1;
                isect = Intersections.intersectEllipseEllipse(e0.getCenterX(), e0.getCenterY(), e0.getRadiusX(), e0.getRadiusY(),
                        e1.getCenterX(), e1.getCenterY(), e1.getRadiusX(), e1.getRadiusY());
            } else if (shape0.getClass() == Ellipse.class && shape1.getClass() == Line.class) {
                Ellipse e0 = (Ellipse) shape0;
                Line l1 = (Line) shape1;
                isect = Intersections.intersectEllipseLine(e0.getCenterX(), e0.getCenterY(), e0.getRadiusX(), e0.getRadiusY(),
                        l1.getStartX(), l1.getStartY(), l1.getEndX(), l1.getEndY());
            } else if (shape0.getClass() == Ellipse.class && shape1.getClass() == Point.class) {
                Ellipse e0 = (Ellipse) shape0;
                Point e1 = (Point) shape1;
                //isect = Intersections.intersectEllipsePoint(e0.getCenterX(), e0.getCenterY(), e0.getRadiusX(), e0.getRadiusY(),
                //      e1.getCenterX(), e1.getCenterY(), e1.getRadius());
            } else if (shape0.getClass() == Ellipse.class && shape1.getClass() == QuadCurve.class) {
                Ellipse e0 = (Ellipse) shape0;
                QuadCurve l1 = (QuadCurve) shape0;
                isect = Intersections.intersectEllipseQuadraticCurve(e0.getCenterX(), e0.getCenterY(), e0.getRadiusX(), e0.getRadiusY(),
                        l1.getStartX(), l1.getStartY(), l1.getControlX(), l1.getControlY(), l1.getEndX(), l1.getEndY());
            } else if (shape0.getClass() == Line.class && shape1.getClass() == Circle.class) {
                Line l0 = (Line) shape0;
                Circle e1 = (Circle) shape1;
                isect = Intersections.intersectLineCircle(l0.getStartX(), l0.getStartY(), l0.getEndX(), l0.getEndY(),
                        e1.getCenterX(), e1.getCenterY(), e1.getRadius());
            } else if (shape0.getClass() == Line.class && shape1.getClass() == Ellipse.class) {
                Line l0 = (Line) shape0;
                Ellipse e1 = (Ellipse) shape1;
                isect = Intersections.intersectLineEllipse(l0.getStartX(), l0.getStartY(), l0.getEndX(), l0.getEndY(),
                        e1.getCenterX(), e1.getCenterY(), e1.getRadiusX(), e1.getRadiusY());
            } else if (shape0.getClass() == Line.class && shape1.getClass() == Point.class) {
                Line l0 = (Line) shape0;
                Point e1 = (Point) shape1;
                isect = Intersections.intersectLinePoint(l0.getStartX(), l0.getStartY(), l0.getEndX(), l0.getEndY(),
                        e1.getCenterX(), e1.getCenterY(), e1.getRadius());
            } else if (shape0.getClass() == Line.class && shape1.getClass() == Line.class) {
                Line l0 = (Line) shape0;
                Line l1 = (Line) shape1;
                isect = Intersections.intersectLineLine(l0.getStartX(), l0.getStartY(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getEndX(), l1.getEndY());
                //
            } else if (shape0.getClass() == QuadCurve.class && shape1.getClass() == QuadCurve.class) {
                QuadCurve l0 = (QuadCurve) shape0;
                QuadCurve l1 = (QuadCurve) shape1;
                isect = Intersections.intersectQuadraticCurveQuadraticCurve(l0.getStartX(), l0.getStartY(), l0.getControlX(), l0.getControlY(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getControlX(), l1.getControlY(), l1.getEndX(), l1.getEndY());
                //
            } else if (shape0.getClass() == QuadCurve.class && shape1.getClass() == Circle.class) {
                QuadCurve l0 = (QuadCurve) shape0;
                Circle l1 = (Circle) shape1;
                isect = Intersections.intersectQuadraticCurveCircle(l0.getStartX(), l0.getStartY(), l0.getControlX(), l0.getControlY(), l0.getEndX(), l0.getEndY(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadius());
                //
            } else if (shape0.getClass() == QuadCurve.class && shape1.getClass() == Ellipse.class) {
                QuadCurve l0 = (QuadCurve) shape0;
                Ellipse l1 = (Ellipse) shape1;
                isect = Intersections.intersectQuadraticCurveEllipse(l0.getStartX(), l0.getStartY(), l0.getControlX(), l0.getControlY(), l0.getEndX(), l0.getEndY(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadiusX(), l1.getRadiusY());
                //
            } else if (shape0.getClass() == QuadCurve.class && shape1.getClass() == Point.class) {
                QuadCurve l0 = (QuadCurve) shape0;
                Point l1 = (Point) shape1;
                isect = Intersections.intersectQuadraticCurvePoint(l0.getStartX(), l0.getStartY(), l0.getControlX(), l0.getControlY(), l0.getEndX(), l0.getEndY(),
                        l1.getCenterX(), l1.getCenterY(), l1.getRadius());

                if (isect.size() == 1) {
                    System.out.println("  t:" + isect.getFirstT() + " ctrlPoint:" + isect.getLastPoint());
                    double[] left = new double[4];
                    double[] right = new double[4];
                    BezierCurves.splitQuadCurveTo(l0.getStartX(), l0.getStartY(), l0.getControlX(), l0.getControlY(), l0.getEndX(), l0.getEndY(), isect.getFirstT(),
                            left, right);
                    System.out.println("  left:" + Arrays.toString(left));
                    System.out.println("  right:" + Arrays.toString(right));
                    double[] newControlPoint = BezierCurves.mergeQuadCurve(
                            l0.getStartX(), l0.getStartY(), left[0], left[1], left[2], left[3], right[0], right[1], right[2], right[3], 1.0);
                    System.out.println("  new ctrlPoints:" + newControlPoint);
                }

            } else if (shape0.getClass() == QuadCurve.class && shape1.getClass() == Line.class) {
                QuadCurve l0 = (QuadCurve) shape0;
                Line l1 = (Line) shape1;
                isect = Intersections.intersectQuadraticCurveLine(l0.getStartX(), l0.getStartY(), l0.getControlX(), l0.getControlY(), l0.getEndX(), l0.getEndY(),
                        l1.getStartX(), l1.getStartY(), l1.getEndX(), l1.getEndY());
                //
            } else if (shape0.getClass() == Rectangle.class && shape1.getClass() == Rectangle.class) {
                Rectangle l0 = (Rectangle) shape0;
                Rectangle l1 = (Rectangle) shape1;
                isect = Intersections.intersectRectangleRectangle(
                        l0.getX(), l0.getY(), l0.getWidth(), l0.getHeight(),
                        l1.getX(), l1.getY(), l1.getWidth(), l1.getHeight()
                );
            }

            if (isect != null) {
                double r = 3.5;
                for (Intersection.IntersectionPoint entry : isect.getIntersections()) {
                    Point2D p = entry.getPoint();
                    System.out.println("  p:" + p);
                    double x = p.getX();
                    double y = p.getY();
                    elems.add(new MoveTo(x - r, y));
                    elems.add(new ArcTo(r, r, 0, x + r, y, true, true));
                    elems.add(new ArcTo(r, r, 0, x - r, y, true, true));
                    elems.add(new ClosePath());
                }
            }
        }
    }

    private void updateShapes(@NonNull ActionEvent evt) {
        int index = (evt.getSource() == choice1) ? 0 : 1;
        Color color = index == 0 ? Color.GREEN : Color.CYAN;
        @SuppressWarnings("unchecked") final Map.Entry<Shape, List<Handle>> newEntry = createShapeAndHandles(((ChoiceBox<String>) evt.getSource()).getValue(), color, color);
        shapes.set(index, newEntry);
        canvas.getChildren().clear();

        for (Map.Entry<Shape, List<Handle>> entry : shapes) {
            if (entry != null) {
                canvas.getChildren().add(entry.getKey());
            }
        }

        for (Map.Entry<Shape, List<Handle>> entry : shapes) {
            if (entry != null) {
                List<Handle> handles = entry.getValue();
                for (Handle h : handles) {
                    canvas.getChildren().add(h.node);
                }
            }
        }
        canvas.getChildren().add(isectPath);
        updateHandles();
        updateIntersections();
    }

}
