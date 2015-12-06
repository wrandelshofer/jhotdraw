/* @(#)CssRegexConverterNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.text;

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
    public void testRegex(String inpuRegex, String inputValue, String expectedValue) throws Exception {
       CssRegexConverter c = new CssRegexConverter(false);
       Regex rgx=c.fromString(inpuRegex);
       String actualValue=rgx.apply(inputValue);
        assertEquals(actualValue, expectedValue);
    }
    @DataProvider
    public Object[][] regexData() {
        return new Object[][]{
            {"/\"\"//","",""},
            {"/\".*@(.*)\"/","a@b","a@b"},
            {"/\".*@(.*)\"/\"$1\"/","a@b","b"},
            {"/\".*@(.*)\"/\"$0\"/","a@b","a@b"},
            {"/\".*@(.*)\"/ ","a@b","a@b"},
            {"/\".*@(.*)\"/\" \"/","a@b"," "},
            {"/\".*@(.*)\"/\"\"/","a@b",""},
        };
                }
}
