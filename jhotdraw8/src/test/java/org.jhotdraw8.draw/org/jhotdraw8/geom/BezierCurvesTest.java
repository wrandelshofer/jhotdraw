package org.jhotdraw8.geom;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.collection.DoubleArrayList;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class BezierCurvesTest {
    @TestFactory
    public @NonNull List<DynamicTest> inflectionsTestFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testInflections(new double[]{135, 25, 25, 135, 215, 75, 215, 240}, new double[]{0.5059963709191709})),
                dynamicTest("2", () -> testInflections(new double[]{80, 40, 210, 40, 190, 110, 190, 20}, new double[]{0.8484087766415317, 0.4746000729159903}))
        );
    }

    private void testInflections(double[] b, double[] expected) {
        DoubleArrayList inflections = BezierCurves.inflectionPoints(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7]);
        assertArrayEquals(expected, inflections.toArray());
    }

    @TestFactory
    public @NonNull List<DynamicTest> alignTestFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testAlign(new double[]{135, 25, 25, 135, 215, 75, 215, 240},
                        new double[]{0, 0,
                                64.73369529397579, 141.4551119386878,
                                74.75978951459155, -57.54106248353403,
                                229.40139493908922, -1.4210854715202004e-14}
                ))
        );
    }

    private void testAlign(double[] b, double[] expected) {
        double[] actual = BezierCurves.align(b[0], b[1], b[2], b[3], b[4], b[5], b[6], b[7]);
        assertArrayEquals(expected, actual);
    }
}