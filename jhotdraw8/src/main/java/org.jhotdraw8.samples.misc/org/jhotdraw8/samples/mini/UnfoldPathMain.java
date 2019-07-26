/*
 * @(#)UnfoldPathMain.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.samples.mini;

import javafx.application.Application;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.stage.Stage;
import javafx.util.Pair;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.OffsetPathBuilder;
import org.jhotdraw8.geom.Shapes;

import java.awt.BasicStroke;
import java.awt.geom.Path2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.BinaryOperator;
import java.util.function.UnaryOperator;

import static java.lang.Math.abs;
import static java.lang.Math.atan2;

/**
 * UnfoldPathMain.
 *
 * @author Werner Randelshofer
 */
public class UnfoldPathMain extends Application {

    @Nonnull
    private Polyline polyline = new Polyline();
    private double width = 40;
    @Nonnull
    private Path skeleton = new Path();
    @Nonnull
    private Path unfoldedSkeleton = new Path();
    @Nonnull
    private Path offsetPath = new Path();
    @Nonnull
    private Path builtOffsetPath = new Path();
    @Nonnull
    private Path strokedPath = new Path();
    @Nonnull
    private Path intersections = new Path();
    @Nonnull
    private Path intersections2 = new Path();
    @Nonnull
    private List<Point2D> points = new ArrayList<>();
    private Point2D pressedp;
    private double tolerance = 10;
    private int selected = -1;

    @Nonnull
    private QuadCurve curve3 = new QuadCurve(150, 100, 300, 700, 450, 100);

    @Nonnull
    private Polyline curve3V0 = new Polyline();
    @Nonnull
    private Polyline curve3V1 = new Polyline();
    @Nonnull
    private Polyline curve3V0Unfold = new Polyline();
    @Nonnull
    private Polyline curve3V1Unfold = new Polyline();

    private void applyToQuadCurve(List<Point2D> list1, QuadCurve curve) {
        curve.setStartX(list1.get(0).getX());
        curve.setStartY(list1.get(0).getY());
        curve.setControlX(list1.get(1).getX());
        curve.setControlY(list1.get(1).getY());
        curve.setEndX(list1.get(2).getX());
        curve.setEndY(list1.get(2).getY());
    }

    private List<PathElement> buildOffsetPath(List<Point2D> points, double width) {
        final FXPathBuilder fxPathBuilder = new FXPathBuilder();
        final OffsetPathBuilder builder = new OffsetPathBuilder(fxPathBuilder, width);
        if (!points.isEmpty()) {
            builder.moveTo(points.get(0).getX(), points.get(0).getY());
            for (int i = 1, n = points.size(); i < n; i++) {
                builder.lineTo(points.get(i).getX(), points.get(i).getY());
            }
            builder.pathDone();
        }
        return fxPathBuilder.getElements();
    }

    private int findIndex(@Nonnull Point2D mousep) {
        int index = -1;
        for (int i = 0, n = points.size(); i < n; i++) {
            Point2D p = points.get(i);
            if (p.distance(mousep) <= tolerance) {
                index = i;
                break;
            }
        }
        return index;
    }

