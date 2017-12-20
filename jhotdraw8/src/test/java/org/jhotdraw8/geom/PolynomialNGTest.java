/* @(#)PolynomialNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.geom;

import java.util.Arrays;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
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

    
    @DataProvider
    public Object[][] polynomials() {
        return new Object[][]{
            {new Polynomial(5),new double[]{}},
            {new Polynomial(2,1),new double[]{-0.5}},
            {new Polynomial(-3, 0, 2, 0, 5),new double[]{1.2909944487358056, -1.2909944487358056}},
        };
    }

    @Test(dataProvider="polynomials")
    public void testGetRoots(Polynomial instance, double[] expected) {
        System.out.println("getRoots");
        System.out.println(instance);
        Arrays.sort(expected);
        double[] actual = instance.getRoots();
        Arrays.sort(actual);
        assertEquals(Arrays.toString(actual),Arrays.toString( expected));
    }

    @Test(dataProvider="polynomials")
    public void testGetRootsInInterval(Polynomial instance, double[] expected) {
        System.out.println("getRootsInInterval");
        System.out.println(instance);
        Arrays.sort(expected);
        double[] actual = instance.getRootsInInterval(-5,5);
        Arrays.sort(actual);
        for (int i=0;i<expected.length;i++) {
            assertEquals(actual[i], expected[i],1e-6,"root #"+i);
        }
    }
    
    public static void main(String[] args){
        PolynomialNGTest test=new PolynomialNGTest();
        for (Object[] a:test.polynomials()){
            test.testGetRoots((Polynomial)a[0],(double[])a[1]);
            test.testGetRootsInInterval((Polynomial)a[0],(double[])a[1]);
        }
    }

}