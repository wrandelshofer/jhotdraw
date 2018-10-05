/* @(#)CssRegexConverterTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.text;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.fail;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * Test for {@link CssRegexConverter}.
 * 
 * @author Werner Randelshofer
 */
public class CssRegexConverterTest {

    static
    public void testFromStringApply(String inputCssRegex, String inputValue, String expectedValue) throws Exception {
        CssRegexConverter c = new CssRegexConverter(false);
        RegexReplace rgx = c.fromString(inputCssRegex);
        String actualValue = rgx.apply(inputValue);
        assertEquals(actualValue, expectedValue);
    }


    static
    public void testRegexFromStringReplace(String inputCssRegex, String expectedFind, String expectedReplace) throws Exception {
        CssRegexConverter c = new CssRegexConverter(false);
        RegexReplace rgx = c.fromString(inputCssRegex);
        assertEquals(rgx.getFind(), expectedFind,"find");
        assertEquals(rgx.getReplace(), expectedReplace,"replace");
    }
    public static void testRegexFromStringReplaceNullable(String inputCssRegex, boolean expectNull) throws Exception {
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


    @TestFactory
    public List<DynamicTest> testRegexFromStringReplaceFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testRegexFromStringReplace("replace('' '')", "", "")),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)' '$1')", ".*@(.*)", "$1")),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)' '$0')", ".*@(.*)", "$0")),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)' '')", ".*@(.*)", "")),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)' '')", ".*@(.*)", "")),
        //
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)' '$1')", ".*@(.*)", "$1")),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)' '$0')", ".*@(.*)", "$0")),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)' '')", ".*@(.*)", "")),
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
            //
            dynamicTest("1", () -> testRegexFromStringReplace("replace('.*\\'(.*)' '$1')", ".*'(.*)", "$1"))
            );
    }

    @TestFactory
    public List<DynamicTest> testRegexFromStringApplyFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testFromStringApply("replace('' '')", "", "")),
                dynamicTest("1", () -> testFromStringApply("replace('.*@(.*)')", "a@b", "a@b")),
                dynamicTest("1", () -> testFromStringApply("replace('.*@(.*)' '$1')", "a@b", "b")),
                dynamicTest("1", () -> testFromStringApply("replace('.*@(.*)' '$0')", "a@b", "a@b")),
                dynamicTest("1", () -> testFromStringApply("replace('.*@(.*)')", "a@b", "a@b")),
                dynamicTest("1", () -> testFromStringApply("replace('.*@(.*)' ' ' )", "a@b", " ")),
                dynamicTest("1", () -> testFromStringApply("replace('.*@(.*)' '')", "a@b", ""))
            );
    }

@TestFactory
public List<DynamicTest> testRegexFromStringReplaceNullableFactory() {
        return Arrays.asList(
        dynamicTest("1", () -> testRegexFromStringReplaceNullable("replace('' '')", false)),
                dynamicTest("1", () -> testRegexFromStringReplaceNullable("none", true))
        );
    }
}
