package org.jhotdraw8.geom;

import javafx.scene.shape.PathElement;
import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.awt.geom.Path2D;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * This test requires Java SE 9 or higher because in Java 9 the precision (and performance) of
 * Math.toRadians() has been improved.
 */
class ShapesTest {

    void testDoubleSvgStringFromAWT(@NonNull String input, String expected) throws ParseException {
        Path2D.Double path = Shapes.awtShapeFromSvgString(input);
        String actual = Shapes.doubleSvgStringFromAWT(path);
        assertEquals(expected, actual);
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsDoubleSvgStringFromAWT() {
        return Arrays.asList(
                dynamicTest("1", () -> testDoubleSvgStringFromAWT("M0,0 1,0 1,1 0,1Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("2", () -> testDoubleSvgStringFromAWT("m0,0 1,0 0,1 -1,0Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("3", () -> testDoubleSvgStringFromAWT("M0,0 1,0 1,1 0,1Z -1,0 -1,1 0,1 Z", "M0,0 1,0 1,1 0,1 Z L-1,0 -1,1 0,1 Z")),
                dynamicTest("4", () -> testDoubleSvgStringFromAWT("m0,0 1,0 0,1 -1,0z -1,0 0,1 1,0 z", "M0,0 1,0 1,1 0,1 Z L-1,0 -1,1 0,1 Z")),
                dynamicTest("5", () -> testDoubleSvgStringFromAWT(
                        "M600,350 l 50,-25 "
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "M600,350 650,325 " +
                                "C643.096440627115,311.1928812542302,648.6928812542301,294.40355937288496,662.5,287.5 " +
                                "676.3071187457698,280.5964406271151,693.096440627115,286.19288125423014,700,300 " +
                                "L750,275 " +
                                "C734.991398858547,248.0794386930346,734.0174049332719,220.65957121240578,747.8245236790418,213.75601183952088 " +
                                "761.6316424248116,206.85245246663598,784.991398858547,223.07943869303466,800,250 " +
                                "L850,225 " +
                                "C827.1533531779085,184.81184586860044,819.8253677947489,146.636469309811,833.6324865405187,139.73290993692603 " +
                                "847.4396052862885,132.82935056404108,877.1533531779083,159.8118458686003,900,200 " +
                                "L950,175 " +
                                "C919.3820565192525,121.50571547835688,905.7541904518346,72.54358897168744,919.5613091976044,65.64002959880229 " +
                                "933.3684279433741,58.736470225917145,969.3820565192519,96.50571547835607,1000,150 " +
                                "L1050,125"))

        );
    }

    void testDoubleRelativeSvgStringFromAWT(@NonNull String input, String expected) throws ParseException {
        Path2D.Double path = Shapes.awtShapeFromSvgString(input);
        String actual = Shapes.doubleRelativeSvgStringFromAWT(path.getPathIterator(null));
        assertEquals(expected, actual);
    }

    void testFloatRelativeSvgStringFromAWT(@NonNull String input, String expected) throws ParseException {
        Path2D.Double path = Shapes.awtShapeFromSvgString(input);
        String actual = Shapes.floatRelativeSvgStringFromAWT(path.getPathIterator(null));
        assertEquals(expected, actual);
    }

    void testFloatSvgStringFromAWT(@NonNull String input, String expected) throws ParseException {
        Path2D.Double path = Shapes.awtShapeFromSvgString(input);
        String actual = Shapes.floatSvgStringFromAWT(path.getPathIterator(null));
        assertEquals(expected, actual);
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFloatSvgStringFromAWT() {
        return Arrays.asList(
                dynamicTest("1", () -> testFloatSvgStringFromAWT("M0,0 1,0 1,1 0,1Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("2", () -> testFloatSvgStringFromAWT("m0,0 1,0 0,1 -1,0Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("3", () -> testFloatSvgStringFromAWT("M0,0 1,0 1,1 0,1Z -1,0 -1,1 0,1 Z", "M0,0 1,0 1,1 0,1 Z L-1,0 -1,1 0,1 Z")),
                dynamicTest("4", () -> testFloatSvgStringFromAWT("m0,0 1,0 0,1 -1,0z -1,0 0,1 1,0 z", "M0,0 1,0 1,1 0,1 Z L-1,0 -1,1 0,1 Z")),
                dynamicTest("5", () -> testFloatSvgStringFromAWT(
                        "M600,350 l50,-25 "
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "M600,350 " +
                                "650,325 " +
                                "C643.09643555,311.19287109,648.69287109,294.40356445,662.5,287.5 " +
                                "676.30712891,280.59643555,693.09643555,286.19287109,700,300 " +
                                "L750,275 " +
                                "C734.99139404,248.07943726,734.01739502,220.65957642,747.82452393,213.75601196 " +
                                "761.63165283,206.85244751,784.99139404,223.07943726,800,250 " +
                                "L850,225 " +
                                "C827.15338135,184.81184387,819.82537842,146.63647461,833.63250732,139.73291016 " +
                                "847.4395752,132.8293457,877.15338135,159.81184387,900,200 " +
                                "L950,175 " +
                                "C919.38208008,121.50571442,905.75421143,72.54358673,919.5612793,65.64002991 " +
                                "933.3684082,58.73646927,969.38208008,96.50571442,1000,150 " +
                                "L1050,125"))

        );
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsDoubleRelativeSvgStringFromAWT() {
        return Arrays.asList(
                dynamicTest("1", () -> testDoubleRelativeSvgStringFromAWT("M0,0 1,0 1,1 0,1Z", "m0,0 1,0 0,1 -1,0 z")),
                dynamicTest("2", () -> testDoubleRelativeSvgStringFromAWT("m0,0 1,0 0,1 -1,0Z", "m0,0 1,0 0,1 -1,0 z")),
                dynamicTest("3", () -> testDoubleRelativeSvgStringFromAWT(
                        "M600,350 l 50,-25 "
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "m600,350 50,-25 " +
                                "c-6.903559372884956,-13.807118745769799,-1.3071187457699125,-30.596440627115044,12.5,-37.5 " +
                                "13.807118745769799,-6.903559372884899,30.596440627115044,-1.3071187457698556,37.5,12.5 " +
                                "l50,-25 " +
                                "c-15.008601141453028,-26.920561306965396,-15.982595066728095,-54.34042878759422,-2.1754763209581824,-61.24398816047912 " +
                                "13.807118745769799,-6.903559372884899,37.166875179505155,9.32342685351378,52.17547632095818,36.24398816047912 " +
                                "l50,-25 " +
                                "c-22.846646822091543,-40.18815413139956,-30.1746322052511,-78.36353069018901,-16.3675134594813,-85.26709006307397 " +
                                "13.807118745769799,-6.903559372884956,43.520866637389645,20.078935931674266,66.3675134594813,60.267090063073965 " +
                                "l50,-25 " +
                                "c-30.6179434807475,-53.494284521643124,-44.245809548165425,-102.45641102831256,-30.438690802395627,-109.35997040119771 " +
                                "13.807118745769685,-6.903559372885141,49.82074732164756,30.86568587955378,80.43869080239563,84.35997040119771 " +
                                "l50,-25"))
        );
    }

    void testSvgStringFromElements(@NonNull String input, String expected) throws ParseException {
        List<PathElement> elements = Shapes.fxPathElementsFromSvgString(input);
        String actual = Shapes.doubleSvgStringFromElements(elements);
        assertEquals(expected, actual);
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsDoubleSvgStringFromElements() {
        return Arrays.asList(
                dynamicTest("1", () -> testSvgStringFromElements("M0,0 1,0 1,1 0,1Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("2", () -> testSvgStringFromElements("m0,0 1,0 0,1 -1,0Z", "m0,0 1,0 0,1 -1,0 Z")),
                dynamicTest("3", () -> testSvgStringFromElements(
                        "M600,350 l 50,-25 "
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "M600,350 l50,-25 "
                                + "a25,25 -30 0,1 50,-25 l50,-25 "
                                + "a25,50 -30 0,1 50,-25 l50,-25 "
                                + "a25,75 -30 0,1 50,-25 l50,-25 "
                                + "a25,100 -30 0,1 50,-25 l50,-25"))
        );
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFloatRelativeSvgStringFromAWT() {
        return Arrays.asList(
                dynamicTest("1", () -> testFloatRelativeSvgStringFromAWT("M0,0 1,0 1,1 0,1Z", "m0,0 1,0 0,1 -1,0 z")),
                dynamicTest("2", () -> testFloatRelativeSvgStringFromAWT("m0,0 1,0 0,1 -1,0Z", "m0,0 1,0 0,1 -1,0 z")),
                dynamicTest("3", () -> testFloatRelativeSvgStringFromAWT(
                        "M600,350 l 50,-25 "
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "m600,350 50,-25 " +
                                "c-6.90356445,-13.80712891,-1.30712891,-30.59643555,12.5,-37.5 " +
                                "13.80712891,-6.90356445,30.59643555,-1.30712891,37.5,12.5 " +
                                "l50,-25 " +
                                "c-15.00860596,-26.92056274,-15.98260498,-54.34042358,-2.17547607,-61.24398804 " +
                                "13.80712891,-6.90356445,37.16687012,9.32342529,52.17547607,36.24398804 l50,-25 " +
                                "c-22.84661865,-40.18815613,-30.17462158,-78.36352539,-16.36749268,-85.26708984 " +
                                "13.80706787,-6.90356445,43.52087402,20.07893372,66.36749268,60.26708984 " +
                                "l50,-25 " +
                                "c-30.61791992,-53.49428558,-44.24578857,-102.45641327,-30.4387207,-109.35997009 " +
                                "13.80712891,-6.90356064,49.82080078,30.86568451,80.4387207,84.35997009 " +
                                "l50,-25"))
        );
    }
}