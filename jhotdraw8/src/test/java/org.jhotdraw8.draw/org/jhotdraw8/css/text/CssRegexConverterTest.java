/* @(#)CssRegexConverterTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css.text;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.text.RegexReplace;
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

    public static void testFromStringApply(@NonNull String inputCssRegex, String inputValue, String expectedValue) throws Exception {
        CssRegexConverter c = new CssRegexConverter(false);
        RegexReplace rgx = c.fromString(inputCssRegex);
        String actualValue = rgx.apply(inputValue);
        assertEquals(actualValue, expectedValue);
    }


    public static void testRegexFromStringReplace(@NonNull String inputCssRegex, String expectedFind, String expectedReplace) throws Exception {
        CssRegexConverter c = new CssRegexConverter(false);
        RegexReplace rgx = c.fromString(inputCssRegex);
        assertEquals(rgx.getFind(), expectedFind, "find");
        assertEquals(rgx.getReplace(), expectedReplace, "replace");
    }

    public static void testRegexFromStringReplaceNullable(@NonNull String inputCssRegex, boolean expectNull) throws Exception {
        CssRegexConverter cNullable = new CssRegexConverter(true);
        RegexReplace rgx = cNullable.fromString(inputCssRegex);
        if (expectNull) {
            assertNull(rgx, "Nullable converter must return null regex");
        } else {
            assertNotNull(rgx, "Nullable converter most not return null regex");
        }

        CssRegexConverter cNonNull = new CssRegexConverter(false);
        try {
            RegexReplace rgxNonNull = cNonNull.fromString(inputCssRegex);
            if (expectNull) {
                fail("NonNull converter must produce ParseException");
            }
        } catch (ParseException e) {
            if (!expectNull) {
                fail("NonNull converter must not produce ParseException");
            }
        }
    }


    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsRegexFromStringReplace() {
        return Arrays.asList(
                dynamicTest("1", () -> testRegexFromStringReplace("replace('' '')", "", "")),
                dynamicTest("2", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
                dynamicTest("3", () -> testRegexFromStringReplace("replace('.*@(.*)' '$1')", ".*@(.*)", "$1")),
                dynamicTest("4", () -> testRegexFromStringReplace("replace('.*@(.*)' '$0')", ".*@(.*)", "$0")),
                dynamicTest("5", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
                dynamicTest("6", () -> testRegexFromStringReplace("replace('.*@(.*)' '')", ".*@(.*)", "")),
                dynamicTest("7", () -> testRegexFromStringReplace("replace('.*@(.*)' '')", ".*@(.*)", "")),
                //
                dynamicTest("8", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
                dynamicTest("9", () -> testRegexFromStringReplace("replace('.*@(.*)' '$1')", ".*@(.*)", "$1")),
                dynamicTest("10", () -> testRegexFromStringReplace("replace('.*@(.*)' '$0')", ".*@(.*)", "$0")),
                dynamicTest("11", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
                dynamicTest("12", () -> testRegexFromStringReplace("replace('.*@(.*)' '')", ".*@(.*)", "")),
                dynamicTest("13", () -> testRegexFromStringReplace("replace('.*@(.*)')", ".*@(.*)", null)),
                //
                dynamicTest("14", () -> testRegexFromStringReplace("replace('.*\\'(.*)' '$1')", ".*'(.*)", "$1"))
        );
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsRegexFromStringApply() {
        return Arrays.asList(
                dynamicTest("1", () -> testFromStringApply("replace('' '')", "", "")),
                dynamicTest("2", () -> testFromStringApply("replace('.*@(.*)')", "a@b", "a@b")),
                dynamicTest("3", () -> testFromStringApply("replace('.*@(.*)' '$1')", "a@b", "b")),
                dynamicTest("4", () -> testFromStringApply("replace('.*@(.*)' '$0')", "a@b", "a@b")),
                dynamicTest("5", () -> testFromStringApply("replace('.*@(.*)')", "a@b", "a@b")),
                dynamicTest("6", () -> testFromStringApply("replace('.*@(.*)' ' ' )", "a@b", " ")),
                dynamicTest("7", () -> testFromStringApply("replace('.*@(.*)' '')", "a@b", ""))
        );
    }

    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsRegexFromStringReplaceNullable() {
        return Arrays.asList(
                dynamicTest("1", () -> testRegexFromStringReplaceNullable("replace('' '')", false)),
                dynamicTest("2", () -> testRegexFromStringReplaceNullable("none", true))
        );
    }
}
