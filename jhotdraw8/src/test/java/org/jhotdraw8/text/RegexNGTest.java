/* @(#)RegexNGTest.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.text;

import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * RegexNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class RegexNGTest {

    public RegexNGTest() {
    }
    /**
     * Test of toString method, of class Regex.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Regex instance = new Regex();
        String expResult = "///";
        String result = instance.toString();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
    }
}