/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.jhotdraw8.text;

import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * @author Werner Randelshofer
 */
public class DefaultConverterTest {


    public static void testFromString(String expectedOutput, @NonNull String input) throws Exception {
        DefaultConverter c = new DefaultConverter();

        Object actualOutput = c.fromString(input);

        assertEquals(actualOutput, expectedOutput);
    }


    public static void testToString(String input, String expectedOutput) throws Exception {
        DefaultConverter c = new DefaultConverter();

        Object actualOutput = c.toString(input);

        assertEquals(actualOutput, expectedOutput);
    }

    @NonNull
    public static Object[][] textData() {
        return new Object[][]{
                {"hello world", "hello world"},
        };

    }

    @NonNull
    @TestFactory
    public List<DynamicTest> testFromStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testFromString("hello", "hello"))
        );

    }

    @NonNull
    @TestFactory
    public List<DynamicTest> testToStringFactory() {
        return Arrays.asList(
                dynamicTest("1", () -> testToString("hello", "hello"))
        );

    }

}
