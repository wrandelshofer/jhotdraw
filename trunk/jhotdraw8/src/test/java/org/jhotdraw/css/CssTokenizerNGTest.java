/* @(#)CssTokenizerNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.css;

import java.io.StringReader;
import static org.testng.Assert.*;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * CssTokenizerNGTest.
 *
 * @author Werner Randelshofer
 */
public class CssTokenizerNGTest {

    public CssTokenizerNGTest() {
    }

    /**
     * Test of nextChar method, of class CssScanner.
     */
    @Test(dataProvider = "tokenizerData")
    public void testTokenizer(String inputData, String expectedValue) throws Exception {
        CssTokenizer s = new CssTokenizer(new StringReader(inputData));
        //
        StringBuilder buf = new StringBuilder();
        while (s.nextToken() != CssTokenizer.TT_EOF) {
            if (buf.length() > 0) {
                buf.append(' ');
            }
            if (s.currentToken() < 0) {
                buf.append(s.currentToken());
            } else {
                buf.append((char) s.currentToken());
            }
            buf.append(':');
            buf.append(s.currentValue());
        }
        String actualValue = buf.toString();
        actualValue=actualValue.replaceAll("\\n", "\\\\n");
        actualValue=actualValue.replaceAll("\\t", "\\\\t");
        expectedValue=expectedValue.replaceAll("\\n", "\\\\n");
        expectedValue=expectedValue.replaceAll("\\t", "\\\\t");
        System.out.println("testTokenizer:" + actualValue + " :: " + expectedValue);

        assertEquals(actualValue, expectedValue);
    }

    @DataProvider
    public Object[][] tokenizerData() {
        return new Object[][]{
            {"func(", "-18:func"},
            {"x[]()", "-2:x [:[ ]:] (:( ):)"},
            {"x{a:b}", "-2:x {:{ -2:a ::: -2:b }:}"},
            {"<!--", "-14:<!--"},
            {"<!-", "<:< !:! -:-"},
            {"<!", "<:< !:!"},
            {"<!--", "-14:<!--"},
            {"-->", "-15:-->"},
            {"->", "-:- >:>"},
            {"--", "-:- -:-"},
            {">", ">:>"},
            {"<!--a", "-14:<!-- -2:a"},
            {"-->a", "-15:--> -2:a"},
            {"/*bla*/", "-17:bla"},
            {"/**bla**/", "-17:*bla*"},
            {"/*bla*", "-7:bla*"},
            {"/*bla", "-7:bla"},
            {"/bla", "/:/ -2:bla"},
            {"/*bla*/bla", "-17:bla -2:bla"},
            {"16km", "-11:16km"},
            {"16%", "-10:16"},
            {"16","-9:16"},
            {"'hel\nlo'", "-5:hel -16:\n -2:lo -5:"},
            {"\nlo", "-16:\n -2:lo"},
            {"'hel\\\nlo'", "-4:hel\nlo"},
            {"'hello", "-5:hello"},
            {"\"hello", "-5:hello"},
            {"'hello'", "-4:hello"},
            {"\"hello\"", "-4:hello"},
            {"@", "@:@"},
            {"@xy", "-3:xy"},
            {"@0xy", "@:@ -11:0xy"},
            {"@0.xy", "@:@ -9:0 .:. -2:xy"},
            {"@0.5xy", "@:@ -11:0.5xy"},
            {"#", "#:#"},
            {"#xy", "-8:xy"},
            {"#0xy", "-8:0xy"},
            {"\\xy", "-2:xy"},
            {"äbcd", "-2:äbcd"},
            {"-abcd", "-2:-abcd"},
            {"abcd", "-2:abcd"},
            {"abcd()", "-18:abcd ):)"},
            {"~=", "-19:~="},
            {"|=", "-20:|="},
            {"~", "~:~"},
            {"|", "|:|"},
            {"=", "=:="},
            {"url(","-6:"},
            {"url()","-12:"},
            {"url('hallo'","-6:hallo"},
            {"url('hallo')","-12:hallo"},
            {"url( 'hallo')","-12:hallo"},
            {"url( 'hallo' )","-12:hallo"},
            {"url(http://www.w3.org/css.html","-6:http://www.w3.org/css.html"},
            {"url(http://www.w3.org/css.html)","-12:http://www.w3.org/css.html"},
            {"url(http://www.w3.org/css.html   )","-12:http://www.w3.org/css.html"},
            {"url(   http://www.w3.org/css.html)","-12:http://www.w3.org/css.html"},
            {"url(   http://www. w3.html)","-6:http://www. -2:w3 .:. -2:html ):)"},
        //
        };
    }

}
