/* @(#)IntersectionNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom;

import java.util.Arrays;
import javafx.geometry.Point2D;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
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
    @DataProvider
    public Object[][] lineBezier2() {
        return new Object[][]{
            {new Line(10,40,210,175),new QuadCurve(125,200,250,225,275,100),new double[]{}},
        };
    }
    @DataProvider
    public Object[][] lineEllipse() {
        return new Object[][]{
            {new Line(10,40,200,40),new Ellipse(100,100,60,60),new double[]{0.47368421052631576}},
            {new Line(10,40,200,40),new Ellipse(100,100,50,60),new double[]{0.47368421052631576}},
        };
    }
    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    @Test(dataProvider="lineBezier2")
    public void testIntersectLineBezier2_5args(Line a, QuadCurve b, double[] expected) {
        System.out.println("intersectLineBezier2");
        Point2D b1 = new Point2D(b.getStartX(), b.getEndX());
        Point2D b2 = new Point2D(b.getControlX(), b.getControlY());
        Point2D b3 = new Point2D(b.getEndX(),b.getEndX());
        Point2D a1 = new Point2D(a.getStartX(), a.getStartY());
        Point2D a2 = new Point2D(a.getEndX(), a.getEndY());
        System.out.println("line->bezier2");
        Intersection isec = Intersections.intersectLineBezier2(a1, a2, b1, b2, b3);
        System.out.println("  isec: "+isec);
        double[] actual=new double[isec.size()];
        for (int i=0;i<actual.length;i++) {
            actual[i]=isec.getTs().get(i);
        }
        Arrays.sort(actual);
        for (int i=0;i<expected.length;i++) {
            assertEquals(actual[i],expected[i],1e6,"root #"+i);
        }
    }
    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    @Test(dataProvider="lineEllipse")
    public void testIntersectLineEllipse_5args(Line a, Ellipse b, double[] expected) {
        System.out.println("intersectLineEllipse");
        Point2D bc = new Point2D(b.getCenterX(), b.getCenterX());
        double brx = b.getRadiusX();
        double bry = b.getRadiusY();
        Point2D a1 = new Point2D(a.getStartX(), a.getStartY());
        Point2D a2 = new Point2D(a.getEndX(), a.getEndY());
        System.out.println("line->ellipse");
        Intersection isec = Intersections.intersectLineEllipse(a1, a2, bc, brx, bry);
        System.out.println("  isec: "+isec);
        double[] actual=new double[isec.size()];
        for (int i=0;i<actual.length;i++) {
            actual[i]=isec.getTs().get(i);
        }
        Arrays.sort(actual);
        for (int i=0;i<expected.length;i++) {
            assertEquals(actual[i],expected[i],1e6,"root #"+i);
        }
    }

}
