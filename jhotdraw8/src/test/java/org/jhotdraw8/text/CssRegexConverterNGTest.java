/* @(#)CssRegexConverterNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.text.CssRegexConverter;
import org.jhotdraw8.text.Regex;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * Test for {@link CssRegexConverter}.
 * 
 * @author Werner Randelshofer
 */
public class CssRegexConverterNGTest {

    @Test(dataProvider = "regexOutputData")
    public void testRegexOutput(String inputCssRegex, String inputValue, String expectedValue) throws Exception {
        CssRegexConverter c = new CssRegexConverter(false);
        Regex rgx = c.fromString(inputCssRegex);
        String actualValue = rgx.apply(inputValue);
        assertEquals(actualValue, expectedValue);
    }

    @DataProvider
    public Object[][] regexOutputData() {
        return new Object[][]{
            {"'' ''", "", ""},
            {"'.*@(.*)'", "a@b", "a@b"},
            {"'.*@(.*)' '$1'", "a@b", "b"},
            {"'.*@(.*)' '$0'", "a@b", "a@b"},
            {"'.*@(.*)'", "a@b", "a@b"},
            {"'.*@(.*)' ' ' ", "a@b", " "},
            {"'.*@(.*)' ''", "a@b", ""},};
    }
    @Test(dataProvider = "regexConverterData")
    public void testRegexConverter(String inputCssRegex, String expectedFind, String expectedReplace) throws Exception {
        CssRegexConverter c = new CssRegexConverter(false);
        Regex rgx = c.fromString(inputCssRegex);
        assertEquals(rgx.getFind(), expectedFind,"find");
        assertEquals(rgx.getReplace(), expectedReplace,"replace");
    }

    @DataProvider
    public Object[][] regexConverterData() {
        return new Object[][]{
            {"'' ''", "", ""},
            {"'.*@(.*)'", ".*@(.*)", null},
            {"'.*@(.*)' '$1''", ".*@(.*)", "$1"},
            {"'.*@(.*)' '$0''", ".*@(.*)", "$0"},
            {"'.*@(.*)'", ".*@(.*)", null},
            {"'.*@(.*)' ''", ".*@(.*)", ""},
            {"'.*@(.*)' ''", ".*@(.*)", ""},
        //
            {"none", null, null},
            {"'.*@(.*)'", ".*@(.*)", null},
            {"'.*@(.*)' '$1'", ".*@(.*)", "$1"},
            {"'.*@(.*)' '$0'", ".*@(.*)", "$0"},
            {"'.*@(.*)'", ".*@(.*)", null},
            {"'.*@(.*)' ''", ".*@(.*)", ""},
            {"'.*@(.*)'", ".*@(.*)", null},
            //
            {"'.*\\'(.*)' '$1'", ".*'(.*)", "$1"},
        };
    }
}
