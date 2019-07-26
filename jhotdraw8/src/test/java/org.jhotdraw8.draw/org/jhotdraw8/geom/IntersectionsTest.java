/* @(#)IntersectionsTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

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

    @TestFactory
    public List<DynamicTest> testIntersectLineQuadraticCurve_5argsFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testIntersectLineQuadraticCurve_5args(new Line(10, 40, 210, 175), new QuadCurve(125, 200, 250, 225, 275, 100), new double[]{}))
        );
    }

    @TestFactory
    public List<DynamicTest> testLinieEllipseFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testIntersectLineEllipse_5args(
                        new Line(10, 40, 200, 40), new Ellipse(100, 100, 60, 60), new double[]{0.47368421052631576})),
                dynamicTest("1", () -> testIntersectLineEllipse_5args(
                        new Line(10, 40, 200, 40), new Ellipse(100, 100, 50, 60), new double[]{0.47368421052631576}))
        );
    }

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
    public static void testIntersectLineQuadraticCurve_5args(Line a, QuadCurve b, double[] expected) {
        System.out.println("intersectLineBezier2");
        Point2D b1 = new Point2D(b.getStartX(), b.getEndX());
        Point2D b2 = new Point2D(b.getControlX(), b.getControlY());
        Point2D b3 = new Point2D(b.getEndX(), b.getEndX());
        Point2D a1 = new Point2D(a.getStartX(), a.getStartY());
        Point2D a2 = new Point2D(a.getEndX(), a.getEndY());
        System.out.println("line->bezier2");
        Intersection isec = Intersections.intersectLineQuadraticCurve(a1, a2, b1, b2, b3);
        System.out.println("  isec: " + isec);
        double[] actual = new double[isec.size()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = isec.getTs().get(i);
        }
        Arrays.sort(actual);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(actual[i], expected[i], 1e-6, "root #" + i);
        }
    }

    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    public static void testIntersectLineEllipse_5args(Line a, Ellipse b, double[] expected) {
        System.out.println("intersectLineEllipse");
        Point2D bc = new Point2D(b.getCenterX(), b.getCenterX());
        double brx = b.getRadiusX();
        double bry = b.getRadiusY();
        Point2D a1 = new Point2D(a.getStartX(), a.getStartY());
        Point2D a2 = new Point2D(a.getEndX(), a.getEndY());
        Intersection isec = Intersections.intersectLineEllipse(a1, a2, bc, brx, bry);
        System.out.println("  isec: " + isec);
        double[] actual = new double[isec.size()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = isec.getTs().get(i);
        }
        Arrays.sort(actual);
        for (int i = 0; i < expected.length; i++) {
            assertEquals(actual[i], expected[i], 1e-6, "root #" + i);
        }
    }

    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    public static void testIntersectCubicCurvePoint_11args(CubicCurve a, Circle b, double[] expected) {
        System.out.println("testIntersectBezier3Point_5args");
        System.out.println("bezier3->point");
        System.out.println("a:" + a);
        System.out.println("b:" + b);
        Intersection isec = Intersections.intersectCubicCurvePoint(
                a.getStartX(), a.getStartY(), a.getControlX1(), a.getControlY1(),
                a.getControlX2(), a.getControlY2(), a.getEndX(), a.getEndY(),
                b.getCenterX(), b.getCenterY(), b.getRadius());
        double[] actual = new double[isec.size()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = isec.getTs().get(i);
        }
        Arrays.sort(actual);
        Arrays.sort(expected);
        System.out.println("  expected: " + Arrays.toString(expected));
        System.out.println("  actual: " + Arrays.toString(actual));
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], 1e-6, "root #" + i);
        }
    }

    @TestFactory
    public List<DynamicTest> testIntersectLineLine() {
        return Arrays.asList(
                dynamicTest("1", () -> testIntersectLineLine(new Line(0, 0.0, 10.0, 0), new Line(5.0, -5.0, 5.0, 5.0), new double[]{0.5})),
                dynamicTest("2", () -> testIntersectLineLine(new Line(0, 0.0, 10.0, 0), new Line(50.0, -5.0, 5.0, 50.0), new double[]{}))
        );
    }

    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    public static void testIntersectLineLine(Line a, Line b, double[] expected) {
        Intersection isec = Intersections.intersectLineLine(a.getStartX(), a.getStartY(),
                a.getEndX(), a.getEndY(),
                b.getStartX(), b.getStartY(), b.getEndX(), b.getEndY());
        double[] actual = new double[isec.size()];
        for (int i = 0; i < actual.length; i++) {
            actual[i] = isec.getTs().get(i);
        }
        Arrays.sort(actual);
        Arrays.sort(expected);
        System.out.println("  expected: " + Arrays.toString(expected));
        System.out.println("  actual: " + Arrays.toString(actual));
        for (int i = 0; i < expected.length; i++) {
            assertEquals(expected[i], actual[i], 1e-6, "root #" + i);
        }
    }
}
