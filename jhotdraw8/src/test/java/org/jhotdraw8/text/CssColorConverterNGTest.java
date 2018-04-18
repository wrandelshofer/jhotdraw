/* @(#)CssColorConverterNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.draw.key.CssColor;
import java.nio.CharBuffer;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import org.jhotdraw8.css.CssTokenizerInterface;
import org.jhotdraw8.io.IdFactory;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CssColorConverterNGTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssColorConverterNGTest {

    public CssColorConverterNGTest() {
    }

    /**
     * Test of fromString method, of class CssColorConverter.
     */
    @Test(dataProvider = "fromStringData")
    public void testFromString(CssColor expected, String string) throws Exception {
        System.out.println("fromString(" + string + ")");
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = null;
        CssColorConverter instance = new CssColorConverter(true);
        CssColor actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
        if (actual != null) {
            assertEquals(actual.getName(), expected.getName());
        }
    }

    @DataProvider
    public Object[][] fromStringData() {
        return new Object[][]{
            //value,string
            {null, "none"},
            {new CssColor("white", Color.WHITE), "white"},
            {new CssColor("#abc", Color.web("#abc")), "#abc"},
            {new CssColor("#abcdef", Color.web("#abcdef")), "#abcdef"},
            {new CssColor("#660033", Color.web("#660033")), "#660033"},
            {new CssColor("rgb(10,20,30)", new Color(10 / 255.0, 20 / 255.0, 30 / 255.0, 1.0)), "rgb(10,20,30)"},
            {new CssColor("rgb(10%,20%,30%)", new Color(0.1, 0.2, 0.3, 1.0)), "rgb(10%,20%,30%)"},
            {new CssColor("rgba(10%,20%,30%,80%)", new Color(0.1, 0.2, 0.3, 0.8)), "rgba(10%,20%,30%,80%)"},
            {new CssColor("rgba(10%,20%,30%,0.8)", new Color(0.1, 0.2, 0.3, 0.8)), "rgba(10%,20%,30%,0.8)"},
            {new CssColor("hsb(10,0.2,0.3)", Color.hsb(10, 0.20, 0.30)), "hsb(10,.20,.30)"},
            {new CssColor("hsb(10,20%,30%)", Color.hsb(10, 0.20, 0.30)), "hsb(10,20%,30%)"},
            {new CssColor("hsba(10,0.2,0.3,80%)", Color.hsb(10, 0.20, 0.30, 0.8)), "hsba(10,.2,.3,80%)"},
            {new CssColor("hsba(10,20%,30%,0.8)", Color.hsb(10, 0.20, 0.30, 0.8)), "hsba(10,20%,30%,0.8)"},};

    }
}
