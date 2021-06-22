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
        Path2D.Double path = SvgPaths.awtShapeFromSvgString(input);
        String actual = SvgPaths.doubleSvgStringFromAwt(path);
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
        Path2D.Double path = SvgPaths.awtShapeFromSvgString(input);
        String actual = SvgPaths.doubleRelativeSvgStringFromAWT(path.getPathIterator(null));
        assertEquals(expected, actual);
    }

    void testFloatRelativeSvgStringFromAWT(@NonNull String input, String expected) throws ParseException {
        Path2D.Double path = SvgPaths.awtShapeFromSvgString(input);
        String actual = SvgPaths.floatRelativeSvgStringFromAWT(path.getPathIterator(null));
        assertEquals(expected, actual);
    }

    void testFloatSvgStringFromAWT(@NonNull String input, String expected) throws ParseException {
        Path2D.Double path = SvgPaths.awtShapeFromSvgString(input);
        String actual = SvgPaths.floatSvgStringFromAWT(path.getPathIterator(null));
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
                        "M600,350 l50,-25"
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "M600,350 650,325" +
                                " C643.09644,311.19287,648.6929,294.40356,662.5,287.5 676.3071,280.59644,693.09644,286.19287,700,300" +
                                " L750,275" +
                                " C734.9914,248.07944,734.0174,220.65958,747.8245,213.75601 761.63165,206.85245,784.9914,223.07944,800,250" +
                                " L850,225" +
                                " C827.1534,184.81184,819.8254,146.63647,833.6325,139.73291 847.4396,132.82935,877.1534,159.81184,900,200 L950,175" +
                                " C919.3821,121.505714,905.7542,72.54359,919.5613,65.64003 933.3684,58.73647,969.3821,96.505714,1000,150" +
                                " L1050,125"))

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
        List<PathElement> elements = FXSvgPaths.fxPathElementsFromSvgString(input);
        String actual = FXSvgPaths.doubleSvgStringFromElements(elements);
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
                        "M600,350 l 50,-25"
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "m600,350 50,-25" +
                                " c-6.9035645,-13.807129,-1.3071289,-30.596436,12.5,-37.5 13.807129,-6.9035645,30.596436,-1.3071289,37.5,12.5" +
                                " l50,-25" +
                                " c-15.008606,-26.920563,-15.982605,-54.340424,-2.175476,-61.243988 13.807129,-6.9035645,37.16687,9.323425,52.175476,36.243988" +
                                " l50,-25" +
                                " c-22.846619,-40.188156,-30.174622,-78.363525,-16.367493,-85.26709 13.807068,-6.9035645,43.520874,20.078934,66.36749,60.26709" +
                                " l50,-25" +
                                " c-30.61792,-53.494286,-44.24579,-102.45641,-30.43872,-109.35997 13.807129,-6.9035606,49.8208,30.865685,80.43872,84.35997" +
                                " l50,-25"))
        );
    }
}