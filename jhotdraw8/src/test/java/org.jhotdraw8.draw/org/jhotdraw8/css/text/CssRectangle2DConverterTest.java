/* @(#)CssRectangle2DConverterTest.java
 * Copyright (c) 2016 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssRectangle2DConverterTest.
 *
 * @author Werner Randelshofer
 */
public class CssRectangle2DConverterTest {

    public CssRectangle2DConverterTest() {
    }


    /**
     * Test of fromString method, of class CssDoubleConverter.
     */
    public static void testFromString(CssRectangle2D expected, @NonNull String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = new SimpleIdFactory();
        CssRectangle2DConverter instance = new CssRectangle2DConverter(false);
        CssRectangle2D actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        String actualString = instance.toString(expected);
        System.out.println("  expectedString: " + string);
        System.out.println("    actualString: " + actualString);
        assertEquals(expected, actual);
        assertEquals(string, actualString);
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsFromString() {
        return Arrays.asList(
                dynamicTest("1", () -> testFromString(new CssRectangle2D(11, 22, 33, 44), "11 22 33 44")),
                dynamicTest("1", () -> testFromString(new CssRectangle2D(new CssSize(11, "cm"),
                        new CssSize(22, "cm"),
                        new CssSize(33, "cm"),
                        new CssSize(44, "cm")), "11cm 22cm 33cm 44cm"))
        );
    }
}