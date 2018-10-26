package org.jhotdraw8.css.text;

import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

class DimensionInsetsConverterTest {

    /**
     * Test of fromString method, of class Dimension2DConverterTest.
     */
    static
    public void doTestFromString(CssDimensionInsets expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssDimensionInsetsConverter instance = new CssDimensionInsetsConverter(false);
        CssDimensionInsets actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestFromString(new CssDimensionInsets(10,20,30,40,"mm"), "10mm 20mm 30mm 40mm")),
                dynamicTest("2", () -> doTestFromString(new CssDimensionInsets(10,10,20,40,"mm"), "10mm 10mm 20mm 40mm")),
                dynamicTest("3", () -> doTestFromString(new CssDimensionInsets(10,10,10,40,"mm"), "10mm 10mm 10mm 40mm")),
                dynamicTest("4", () -> doTestFromString(new CssDimensionInsets(10,20,10,20,"mm"), "10mm 20mm 10mm 20mm"))
        );
    }

}