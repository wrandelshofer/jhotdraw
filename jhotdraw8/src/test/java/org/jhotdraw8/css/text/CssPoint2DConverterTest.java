package org.jhotdraw8.css.text;

import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CssPoint2DConverterTest {

    /**
     * Test of fromString method, of class CssPoint2DConverterTest.
     */
    static
    public void doTestFromString(CssPoint2D expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssPoint2DConverter instance = new CssPoint2DConverter(false);
        CssPoint2D actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestFromString(new CssPoint2D(40,40,"cm"), "40cm 40cm"))
        );
    }
}