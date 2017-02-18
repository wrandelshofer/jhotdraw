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
     * Test of getPoints method, of class Intersection.
     */
    @Test
    public void testGetPoints() {
        System.out.println("getPoints");
        Intersection instance = null;
        List expResult = null;
        Collection<Point2D> result = instance.getPoints();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of getTs method, of class Intersection.
     */
    @Test
    public void testGetTs() {
        System.out.println("getTs");
        Intersection instance = null;
        List expResult = null;
        Set<Double> result = instance.getTs();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of isEmpty method, of class Intersection.
     */
    @Test
    public void testIsEmpty() {
        System.out.println("isEmpty");
        Intersection instance = null;
        boolean expResult = false;
        boolean result = instance.isEmpty();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of size method, of class Intersection.
     */
    @Test
    public void testSize() {
        System.out.println("size");
        Intersection instance = null;
        int expResult = 0;
        int result = instance.size();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of toString method, of class Intersection.
     */
    @Test
    public void testToString() {
        System.out.println("toString");
        Intersection instance = null;
        String expResult = "";
        String result = instance.toString();
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of bezout method, of class Intersection.
     */
    @Test
    public void testBezout() {
        System.out.println("bezout");
        double[] e1 = null;
        double[] e2 = null;
        Polynomial expResult = null;
        Polynomial result = Intersection.bezout(e1, e2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier2Bezier2 method, of class Intersection.
     */
    @Test
    public void testIntersectBezier2Bezier2() {
        System.out.println("intersectBezier2Bezier2");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D a3 = null;
        Point2D b1 = null;
        Point2D b2 = null;
        Point2D b3 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier2Bezier2(a1, a2, a3, b1, b2, b3);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier2Bezier3 method, of class Intersection.
     */
    @Test
    public void testIntersectBezier2Bezier3() {
        System.out.println("intersectBezier2Bezier3");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D a3 = null;
        Point2D b1 = null;
        Point2D b2 = null;
        Point2D b3 = null;
        Point2D b4 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier2Bezier3(a1, a2, a3, b1, b2, b3, b4);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier2Circle method, of class Intersection.
     */
    @Test
    public void testIntersectBezier2Circle() {
        System.out.println("intersectBezier2Circle");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D c = null;
        double r = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier2Circle(p1, p2, p3, c, r);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier2Ellipse method, of class Intersection.
     */
    @Test
    public void testIntersectBezier2Ellipse() {
        System.out.println("intersectBezier2Ellipse");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D ec = null;
        double rx = 0.0;
        double ry = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier2Ellipse(p1, p2, p3, ec, rx, ry);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier2Line method, of class Intersection.
     */
    @Test
    public void testIntersectBezier2Line() {
        System.out.println("intersectBezier2Line");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D a1 = null;
        Point2D a2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier2Line(p1, p2, p3, a1, a2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
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

    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    @Test
    public void testIntersectLineBezier2_10args() {
        System.out.println("intersectLineBezier2");
        double ax = 0.0;
        double ay = 0.0;
        double bx = 0.0;
        double by = 0.0;
        double p1x = 0.0;
        double p1y = 0.0;
        double p2x = 0.0;
        double p2y = 0.0;
        double p3x = 0.0;
        double p3y = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineBezier2(ax, ay, bx, by, p1x, p1y, p2x, p2y, p3x, p3y);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier2Polygon method, of class Intersection.
     */
    @Test
    public void testIntersectBezier2Polygon() {
        System.out.println("intersectBezier2Polygon");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        List<Point2D> points = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier2Polygon(p1, p2, p3, points);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier2Rectangle method, of class Intersection.
     */
    @Test
    public void testIntersectBezier2Rectangle() {
        System.out.println("intersectBezier2Rectangle");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D r1 = null;
        Point2D r2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier2Rectangle(p1, p2, p3, r1, r2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier3Bezier3 method, of class Intersection.
     */
    @Test
    public void testIntersectBezier3Bezier3() {
        System.out.println("intersectBezier3Bezier3");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D a3 = null;
        Point2D a4 = null;
        Point2D b1 = null;
        Point2D b2 = null;
        Point2D b3 = null;
        Point2D b4 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier3Bezier3(a1, a2, a3, a4, b1, b2, b3, b4);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier3Circle method, of class Intersection.
     */
    @Test
    public void testIntersectBezier3Circle() {
        System.out.println("intersectBezier3Circle");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D p4 = null;
        Point2D c = null;
        double r = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier3Circle(p1, p2, p3, p4, c, r);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier3Ellipse method, of class Intersection.
     */
    @Test
    public void testIntersectBezier3Ellipse() {
        System.out.println("intersectBezier3Ellipse");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D p4 = null;
        Point2D ec = null;
        double rx = 0.0;
        double ry = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier3Ellipse(p1, p2, p3, p4, ec, rx, ry);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier3Line method, of class Intersection.
     */
    @Test
    public void testIntersectBezier3Line() {
        System.out.println("intersectBezier3Line");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D p4 = null;
        Point2D a1 = null;
        Point2D a2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier3Line(p1, p2, p3, p4, a1, a2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier3Polygon method, of class Intersection.
     */
    @Test
    public void testIntersectBezier3Polygon() {
        System.out.println("intersectBezier3Polygon");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D p4 = null;
        List<Point2D> points = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier3Polygon(p1, p2, p3, p4, points);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectBezier3Rectangle method, of class Intersection.
     */
    @Test
    public void testIntersectBezier3Rectangle() {
        System.out.println("intersectBezier3Rectangle");
        Point2D p1 = null;
        Point2D p2 = null;
        Point2D p3 = null;
        Point2D p4 = null;
        Point2D r1 = null;
        Point2D r2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectBezier3Rectangle(p1, p2, p3, p4, r1, r2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectCircleCircle method, of class Intersection.
     */
    @Test
    public void testIntersectCircleCircle() {
        System.out.println("intersectCircleCircle");
        Point2D c1 = null;
        double r1 = 0.0;
        Point2D c2 = null;
        double r2 = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectCircleCircle(c1, r1, c2, r2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectCircleEllipse method, of class Intersection.
     */
    @Test
    public void testIntersectCircleEllipse() {
        System.out.println("intersectCircleEllipse");
        Point2D cc = null;
        double r = 0.0;
        Point2D ec = null;
        double rx = 0.0;
        double ry = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectCircleEllipse(cc, r, ec, rx, ry);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectCircleLine method, of class Intersection.
     */
    @Test
    public void testIntersectCircleLine() {
        System.out.println("intersectCircleLine");
        Point2D c = null;
        double r = 0.0;
        Point2D a1 = null;
        Point2D a2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectCircleLine(c, r, a1, a2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectCirclePolygon method, of class Intersection.
     */
    @Test
    public void testIntersectCirclePolygon() {
        System.out.println("intersectCirclePolygon");
        Point2D c = null;
        double r = 0.0;
        List<Point2D> points = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectCirclePolygon(c, r, points);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectCircleRectangle method, of class Intersection.
     */
    @Test
    public void testIntersectCircleRectangle() {
        System.out.println("intersectCircleRectangle");
        Point2D c = null;
        double r = 0.0;
        Point2D r1 = null;
        Point2D r2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectCircleRectangle(c, r, r1, r2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectEllipseEllipse method, of class Intersection.
     */
    @Test
    public void testIntersectEllipseEllipse() {
        System.out.println("intersectEllipseEllipse");
        Point2D c1 = null;
        double rx1 = 0.0;
        double ry1 = 0.0;
        Point2D c2 = null;
        double rx2 = 0.0;
        double ry2 = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectEllipseEllipse(c1, rx1, ry1, c2, rx2, ry2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectEllipseLine method, of class Intersection.
     */
    @Test
    public void testIntersectEllipseLine() {
        System.out.println("intersectEllipseLine");
        Point2D ec = null;
        double rx = 0.0;
        double ry = 0.0;
        Point2D a1 = null;
        Point2D a2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectEllipseLine(ec, rx, ry, a1, a2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectEllipsePolygon method, of class Intersection.
     */
    @Test
    public void testIntersectEllipsePolygon() {
        System.out.println("intersectEllipsePolygon");
        Point2D c = null;
        double rx = 0.0;
        double ry = 0.0;
        List<Point2D> points = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectEllipsePolygon(c, rx, ry, points);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectEllipseRectangle method, of class Intersection.
     */
    @Test
    public void testIntersectEllipseRectangle() {
        System.out.println("intersectEllipseRectangle");
        Point2D c = null;
        double rx = 0.0;
        double ry = 0.0;
        Point2D r1 = null;
        Point2D r2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectEllipseRectangle(c, rx, ry, r1, r2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineCircle method, of class Intersection.
     */
    @Test
    public void testIntersectLineCircle_4args() {
        System.out.println("intersectLineCircle");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D c = null;
        double r = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineCircle(a1, a2, c, r);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineCircle method, of class Intersection.
     */
    @Test
    public void testIntersectLineCircle_7args() {
        System.out.println("intersectLineCircle");
        double x1 = 0.0;
        double y1 = 0.0;
        double x2 = 0.0;
        double y2 = 0.0;
        double cx = 0.0;
        double cy = 0.0;
        double r = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineCircle(x1, y1, x2, y2, cx, cy, r);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineEllipse method, of class Intersection.
     */
    @Test
    public void testIntersectLineEllipse_3args() {
        System.out.println("intersectLineEllipse");
        Point2D a1 = null;
        Point2D a2 = null;
        Bounds e = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineEllipse(a1, a2, e);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineEllipse method, of class Intersection.
     */
    @Test
    public void testIntersectLineEllipse_5args() {
        System.out.println("intersectLineEllipse");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D ec = null;
        double rx = 0.0;
        double ry = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineEllipse(a1, a2, ec, rx, ry);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineLine method, of class Intersection.
     */
    @Test
    public void testIntersectLineLine_4args() {
        System.out.println("intersectLineLine");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D b1 = null;
        Point2D b2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineLine(a1, a2, b1, b2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineLine method, of class Intersection.
     */
    @Test
    public void testIntersectLineLine_8args() {
        System.out.println("intersectLineLine");
        double a1x = 0.0;
        double a1y = 0.0;
        double a2x = 0.0;
        double a2y = 0.0;
        double b1x = 0.0;
        double b1y = 0.0;
        double b2x = 0.0;
        double b2y = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineLine(a1x, a1y, a2x, a2y, b1x, b1y, b2x, b2y);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLinePathIterator method, of class Intersection.
     */
    @Test
    public void testIntersectLinePathIterator() {
        System.out.println("intersectLinePathIterator");
        Point2D a = null;
        Point2D b = null;
        PathIterator pit = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLinePathIterator(a, b, pit);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLinePolygon method, of class Intersection.
     */
    @Test
    public void testIntersectLinePolygon() {
        System.out.println("intersectLinePolygon");
        Point2D a1 = null;
        Point2D a2 = null;
        List<Point2D> points = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLinePolygon(a1, a2, points);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineRectangle method, of class Intersection.
     */
    @Test
    public void testIntersectLineRectangle_4args() {
        System.out.println("intersectLineRectangle");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D r1 = null;
        Point2D r2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineRectangle(a1, a2, r1, r2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineRectangle method, of class Intersection.
     */
    @Test
    public void testIntersectLineRectangle_3args() {
        System.out.println("intersectLineRectangle");
        Point2D a1 = null;
        Point2D a2 = null;
        Bounds r = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineRectangle(a1, a2, r);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectLineRectangle method, of class Intersection.
     */
    @Test
    public void testIntersectLineRectangle_6args() {
        System.out.println("intersectLineRectangle");
        Point2D a1 = null;
        Point2D a2 = null;
        double rminx = 0.0;
        double rminy = 0.0;
        double rmaxx = 0.0;
        double rmaxy = 0.0;
        Intersection expResult = null;
        Intersection result = Intersection.intersectLineRectangle(a1, a2, rminx, rminy, rmaxx, rmaxy);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectPolygonPolygon method, of class Intersection.
     */
    @Test
    public void testIntersectPolygonPolygon() {
        System.out.println("intersectPolygonPolygon");
        List<Point2D> points1 = null;
        List<Point2D> points2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectPolygonPolygon(points1, points2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectPolygonRectangle method, of class Intersection.
     */
    @Test
    public void testIntersectPolygonRectangle() {
        System.out.println("intersectPolygonRectangle");
        List<Point2D> points = null;
        Point2D r1 = null;
        Point2D r2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectPolygonRectangle(points, r1, r2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectRayRay method, of class Intersection.
     */
    @Test
    public void testIntersectRayRay() {
        System.out.println("intersectRayRay");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D b1 = null;
        Point2D b2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectRayRay(a1, a2, b1, b2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

    /**
     * Test of intersectRectangleRectangle method, of class Intersection.
     */
    @Test
    public void testIntersectRectangleRectangle() {
        System.out.println("intersectRectangleRectangle");
        Point2D a1 = null;
        Point2D a2 = null;
        Point2D b1 = null;
        Point2D b2 = null;
        Intersection expResult = null;
        Intersection result = Intersection.intersectRectangleRectangle(a1, a2, b1, b2);
        assertEquals(result, expResult);
        // TODO review the generated test code and remove the default call to fail.
        fail("The test case is a prototype.");
    }

}