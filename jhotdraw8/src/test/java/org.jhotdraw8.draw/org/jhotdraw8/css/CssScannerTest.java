/* @(#)CssScannerNGTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;


import org.jhotdraw8.annotation.NonNull;
import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;

/**
 * CssScannerNGTest.
 *
 * @author Werner Randelshofer
 */
public class CssScannerTest {

    public CssScannerTest() {
    }


    /**
     * Test of nextChar method, of class CssScanner.
     */
    public void testScanner(@NonNull String inputData, String expectedValue) throws Exception {
        CssScanner s = new CssScanner(new StringReader(inputData));
        //
        StringBuilder buf = new StringBuilder();
        while (s.nextChar() != -1) {
            buf.append((char) s.currentChar());
        }
        String actualValue = buf.toString();
        System.out.println("testScanner:" + actualValue + " :: " + expectedValue);
        for (char c : actualValue.toCharArray()) {
            System.out.print(Integer.toHexString(c) + ' ');
        }
        System.out.println();
        for (Integer i : actualValue.codePoints().toArray()) {
            System.out.print(Integer.toHexString(i) + ' ');
        }
        System.out.println();

        assertEquals(actualValue, expectedValue);
    }


    @TestFactory
    public @NonNull List<DynamicTest> dynamicTestsScanner() {
        return Arrays.asList(
                dynamicTest("abcd abcd", () -> testScanner("abcd", "abcd")),
                //
                dynamicTest("ab\ncd ab\ncd", () -> testScanner("ab\ncd", "ab\ncd")),
                dynamicTest("ab\r\ncd ab\ncd", () -> testScanner("ab\r\ncd", "ab\ncd")),
                dynamicTest("ab\fcd ab\ncd", () -> testScanner("ab\fcd", "ab\ncd")),
                dynamicTest("ab\rcd ab\ncd", () -> testScanner("ab\rcd", "ab\ncd")),
                //
                dynamicTest("abcd\n abcd\n", () -> testScanner("abcd\n", "abcd\n")),
                dynamicTest("abcd\r\n abcd\n", () -> testScanner("abcd\r\n", "abcd\n")),
                dynamicTest("abcd\f abcd\n", () -> testScanner("abcd\f", "abcd\n")),
                dynamicTest("abcd\r abcd\n", () -> testScanner("abcd\r", "abcd\n"))
        );
    }

}
