/* @(#)CssLinearGradientConverterTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.draw.key.CssLinearGradient;
import org.jhotdraw8.draw.key.CssColor;
import java.nio.CharBuffer;
import java.util.Arrays;
import java.util.List;

import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.jhotdraw8.io.IdFactory;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.provider.MethodSource;

import static org.junit.jupiter.api.Assertions.*;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 *
 * @author Werner Randelshofer
 */
public class CssLinearGradientConverterTest {

    public CssLinearGradientConverterTest() {
    }

    /**
     * Test of toString method, of class CssLinearGradientConverter.
     */
    public static void testToString(CssLinearGradient value, String expected) throws Exception {
        System.out.println("toString(" + value + ")");
        StringBuilder out = new StringBuilder();
        IdFactory idFactory = null;
        CssLinearGradientConverter instance = new CssLinearGradientConverter();
        instance.toString(out, idFactory, value);
        String actual = out.toString();
        System.out.println("  expected: "+expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }

    /**
     * Test of fromString method, of class CssLinearGradientConverter.
     */
    public static void testFromString(CssLinearGradient expected, String string) throws Exception {
        System.out.println("fromString(" + string + ")");
        CharBuffer in = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssLinearGradientConverter instance = new CssLinearGradientConverter();
        CssLinearGradient actual = instance.fromString(in, idFactory);
        System.out.println("  expected: "+expected);
        System.out.println("    actual: "+actual);
        assertEquals(actual, expected);
    }

    /**
     * Test of getDefaultValue method, of class CssLinearGradientConverter.
     */
    @Test
    public void testGetDefaultValue() {
        System.out.println("getDefaultValue");
        CssLinearGradientConverter instance = new CssLinearGradientConverter();
        CssLinearGradient expResult = null;
        CssLinearGradient result = instance.getDefaultValue();
        assertEquals(result, expResult);
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testFromString(null, "none")),
            dynamicTest("1", () -> testFromString(new CssLinearGradient(0, 1, 2, 3, false, CycleMethod.NO_CYCLE, new CssStop(0.0, new CssColor("red", Color.RED))), "linear-gradient(from 0px 1px to 2px 3px, red 0)")),
            dynamicTest("1", () -> testFromString(new CssLinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new CssStop(0.5, new CssColor("red", Color.RED)), new CssStop(0.5,new CssColor("transparent", Color.TRANSPARENT))), "linear-gradient(red 50%, transparent 50% )")),
            dynamicTest("1", () -> testFromString(new CssLinearGradient(0, 0, 1.14, 1.14, false, CycleMethod.REPEAT, new CssStop(0.5, new CssColor("red", Color.RED)), new CssStop(0.5,new CssColor("transparent", Color.TRANSPARENT))), "linear-gradient(from 0 0 to 1.14 1.14, repeat,red 50%, transparent 50% )")),
            dynamicTest("1", () -> testFromString(new CssLinearGradient(0, 0, 1, 0, true, CycleMethod.REPEAT, new CssStop(null, new CssColor("red", Color.RED)), new CssStop(null,new CssColor("transparent", Color.TRANSPARENT))), "linear-gradient(to right, repeat,red , transparent )")),
            dynamicTest("1", () -> testFromString(new CssLinearGradient(0, 0, 10, 0, false, CycleMethod.REFLECT, new CssStop(0.1, new CssColor("red", Color.RED)), new CssStop(0.2,new CssColor("white", Color.WHITE))), "linear-gradient(from 0 0 to 10 0, reflect, red 10%, white 20%)")),
             dynamicTest("1", () -> testFromString(new CssLinearGradient(0, 0, 10, 10, false, CycleMethod.NO_CYCLE, new CssStop(null, new CssColor("red", Color.RED)), new CssStop(null,new CssColor("white", Color.WHITE))), "linear-gradient(from 0px 0px to 10px 10px, red, white)")),
             dynamicTest("1", () -> testFromString(new CssLinearGradient(0, 0, 0.10, 0.10, true, CycleMethod.NO_CYCLE, new CssStop(null, new CssColor("red", Color.RED)), new CssStop(null,new CssColor("white", Color.WHITE))), "linear-gradient(from 0% 0% to 10% 10%, red, white)"))
                );
        
    }

    @TestFactory
    public List<DynamicTest> testToStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testToString(null, "none")),
            dynamicTest("1", () -> testToString(new CssLinearGradient(0, 1, 2, 3, false, CycleMethod.NO_CYCLE, new CssStop(0.0, new CssColor("red", Color.RED))), "linear-gradient(from 0 1 to 2 3, red 0%)")),
            dynamicTest("1", () -> testToString(new CssLinearGradient(0, 0, 0, 1, true, CycleMethod.NO_CYCLE, new CssStop(0.5, new CssColor("red", Color.RED)), new CssStop(0.5,new CssColor("transparent", Color.TRANSPARENT))), "linear-gradient(red 50%, transparent 50%)")),
            dynamicTest("1", () -> testToString(new CssLinearGradient(0, 0, 1.14, 1.14, false, CycleMethod.REPEAT, new CssStop(0.5, new CssColor("red", Color.RED)), new CssStop(0.5,new CssColor("transparent", Color.TRANSPARENT))), "linear-gradient(from 0 0 to 1.14 1.14, repeat, red 50%, transparent 50%)")), dynamicTest("1", () -> testToString(new CssLinearGradient(0, 0, 1, 0, true, CycleMethod.REPEAT, new CssStop(null, new CssColor("red", Color.RED)), new CssStop(null,new CssColor("transparent", Color.TRANSPARENT))), "linear-gradient(to right, repeat, red, transparent)"))
                );
        
    }
}
