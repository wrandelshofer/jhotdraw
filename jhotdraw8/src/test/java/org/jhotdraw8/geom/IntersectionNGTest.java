/* @(#)IntersectionNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.geom;

import java.awt.geom.PathIterator;
import java.util.Collection;
import java.util.List;
import java.util.Set;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import static org.testng.Assert.*;
import org.testng.annotations.Test;

/**
 * IntersectionNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntersectionNGTest {

    public IntersectionNGTest() {
    }


    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    @Test
    public void testIntersectLineBezier2_5args() {
        System.out.println("intersectLineBezier2");
        Point2D p1 = new Point2D(125,200);
        Point2D p2 = new Point2D(250,225);
        Point2D p3 = new Point2D(275,100);
        Point2D a1 = new Point2D(30,125);
        Point2D a2 = new Point2D(300,175);
        Intersection expResult = null;
System.out.println("line->bezier2");        
        Intersection result = Intersection.intersectLineBezier2(a1, a2, p1, p2, p3);
System.out.println(result.getPoints());      
System.out.println(result.getTs());     
System.out.println("bezier2->line");
         result = Intersection.intersectBezier2Line( p1, p2, p3,a1, a2);
System.out.println(result.getPoints());      
System.out.println(result.getTs());      
System.out.println("line->line");
      result = Intersection.intersectLineLine(a1, a2, p1,  p3);
System.out.println(result.getPoints());      
System.out.println(result.getTs());      
for (double t:result.getTs()) {
    System.out.print(", "+Geom.lerp(a1,a2,t));
}System.out.println();
        
     //   assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
      //  fail("The test case is a prototype.");
    }

 

}