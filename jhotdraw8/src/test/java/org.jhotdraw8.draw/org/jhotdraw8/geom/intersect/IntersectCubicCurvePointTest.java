/* @(#)IntersectionsTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom.intersect;

import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * IntersectionTest.
 *
 * @author Werner Randelshofer
 */
public class IntersectCubicCurvePointTest {


    @TestFactory
    public @NonNull List<DynamicTest> testIntersectCubicCurvePoint_11argsFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testIntersectCubicCurvePoint_11args(new CubicCurve(900.0, 700.0, 60.0, 100.0, 70.0, 700.0, 900.0, 100.0), new Circle(410.0, 400.0, 60.0), new double[]{0.7244335835816225})),
                dynamicTest("2", () -> testIntersectCubicCurvePoint_11args(new CubicCurve(200.0, 20.0, 40.0, 240.0, 40.0, 20.0, 200.0, 240.0), new Circle(130, 180, 40), new double[]{0.8548192690545715})),
                dynamicTest("3", () -> testIntersectCubicCurvePoint_11args(new CubicCurve(200.0, 20.0, 40.0, 240.0, 40.0, 20.0, 200.0, 240.0), new Circle(120, 180, 40), new double[]{0.8380940208991527}))
        );
    }


    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    public static void testIntersectCubicCurvePoint_11args(@NonNull CubicCurve a, @NonNull Circle b, @NonNull double[] expected) {
        System.out.println("testIntersectBezier3Point_5args");
        System.out.println("bezier3->point");
        System.out.println("a:" + a);
        System.out.println("b:" + b);
        IntersectionResult isec = IntersectCubicCurvePoint.intersectCubicCurvePoint(
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
}
