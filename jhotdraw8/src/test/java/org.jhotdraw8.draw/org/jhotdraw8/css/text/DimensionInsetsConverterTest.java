package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssInsets;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class DimensionInsetsConverterTest {

    /**
     * Test of fromString method, of class CssPoint2DConverterTest.
     */
    static
    public void doTestFromString(CssInsets expected, @NonNull String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssInsetsConverter instance = new CssInsetsConverter(false);
        CssInsets actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    @NonNull
    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestFromString(new CssInsets(10, 20, 30, 40, "mm"), "10mm 20mm 30mm 40mm")),
                dynamicTest("2", () -> doTestFromString(new CssInsets(10, 10, 20, 40, "mm"), "10mm 10mm 20mm 40mm")),
                dynamicTest("3", () -> doTestFromString(new CssInsets(10, 10, 10, 40, "mm"), "10mm 10mm 10mm 40mm")),
                dynamicTest("4", () -> doTestFromString(new CssInsets(10, 20, 10, 20, "mm"), "10mm 20mm 10mm 20mm"))
        );
    }

}