/* @(#)FigureNGTest.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import java.util.HashSet;
import java.util.Set;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.figure.shape.RectangleFigure;
import static org.testng.Assert.*;
import org.testng.annotations.AfterClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 *
 * @author werni
 */
public class FigureNGTest {
    
    public FigureNGTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }

    @BeforeMethod
    public void setUpMethod() throws Exception {
    }

    @AfterMethod
    public void tearDownMethod() throws Exception {
    }

    /**
     * Test of getDeclaredAndInheritedKeys method, of class Figure.
     */
    @Test
    public void testGetDeclaredAndInheritedKeys() {
       Set<Key<?>> figureKeys = Figure.getDeclaredAndInheritedKeys(Figure.class);
         Set<Key<?>> rectangleFigureKeys = Figure.getDeclaredAndInheritedKeys(RectangleFigure.class);
System.out.println("rr:"+rectangleFigureKeys);         
         Set<Key<?>> intersection=new HashSet<>(figureKeys);
         intersection.retainAll(rectangleFigureKeys);
System.out.println("ri:"+intersection);         
        assertEquals(figureKeys,intersection);
    }
    
}
