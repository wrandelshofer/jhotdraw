/* @(#)CssRectangle2DConverterNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import java.nio.CharBuffer;
import javafx.geometry.Rectangle2D;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CssRectangle2DConverterNGTest.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class CssRectangle2DConverterNGTest {

    public CssRectangle2DConverterNGTest() {
    }




    /**
     * Test of fromString method, of class CssDoubleConverter.
     */
    @Test(dataProvider = "fromStringData")
    public void testFromString(Rectangle2D expected, String string) throws Exception {
        System.out.println("fromString " + string);
        CharBuffer buf = CharBuffer.wrap(string);
        IdFactory idFactory = new SimpleIdFactory();
        CssRectangle2DConverter instance = new CssRectangle2DConverter();
        Rectangle2D actual = instance.fromString(buf, idFactory);
        System.out.println("  expected: " + expected);
        System.out.println("    actual: " + actual);
        assertEquals(actual, expected);
    }
@DataProvider
    public Object[][] fromStringData() {
        return new Object[][]{
            //value,string
            {new Rectangle2D(11,22,33,44), "11 22 33 44"},
        };
    }
}