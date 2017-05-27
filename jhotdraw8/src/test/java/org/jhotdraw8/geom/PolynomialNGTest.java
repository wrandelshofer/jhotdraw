/* @(#)PolynomialNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.geom;

import java.util.Arrays;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * PolynomialNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PolynomialNGTest {

    public PolynomialNGTest() {
    }

    /**
     * Test of getRoots method, of class Polynomial.
     */
    @Test
    public void testGetRoots() {
        System.out.println("getRoots");
        Polynomial instance = new Polynomial(0.0,13885.714285714286,0,-13885.714285714286);
        System.out.println(instance);
        double[] expResult = new double[]{1,-1};
        double[] result = instance.getRoots();
        assertEquals(Arrays.toString(result),Arrays.toString( expResult));
    }

}