/* @(#)IntersectionsNGTest.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.geom;

import java.util.Arrays;
import javafx.geometry.Point2D;
import javafx.scene.shape.Circle;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.QuadCurve;
import static org.testng.Assert.assertEquals;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * IntersectionsNGTest.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class IntersectionsNGTest {

    public IntersectionsNGTest() {
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
    @DataProvider
    public Object[][] bezier3Point() {
        return new Object[][]{
            { new CubicCurve(900.0, 700.0, 60.0, 100.0, 70.0, 700.0, 900.0, 100.0), new Circle(410.0, 400.0, 60.0),new double[]{0.47368421052631576,0.7}},
            {new CubicCurve(200.0,20.0,40.0,240.0,40.0,20.0,200.0,240.0),new Circle(130,180,40),new double[]{0.8548192690545715}},
            {new CubicCurve(200.0,20.0,40.0,240.0,40.0,20.0,200.0,240.0),new Circle(120,180,40),new double[]{0.8380940208991527}},
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
            assertEquals(actual[i],expected[i],1e-6,"root #"+i);
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
        Intersection isec = Intersections.intersectLineEllipse(a1, a2, bc, brx, bry);
        System.out.println("  isec: "+isec);
        double[] actual=new double[isec.size()];
        for (int i=0;i<actual.length;i++) {
            actual[i]=isec.getTs().get(i);
        }
        Arrays.sort(actual);
        for (int i=0;i<expected.length;i++) {
            assertEquals(actual[i],expected[i],1e-6,"root #"+i);
        }
    }
    /**
     * Test of intersectLineBezier2 method, of class Intersection.
     */
    @Test(dataProvider="bezier3Point")
    public void testIntersectBezier3Point_11args(CubicCurve a, Circle b, double[] expected) {
        System.out.println("testIntersectBezier3Point_5args");
        System.out.println("bezier3->point");
        System.out.println("a:"+a);
        System.out.println("b:"+b);
        Intersection isec = Intersections.intersectBezier3Point(
                a.getStartX(),a.getStartY(),                a.getControlX1(),a.getControlY1(),
                a.getControlX2(),a.getControlY2(),a.getEndX(),a.getEndY(),
                b.getCenterX(),b.getCenterY(),b.getRadius());
        double[] actual=new double[isec.size()];
        for (int i=0;i<actual.length;i++) {
            actual[i]=isec.getTs().get(i);
        }
        Arrays.sort(actual);
        Arrays.sort(expected);
        System.out.println("  expected: "+Arrays.toString(expected));
        System.out.println("  actual: "+Arrays.toString(actual));
        for (int i=0;i<expected.length;i++) {
            assertEquals(actual[i],expected[i],1e-6,"root #"+i);
        }
    }

}
