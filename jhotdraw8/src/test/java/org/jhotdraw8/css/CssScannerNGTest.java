/* @(#)CssScannerNGTest.java
 * Copyright (c) 2015 The authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.css;


import org.junit.jupiter.api.DynamicTest;
import org.junit.jupiter.api.TestFactory;

import java.io.StringReader;
import java.util.Arrays;
import java.util.List;
import static org.junit.jupiter.api.DynamicTest.dynamicTest;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * CssScannerNGTest.
 * @author Werner Randelshofer
 */
public class CssScannerNGTest {
    
    public CssScannerNGTest() {
    }

 

    /**
     * Test of nextChar method, of class CssScanner.
     */
    public void doTestScanner(String inputData, String expectedValue) throws Exception {
        CssScanner s = new CssScanner(new StringReader(inputData));
        //
        StringBuilder buf=new StringBuilder();
        while (s.nextChar()!=-1) {
            buf.append((char)s.currentChar());
        }
        String actualValue=buf.toString();
        System.out.println("testScanner:"+actualValue+" :: "+expectedValue);
        for (char c:actualValue.toCharArray()) {
            System.out.print(Integer.toHexString(c)+ ' ');
        }
        System.out.println();
        for (Integer i:actualValue.codePoints().toArray()) {
            System.out.print(Integer.toHexString(i)+ ' ');
        }
        System.out.println();
        
        assertEquals(actualValue,expectedValue);
    }




    @TestFactory
    public List<DynamicTest> testScanner() {
        return Arrays.asList(
                dynamicTest("abcd abcd",()->doTestScanner("abcd", "abcd")),
            //
            dynamicTest("ab\ncd ab\ncd",()->doTestScanner("ab\ncd", "ab\ncd")),
            dynamicTest("ab\r\ncd ab\ncd",()->doTestScanner("ab\r\ncd", "ab\ncd")),
            dynamicTest("ab\fcd ab\ncd",()->doTestScanner("ab\fcd", "ab\ncd")),
            dynamicTest("ab\rcd ab\ncd",()->doTestScanner("ab\rcd", "ab\ncd")),
            //
            dynamicTest("abcd\n abcd\n",()->doTestScanner("abcd\n", "abcd\n")),
            dynamicTest("abcd\r\n abcd\n",()->doTestScanner("abcd\r\n", "abcd\n")),
            dynamicTest("abcd\f abcd\n",()->doTestScanner("abcd\f", "abcd\n")),
            dynamicTest("abcd\r abcd\n",()->doTestScanner("abcd\r", "abcd\n"))
        );
    }
    
}
