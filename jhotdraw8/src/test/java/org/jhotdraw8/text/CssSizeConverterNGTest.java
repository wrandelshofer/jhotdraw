/* @(#)CssSizeConverterNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.nio.CharBuffer;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CssSizeConverterNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id: CssSizeConverterNGTest_1.java 1176 2016-12-11 19:48:19Z
 * rawcoder $$
 */
public class CssSizeConverterNGTest {

    public CssSizeConverterNGTest() {
    }

    /**
     * Test of toString method, of class CssDoubleConverter.
     */
    @Test(dataProvider = "toStringData")
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
    @Test(dataProvider = "fromStringData")
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

    @DataProvider
    public Object[][] fromStringData() {
        return new Object[][]{
            //value,string
            {null, "none"},
            {1.0, "1"},
            {3.0e30, "3e30"},
            {Double.POSITIVE_INFINITY, "INF"},
            {Double.NEGATIVE_INFINITY, "-INF"},
            {Double.NaN, "NaN"},
            {0.01, "1%"},
            {90.0, "1in"},
            {90.0/2.54, "1cm"},
            {90.0/25.4, "1mm"},
            {12.0, "1em"},
            {8.0, "1ex"},
            {90.0/72, "1pt"},
            {90.0/72, "12pc"},
            {0.01*3.14, "3.14%"},
            {90.0*3.14, "3.14in"},
            {90.0/2.54*3.14, "3.14cm"},
            {90.0/25.4*3.14, "3.14mm"},
            {12.0*3.14, "3.14em"},
            {8.0*3.14, "3.14ex"},
            {90.0/72*3.14, "3.14pt"},
            {90.0/72/12*3.14, "3.14pc"},
            {12.0*31.4, "3.14e1em"},
            {8.0*31.4, "3.14e1ex"},
            {1.0, "1"},
        };
    }

    @DataProvider
    public Object[][] toStringData() {
        return new Object[][]{
            //value,string
            {null, "none"},
            {1.0, "1"},
            {3.0e30, "3.0E30"},
            {Double.POSITIVE_INFINITY, "INF"},
            {Double.NEGATIVE_INFINITY, "-INF"},
            {Double.NaN, "NaN"},};
    }
}
