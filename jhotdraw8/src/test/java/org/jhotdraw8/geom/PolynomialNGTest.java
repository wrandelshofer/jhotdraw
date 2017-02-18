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
     * Test of eval method, of class Polynomial.
     */
    @Test
    public void testEval() {
        System.out.println("eval");
        double x = 0.0;
        Polynomial instance = null;
        double expResult = 0.0;
        double result = instance.eval(x);
        assertEquals(result, expResult, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of add method, of class Polynomial.
     */
    @Test
    public void testAdd() {
        System.out.println("add");
        Polynomial that = null;
        Polynomial instance = null;
        Polynomial expResult = null;
        Polynomial result = instance.add(that);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of multiply method, of class Polynomial.
     */
    @Test
    public void testMultiply() {
        System.out.println("multiply");
        Polynomial that = null;
        Polynomial instance = null;
        Polynomial expResult = null;
        Polynomial result = instance.multiply(that);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of divide_scalar method, of class Polynomial.
     */
    @Test
    public void testDivide_scalar() {
        System.out.println("divide_scalar");
        double scalar = 0.0;
        Polynomial instance = null;
        instance.divide_scalar(scalar);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of simplify method, of class Polynomial.
     */
    @Test
    public void testSimplify() {
        System.out.println("simplify");
        Polynomial instance = null;
        Polynomial expResult = null;
        Polynomial result = instance.simplify();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of bisection method, of class Polynomial.
     */
    @Test
    public void testBisection() {
        System.out.println("bisection");
        double min = 0.0;
        double max = 0.0;
        Polynomial instance = null;
        double expResult = 0.0;
        double result = instance.bisection(min, max);
        assertEquals(result, expResult, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Polynomial.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Polynomial instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of trapezoid method, of class Polynomial.
     */
    @Test
    public void testTrapezoid() {
        System.out.println("trapezoid");
        double min = 0.0;
        double max = 0.0;
        int n = 0;
        Polynomial instance = null;
        double expResult = 0.0;
        double result = instance.trapezoid(min, max, n);
        assertEquals(result, expResult, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of simpson method, of class Polynomial.
     */
    @Test
    public void testSimpson() {
        System.out.println("simpson");
        double min = 0.0;
        double max = 0.0;
        Polynomial instance = null;
        double expResult = 0.0;
        double result = instance.simpson(min, max);
        assertEquals(result, expResult, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of romberg method, of class Polynomial.
     */
    @Test
    public void testRomberg() {
        System.out.println("romberg");
        double min = 0.0;
        double max = 0.0;
        Polynomial instance = null;
        double expResult = 0.0;
        double result = instance.romberg(min, max);
        assertEquals(result, expResult, 0.0);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDegree method, of class Polynomial.
     */
    @Test
    public void testGetDegree() {
        System.out.println("getDegree");
        Polynomial instance = null;
        int expResult = 0;
        int result = instance.getDegree();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getDerivative method, of class Polynomial.
     */
    @Test
    public void testGetDerivative() {
        System.out.println("getDerivative");
        Polynomial instance = null;
        Polynomial expResult = null;
        Polynomial result = instance.getDerivative();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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

    /**
     * Test of getRootsInInterval method, of class Polynomial.
     */
    @Test
    public void testGetRootsInInterval() {
        System.out.println("getRootsInInterval");
        double min = 0.0;
        double max = 0.0;
        Polynomial instance = null;
        double[] expResult = null;
        double[] result = instance.getRootsInInterval(min, max);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}