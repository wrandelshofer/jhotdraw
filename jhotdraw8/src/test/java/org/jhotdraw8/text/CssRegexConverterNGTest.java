/* @(#)CssRegexConverterNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import java.text.ParseException;
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
    public void testFromStringApply(String inputCssRegex, String inputValue, String expectedValue) throws Exception {
        CssRegexConverter c = new CssRegexConverter(false);
        RegexReplace rgx = c.fromString(inputCssRegex);
        String actualValue = rgx.apply(inputValue);
        assertEquals(actualValue, expectedValue);
    }

    @DataProvider
    public Object[][] regexOutputData() {
        return new Object[][]{
            {"replace('' '')", "", ""},
            {"replace('.*@(.*)')", "a@b", "a@b"},
            {"replace('.*@(.*)' '$1')", "a@b", "b"},
            {"replace('.*@(.*)' '$0')", "a@b", "a@b"},
            {"replace('.*@(.*)')", "a@b", "a@b"},
            {"replace('.*@(.*)' ' ' )", "a@b", " "},
            {"replace('.*@(.*)' '')", "a@b", ""},};
    }
    @Test(dataProvider = "regexConverterData")
    public void testRegexFromStringReplace(String inputCssRegex, String expectedFind, String expectedReplace) throws Exception {
        CssRegexConverter c = new CssRegexConverter(false);
        RegexReplace rgx = c.fromString(inputCssRegex);
        assertEquals(rgx.getFind(), expectedFind,"find");
        assertEquals(rgx.getReplace(), expectedReplace,"replace");
    }
    @Test(dataProvider = "nullableRegexConverterData")
    public void testRegexFromStringReplaceNullable(String inputCssRegex, boolean expectNull) throws Exception {
        CssRegexConverter cNullable = new CssRegexConverter(true);
        RegexReplace rgx = cNullable.fromString(inputCssRegex);
        if (expectNull)
            assertNull(rgx,"Nullable converter must return null regex");
        else
            assertNotNull(rgx,"Nullable converter most not return null regex");
        
        CssRegexConverter cNonnull = new CssRegexConverter(false);
        try {
        RegexReplace rgxNonnull = cNonnull.fromString(inputCssRegex);
            if (expectNull)
                fail("Nonnull converter must produce ParseException");
        } catch (ParseException e) {
            if (!expectNull)
                fail("Nonnull converter must not produce ParseException");
        }
    }
    

    @DataProvider
    public Object[][] regexConverterData() {
        return new Object[][]{
            {"replace('' '')", "", ""},
            {"replace('.*@(.*)')", ".*@(.*)", null},
            {"replace('.*@(.*)' '$1')", ".*@(.*)", "$1"},
            {"replace('.*@(.*)' '$0')", ".*@(.*)", "$0"},
            {"replace('.*@(.*)')", ".*@(.*)", null},
            {"replace('.*@(.*)' '')", ".*@(.*)", ""},
            {"replace('.*@(.*)' '')", ".*@(.*)", ""},
        //
            {"replace('.*@(.*)')", ".*@(.*)", null},
            {"replace('.*@(.*)' '$1')", ".*@(.*)", "$1"},
            {"replace('.*@(.*)' '$0')", ".*@(.*)", "$0"},
            {"replace('.*@(.*)')", ".*@(.*)", null},
            {"replace('.*@(.*)' '')", ".*@(.*)", ""},
            {"replace('.*@(.*)')", ".*@(.*)", null},
            //
            {"replace('.*\\'(.*)' '$1')", ".*'(.*)", "$1"},
        };
    }
    @DataProvider
    public Object[][] nullableRegexConverterData() {
        return new Object[][]{
            {"replace('' '')", false},
            {"none", true},
        };
    }
}
