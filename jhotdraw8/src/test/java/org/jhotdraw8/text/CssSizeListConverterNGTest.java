/* @(#)CssSizeListConverterNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.scene.paint.Color;
import org.jhotdraw8.io.IdFactory;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CssSizeListConverterNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CssSizeListConverterNGTest {

    public CssSizeListConverterNGTest() {
    }

    /**
     * Test of toString method, of class CssSizeListConverter.
     */
    @Test(dataProvider = "toStringData")
    public void testToString( List<Double> value, String expected ) throws Exception {
        System.out.println("toString "+value);
        StringBuilder out =new StringBuilder();
        IdFactory idFactory = null;
        CssSizeListConverter instance = new CssSizeListConverter();
        instance.toString(out, idFactory, value);
        String actual = out.toString();
         System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test of fromString method, of class CssSizeListConverter.
     */
    @Test(dataProvider = "fromStringData")
    public void testFromString(List<Double> expected, String string) throws Exception {
        System.out.println("fromString "+string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssSizeListConverter instance = new CssSizeListConverter();
        List<Double> actual = instance.fromString(buf, idFactory);
         System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test of getDefaultValue method, of class CssSizeListConverter.
     */
    @Test
    public void testGetDefaultValue() {
        System.out.println("getDefaultValue");
        CssSizeListConverter instance = new CssSizeListConverter();
        List<Double> expected = Collections.emptyList();
        List<Double> actual = instance.getDefaultValue();
        assertEquals(actual, expected);
    }
    @DataProvider
    public Object[][] fromStringData() {
        return new Object[][]{
            //value,string
            {Collections.emptyList(), "none"},
            {Arrays.asList(1.0, 2.0, 3.0), "1 2 3"},
            {Arrays.asList(1.0, 3.0e30, 3.0), "1 3e30 3"},
            {Arrays.asList(1.0, 2.0, Double.POSITIVE_INFINITY), "1 2 INF"},
            {Arrays.asList(1.0,  Double.NEGATIVE_INFINITY,3.0), "1 -INF 3"},
            {Arrays.asList(1.0, Double.NaN,3.0), "1 NaN 3"},
        };
        }
    @DataProvider
    public Object[][] toStringData() {
        return new Object[][]{
            //value,string
            {null, "none"},
            {Collections.emptyList(), "none"},
            {Arrays.asList(1.0, 2.0, 3.0), "1 2 3"},
            {Arrays.asList(1.0, 3.0e30, 3.0), "1 3.0E30 3"},
            {Arrays.asList(1.0, 2.0, Double.POSITIVE_INFINITY), "1 2 INF"},
            {Arrays.asList(1.0,  Double.NEGATIVE_INFINITY,3.0), "1 -INF 3"},
            {Arrays.asList(1.0, Double.NaN,3.0), "1 NaN 3"},
        };
        }
}