/* @(#)CssRegexConverterNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

import java.io.StringReader;
import org.jhotdraw.css.CssTokenizer;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 *
 * @author wr
 */
public class CssRegexConverterNGTest {
    

    /**
     * Test of nextChar method, of class CssScanner.
     */
    @Test(dataProvider = "regexData")
    public void testRegex(String inputData, String expectedValue) throws Exception {
       CssRegexConverter c = new CssRegexConverter();
       Regex out=c.fromString(inputData);
       
    }
    @DataProvider
    public Object[][] regexData() {
        return new Object[][]{
            {"///",""}
        };
                }
}
