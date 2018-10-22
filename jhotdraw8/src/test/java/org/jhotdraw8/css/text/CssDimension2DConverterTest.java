package org.jhotdraw8.css.text;

import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class CssDimension2DConverterTest {

    /**
     * Test of fromString method, of class CssDimension2DConverterTest.
     */
    static
    public void doTestFromString(CssSize2D expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssSize2DConverter instance = new CssSize2DConverter();
        CssSize2D actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestFromString(new CssSize2D(40,40,"cm"), "40cm 40cm"))
        );
    }
}