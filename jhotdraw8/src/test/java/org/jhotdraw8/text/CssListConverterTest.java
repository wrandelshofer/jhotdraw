/* @(#)CssListConverterTest.java
 * Copyright (c) 2016 by the authors and contributors ofCollection JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssStringConverter;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssListConverterTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssListConverterTest {

    public CssListConverterTest() {
    }

    /**
     * Test of toString method.
     */
    public void doTestToString(List<Double> value, String expected) throws Exception {
        System.out.println("toString " + value);
        StringBuilder out = new StringBuilder();
        IdFactory idFactory = null;
        CssListConverter<Double> instance = new CssListConverter<>(new CssDoubleConverter(false), " ");
        instance.toString(out, idFactory, value == null ? null : ImmutableList.ofCollection(value));
        String actual = out.toString();
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test of fromString method with a {@code Double} element type.
     */
    public void doTestDoubleFromString(List<Double> expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssListConverter<Double> instance = new CssListConverter<>(new CssDoubleConverter(false));
        ImmutableList<Double> actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals( expected, actual.toArrayList());
    }


    /**
     * Test of fromString method with a {@code String} element type.
     */
    public void doTestStringFromString(List<String> expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssListConverter<String> instance = new CssListConverter<>(new CssStringConverter(false));
        ImmutableList<String> actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals( expected, actual.toArrayList());
    }
    @TestFactory
    public List<DynamicTest> testDoubleFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestDoubleFromString(Collections.emptyList(), "none")),
                dynamicTest("2", () -> doTestDoubleFromString(Arrays.asList(1.0, 2.0, 3.0), "1 2 3")),
                dynamicTest("3", () -> doTestDoubleFromString(Arrays.asList(1.0, 3.0e30, 3.0), "1 3e30 3")),
                dynamicTest("4", () -> doTestDoubleFromString(Arrays.asList(1.0, 2.0, Double.POSITIVE_INFINITY), "1 2 INF")),
                dynamicTest("5", () -> doTestDoubleFromString(Arrays.asList(1.0, Double.NEGATIVE_INFINITY, 3.0), "1 -INF 3")),
                dynamicTest("6", () -> doTestDoubleFromString(Arrays.asList(1.0, Double.NaN, 3.0), "1 NaN 3")),
                //
                dynamicTest("12", () -> doTestDoubleFromString(Arrays.asList(1.0, 2.0, 3.0), "1, 2, 3")),
                //
                // should stop at semicolon and at right brackets:
                dynamicTest("21", () -> doTestDoubleFromString(Arrays.asList(1.0, 2.0, 3.0), "1, 2, 3; 4")),
                dynamicTest("22", () -> doTestDoubleFromString(Arrays.asList(1.0, 2.0, 3.0), "1, 2, 3) 4")),
                dynamicTest("23", () -> doTestDoubleFromString(Arrays.asList(1.0, 2.0, 3.0), "1, 2, 3} 4")),
                dynamicTest("24", () -> doTestDoubleFromString(Arrays.asList(1.0, 2.0, 3.0), "1, 2, 3] 4"))
        );
    }
    @TestFactory
    public List<DynamicTest> testStringFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestStringFromString(Collections.emptyList(), "none")),
                dynamicTest("2", () -> doTestStringFromString(Arrays.asList("a","b","c"), "'a' 'b' 'c'")),
                dynamicTest("3", () -> doTestStringFromString(Arrays.asList("a","b","c"), "'a''b''c'")),
                dynamicTest("4", () -> doTestStringFromString(Arrays.asList("a","b","c"), "'a','b','c'")),
                dynamicTest("5", () -> doTestStringFromString(Arrays.asList("a","b","c"), "'a', 'b', 'c'")),
                dynamicTest("5", () -> doTestStringFromString(Arrays.asList("a","b","c"), "'a',,'b',,'c'"))
        );
    }

    @TestFactory
    public List<DynamicTest> testToStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> doTestToString(null, "none")),
                dynamicTest("2", () -> doTestToString(Collections.emptyList(), "none")),
                dynamicTest("3", () -> doTestToString(Arrays.asList(1.0, 2.0, 3.0), "1 2 3")),
                dynamicTest("4", () -> doTestToString(Arrays.asList(1.0, 3.0e30, 3.0), "1 3.0E30 3")),
                dynamicTest("5", () -> doTestToString(Arrays.asList(1.0, 2.0, Double.POSITIVE_INFINITY), "1 2 INF")),
                dynamicTest("6", () -> doTestToString(Arrays.asList(1.0, Double.NEGATIVE_INFINITY, 3.0), "1 -INF 3")),
                dynamicTest("7", () -> doTestToString(Arrays.asList(1.0, Double.NaN, 3.0), "1 NaN 3"))
        );
    }
}