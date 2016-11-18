/* @(#)CssCLinearGradientConverterNGTest.java
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
 * @author werni
 */
public class CssCLinearGradientConverterNGTest {
    
    public CssCLinearGradientConverterNGTest() {
    }

    /**
     * Test of toString method, of class CssCLinearGradientConverter.
     */
    @Test(dataProvider="toStringData")
    public void testToString(CLinearGradient value, String expected) throws Exception {
        System.out.println("toString("+value+"):"+expected);
        StringBuilder out = new StringBuilder();
        IdFactory idFactory = null;
        CssCLinearGradientConverter instance = new CssCLinearGradientConverter();
        instance.toString(out, idFactory, value);
        String actual=out.toString();
        assertEquals(actual,expected);
    }

    /**
     * Test of fromString method, of class CssCLinearGradientConverter.
     */
    @Test(dataProvider="fromStringData")
    public void testFromString(String string, CLinearGradient expected) throws Exception {
        System.out.println("fromString("+string+"):"+expected);
        CharBuffer in = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssCLinearGradientConverter instance = new CssCLinearGradientConverter();
        CLinearGradient actual = instance.fromString(in, idFactory);
        assertEquals(actual,expected);
    }

    /**
     * Test of getDefaultValue method, of class CssCLinearGradientConverter.
     */
    @Test
    public void testGetDefaultValue() {
        System.out.println("getDefaultValue");
        CssCLinearGradientConverter instance = new CssCLinearGradientConverter();
        CLinearGradient expResult = null;
        CLinearGradient result = instance.getDefaultValue();
        assertEquals(result, expResult);
    }
        @DataProvider
    public Object[][] fromStringData() {
        return new Object[][]{
            //string,value
            {"none",null},
        };
        }
        @DataProvider
    public Object[][] toStringData() {
        return new Object[][]{
            //value,string
            {null,"none"},
            {new CLinearGradient(0,1,2,3,false,CycleMethod.NO_CYCLE,new CStop(0.0,new CColor("red",Color.RED))),null},
        };
        }
}
