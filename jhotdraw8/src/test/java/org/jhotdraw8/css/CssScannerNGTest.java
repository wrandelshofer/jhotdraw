/* @(#)CssScannerNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;

import java.io.StringReader;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CssScannerNGTest.
 * @author Werner Randelshofer
 */
public class CssScannerNGTest {
    
    public CssScannerNGTest() {
    }

 

    /**
     * Test of nextChar method, of class CssScanner.
     */
    @Test(dataProvider="scannerData")
    public void testScanner(String inputData, String expectedValue) throws Exception {
        CssScanner s = new CssScanner(new StringReader(inputData));
        //
        StringBuilder buf=new StringBuilder();
        while (s.nextChar()!=-1) {
            buf.append((char)s.currentChar());
        }
        String actualValue=buf.toString();
        System.out.println("testScanner:"+actualValue+" :: "+expectedValue);
        for (char c:actualValue.toCharArray()) {
            System.out.print(Integer.toHexString(c)+ ' ');
        }
        System.out.println();
        for (Integer i:actualValue.codePoints().toArray()) {
            System.out.print(Integer.toHexString(i)+ ' ');
        }
        System.out.println();
        
        assertEquals(actualValue,expectedValue);
    }


     @DataProvider
    public Object[][] scannerData() {
        return new Object[][]{
            {"abcd", "abcd"},
            //
            {"ab\ncd", "ab\ncd"},
            {"ab\r\ncd", "ab\ncd"},
            {"ab\fcd", "ab\ncd"},
            {"ab\rcd", "ab\ncd"},
            //
            {"abcd\n", "abcd\n"},
            {"abcd\r\n", "abcd\n"},
            {"abcd\f", "abcd\n"},
            {"abcd\r", "abcd\n"},
        };
    }
    
}
