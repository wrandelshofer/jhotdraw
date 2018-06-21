/* @(#)CssSizeConverterTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssSizeConverterTest.
 *
 * @author Werner Randelshofer
 * @version $$Id: CssSizeConverterNGTest_1.java 1176 2016-12-11 19:48:19Z
 * rawcoder $$
 */
public class CssSizeConverterTest {

    public CssSizeConverterTest() {
    }

    /**
     * Test of toString method, of class CssDoubleConverter.
     */
   static
    public void testToString(Double value, String expected) throws Exception {
        System.out.println("toString " + value);
        StringBuilder out = new StringBuilder();
        IdFactory idFactory = null;
        CssDoubleConverter instance = new CssDoubleConverter(new DefaultUnitConverter(90.0),true);
        instance.toString(out, idFactory, value);
        String actual = out.toString();
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test of fromString method, of class CssDoubleConverter.
     */
    static
    public void testFromString(Double expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = new SimpleIdFactory();
        CssDoubleConverter instance = new CssDoubleConverter(new DefaultUnitConverter(90.0),true);
        Double actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        if (expected==null||Double.isNaN(expected))
        assertEquals(actual, expected);
        else
        assertEquals(actual, expected,1e-4);
    }

    /**
     * Test of getDefaultValue method, of class CssDoubleConverter.
     */
    @Test
    public void testGetDefaultValue() {
        System.out.println("getDefaultValue");
        CssDoubleConverter instance = new CssDoubleConverter();
        Double expected = 0.0;
        Double actual = instance.getDefaultValue();
        assertEquals(actual, expected);
    }

        @TestFactory
        public List<DynamicTest> testFromStringFactory() {
            return Arrays.asList(
                    dynamicTest("1", () -> testFromString(null, "none")),
            dynamicTest("1", () -> testFromString(1.0, "1")),
            dynamicTest("1", () -> testFromString(3.0e30, "3e30")),
            dynamicTest("1", () -> testFromString(Double.POSITIVE_INFINITY, "INF")),
            dynamicTest("1", () -> testFromString(Double.NEGATIVE_INFINITY, "-INF")),
            dynamicTest("1", () -> testFromString(Double.NaN, "NaN")),
            dynamicTest("1", () -> testFromString(0.01, "1%")),
            dynamicTest("1", () -> testFromString(90.0, "1in")),
            dynamicTest("1", () -> testFromString(90.0/2.54, "1cm")),
            dynamicTest("1", () -> testFromString(90.0/25.4, "1mm")),
            dynamicTest("1", () -> testFromString(12.0, "1em")),
            dynamicTest("1", () -> testFromString(8.0, "1ex")),
            dynamicTest("1", () -> testFromString(90.0/72, "1pt")),
            dynamicTest("1", () -> testFromString(90.0/72, "12pc")),
            dynamicTest("1", () -> testFromString(0.01*3.14, "3.14%")),
            dynamicTest("1", () -> testFromString(90.0*3.14, "3.14in")),
            dynamicTest("1", () -> testFromString(90.0/2.54*3.14, "3.14cm")),
            dynamicTest("1", () -> testFromString(90.0/25.4*3.14, "3.14mm")),
            dynamicTest("1", () -> testFromString(12.0*3.14, "3.14em")),
            dynamicTest("1", () -> testFromString(8.0*3.14, "3.14ex")),
            dynamicTest("1", () -> testFromString(90.0/72*3.14, "3.14pt")),
            dynamicTest("1", () -> testFromString(90.0/72/12*3.14, "3.14pc")),
            dynamicTest("1", () -> testFromString(12.0*31.4, "3.14e1em")),
            dynamicTest("1", () -> testFromString(8.0*31.4, "3.14e1ex")),
            dynamicTest("1", () -> testFromString(1.0, "1"))
            );
    }

@TestFactory
public List<DynamicTest> testToStringFactory() {
        return Arrays.asList(
        dynamicTest("1", () -> testToString(null, "none")),
            dynamicTest("1", () -> testToString(1.0, "1")),
            dynamicTest("1", () -> testToString(3.0e30, "3.0E30")),
            dynamicTest("1", () -> testToString(Double.POSITIVE_INFINITY, "INF")),
            dynamicTest("1", () -> testToString(Double.NEGATIVE_INFINITY, "-INF")),
            dynamicTest("1", () -> testToString(Double.NaN, "NaN"))
        );
    }
}
