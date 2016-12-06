/* @(#)CssLinearGradientConverterNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.nio.CharBuffer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;
import org.jhotdraw8.draw.io.IdFactory;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author Werner Randelshofer
 */
public class CssLinearGradientConverterNGTest {
    
    public CssLinearGradientConverterNGTest() {
    }

    /**
     * Test of toString method, of class CssLinearGradientConverter.
     */
    @Test(dataProvider="toStringData")
    public void testToString(CssLinearGradient value, String expected) throws Exception {
        System.out.println("toString("+value+"):"+expected);
        StringBuilder out = new StringBuilder();
        IdFactory idFactory = null;
        CssLinearGradientConverter instance = new CssLinearGradientConverter();
        instance.toString(out, idFactory, value);
        String actual=out.toString();
        System.out.println("actual: "+actual);
        assertEquals(actual,expected);
    }

    /**
     * Test of fromString method, of class CssLinearGradientConverter.
     */
    @Test(dataProvider="fromStringData")
    public void testFromString(CssLinearGradient expected, String string) throws Exception {
        System.out.println("fromString("+string+"):"+expected);
        CharBuffer in = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssLinearGradientConverter instance = new CssLinearGradientConverter();
        CssLinearGradient actual = instance.fromString(in, idFactory);
        assertEquals(actual,expected);
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
        @DataProvider
    public Object[][] fromStringData() {
        return new Object[][]{
            //value,string
            {null,"none"},
            {new CssLinearGradient(0,1,2,3,false,CycleMethod.NO_CYCLE,new CssStop(0.0,new CssColor("red",Color.RED))),"linear-gradient(from 0px 1px to 2px 3px, red 0)"},
        };
        }
        @DataProvider
    public Object[][] toStringData() {
        return new Object[][]{
            //value,string
            {null,"none"},
            {new CssLinearGradient(0,1,2,3,false,CycleMethod.NO_CYCLE,new CssStop(0.0,new CssColor("red",Color.RED))),"linear-gradient(from 0px 1px to 2px 3px, red 0)"},
        };
        }
}
