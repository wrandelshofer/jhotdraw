/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.text;

import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.IOException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author werni
 */
public class PatternConverterTest {

    public PatternConverterTest() {
    }

    /**
     * Test of toString method, of class PatternConverter.
     */
    static
    public void testToString(String pattern, Object[] value, String expectedOutput) throws IOException {
        PatternConverter c = new PatternConverter(pattern, new MessageFormatConverterFactory());
        String actualOutput = c.toString(value);
        assertEquals(actualOutput, expectedOutput);
    }

    /**
     * Test of fromString method, of class PatternConverter.
     */
    static
    public void testFromString(String pattern, Object[] expectedValue, String input) throws IOException, ParseException {
        PatternConverter c = new PatternConverter(pattern, new MessageFormatConverterFactory());
        PatternConverter.AST ast = PatternConverter.parseTextFormatPattern(pattern);
        System.out.println("ast:" + ast);
        Object[] actualValue = c.fromString(input);
        System.out.println(Arrays.asList(actualValue));
        assertArrayEquals(actualValue, expectedValue);
    }

    @TestFactory
    public List<DynamicTest> testToStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testToString("{0,list,{1}}", new Object[]{0}, "")),
                dynamicTest("2", () -> testToString("{0,list,{1}}", new Object[]{1, "i0"}, "i0")),
                dynamicTest("3", () -> testToString("{0,list,{1}}", new Object[]{2, "i0", "i1"}, "i0i1")),
                dynamicTest("4", () -> testToString("{0,list,{1}|,}", new Object[]{2, "i0", "i1"}, "i0,i1")),
                dynamicTest("5", () -> testToString("{0,list,{1}|,}{2}", new Object[]{2, "i0", "i1", "x"}, "i0,i1x")),

                dynamicTest("1", () -> testToString("hello world", new Object[]{}, "hello world")),
                dynamicTest("1", () -> testToString("'hello world'", new Object[]{}, "hello world")),
                dynamicTest("1", () -> testToString("he+llo", new Object[]{}, "hello")),
                dynamicTest("1", () -> testToString("he*llo", new Object[]{}, "hllo")),
                dynamicTest("1", () -> testToString("h(e|a)llo", new Object[]{}, "hello")),
                dynamicTest("1", () -> testToString("h[ea]llo", new Object[]{}, "hello")),
                dynamicTest("1", () -> testToString("hello {0}", new Object[]{"world"}, "hello world")),
                dynamicTest("1", () -> testToString("left brace '{'", new Object[]{}, "left brace {")),
                dynamicTest("1", () -> testToString("right brace '}'", new Object[]{}, "right brace }")),
                dynamicTest("1", () -> testToString("quote ''", new Object[]{}, "quote '")),
                dynamicTest("1", () -> testToString("{0} world", new Object[]{"hello"}, "hello world")),
                dynamicTest("1", () -> testToString("{0} {1}", new Object[]{"hello", "world"}, "hello world")),

                dynamicTest("1", () -> testToString("{0,choice,0#hello}", new Object[]{0}, "hello")),
                dynamicTest("1", () -> testToString("{0,choice,0#hello world|1#good morning}", new Object[]{0}, "hello world")),
                dynamicTest("1", () -> testToString("{0,choice,0#hello world|1#good morning}minusone", new Object[]{-1}, "hello worldminusone")),
                dynamicTest("1", () -> testToString("{0,choice,0#hello world|1#good morning}zero", new Object[]{0}, "hello worldzero")),
                dynamicTest("1", () -> testToString("{0,choice,0#hello world|1#good morning}one", new Object[]{1}, "good morningone")),
                dynamicTest("1", () -> testToString("{0,choice,0#hello world|1#good morning}two", new Object[]{2}, "good morningtwo"))

        );
    }

    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testFromString("hello world", new Object[]{}, "hello world")),
                dynamicTest("1", () -> testFromString("'hello world'", new Object[]{}, "hello world")),
                dynamicTest("2", () -> testFromString("he+llo", new Object[]{}, "hello")),
                dynamicTest("1", () -> testFromString("he*llo", new Object[]{}, "hllo")),
                dynamicTest("1", () -> testFromString("h(e|a)llo", new Object[]{}, "hello")),
                dynamicTest("1", () -> testFromString("h[ea]llo", new Object[]{}, "hello")),
                dynamicTest("1", () -> testFromString("hello {0}", new Object[]{"world"}, "hello world")),
                dynamicTest("1", () -> testFromString("left brace '{'", new Object[]{}, "left brace {")),
                dynamicTest("1", () -> testFromString("right brace '}'", new Object[]{}, "right brace }")),
                dynamicTest("1", () -> testFromString("quote ''", new Object[]{}, "quote '")),
                dynamicTest("1", () -> testFromString("{0,word} world", new Object[]{"hello"}, "hello world")),
                dynamicTest("1", () -> testFromString("{0,word} {1}", new Object[]{"hello", "world"}, "hello world")),
                dynamicTest("1", () -> testFromString("{1,word} {0,word}", new Object[]{"world", "hello"}, "hello world")),
                dynamicTest("1", () -> testFromString("{0,number}", new Object[]{12L}, "12")),
                dynamicTest("1", () -> testFromString("{0,number}", new Object[]{12.5}, "12.5")),
                dynamicTest("1", () -> testFromString("{0,number}{1,choice,0#|1#px}", new Object[]{0.5, 0.0}, "0.5")),
                dynamicTest("1", () -> testFromString("{0,number}{1,choice,0#|1#px}", new Object[]{0.5, 1.0}, "0.5px")),
                dynamicTest("1", () -> testFromString("{0,list,{1,word}|[ ]+}", new Object[]{0}, "")),
                dynamicTest("1", () -> testFromString("{0,list,{1,word}|[ ]+}", new Object[]{1, "hello"}, "hello")),
                dynamicTest("1", () -> testFromString("{0,list,{1,word}|[ ]+}", new Object[]{2, "hello", "world"}, "hello world"))
        );

    }
}
