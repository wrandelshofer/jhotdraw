/* @(#)IntersectionsTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom.intersect;

import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.awt.geom.Point2D;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * IntersectionsTest.
 *
 * @author Werner Randelshofer
 */
public class IntersectionsTest {

    @NonNull
    @TestFactory
    public List<DynamicTest> testIntersectLineQuadraticCurve_5argsFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testIntersectLineQuadraticCurve_5args(new Line(10, 40, 210, 175), new QuadCurve(125, 200, 250, 225, 275, 100), new double[]{}))
        );
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> testCircleCircleFactory() {
        return Arrays.asList(
                dynamicTest("2 intersections same radius", () -> testIntersectCircleCircle_5args(
                        100, 100, 100, 150, 100, 100, IntersectionStatus.INTERSECTION, new double[]{1.318116071652818, -1.318116071652818})),
                dynamicTest("2 intersections different radii", () -> testIntersectCircleCircle_5args(
                        100, 90, 80, 120, 90, 70, IntersectionStatus.INTERSECTION, new double[]{0.9350850413935946, -0.9350850413935945})),
                dynamicTest("coincident", () -> testIntersectCircleCircle_5args(
                        100, 100, 100, 100, 100, 100, IntersectionStatus.NO_INTERSECTION_COINCIDENT, new double[]{})),
                dynamicTest("1 intersection", () -> testIntersectCircleCircle_5args(
                        100, 100, 100, 300, 100, 100, IntersectionStatus.INTERSECTION, new double[]{0.0})),
                dynamicTest("0 intersections shapes are separate from each other", () -> testIntersectCircleCircle_5args(
                        100, 100, 100, 400, 100, 100, IntersectionStatus.NO_INTERSECTION_OUTSIDE, new double[]{})),
                dynamicTest("0 intersections shape1 inside the other with different centers", () -> testIntersectCircleCircle_5args(
                        100, 100, 10, 130, 100, 100, IntersectionStatus.NO_INTERSECTION_INSIDE, new double[]{})),
                dynamicTest("0 intersections shape1 outside the other with same center", () -> testIntersectCircleCircle_5args(
                        100, 100, 100, 100, 100, 10, IntersectionStatus.NO_INTERSECTION_OUTSIDE, new double[]{})),
                dynamicTest("0 intersections shape1 inside the other at same center", () -> testIntersectCircleCircle_5args(
                        100, 100, 10, 100, 100, 100, IntersectionStatus.NO_INTERSECTION_INSIDE, new double[]{}))
        );
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> testCircleCircleFactoryEx() {
        return Arrays.asList(
                dynamicTest("2 intersections same radius", () -> testIntersectCircleCircle_5argsEx(
                        100, 100, 100, 150, 100, 100, IntersectionStatus.INTERSECTION, new double[]{1.318116071652818, -1.318116071652818})),
                dynamicTest("2 intersections different radii", () -> testIntersectCircleCircle_5argsEx(
                        100, 90, 80, 120, 90, 70, IntersectionStatus.INTERSECTION, new double[]{0.9350850413935946, -0.9350850413935946})),
                dynamicTest("coincident", () -> testIntersectCircleCircle_5argsEx(
                        100, 100, 100, 100, 100, 100, IntersectionStatus.NO_INTERSECTION_COINCIDENT, new double[]{})),
                dynamicTest("1 intersection", () -> testIntersectCircleCircle_5argsEx(
                        100, 100, 100, 300, 100, 100, IntersectionStatus.INTERSECTION, new double[]{0.0})),
                dynamicTest("0 intersections shapes are separate from each other", () -> testIntersectCircleCircle_5argsEx(
                        100, 100, 100, 400, 100, 100, IntersectionStatus.NO_INTERSECTION_OUTSIDE, new double[]{})),
                dynamicTest("0 intersections shape1 inside the other with different centers", () -> testIntersectCircleCircle_5argsEx(
                        100, 100, 10, 130, 100, 100, IntersectionStatus.NO_INTERSECTION_INSIDE, new double[]{})),
                dynamicTest("0 intersections shape1 outside the other with same center", () -> testIntersectCircleCircle_5argsEx(
                        100, 100, 100, 100, 100, 10, IntersectionStatus.NO_INTERSECTION_OUTSIDE, new double[]{})),
                dynamicTest("0 intersections shape1 inside the other at same center", () -> testIntersectCircleCircle_5argsEx(
                        100, 100, 10, 100, 100, 100, IntersectionStatus.NO_INTERSECTION_INSIDE, new double[]{}))
        );
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> testLineEllipseFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testIntersectLineEllipse_5args(
                        new Line(10, 40, 200, 40), new Ellipse(100, 100, 60, 60), new double[]{0.47368421052631576})),
                dynamicTest("1", () -> testIntersectLineEllipse_5args(
                        new Line(10, 40, 200, 40), new Ellipse(100, 100, 50, 60), new double[]{0.47368421052631576}))
        );
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> testIntersectCubicCurvePoint_11argsFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testIntersectCubicCurvePoint_11args(new CubicCurve(900.0, 700.0, 60.0, 100.0, 70.0, 700.0, 900.0, 100.0), new Circle(410.0, 400.0, 60.0), new double[]{0.7244335835816225})),
                dynamicTest("2", () -> testIntersectCubicCurvePoint_11args(new CubicCurve(200.0, 20.0, 40.0, 240.0, 40.0, 20.0, 200.0, 240.0), new Circle(130, 180, 40), new double[]{0.8548192690545715})),
                dynamicTest("3", () -> testIntersectCubicCurvePoint_11args(new CubicCurve(200.0, 20.0, 40.0, 240.0, 40.0, 20.0, 200.0, 240.0), new Circle(120, 180, 40), new double[]{0.8380940208991527}))
        );
    }

    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    public static void testIntersectLineQuadraticCurve_5args(@NonNull Line a, @NonNull QuadCurve b, @NonNull double[] expected) {
        System.out.println("intersectLineBezier2");
        Point2D b1 = new Point2D.Double(b.getStartX(), b.getEndX());
        Point2D b2 = new Point2D.Double(b.getControlX(), b.getControlY());
        Point2D b3 = new Point2D.Double(b.getEndX(), b.getEndX());
        Point2D a1 = new Point2D.Double(a.getStartX(), a.getStartY());
        Point2D a2 = new Point2D.Double(a.getEndX(), a.getEndY());
        System.out.println("line->bezier2");
        IntersectionResultEx isec = IntersectLineQuadraticCurve.intersectLineQuadraticCurveEx(a1, a2, b1, b2, b3);
        System.out.println("  isec: " + isec);
        double[] actual = new double[isec.size()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = isec.getAllArgumentsA().get(i);
        }
        Arrays.sort(actual);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(actual[i], expected[i], 1e-6, "root #" + i);
        }
    }

    public static void testIntersectCircleCircle_5argsEx(double c1x, double c1y, double r1, double c2x, double c2y, double r2,
                                                         @NonNull IntersectionStatus expectedStatus, @NonNull double[] expected) {
        System.out.println("intersectCircleCircle");
        IntersectionResultEx isect = IntersectCircleCircle.intersectCircleCircleEx(c1x, c1y, r1, c2x, c2y, r2, Intersections.EPSILON);
        IntersectionStatus actualStatus = isect.getStatus();

        assertEquals(expectedStatus, actualStatus);
        List<IntersectionPointEx> points = isect.asList();
        assertEquals(expected.length, points.size());
        for (int i = 0; i < points.size(); i++) {
            assertEquals(expected[i], points.get(i).getArgumentA(), 1e-6);
        }

    }

    public static void testIntersectCircleCircle_5args(double c1x, double c1y, double r1, double c2x, double c2y, double r2,
                                                       @NonNull IntersectionStatus expectedStatus, @NonNull double[] expected) {
        System.out.println("intersectCircleCircle");
        IntersectionResult isect = IntersectCircleCircle.intersectCircleCircle(c1x, c1y, r1, c2x, c2y, r2, Intersections.EPSILON);
        IntersectionStatus actualStatus = isect.getStatus();

        assertEquals(expectedStatus, actualStatus);
        List<IntersectionPoint> points = isect.asList();
        assertEquals(expected.length, points.size());
        for (int i = 0; i < points.size(); i++) {
            assertEquals(expected[i], points.get(i).getArgument(), 1e-6);
        }

    }

    public static void testIntersectLineEllipse_5args(@NonNull Line a, @NonNull Ellipse b, @NonNull double[] expected) {
        System.out.println("intersectLineEllipse");
        Point2D bc = new Point2D.Double(b.getCenterX(), b.getCenterX());
        double brx = b.getRadiusX();
        double bry = b.getRadiusY();
        Point2D a1 = new Point2D.Double(a.getStartX(), a.getStartY());
        Point2D a2 = new Point2D.Double(a.getEndX(), a.getEndY());
        IntersectionResultEx isec = IntersectEllipseLine.intersectLineEllipseEx(a1, a2, bc, brx, bry);
        System.out.println("  isec: " + isec);
        double[] actual = new double[isec.size()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = isec.getAllArgumentsA().get(i);
        }
        Arrays.sort(actual);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(actual[i], expected[i], 1e-6, "root #" + i);
        }
    }

    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    public static void testIntersectCubicCurvePoint_11args(@NonNull CubicCurve a, @NonNull Circle b, @NonNull double[] expected) {
        System.out.println("testIntersectBezier3Point_5args");
        System.out.println("bezier3->point");
        System.out.println("a:" + a);
        System.out.println("b:" + b);
        IntersectionResultEx isec = IntersectCubicCurvePoint.intersectCubicCurvePointEx(
                a.getStartX(), a.getStartY(), a.getControlX1(), a.getControlY1(),
                a.getControlX2(), a.getControlY2(), a.getEndX(), a.getEndY(),
                b.getCenterX(), b.getCenterY(), b.getRadius());
        double[] actual = new double[isec.size()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = isec.getAllArgumentsA().get(i);
        }
        Arrays.sort(actual);
        Arrays.sort(expected);
        System.out.println("  expected: " + Arrays.toString(expected));
        System.out.println("  actual: " + Arrays.toString(actual));
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], 1e-6, "root #" + i);
        }
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> intersectLineCircleFactory() {
        return Arrays.asList(
                dynamicTest("intersection test case", () -> testIntersectLineCircle(
                        new Line(226.4340152101264, 42.75743509005417, 196.4340152101264, 112.75743509005417),
                        new Circle(191.48426774182056, 107.80768762174836, 7.000000000000003),
                        1e-8,
                        IntersectionStatus.INTERSECTION, new double[]{0.931727621126784, 1.0000000000000329}, new double[]{0.02438540917239233, 0.7853981633977785})),
                dynamicTest("line inside circle", () -> testIntersectLineCircle(
                        new Line(-5, 0, 5, 0),
                        new Circle(0, 0, 10),
                        1e-8,
                        IntersectionStatus.NO_INTERSECTION_INSIDE, new double[]{}, new double[]{})),
                dynamicTest("line outside circle", () -> testIntersectLineCircle(
                        new Line(15, 0, 20, 0),
                        new Circle(0, 0, 10),
                        1e-8,
                        IntersectionStatus.NO_INTERSECTION_OUTSIDE, new double[]{}, new double[]{})),
                dynamicTest("horizontal line through circle", () -> testIntersectLineCircle(
                        new Line(-20, 0, 20, 0),
                        new Circle(0, 0, 10),
                        1e-8,
                        IntersectionStatus.INTERSECTION, new double[]{0.25, 0.75}, new double[]{3.141592653589793, 0.0})),
                dynamicTest("horizontal line starts inside circle", () -> testIntersectLineCircle(
                        new Line(0, 0, 20, 0),
                        new Circle(0, 0, 10),
                        1e-8,
                        IntersectionStatus.INTERSECTION, new double[]{0.5}, new double[]{0.0})),
                dynamicTest("horizontal line tangent to circle", () -> testIntersectLineCircle(
                        new Line(-20, 10, 20, 10),
                        new Circle(0, 0, 10),
                        1e-8,
                        IntersectionStatus.INTERSECTION, new double[]{0.5}, new double[]{1.5707963267948966}))
        );
    }

    private void testIntersectLineCircle(Line line, Circle circle, double eps, IntersectionStatus expectedStatus, double[] expectedAs, double[] expectedBs) {
        IntersectionResultEx isect = IntersectCircleLine.intersectLineCircleEx(
                line.getStartX(), line.getStartY(), line.getEndX(), line.getEndY(),
                circle.getCenterX(), circle.getCenterY(), circle.getRadius(),
                eps);
        assertEquals(expectedStatus, isect.getStatus());
        assertEquals(DoubleArrayList.of(expectedAs), isect.getAllArgumentsA());
        assertEquals(DoubleArrayList.of(expectedBs), isect.getAllArgumentsB());
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> intersectLineLineFactory() {
        return Arrays.asList(
                dynamicTest("intersection", () -> testIntersectLineLine(
                        new Line(0, 0.0, 10.0, 0),
                        new Line(5.0, -5.0, 5.0, 5.0),
                        IntersectionStatus.INTERSECTION, new double[]{0.5}, new double[]{0.5})),
                dynamicTest("coincident horizontal", () -> testIntersectLineLine(
                        new Line(0, 0.0, 10.0, 0),
                        new Line(2.0, 0.0, 12.0, 0),
                        IntersectionStatus.NO_INTERSECTION_COINCIDENT,
                        new double[]{0.2, 1.0}, new double[]{0.0, 0.8})),
                dynamicTest("point after line", () -> testIntersectLineLine(
                        new Line(15.0, 0.0, 15.0, 0),
                        new Line(0.0, 0.0, 10.0, 0),
                        IntersectionStatus.NO_INTERSECTION_PARALLEL,
                        new double[]{}, new double[]{})),
                dynamicTest("point away from line", () -> testIntersectLineLine(
                        new Line(5.0, 5.0, 5.0, 5.0),
                        new Line(0.0, 0.0, 10.0, 0),
                        IntersectionStatus.NO_INTERSECTION_PARALLEL,
                        new double[]{}, new double[]{})),
                dynamicTest("point on line", () -> testIntersectLineLine(
                        new Line(5.0, 0.0, 5.0, 0),
                        new Line(0.0, 0.0, 10.0, 0),
                        IntersectionStatus.INTERSECTION,
                        new double[]{0}, new double[]{0.5})),
                dynamicTest("line on point", () -> testIntersectLineLine(
                        new Line(0.0, 0.0, 10.0, 0),
                        new Line(5.0, 0.0, 5.0, 0),
                        IntersectionStatus.INTERSECTION,
                        new double[]{0.5}, new double[]{0.0})),
                dynamicTest("coincident diagonal opposite direction test case", () -> testIntersectLineLine(
                        new Line(214.94974746830584, 94.94974746830583, 209.8994949366117, 100.0),
                        new Line(194.94974746830584, 114.94974746830583, 214.94974746830584, 94.94974746830583),
                        IntersectionStatus.NO_INTERSECTION_COINCIDENT,
                        new double[]{-0.0, 1.0}, new double[]{1.0, 0.7474873734152923})),
                dynamicTest("coincident diagonal opposite direction", () -> testIntersectLineLine(
                        new Line(100, 50, 50, 100.0),
                        new Line(0, 150, 150, 0),
                        IntersectionStatus.NO_INTERSECTION_COINCIDENT,
                        new double[]{0.0, 1.0}, new double[]{0.6666666666666666, 0.3333333333333333})),
                dynamicTest("parallel on same line", () -> testIntersectLineLine(
                        new Line(0, 0.0, 10.0, 0),
                        new Line(12.0, 0, 22.0, 0),
                        IntersectionStatus.NO_INTERSECTION_PARALLEL,
                        new double[]{}, new double[]{})),
                dynamicTest("parallel on different lines", () -> testIntersectLineLine(
                        new Line(0, 0.0, 10.0, 0),
                        new Line(0, 10, 10.0, 10),
                        IntersectionStatus.NO_INTERSECTION_PARALLEL,
                        new double[]{}, new double[]{})),
                dynamicTest("coincident diagonal", () -> testIntersectLineLine(
                        new Line(0, 0.0, 10.0, 10.0),
                        new Line(2.0, 2.0, 12.0, 12.0),
                        IntersectionStatus.NO_INTERSECTION_COINCIDENT,
                        new double[]{0.2, 1.0}, new double[]{0.0, 0.8}))
        );
    }

    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    public static void testIntersectLineLine(@NonNull Line a, @NonNull Line b, @NonNull IntersectionStatus expectedStatus,
                                             double[] expectedParamsA, double[] expectedParamsB) {
        IntersectionResultEx isec = IntersectLineLine.intersectLineLineEx(a.getStartX(), a.getStartY(),
                a.getEndX(), a.getEndY(),
                b.getStartX(), b.getStartY(), b.getEndX(), b.getEndY());

        DoubleArrayList actualA = isec.getAllArgumentsA();
        DoubleArrayList expectedA = DoubleArrayList.of(expectedParamsA);
        System.out.println("  expected A: " + expectedA);
        System.out.println("  actual A: " + actualA);
        assertEquals(expectedA, actualA);

        DoubleArrayList actualB = isec.getAllArgumentsB();
        DoubleArrayList expectedB = DoubleArrayList.of(expectedParamsB);
        System.out.println("  expected B: " + expectedB);
        System.out.println("  actual B: " + actualB);
        assertEquals(expectedB, actualB);

        assertEquals(expectedStatus, isec.getStatus());
    }
}
