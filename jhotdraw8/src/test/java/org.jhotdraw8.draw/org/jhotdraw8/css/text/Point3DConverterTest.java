package org.jhotdraw8.css.text;

import javafx.geometry.Point3D;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class Point3DConverterTest {
    /**
     * Test of fromString method, of class Point3DConverter.
     */
    static
    public void doTestFromString(Point3D expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        Point3DConverter instance = new Point3DConverter(false);
        Point3D actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test of toString method, of class Point3DConverter.
     */
    static
    public void doTestToString(Point3D value, String expected) throws Exception {
        System.out.println("toString " + value);
        Point3DConverter instance = new Point3DConverter(false);
        String actual = instance.toString(value);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of fromString and toString methods, of class Point3DConverter.
     */
    static
    public void doTest(Point3D value, String str) throws Exception {
        doTestFromString(value, str);
        doTestToString(value, str);
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTest(new Point3D(1, 2, 3), "1, 2, 3")),
                dynamicTest("1", () -> doTest(new Point3D(1, 2, 0), "1, 2"))
        );
    }

}