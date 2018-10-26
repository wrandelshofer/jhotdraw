package org.jhotdraw8.css.text;

import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import javafx.geometry.Point2D;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssSymmetricPoint2DConverterTest.
 *
 * @author Werner Randelshofer
 */
class CssSymmetricPoint2DConverterTest {

    /**
     * Test of fromString method, of class Dimension2DConverterTest.
     */
    static
    public void doTestFromString(Point2D expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssSymmetricPoint2DConverter instance = new CssSymmetricPoint2DConverter(false);
        Point2D actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestFromString(new Point2D(10,20), "10 20")),
                dynamicTest("2", () -> doTestFromString(new Point2D(10,10), "10")),
                dynamicTest("1", () -> doTestFromString(new Point2D(10,20), "10, 20")),
                dynamicTest("2", () -> doTestFromString(new Point2D(10,10), "10,"))
        );
    }

}