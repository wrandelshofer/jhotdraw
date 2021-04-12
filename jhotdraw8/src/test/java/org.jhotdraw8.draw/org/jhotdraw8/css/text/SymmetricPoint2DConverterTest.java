package org.jhotdraw8.css.text;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssSymmetricPoint2DConverterTest.
 *
 * @author Werner Randelshofer
 */
class SymmetricPoint2DConverterTest {

    /**
     * Test of fromString method, of class CssPoint2DConverterTest.
     */
    public static void doTestFromString(Point2D expected, @NonNull String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        SymmetricPoint2DConverter instance = new SymmetricPoint2DConverter(false);
        Point2D actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of fromString method, of class CssPoint2DConverterTest.
     */
    public static void doTestFromIllegalString(@NonNull String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        SymmetricPoint2DConverter instance = new SymmetricPoint2DConverter(false);
        try {
            Point2D actual = instance.fromString(buf, idFactory);
            fail();
        } catch (ParseException e) {

        }
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFromString() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestFromString(new Point2D(10, 20), "10 20")),
                dynamicTest("2", () -> doTestFromString(new Point2D(10, 20), "10 20 ")),
                dynamicTest("3", () -> doTestFromString(new Point2D(10, 20), "10, 20")),
                dynamicTest("4", () -> doTestFromString(new Point2D(10, 10), "10")),
                dynamicTest("5", () -> doTestFromString(new Point2D(10, 10), "10 "))
        );
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFromIllegalString() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestFromIllegalString("")),
                dynamicTest("2", () -> doTestFromIllegalString(",")),
                dynamicTest("3", () -> doTestFromIllegalString("10,"))
        );
    }

}