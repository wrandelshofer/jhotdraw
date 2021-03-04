/*
 * @(#)GeomTest.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class GeomTest {
    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsSinDegrees() {
        List<DynamicTest> list = new ArrayList<>();
        for (double i = -360; i <= 360; i += 0.5) {
            double aDeg = i;
            list.add(dynamicTest(aDeg + "", () -> testSinDegrees(aDeg)));
        }
        return list;
    }

    private void testSinDegrees(double aDeg) {
        double expected = Math.sin(Math.toRadians(aDeg));
        double actual = Geom.sinDegrees(aDeg);
        assertEquals(expected, actual, 0.0001, "sin(" + aDeg + "deg)");
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsCosDegrees() {
        List<DynamicTest> list = new ArrayList<>();
        for (double i = -360; i <= 360; i += 0.5) {
            double aDeg = i;
            list.add(dynamicTest(aDeg + "", () -> testCosDegrees(aDeg)));
        }
        return list;
    }

    private void testCosDegrees(double aDeg) {
        double expected = Math.cos(Math.toRadians(aDeg));
        double actual = Geom.cosDegrees(aDeg);
        assertEquals(expected, actual, 0.0001, "sin(" + aDeg + "deg)");
    }
}