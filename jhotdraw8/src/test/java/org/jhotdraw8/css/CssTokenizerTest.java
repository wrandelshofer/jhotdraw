/* @(#)CssTokenizerTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestFactory;
import org.junit.jupiter.params.provider.MethodSource;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssTokenizerTest.
 *
 * @author Werner Randelshofer
 */
public class CssTokenizerTest {

    public CssTokenizerTest() {
    }

    /**
     * Test of nextChar method, of class CssScanner.
     */
    public static void testTokenizer(String inputData, String expectedValue) throws Exception {
        CssTokenizer tt = new CssTokenizer(new StringReader(inputData),false);
        //
        StringBuilder buf = new StringBuilder();
        while (tt.nextToken() != CssTokenizer.TT_EOF) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            if (tt.currentToken() < 0) {
                buf.append(tt.currentToken());
            } else {
                buf.append((char) tt.currentToken());
            }
            buf.append(':');
            if (tt.currentNumericValue()!=null)
            buf.append(tt.currentNumericValue());
            if (tt.currentStringValue()!=null)
            buf.append(tt.currentStringValue());
        }
        String actualValue = buf.toString();
        actualValue=actualValue.replaceAll("\\n", "\\\\n");
        actualValue=actualValue.replaceAll("\\t", "\\\\t");
        expectedValue=expectedValue.replaceAll("\\n", "\\\\n");
        expectedValue=expectedValue.replaceAll("\\t", "\\\\t");
        System.out.println("testTokenizer:" + actualValue + " :: " + expectedValue);

        assertEquals(actualValue, expectedValue);
    }

    @TestFactory
    public List<DynamicTest> tokenizerData() {
        return Arrays.asList(
                dynamicTest("1", () -> testTokenizer("<!-", "<:< !:! -:-")),
dynamicTest("1", () -> testTokenizer("func(", "-18:func")),
dynamicTest("1", () -> testTokenizer("x[]()", "-2:x [:[ ]:] (:( ):)")),
dynamicTest("1", () -> testTokenizer("x{a:b}", "-2:x {:{ -2:a ::: -2:b }:}")),
dynamicTest("1", () -> testTokenizer("<!--", "-14:<!--")),
dynamicTest("1", () -> testTokenizer("<!", "<:< !:!")),
dynamicTest("1", () -> testTokenizer("<!--", "-14:<!--")),
dynamicTest("1", () -> testTokenizer("-->", "-15:-->")),
dynamicTest("1", () -> testTokenizer("->", "-:- >:>")),
dynamicTest("1", () -> testTokenizer("--", "-:- -:-")),
dynamicTest("1", () -> testTokenizer(">", ">:>")),
dynamicTest("1", () -> testTokenizer("<!--a", "-14:<!-- -2:a")),
dynamicTest("1", () -> testTokenizer("-->a", "-15:--> -2:a")),
dynamicTest("1", () -> testTokenizer("/*bla*/", "-17:bla")),
dynamicTest("1", () -> testTokenizer("/**bla**/", "-17:*bla*")),
dynamicTest("1", () -> testTokenizer("/*bla*", "-7:bla*")),
dynamicTest("1", () -> testTokenizer("/*bla", "-7:bla")),
dynamicTest("1", () -> testTokenizer("/bla", "/:/ -2:bla")),
dynamicTest("1", () -> testTokenizer("/*bla*/bla", "-17:bla -2:bla")),
dynamicTest("1", () -> testTokenizer("16km", "-11:16km")),
dynamicTest("1", () -> testTokenizer("16%", "-10:16%")),
dynamicTest("1", () -> testTokenizer("16","-9:16")),
dynamicTest("1", () -> testTokenizer("'hel\nlo'", "-5:hel -16:\n -2:lo -5:")),
dynamicTest("1", () -> testTokenizer("\nlo", "-16:\n -2:lo")),
dynamicTest("1", () -> testTokenizer("'hel\\\nlo'", "-4:hel\nlo")),
dynamicTest("1", () -> testTokenizer("'hello", "-5:hello")),
dynamicTest("1", () -> testTokenizer("\"hello", "-5:hello")),
dynamicTest("1", () -> testTokenizer("'hello'", "-4:hello")),
dynamicTest("1", () -> testTokenizer("\"hello\"", "-4:hello")),
dynamicTest("1", () -> testTokenizer("@", "@:@")),
dynamicTest("1", () -> testTokenizer("@xy", "-3:xy")),
dynamicTest("1", () -> testTokenizer("@0xy", "@:@ -11:0xy")),
dynamicTest("1", () -> testTokenizer("@0.xy", "@:@ -9:0 .:. -2:xy")),
dynamicTest("1", () -> testTokenizer("@0.5xy", "@:@ -11:0.5xy")),
dynamicTest("1", () -> testTokenizer("#", "#:#")),
dynamicTest("1", () -> testTokenizer("#xy", "-8:xy")),
dynamicTest("1", () -> testTokenizer("#0xy", "-8:0xy")),
dynamicTest("1", () -> testTokenizer("\\xy", "-2:xy")),
dynamicTest("1", () -> testTokenizer("äbcd", "-2:äbcd")),
dynamicTest("1", () -> testTokenizer("-abcd", "-2:-abcd")),
dynamicTest("1", () -> testTokenizer("abcd", "-2:abcd")),
dynamicTest("1", () -> testTokenizer("abcd()", "-18:abcd ):)")),
dynamicTest("1", () -> testTokenizer("~=", "-19:~=")),
dynamicTest("1", () -> testTokenizer("|=", "-20:|=")),
dynamicTest("1", () -> testTokenizer("~", "~:~")),
dynamicTest("1", () -> testTokenizer("|", "|:|")),
dynamicTest("1", () -> testTokenizer("=", "=:=")),
dynamicTest("1", () -> testTokenizer("url(","-6:")),
dynamicTest("1", () -> testTokenizer("url()","-12:")),
dynamicTest("1", () -> testTokenizer("url('hallo'","-6:hallo")),
dynamicTest("1", () -> testTokenizer("url('hallo')","-12:hallo")),
dynamicTest("1", () -> testTokenizer("url( 'hallo')","-12:hallo")),
dynamicTest("1", () -> testTokenizer("url( 'hallo' )","-12:hallo")),
dynamicTest("1", () -> testTokenizer("url(http://www.w3.org/css.html","-6:http://www.w3.org/css.html")),
dynamicTest("1", () -> testTokenizer("url(http://www.w3.org/css.html)","-12:http://www.w3.org/css.html")),
dynamicTest("1", () -> testTokenizer("url(http://www.w3.org/css.html   )","-12:http://www.w3.org/css.html")),
dynamicTest("1", () -> testTokenizer("url(   http://www.w3.org/css.html)","-12:http://www.w3.org/css.html")),
dynamicTest("1", () -> testTokenizer("url(   http://www. w3.html)","-6:http://www. -2:w3 .:. -2:html ):)"))
        //
        );
    }
}
