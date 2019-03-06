package org.jhotdraw8.geom;

import javafx.scene.shape.PathElement;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.awt.geom.Path2D;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class ShapesTest {

    void testSvgStringFromAWT(String input, String expected) throws IOException {
        Path2D.Double path = Shapes.awtShapeFromSvgString(input);
        String actual = Shapes.doubleSvgStringFromAWT(path);
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> doubleSvgStringFromAWTFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testSvgStringFromAWT("M0,0 1,0 1,1 0,1Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("2", () -> testSvgStringFromAWT("m0,0 1,0 0,1 -1,0Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("3", () -> testSvgStringFromAWT("M0,0 1,0 1,1 0,1Z -1,0 -1,1 0,1 Z", "M0,0 1,0 1,1 0,1 Z L-1,0 -1,1 0,1 Z")),
                dynamicTest("4", () -> testSvgStringFromAWT("m0,0 1,0 0,1 -1,0z -1,0 0,1 1,0 z", "M0,0 1,0 1,1 0,1 Z L-1,0 -1,1 0,1 Z")),
                dynamicTest("5", () -> testSvgStringFromAWT(
                        "M600,350 l 50,-25 "
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "M600,350 650,325 " +
                                "C643.096440627115,311.19288125423026,648.6928812542301,294.40355937288496,662.5,287.50000000000006 " +
                                "676.3071187457697,280.59644062711504,693.096440627115,286.19288125423014,700,300 " +
                                "L750,275 " +
                                "C734.9913988585469,248.07943869303458,734.0174049332719,220.65957121240575,747.8245236790418,213.75601183952085 " +
                                "761.6316424248116,206.85245246663598,784.991398858547,223.07943869303463,800,250.00000000000006 " +
                                "L850,225 " +
                                "C827.1533531779083,184.81184586860041,819.8253677947489,146.636469309811,833.6324865405187,139.73290993692603 " +
                                "847.4396052862885,132.82935056404108,877.1533531779085,159.8118458686003,900,199.99999999999994 " +
                                "L950,175 " +
                                "C919.3820570331943,121.50571594634579,905.7541913714119,72.5435903100535,919.5613099397395,65.64003095406952 " +
                                "933.3684285080673,58.73647159808547,969.382056505971,96.5057159498736,999.9999998842609,149.99999979778607 " +
                                "L1050,125"))

        );
    }

    void testRelativeSvgStringFromAWT(String input, String expected) throws IOException {
        Path2D.Double path = Shapes.awtShapeFromSvgString(input);
        String actual = Shapes.doubleSvgStringFromAWT(path);
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> doubleRelativeSvgStringFromAWTFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testRelativeSvgStringFromAWT("M0,0 1,0 1,1 0,1Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("2", () -> testRelativeSvgStringFromAWT("m0,0 1,0 0,1 -1,0Z", "M0,0 1,0 1,1 0,1 Z")),
                dynamicTest("3", () -> testRelativeSvgStringFromAWT(
                        "M600,350 l 50,-25 "
                                + "a25,25 -30 0,1 50,-25 l 50,-25"
                                + "a25,50 -30 0,1 50,-25 l 50,-25"
                                + "a25,75 -30 0,1 50,-25 l 50,-25"
                                + "a25,100 -30 0,1 50,-25 l 50,-25",
                        "M600,350 650,325 " +
                                "C643.096440627115,311.19288125423026,648.6928812542301,294.40355937288496,662.5,287.50000000000006 " +
                                "676.3071187457697,280.59644062711504,693.096440627115,286.19288125423014,700,300 " +
                                "L750,275 " +
                                "C734.9913988585469,248.07943869303458,734.0174049332719,220.65957121240575,747.8245236790418,213.75601183952085 " +
                                "761.6316424248116,206.85245246663598,784.991398858547,223.07943869303463,800,250.00000000000006 " +
                                "L850,225 " +
                                "C827.1533531779083,184.81184586860041,819.8253677947489,146.636469309811,833.6324865405187,139.73290993692603 " +
                                "847.4396052862885,132.82935056404108,877.1533531779085,159.8118458686003,900,199.99999999999994 " +
                                "L950,175 " +
                                "C919.3820570331943,121.50571594634579,905.7541913714119,72.5435903100535,919.5613099397395,65.64003095406952 " +
                                "933.3684285080673,58.73647159808547,969.382056505971,96.5057159498736,999.9999998842609,149.99999979778607 " +
                                "L1050,125"))
        );
    }

    void testSvgStringFromElements(String input, String expected) throws IOException {
        List<PathElement> elements = Shapes.fxPathElementsFromSvgString(input);
        String actual = Shapes.doubleSvgStringFromElements(elements);
        assertEquals(expected, actual);
    }

    @TestFactory
    public List<DynamicTest> doubleSvgStringFromElementsFactory() {
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

}