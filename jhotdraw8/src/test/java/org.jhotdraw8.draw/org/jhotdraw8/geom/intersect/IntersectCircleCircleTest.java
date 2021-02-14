/* @(#)IntersectionsTest.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom.intersect;

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
public class IntersectCircleCircleTest {


    @TestFactory
    public @NonNull List<DynamicTest> testCircleCircleFactory() {
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

    @TestFactory
    public @NonNull List<DynamicTest> testCircleCircleFactoryEx() {
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
            assertEquals(expected[i], points.get(i).getArgumentA(), 1e-6);
        }

    }
}
