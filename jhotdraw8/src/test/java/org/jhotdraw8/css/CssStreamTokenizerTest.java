/* @(#)CssStreamTokenizerTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssStreamTokenizerTest.
 *
 * @author Werner Randelshofer
 */
public class CssStreamTokenizerTest {

    public CssStreamTokenizerTest() {
    }

    /**
     * Test of nextChar method, of class CssScanner.
     */
    public static void testTokenizer(String inputData, String expectedValue) throws Exception {
        CssStreamTokenizer tt = new CssStreamTokenizer(new StringReader(inputData));
        //
        StringBuilder buf = new StringBuilder();
        while (tt.nextNoSkip() != CssTokenType.TT_EOF) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            if (tt.current() < 0) {
                buf.append(tt.current());
            } else {
                buf.append((char) tt.current());
            }
            buf.append(':');
            if (tt.currentNumber() != null)
                buf.append(tt.currentNumber());
            if (tt.currentString() != null)
                buf.append(tt.currentString());
        }
        String actualValue = buf.toString();
        actualValue = actualValue.replaceAll("\\n", "\\\\n");
        actualValue = actualValue.replaceAll("\\t", "\\\\t");
        expectedValue = expectedValue.replaceAll("\\n", "\\\\n");
        expectedValue = expectedValue.replaceAll("\\t", "\\\\t");
        System.out.println("testTokenizer:" + actualValue + " :: " + expectedValue);

        assertEquals(expectedValue, actualValue);
    }

    @TestFactory
    List<DynamicTest> tokenizerData() {
        return Arrays.asList(
                dynamicTest("1", () -> testTokenizer("<!-", "<:< !:! -:-")),
                dynamicTest("2", () -> testTokenizer("func(", "-18:func")),
                dynamicTest("3", () -> testTokenizer("x[]()", "-2:x [:[ ]:] (:( ):)")),
                dynamicTest("4", () -> testTokenizer("x{a:b}", "-2:x {:{ -2:a ::: -2:b }:}")),
                dynamicTest("5", () -> testTokenizer("<!--", "-14:<!--")),
                dynamicTest("6", () -> testTokenizer("<!", "<:< !:!")),
                dynamicTest("7", () -> testTokenizer("<!--", "-14:<!--")),
                dynamicTest("8", () -> testTokenizer("-->", "-15:-->")),
                dynamicTest("9", () -> testTokenizer("->", "-:- >:>")),
                dynamicTest("10", () -> testTokenizer("--", "-:- -:-")),
                dynamicTest("11", () -> testTokenizer(">", ">:>")),
                dynamicTest("12", () -> testTokenizer("<!--a", "-14:<!-- -2:a")),
                dynamicTest("13", () -> testTokenizer("-->a", "-15:--> -2:a")),
                dynamicTest("14", () -> testTokenizer("/*bla*/", "-17:bla")),
                dynamicTest("15", () -> testTokenizer("/**bla**/", "-17:*bla*")),
                dynamicTest("16", () -> testTokenizer("/*bla*", "-7:bla*")),
                dynamicTest("17", () -> testTokenizer("/*bla", "-7:bla")),
                dynamicTest("18", () -> testTokenizer("/bla", "/:/ -2:bla")),
                dynamicTest("19", () -> testTokenizer("/*bla*/bla", "-17:bla -2:bla")),
                dynamicTest("20", () -> testTokenizer("16km", "-11:16km")),
                dynamicTest("21", () -> testTokenizer("16%", "-10:16%")),
                dynamicTest("22", () -> testTokenizer("16", "-9:16")),
                dynamicTest("23", () -> testTokenizer("'hel\nlo'", "-5:hel -16:\n -2:lo -5:")),
                dynamicTest("24", () -> testTokenizer("\nlo", "-16:\n -2:lo")),
                dynamicTest("25", () -> testTokenizer("'hel\\\nlo'", "-4:hel\nlo")),
                dynamicTest("26", () -> testTokenizer("'hello", "-5:hello")),
                dynamicTest("27", () -> testTokenizer("\"hello", "-5:hello")),
                dynamicTest("28", () -> testTokenizer("'hello'", "-4:hello")),
                dynamicTest("29", () -> testTokenizer("\"hello\"", "-4:hello")),
                dynamicTest("30", () -> testTokenizer("@", "@:@")),
                dynamicTest("31", () -> testTokenizer("@xy", "-3:xy")),
                dynamicTest("32", () -> testTokenizer("@0xy", "@:@ -11:0xy")),
                dynamicTest("33", () -> testTokenizer("@0.xy", "@:@ -9:0 .:. -2:xy")),
                dynamicTest("34", () -> testTokenizer("@0.5xy", "@:@ -11:0.5xy")),
                dynamicTest("35", () -> testTokenizer("#", "#:#")),
                dynamicTest("36", () -> testTokenizer("#xy", "-8:xy")),
                dynamicTest("37", () -> testTokenizer("#0xy", "-8:0xy")),
                dynamicTest("38", () -> testTokenizer("\\xy", "-2:xy")),
                dynamicTest("39", () -> testTokenizer("äbcd", "-2:äbcd")),
                dynamicTest("40", () -> testTokenizer("-abcd", "-2:-abcd")),
                dynamicTest("41", () -> testTokenizer("abcd", "-2:abcd")),
                dynamicTest("42", () -> testTokenizer("abcd()", "-18:abcd ):)")),
                dynamicTest("43", () -> testTokenizer("~=", "-19:~=")),
                dynamicTest("44", () -> testTokenizer("|=", "-20:|=")),
                dynamicTest("45", () -> testTokenizer("~", "~:~")),
                dynamicTest("46", () -> testTokenizer("|", "|:|")),
                dynamicTest("47", () -> testTokenizer("=", "=:=")),
                dynamicTest("48", () -> testTokenizer("url(", "-6:")),
                dynamicTest("49", () -> testTokenizer("url()", "-12:")),
                dynamicTest("50", () -> testTokenizer("url('hallo'", "-6:hallo")),
                dynamicTest("51", () -> testTokenizer("url('hallo')", "-12:hallo")),
                dynamicTest("52", () -> testTokenizer("url( 'hallo')", "-12:hallo")),
                dynamicTest("53", () -> testTokenizer("url( 'hallo' )", "-12:hallo")),
                dynamicTest("54", () -> testTokenizer("url(http://www.w3.org/css.html", "-6:http://www.w3.org/css.html")),
                dynamicTest("55", () -> testTokenizer("url(http://www.w3.org/css.html)", "-12:http://www.w3.org/css.html")),
                dynamicTest("56", () -> testTokenizer("url(http://www.w3.org/css.html   )", "-12:http://www.w3.org/css.html")),
                dynamicTest("57", () -> testTokenizer("url(   http://www.w3.org/css.html)", "-12:http://www.w3.org/css.html")),
                dynamicTest("58", () -> testTokenizer("url(   http://www. w3.html)", "-6:http://www. -2:w3 .:. -2:html ):)"))
                //
        );
    }
}
