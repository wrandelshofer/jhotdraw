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
    public @NonNull List<DynamicTest> doubleSvgStringFromAWTFactory() {
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
                                "C643.096440627115,311.19288125423026,648.6928812542301,294.40355937288496,662.5,287.50000000000006 " +
                                "676.3071187457697,280.59644062711504,693.096440627115,286.19288125423014,700,300 " +
                                "L750,275 " +
                                "C734.991398858547,248.07943869303458,734.0174049332719,220.65957121240578,747.8245236790418,213.75601183952085 " +
                                "761.6316424248116,206.85245246663598,784.991398858547,223.0794386930346,800,250 " +
                                "L850,225 " +
                                "C827.1533531779083,184.81184586860041,819.8253677947489,146.636469309811,833.6324865405187,139.73290993692603 " +
                                "847.4396052862885,132.82935056404108,877.1533531779085,159.8118458686003,900,199.99999999999994 " +
                                "L950,175 " +
                                "C919.3820570331943,121.50571594634573,905.7541913714119,72.5435903100535,919.5613099397397,65.64003095406946 " +
                                "933.3684285080673,58.73647159808553,969.382056505971,96.50571594987366,999.9999998842609,149.99999979778607 " +
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
    public @NonNull List<DynamicTest> floatSvgStringFromAWTFactory() {
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
                                "L850,225 C827.15338135,184.81184387,819.82537842,146.63647461,833.63250732,139.73291016 " +
                                "847.4395752,132.8293457,877.15338135,159.81184387,900,200 " +
                                "L950,175 " +
                                "C919.38208008,121.50571442,905.75421143,72.54358673,919.56134033,65.64002991 " +
                                "933.3684082,58.73647308,969.38208008,96.50571442,1000,150 " +
                                "L1050,125"))

        );
    }

    @TestFactory
    public @NonNull List<DynamicTest> doubleRelativeSvgStringFromAWTFactory() {
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
                                "c-6.903559372884956,-13.807118745769742,-1.3071187457699125,-30.596440627115044,12.5,-37.49999999999994 " +
                                "13.807118745769685,-6.903559372885013,30.596440627115044,-1.3071187457699125,37.5,12.499999999999943 " +
                                "l50,-25 " +
                                "c-15.008601141453028,-26.920561306965425,-15.982595066728095,-54.34042878759422,-2.1754763209581824,-61.24398816047915 13.807118745769799,-6.903559372884871,37.166875179505155,9.323426853513752,52.17547632095818,36.24398816047915 " +
                                "l50,-25 " +
                                "c-22.846646822091657,-40.188154131399585,-30.1746322052511,-78.36353069018901,-16.3675134594813,-85.26709006307397 13.807118745769799,-6.903559372884956,43.52086663738976,20.078935931674266,66.3675134594813,60.26709006307391 " +
                                "l50,-24.999999999999943 " +
                                "c-30.617942966805686,-53.49428405365427,-44.245808628588065,-102.4564096899465,-30.438690060260342,-109.35996904593054 13.80711856832761,-6.90355935598393,49.82074656623138,30.8656849958042,80.43868994452123,84.35996884371662 " +
                                "l50.00000011573911,-24.999999797786074"))
        );
    }

    void testSvgStringFromElements(@NonNull String input, String expected) throws ParseException {
        List<PathElement> elements = Shapes.fxPathElementsFromSvgString(input);
        String actual = Shapes.doubleSvgStringFromElements(elements);
        assertEquals(expected, actual);
    }

    @TestFactory
    public @NonNull List<DynamicTest> doubleSvgStringFromElementsFactory() {
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
    public @NonNull List<DynamicTest> floatRelativeSvgStringFromAWTFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testFloatRelativeSvgStringFromAWT("M0,0 1,0 1,1 0,1Z", "m0,0 1,0 0,1 -1,0 z")),
                dynamicTest("2", () -> testFloatRelativeSvgStringFromAWT("m0,0 1,0 0,1 -1,0Z", "m0,0 1,0 0,1 -1,0 z")),
                dynamicTest("3", () -> testFloatRelativeSvgStringFromAWT(
                        "M600,350 l 50,-25 "
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "m600,350 " +
                                "50,-25 " +
                                "c-6.903564453125,-13.80712890625,-1.30712890625,-30.596435546875,12.5,-37.5 " +
                                "13.80712890625,-6.903564453125,30.596435546875,-1.30712890625,37.5,12.5 " +
                                "l50,-25 " +
                                "c-15.00860595703125,-26.920562744140625,-15.98260498046875,-54.340423583984375,-2.17547607421875,-61.243988037109375 " +
                                "13.80712890625,-6.903564453125,37.1668701171875,9.32342529296875,52.17547607421875,36.243988037109375 " +
                                "l50,-25 " +
                                "c-22.84661865234375,-40.18815612792969,-30.17462158203125,-78.363525390625,-16.36749267578125,-85.26708984375 " +
                                "13.80706787109375,-6.903564453125,43.5208740234375,20.078933715820312,66.36749267578125,60.26708984375 " +
                                "l50,-25 " +
                                "c-30.617919921875,-53.494285583496094,-44.24578857421875,-102.45641326904297,-30.43865966796875,-109.35997009277344 " +
                                "13.80706787109375,-6.903556823730469,49.82073974609375,30.865684509277344,80.43865966796875,84.35997009277344 " +
                                "l50,-25"))
        );
    }
}