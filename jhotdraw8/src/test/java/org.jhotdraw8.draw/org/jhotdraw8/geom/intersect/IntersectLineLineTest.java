/* @(#)IntersectionsTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom.intersect;

import javafx.scene.shape.Line;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
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
public class IntersectLineLineTest {
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
