/* @(#)OSXCollatorNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import java.text.CollationKey;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * OSXCollatorNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class OSXCollatorNGTest {

    public OSXCollatorNGTest() {
    }

    /**
     * Test of compare method, of class OSXCollator.
     */
    @Test
    public void testExpandNumbers() {
        System.out.println("expandNumbers");
        OSXCollator instance = new OSXCollator();
        String input="a1b34";
       String expected = "a001b0134";
        String actual = instance.expandNumbers(input);
        assertEquals(actual, expected,actual+" == "+expected);
    }

}