    private void renderSkeleton(@Nonnull final ObservableList<PathElement> elements, List<Point2D> points) {
        for (int i = 0, n = points.size() - 1; i < n; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i + 1);
            Point2D perp = perp(p2.subtract(p1)).normalize();
            moveTo(elements, p1.subtract(perp.multiply(width / 2)));
            lineTo(elements, p1.add(perp.multiply(width / 2)));
            moveTo(elements, p2.subtract(perp.multiply(width / 2)));
            lineTo(elements, p2.add(perp.multiply(width / 2)));

        }
    }

    private void renderSkeleton(@Nonnull final ObservableList<PathElement> elements, List<Point2D> p1s, @Nonnull List<Point2D> p2s) {
        for (int i = 0, n = p1s.size(); i < n; i++) {
            Point2D p1 = p1s.get(i);
            Point2D p2 = p2s.get(i);
            moveTo(elements, p1);
            lineTo(elements, p2);
        }
    }

    private void setPoints(Path poly, List<Point2D> points) {
        final List<PathElement> elems = poly.getElements();
        boolean first = true;
        for (Point2D p : points) {
            if (first) {
                first = false;
                elems.add(new MoveTo(p.getX(), p.getY()));
            } else {
                elems.add(new LineTo(p.getX(), p.getY()));
            }
        }
    }

    private void renderOffsetPath(final ObservableList<PathElement> elements) {
        elements.clear();
        List<Point2D> offsetList = new ArrayList<>();
        for (int i = 0, n = points.size() - 1; i < n; i++) {
            Point2D p1 = points.get(i);
            Point2D p2 = points.get(i + 1);
            Point2D perp = perp(p2.subtract(p1)).normalize();
            final Point2D op1 = p1.add(perp.multiply(width / 2));
            moveTo(elements, op1);
            final Point2D op2 = p2.add(perp.multiply(width / 2));
            lineTo(elements, op2);
            offsetList.add(op1);
            offsetList.add(op2);
        }

        final ObservableList<PathElement> isects = intersections.getElements();
        final ObservableList<PathElement> isects2 = intersections2.getElements();
        isects.clear();
        isects2.clear();
        for (int i = 0, n = offsetList.size() - 2; i < n; i += 2) {
            Point2D isect = intersection(offsetList.get(i), offsetList.get(i + 1), offsetList.get(i + 2), offsetList.get(i + 3));
            if (isect != null) {
                addDisc(isects, isect);
            }
            Point2D isect2 = intersection2(offsetList.get(i), offsetList.get(i + 1), offsetList.get(i + 2), offsetList.get(i + 3));
            if (isect2 != null) {
                addDisc(isects2, isect2);
            }
        }
    }

    @Override
    public void start(@Nonnull Stage primaryStage) {

        Group root = new Group();

        Scene scene = new Scene(root, 300, 250);
        offsetPath.setStroke(Color.GREEN);
        intersections.setFill(Color.BLUE);
        intersections2.setFill(Color.CYAN);
        strokedPath.setFill(null);
        strokedPath.setStroke(Color.PURPLE);
        strokedPath.getStrokeDashArray().addAll(2.0, 10.0);

        builtOffsetPath.setStroke(Color.LIGHTGREEN);
        /*
        curve3.setFill(null);
        curve3.setStroke(Color.GRAY);
        curve3V0.setFill(null);
        curve3V0.setStroke(Color.LIGHTGRAY);
        curve3V1.setFill(null);
        curve3V1.setStroke(Color.LIGHTGRAY);
        curve3V0Unfold.setFill(null);
        curve3V0Unfold.setStroke(Color.PURPLE);
        curve3V1Unfold.setFill(null);
        curve3V1Unfold.setStroke(Color.PURPLE);
         */
        scene.addEventHandler(MouseEvent.ANY, this::onMouse);
        skeleton.setFill(null);
        skeleton.setStroke(Color.BLACK);
        skeleton.getStrokeDashArray().addAll(2.0, 10.0);
        unfoldedSkeleton.setFill(null);
        unfoldedSkeleton.setStroke(Color.RED);
        unfoldedSkeleton.getStrokeDashArray().addAll(2.0, 10.0);
        root.getChildren().add(polyline);
        root.getChildren().add(skeleton);
        root.getChildren().add(unfoldedSkeleton);
        root.getChildren().add(offsetPath);
        root.getChildren().add(builtOffsetPath);
        root.getChildren().add(intersections);
        root.getChildren().add(intersections2);
        root.getChildren().add(strokedPath);
        /*
        root.getChildren().add(curve3);
        root.getChildren().add(curve3V0);
        root.getChildren().add(curve3V1);
        root.getChildren().add(curve3V0Unfold);
        root.getChildren().add(curve3V1Unfold);
         */
        updateView();
        primaryStage.setTitle("Unfold Path");
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public void onMouse(@Nonnull MouseEvent evt) {
        Point2D mousep = new Point2D(evt.getSceneX(), evt.getSceneY());
        if (evt.getEventType() == MouseEvent.MOUSE_CLICKED) {
            if (mousep.equals(pressedp)) {
                int index = findIndex(mousep);
                if (index == -1) {
                    points.add(mousep);
                } else {
                    points.remove(selected);
                }
                updateView();
            }
        } else if (evt.getEventType() == MouseEvent.MOUSE_PRESSED) {
            pressedp = mousep;
            selected = findIndex(mousep);
        } else if (evt.getEventType() == MouseEvent.MOUSE_DRAGGED) {
            if (selected != -1) {
                points.set(selected, mousep);
            }
            updateView();
        }
    }

    private void updateView() {
        final ObservableList<Double> list = polyline.getPoints();
        list.clear();
        for (Point2D p : points) {
            list.add(p.getX());
            list.add(p.getY());
        }

        final ObservableList<PathElement> skeletonElements = skeleton.getElements();
        skeletonElements.clear();
        final ObservableList<PathElement> unfoldedElements = unfoldedSkeleton.getElements();
        unfoldedElements.clear();
        renderSkeleton(skeletonElements, points);
        intersections.getElements().clear();
        Pair<List<Point2D>, List<Point2D>> result1 = curveThicken(points, width, true);
        if (result1 != null) {
            offsetPath.getElements().clear();
            setPoints(offsetPath, result1.getKey());
            setPoints(offsetPath, result1.getValue());
            renderSkeleton(unfoldedElements, result1.getKey(), result1.getValue());
        }

        builtOffsetPath.getElements().setAll(buildOffsetPath(points, width + 2));


        BasicStroke stroke = new BasicStroke((float) width, BasicStroke.CAP_BUTT, BasicStroke.JOIN_BEVEL);
        Path2D path = new Path2D.Double();
        for (int i = 0, n = points.size(); i < n; i++) {
            Point2D p = points.get(i);
            if (i == 0) {
                path.moveTo(p.getX(), p.getY());
            } else {
                path.lineTo(p.getX(), p.getY());
            }
        }
        path = (Path2D) stroke.createStrokedShape(path);
        FXPathBuilder fxBuilder = new FXPathBuilder();
        Shapes.buildFromPathIterator(fxBuilder, path.getPathIterator(null));
        strokedPath.getElements().setAll(fxBuilder.getElements());

        //renderOffsetPath(offsetPath.getElements());

        /*
        List<Point2D> list3 = curve3Bezier(new Point2D(curve3.getStartX(), curve3.getStartY()),
                new Point2D(curve3.getControlX(), curve3.getControlY()),
                new Point2D(curve3.getEndX(), curve3.getEndY())
        );
        Pair<List<Point2D>, List<Point2D>> result = curveThicken(list3, width, false);
        if (result != null) {
            setPoints(curve3V0, result.getKey());
            setPoints(curve3V1, result.getValue());
            renderSkeleton(skeletonElements, result.getKey(), result.getValue());
        }
        Pair<List<Point2D>, List<Point2D>> resultUnfold = curveThicken(list3, width, true);
        if (resultUnfold != null) {
            setPoints(curve3V0Unfold, resultUnfold.getKey());
            setPoints(curve3V1Unfold, resultUnfold.getValue());
            renderSkeleton(unfoldedElements, resultUnfold.getKey(), resultUnfold.getValue());
        }*/
    }

    private void moveTo(List<PathElement> elements, Point2D p) {
        elements.add(new MoveTo(p.getX(), p.getY()));
    }

    private void lineTo(List<PathElement> elements, Point2D p) {
        elements.add(new LineTo(p.getX(), p.getY()));
    }

    double r = 1.5;

    /**
     * "M 0,0 m -1,0 a 1,1 0 1,0 2,0 a 1,1 0 1,0 -2,0 Z"
     *
     * @param elements
     * @param p
     */
    private void addDisc(List<PathElement> elements, Point2D p) {
        elements.add(new MoveTo(p.getX() - r, p.getY()));
        elements.add(new ArcTo(r, r, 0, p.getX() + r, p.getY(), true, true));
        elements.add(new ArcTo(r, r, 0, p.getX() - r, p.getY(), true, true));
        elements.add(new ClosePath());
    }

    /**
     * @param args the command line arguments
     */
    public static void main(String[] args) {
        launch(args);
    }

    private Point2D perp(Point2D p) {
        return new Point2D(p.getY(), -p.getX());
    }

    private Point2D intersection(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        double x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = p1.getX();
        y1 = p1.getY();
        x2 = p2.getX();
        y2 = p2.getY();
        x3 = p3.getX();
        y3 = p3.getY();
        x4 = p4.getX();
        y4 = p4.getY();
        double den = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (abs(den) < 1e-20) {
            return null;
        }
        double nom1 = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        double nom2 = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);
        double ua = nom1 / den;
        double ub = nom2 / den;
        if (0.0 <= ua && ua <= 1.0 && 0.0 <= ub && ub <= 1.0) {
            return p1.add((p2.subtract(p1).multiply(ua)));
        }
        return null;
    }

    private Point2D intersection2(Point2D p1, Point2D p2, Point2D p3, Point2D p4) {
        double x1, y1, x2, y2, x3, y3, x4, y4;
        x1 = p1.getX();
        y1 = p1.getY();
        x2 = p2.getX();
        y2 = p2.getY();
        x3 = p3.getX();
        y3 = p3.getY();
        x4 = p4.getX();
        y4 = p4.getY();
        double den = (x2 - x1) * (y4 - y3) - (y2 - y1) * (x4 - x3);
        if (abs(den) < 1e-20) {
            return null;
        }
        double nom1 = (x4 - x3) * (y1 - y3) - (y4 - y3) * (x1 - x3);
        double nom2 = (x2 - x1) * (y1 - y3) - (y2 - y1) * (x1 - x3);
        double ua = nom1 / den;
        double ub = nom2 / den;
        if (0.0 <= ua && 0.0 <= ub && ub <= 1.0) {
            return p1.add((p2.subtract(p1).multiply(ua)));
        }
        return null;
    }

    @Nonnull
    private List<Point2D> binary(List<Point2D> lista, @Nonnull List<Point2D> listb, @Nonnull BinaryOperator<Point2D> op) {
        int n = lista.size();
        List<Point2D> listc = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            listc.add(op.apply(lista.get(i), listb.get(i)));
        }
        return listc;
    }

    @Nonnull
    private List<Point2D> unary(List<Point2D> lista, @Nonnull UnaryOperator<Point2D> op) {
        int n = lista.size();
        List<Point2D> listc = new ArrayList<>();
        for (int i = 0; i < n; i++) {
            listc.add(op.apply(lista.get(i)));
        }
        return listc;
    }

    private double calc_sq_distance(double x1, double y1, double x2, double y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        return dx * dx + dy * dy;
    }

    private final static int curve_recursion_limit = 32;
    private final static double curve_distance_epsilon = 1e-30;
    private final static double curve_collinearity_epsilon = 1e-30;
    private final static double m_approximation_scale = 1.0;
    private final static double curve_angle_tolerance_epsilon = 0.01;
    private final static double m_angle_tolerance = 5 * Math.PI / 180.0;
    private final static double m_distance_tolerance_square = (0.5 / m_approximation_scale) * (0.5 / m_approximation_scale);

    private void curve3RecursiveBezier(@Nonnull List<Point2D> points, double x1, double y1, double x2, double y2, double x3, double y3, int level) {
        if (level > curve_recursion_limit) {
            return;
        }

        // Calculate all the mid-points of the line segments
        // -------------------------------------------------
        double x12, y12, x23, y23, x123, y123;
        x12 = (x1 + x2) / 2;
        y12 = (y1 + y2) / 2;
        x23 = (x2 + x3) / 2;
        y23 = (y2 + y3) / 2;
        x123 = (x12 + x23) / 2;
        y123 = (y12 + y23) / 2;

        double dx, dy, d;
        dx = x3 - x1;
        dy = y3 - y1;
        d = abs((x2 - x3) * dy - (y2 - y3) * dx);

        if (d > curve_collinearity_epsilon) {
            // Regular case
            // ------------
            if (d * d <= m_distance_tolerance_square * (dx * dx + dy * dy)) {
                // If the curvature doesn't exceed the distance_tolerance value
                // we tend to finish subdivisions.
                if (m_angle_tolerance < curve_angle_tolerance_epsilon) {
                    points.add(new Point2D(x123, y123));
                    return;
                }

                // Angle & Cusp Condition
                double da = abs(atan2(y3 - y2, x3 - x2) - atan2(y2 - y1, x2 - x1));
                if (da >= Math.PI) {
                    da = 2 * Math.PI - da;
                }
                if (da < m_angle_tolerance) {
                    // Finally we can stop the recursion
                    points.add(new Point2D(x123, y123));
                    return;
                }
            }
        } else {
            // Collinear case
            // --------------
            double da = dx * dx + dy * dy;
            if (da == 0) {
                d = calc_sq_distance(x1, y1, x2, y2);
            } else {
                d = ((x2 - x1) * dx + (y2 - y1) * dy) / da;
                if (d > 0 && d < 1) {
                    // Simple collinear case, 1---2---3, we can leave just two endpoints
                    return;
                }
                if (d <= 0) {
                    d = calc_sq_distance(x2, y2, x1, y1);
                } else if (d >= 1) {
                    d = calc_sq_distance(x2, y2, x3, y3);
                } else {
                    d = calc_sq_distance(x2, y2, x1 + d * dx, y1 + d * dy);
                }
            }
            if (d < m_distance_tolerance_square) {
                points.add(new Point2D(x2, y2));
                return;
            }
        }
        // Continue subdivision
        // --------------------
        curve3RecursiveBezier(points, x1, y1, x12, y12, x123, y123, level + 1);
        curve3RecursiveBezier(points, x123, y123, x23, y23, x3, y3, level + 1);

    }

    @Nonnull
    private List<Point2D> curve3Bezier(Point2D p1, Point2D p2, Point2D p3) {
        double x1, y1, x2, y2, x3, y3;
        x1 = p1.getX();
        y1 = p1.getY();
        x2 = p2.getX();
        y2 = p2.getY();
        x3 = p3.getX();
        y3 = p3.getY();
        List<Point2D> points = new ArrayList<>();
        curve3RecursiveBezier(points, x1, y1, x2, y2, x3, y3, 0);

        Point2D dp1 = points.get(0).subtract(p1);
        if (dp1.magnitude() > 1e-10) {
            points.add(0, p1);
        }
        Point2D dp3 = points.get(points.size() - 1).subtract(p3);
        if (dp3.magnitude() > 1e-10) {
            points.add(p3);
        }
        return points;
    }

    /**
     * @param V      polyline points
     * @param width  stroke width
     * @param unfold true if unfolding is needed
     * @return two bezier curves
     */
    private Pair<List<Point2D>, List<Point2D>> curveThicken(List<Point2D> V_, double width, boolean unfold) {
        int n = V_.size();
        if (n < 2) {
            return new Pair<>(Collections.emptyList(), Collections.emptyList());
        }
        List<Point2D> V0 = new ArrayList<>(n * 2);
        List<Point2D> V1 = new ArrayList<>(n * 2);
        List<Point2D> V = new ArrayList<>(n * 2);
        {
            Point2D p1 = V_.get(0);
            for (int i = 1; i < n; i++) {
                Point2D p2 = V_.get(i);
                Point2D perp = perp(p2.subtract(p1).normalize());
                V1.add(p1.add(perp.multiply(width / 2)));
                V0.add(p1.add(perp.multiply(width / -2)));
                V.add(p1);
                V1.add(p2.add(perp.multiply(width / 2)));
                V0.add(p2.add(perp.multiply(width / -2)));
                V.add(p2);
                p1 = p2;
            }
        }
        n = V.size();
        /*
        if (n < 2) {
            return new Pair<>(Collections.emptyList(), Collections.emptyList());
        }

        List<Point2D> T = binary(V.subList(1, n), V.subList(0, n - 1), (a, b) -> a.subtract(b));
        T = unary(T, Point2D::normalize);
        List<Point2D> N = unary(T, this::perp);
        if (N.size() < 2) {
            return new Pair<>(Collections.emptyList(), Collections.emptyList());
        }
        N.add(0, perp(V.get(1).subtract(V.get(0))).normalize());
        List<Point2D> V0 = binary(V, unary(N, a -> a.multiply(width / 2)), (a, b) -> a.add(b));
        List<Point2D> V1 = binary(V, unary(N, a -> a.multiply(-width / 2)), (a, b) -> a.add(b));
         */
        if (!unfold) {
            return new Pair<>(V0, V1);
        }

        List<Point2D> V0_ = new ArrayList<>(V0);
        List<Point2D> V1_ = new ArrayList<>(V1);

        for (int i = 0; i < n - 1; i++) {
            for (int j = n - 2; j > i + 1; j--) {
                Point2D p1 = V0.get(i), p2 = V0.get(i + 1);
                Point2D p3 = V0.get(j), p4 = V0.get(j + 1);
                Point2D p = intersection(p1, p2, p3, p4);
                if (p != null) {
                    addDisc(intersections.getElements(), p);
                    for (int k = i + 1; k < j + 1; k++) {
                        V0.set(k, p);
                    }
                    if (false) {
                        for (int k = i + 1; k <= j + 1; k++) {
                            for (int l = i; l < j + 1; l++) {
                                p = intersection2(V0.get(k), V.get(k), V1.get(l), V1_.get(l + 1));
                                if (p != null) {
                                    addDisc(intersections.getElements(), p);
                                    V1.set(k, p);
                                    break;
                                }
                            }
                        }
                    }
                    i = j - 1;
                    break;
                }
            }
        }
        for (int i = 0; i < n - 1; i++) {
            for (int j = n - 2; j > i + 1; j--) {
                Point2D p1 = V1.get(i), p2 = V1.get(i + 1);
                Point2D p3 = V1.get(j), p4 = V1.get(j + 1);
                Point2D p = intersection(p1, p2, p3, p4);
                if (p != null) {
                    addDisc(intersections.getElements(), p);
                    for (int k = i + 1; k < j + 1; k++) {
                        V1.set(k, p);
                    }
                    if (false) {
                        for (int k = i + 1; k <= j + 1; k++) {
                            for (int l = i; l < j + 1; l++) {
                                p = intersection2(V1.get(k), V.get(k), V0_.get(l), V0_.get(l + 1));
                                if (p != null) {
                                    addDisc(intersections.getElements(), p);
                                    V0.set(k, p);
                                    break;
                                }
                            }
                        }
                    }
                    i = j - 1;
                    break;
                }
            }
        }

        return new Pair<>(V0, V1);
    }

    /**
     * @param V      polyline points
     * @param width  stroke width
     * @param unfold true if unfolding is needed
     * @return two bezier curves
     */
    private Pair<List<Point2D>, List<Point2D>> curveThickenOriginal(List<Point2D> V, double width, boolean unfold) {
        int n = V.size();

        if (n < 2) {
            return new Pair<>(Collections.emptyList(), Collections.emptyList());
        }

        List<Point2D> T = binary(V.subList(1, n), V.subList(0, n - 1), (a, b) -> a.subtract(b));
        T = unary(T, Point2D::normalize);
        List<Point2D> N = unary(T, this::perp);
        if (N.size() < 2) {
            return new Pair<>(Collections.emptyList(), Collections.emptyList());
        }
        N.add(0, perp(V.get(1).subtract(V.get(0))).normalize());
        List<Point2D> V0 = binary(V, unary(N, a -> a.multiply(width / 2)), (a, b) -> a.add(b));
        List<Point2D> V1 = binary(V, unary(N, a -> a.multiply(-width / 2)), (a, b) -> a.add(b));

        if (!unfold) {
            return new Pair<>(V0, V1);
        }

        List<Point2D> V0_ = new ArrayList<>(V0);
        List<Point2D> V1_ = new ArrayList<>(V1);

        int i = 0;
        while (i < (n - 1)) {
            for (int j = n - 2; j > i + 2; j--) {
                Point2D p1 = V0.get(i), p2 = V0.get(i + 1);
                Point2D p3 = V0.get(j), p4 = V0.get(j + 1);
                Point2D p = intersection(p1, p2, p3, p4);
                if (p != null) {
                    addDisc(intersections.getElements(), p);
                    for (int k = i + 1; k < j + 1; k++) {
                        V0.set(k, p);
                    }
                    for (int k = i + 1; k < j + 1; k++) {
                        for (int l = 0; l < n - 1; l++) {
                            p = intersection2(V0.get(k), V.get(k), V1.get(l), V1_.get(l + 1));
                            if (p != null) {
                                addDisc(intersections.getElements(), p);
                                V1.set(k, p);
                                break;
                            }
                        }
                    }
                    i = j;
                    break;
                }
            }
            i += 1;
        }
        i = 0;
        while (i < (n - 1)) {
            for (int j = n - 2; j > i + 2; j--) {
                Point2D p1 = V1.get(i), p2 = V1.get(i + 1);
                Point2D p3 = V1.get(j), p4 = V1.get(j + 1);
                Point2D p = intersection(p1, p2, p3, p4);
                if (p != null) {
                    addDisc(intersections.getElements(), p);
                    for (int k = i + 1; k < j + 1; k++) {
                        V1.set(k, p);
                    }
                    for (int k = i + 1; k < j + 1; k++) {
                        for (int l = 0; l < n - 1; l++) {
                            p = intersection2(V1.get(k), V.get(k), V0_.get(l), V0_.get(l + 1));
                            if (p != null) {
                                addDisc(intersections.getElements(), p);
                                V0.set(k, p);
                                break;
                            }
                        }
                    }
                    i = j;
                    break;
                }
            }
            i += 1;
        }

        return new Pair<>(V0, V1);
    }
}
