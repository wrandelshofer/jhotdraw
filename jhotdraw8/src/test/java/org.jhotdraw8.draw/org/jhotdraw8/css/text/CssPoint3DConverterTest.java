package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssPoint3D;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CssPoint3DConverterTest {
    /**
     * Test of fromString method, of class CssPoint3DConverter.
     */
    public static void doTestFromString(CssPoint3D expected, @NonNull String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssPoint3DConverter instance = new CssPoint3DConverter(false);
        CssPoint3D actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test of toString method, of class CssPoint3DConverter.
     */
    public static void doTestToString(CssPoint3D value, String expected) throws Exception {
        System.out.println("toString " + value);
        CssPoint3DConverter instance = new CssPoint3DConverter(false);
        String actual = instance.toString(value);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(expected, actual);
    }

    /**
     * Test of fromString and toString methods, of class CssPoint3DConverter.
     */
    public static void doTest(CssPoint3D value, @NonNull String str) throws Exception {
        doTestFromString(value, str);
        doTestToString(value, str);
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFromString() {
        return Arrays.asList(
                dynamicTest("1", () -> doTest(new CssPoint3D(1, 2, 3), "1 2 3")),
                dynamicTest("1", () -> doTest(new CssPoint3D(1, 2, 0), "1 2"))
        );
    }

}