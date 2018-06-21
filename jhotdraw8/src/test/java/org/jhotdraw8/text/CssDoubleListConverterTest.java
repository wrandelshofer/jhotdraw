/* @(#)CssDoubleListConverterTest.java
 * Copyright (c) 2016 by the authors and contributors ofCollection JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssDoubleListConverterTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssDoubleListConverterTest {

    public CssDoubleListConverterTest() {
    }

    /**
     * Test ofCollection toString method, ofCollection class CssDoubleListConverter.
     */
    static
    public void testToString(List<Double> value, String expected) throws Exception {
        System.out.println("toString " + value);
        StringBuilder out = new StringBuilder();
        IdFactory idFactory = null;
        CssDoubleListConverter instance = new CssDoubleListConverter();
        instance.toString(out, idFactory, value == null ? null : ImmutableList.ofCollection(value));
        String actual = out.toString();
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test ofCollection fromString method, ofCollection class CssDoubleListConverter.
     */
    static
    public void testFromString(List<Double> expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssDoubleListConverter instance = new CssDoubleListConverter();
        List<Double> actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test ofCollection getDefaultValue method, ofCollection class CssDoubleListConverter.
     */
    @Test
    public void testGetDefaultValue() {
        System.out.println("getDefaultValue");
        CssDoubleListConverter instance = new CssDoubleListConverter();
        List<Double> expected = Collections.emptyList();
        List<Double> actual = instance.getDefaultValue();
        assertEquals(actual, expected);
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testFromString(Collections.emptyList(), "none")),
                dynamicTest("1", () -> testFromString(Arrays.asList(1.0, 2.0, 3.0), "1 2 3")),
                dynamicTest("1", () -> testFromString(Arrays.asList(1.0, 3.0e30, 3.0), "1 3e30 3")),
                dynamicTest("1", () -> testFromString(Arrays.asList(1.0, 2.0, Double.POSITIVE_INFINITY), "1 2 INF")),
                dynamicTest("1", () -> testFromString(Arrays.asList(1.0, Double.NEGATIVE_INFINITY, 3.0), "1 -INF 3")),
                dynamicTest("1", () -> testFromString(Arrays.asList(1.0, Double.NaN, 3.0), "1 NaN 3"))
        );
    }

    @TestFactory
    public List<DynamicTest> testToStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testToString(null, "none")),
                dynamicTest("1", () -> testToString(Collections.emptyList(), "none")),
                dynamicTest("1", () -> testToString(Arrays.asList(1.0, 2.0, 3.0), "1 2 3")),
                dynamicTest("1", () -> testToString(Arrays.asList(1.0, 3.0e30, 3.0), "1 3.0E30 3")),
                dynamicTest("1", () -> testToString(Arrays.asList(1.0, 2.0, Double.POSITIVE_INFINITY), "1 2 INF")),
                dynamicTest("1", () -> testToString(Arrays.asList(1.0, Double.NEGATIVE_INFINITY, 3.0), "1 -INF 3")),
                dynamicTest("1", () -> testToString(Arrays.asList(1.0, Double.NaN, 3.0), "1 NaN 3"))
        );
    }
}