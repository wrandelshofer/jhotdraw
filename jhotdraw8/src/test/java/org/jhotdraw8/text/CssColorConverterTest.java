/* @(#)CssColorConverterTest.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import javafx.scene.paint.Color;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssColorConverterTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssColorConverterTest {

    public CssColorConverterTest() {
    }

    /**
     * Test of fromString method, of class CssColorConverter.
     */
    static void testFromString(CssColor expected, String string) throws Exception {
        System.out.println("fromString(" + string + ")");
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        Converter<CssColor> instance = new CssColorConverter(true);
        CssColor actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
        if (actual != null) {
            assertEquals(actual.getName(), expected.getName());
        }
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testFromString(null, "none")),
            dynamicTest("2", () -> testFromString(new CssColor("white", Color.WHITE), "white")),
            dynamicTest("3", () -> testFromString(new CssColor("#abc", Color.web("#abc")), "#abc")),
            dynamicTest("4", () -> testFromString(new CssColor("#abcdef", Color.web("#abcdef")), "#abcdef")),
            dynamicTest("5", () -> testFromString(new CssColor("#660033", Color.web("#660033")), "#660033")),
            dynamicTest("6", () -> testFromString(new CssColor("rgb(10,20,30)", new Color(10 / 255.0, 20 / 255.0, 30 / 255.0, 1.0)), "rgb(10,20,30)")),
            dynamicTest("7", () -> testFromString(new CssColor("rgb(10%,20%,30%)", new Color(0.1, 0.2, 0.3, 1.0)), "rgb(10%,20%,30%)")),
            dynamicTest("8", () -> testFromString(new CssColor("rgba(10%,20%,30%,80%)", new Color(0.1, 0.2, 0.3, 0.8)), "rgba(10%,20%,30%,80%)")),
            dynamicTest("9", () -> testFromString(new CssColor("rgba(10%,20%,30%,0.8)", new Color(0.1, 0.2, 0.3, 0.8)), "rgba(10%,20%,30%,0.8)")),
            dynamicTest("10", () -> testFromString(new CssColor("hsb(10,0.2,0.3)", Color.hsb(10, 0.20, 0.30)), "hsb(10,.20,.30)")),
            dynamicTest("11", () -> testFromString(new CssColor("hsb(10,20%,30%)", Color.hsb(10, 0.20, 0.30)), "hsb(10,20%,30%)")),
            dynamicTest("12", () -> testFromString(new CssColor("hsba(10,0.2,0.3,80%)", Color.hsb(10, 0.20, 0.30, 0.8)), "hsba(10,.2,.3,80%)")),
            dynamicTest("13", () -> testFromString(new CssColor("hsba(10,20%,30%,0.8)", Color.hsb(10, 0.20, 0.30, 0.8)), "hsba(10,20%,30%,0.8)"))
        );

    }
}
