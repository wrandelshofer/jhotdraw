/* @(#)Intersection.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 *
 * This class is a based on:
*
*  Polynomial.js by Kevin Lindsey.
 * Copyright (C) 2002, Kevin Lindsey.
 *
 * MgcPolynomial.cpp by David Eberly. 
 * Copyright (c) 2000-2003 Magic Software, Inc.
 */
package org.jhotdraw8.geom;

import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import static org.jhotdraw8.geom.Geom.lerp;

/**
 * Provides a collection of intersection tests.
 *
 * This class is a port of Intersection.js by Kevin Lindsey. Part of
 * Intersection.js is based on MgcPolynomial.cpp written by David Eberly, Magic
 * Software. Inc.
 * <p>
 * References:
 * <p>
 * <a href="http://www.kevlindev.com/gui/index.htm">Intersection.js</a>,
 * Copyright (c) 2002, Kevin Lindsey.
 * <p>
 * <a href="http://www.magic-software.com">MgcPolynomial.cpp </a> Copyright
 * 2000-2003 (c) David Eberly. Magic Software, Inc.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class Intersection {

    private final ArrayList<Point2D> points;
    private Status status;
    private final ArrayList<Double> ts;

    public Intersection(Status status) {

        this.status = status;
        this.points = new ArrayList<>();
        this.ts = new ArrayList<>();
    }

    ;


/** Appends a point and the parameter t. */
private void appendPoint(Point2D point, double t) {
        this.points.add(point);
        this.ts.add(t);
    }

    ;


private void appendPoints(List<Point2D> points) {
        this.points.addAll(points);
    }

    private void appendPoints(List<Point2D> points, List<Double> ts) {
        this.points.addAll(points);
        this.ts.addAll(ts);
    }

    public List<Point2D> getPoints() {
        return Collections.unmodifiableList(points);
    }

    public List<Double> getTs() {
        return Collections.unmodifiableList(ts);
    }

    public boolean isEmpty() {
        return points.isEmpty();
    }

    public int size() {
        return points.size();
    }

    private void throwAwayTs() {
        ts.clear();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Intersection{").append(status).append(", points=");
        boolean first = true;
        for (Point2D p : points) {
            if (first) {
                first = false;
            } else {
                b.append(' ');
            }
            b.append(p.getX()).append(',').append(p.getY());
        }
        b.append(", ts=").append(ts).append('}');
        return b.toString();
    }

    enum Status {
        INTERSECTION,
        NO_INTERSECTION,
        NO_INTERSECTION_INSIDE,
        NO_INTERSECTION_OUTSIDE,
        NO_INTERSECTION_TANGENT,
        NO_INTERSECTION_COINCIDENT,
        NO_INTERSECTION_PARALLEL
    }

    /**
     * Constructs a polynomial as a Bezout determinant given two polynomials e1
     * and e2.
     *
     * @param e1 polynomial e1
     * @param e2 polynomial e2
     * @return the bezout determinant
     */
    public static Polynomial bezout(double[] e1, double[] e2) {
        double AB = e1[0] * e2[1] - e2[0] * e1[1];
        double AC = e1[0] * e2[2] - e2[0] * e1[2];
        double AD = e1[0] * e2[3] - e2[0] * e1[3];
        double AE = e1[0] * e2[4] - e2[0] * e1[4];
        double AF = e1[0] * e2[5] - e2[0] * e1[5];
        double BC = e1[1] * e2[2] - e2[1] * e1[2];
        double BE = e1[1] * e2[4] - e2[1] * e1[4];
        double BF = e1[1] * e2[5] - e2[1] * e1[5];
        double CD = e1[2] * e2[3] - e2[2] * e1[3];
        double DE = e1[3] * e2[4] - e2[3] * e1[4];
        double DF = e1[3] * e2[5] - e2[3] * e1[5];
        double BFpDE = BF + DE;
        double BEmCD = BE - CD;

        return new Polynomial(
                AB * BC - AC * AC,
                AB * BEmCD + AD * BC - 2 * AC * AE,
                AB * BFpDE + AD * BEmCD - AE * AE - 2 * AC * AF,
                AB * DF + AD * BFpDE - 2 * AE * AF,
                AD * DF - AF * AF
        );

    }

    /**
     * Returns true if point 'a' is greater or equal to point 'b'. Compares the
     * x-coordinates first, and if they are equal compares the y-coordinates.
     *
     * @param a point a
     * @param b point b
     * @return true if a is greater or equal b
     */
    private static boolean gte(Point2D a, Point2D b) {
        return a.getX() >= b.getX() && a.getY() >= b.getY();
    }

    /**
     * Computes the intersection between quadratic bezier curve 'a' and
     * quadratic bezier curve 'b'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param a1 control point 1 of 'a'
     * @param a2 control point 2 of 'a'
     * @param a3 control point 3 of 'a'
     * @param b1 control point 1 of 'b'
     * @param b2 control point 2 of 'b'
     * @param b3 control point 3 of 'b'
     * @return the computed result
     */
    public static Intersection intersectBezier2Bezier2(Point2D a1, Point2D a2, Point2D a3, Point2D b1, Point2D b2, Point2D b3) {
        Point2D a, b;
        Point2D c12, c11, c10;
        Point2D c22, c21, c20;
        Intersection result = new Intersection(Status.NO_INTERSECTION);
        Polynomial poly;

        a = a2.multiply(-2);
        c12 = a1.add(a.add(a3));

        a = a1.multiply(-2);
        b = a2.multiply(2);
        c11 = a.add(b);

        c10 = a1;;

        a = b2.multiply(-2);
        c22 = b1.add(a.add(b3));

        a = b1.multiply(-2);
        b = b2.multiply(2);
        c21 = a.add(b);

        c20 = b1;

        if (c12.getY() == 0) {
            double v0 = c12.getX() * (c10.getY() - c20.getY());
            double v1 = v0 - c11.getX() * c11.getY();
            double v2 = v0 + v1;
            double v3 = c11.getY() * c11.getY();

            poly = new Polynomial(
                    c12.getX() * c22.getY() * c22.getY(),
                    2 * c12.getX() * c21.getY() * c22.getY(),
                    c12.getX() * c21.getY() * c21.getY() - c22.getX() * v3 - c22.getY() * v0 - c22.getY() * v1,
                    -c21.getX() * v3 - c21.getY() * v0 - c21.getY() * v1,
                    (c10.getX() - c20.getX()) * v3 + (c10.getY() - c20.getY()) * v1
            );
        } else {
            double v0 = c12.getX() * c22.getY() - c12.getY() * c22.getX();
            double v1 = c12.getX() * c21.getY() - c21.getX() * c12.getY();
            double v2 = c11.getX() * c12.getY() - c11.getY() * c12.getX();
            double v3 = c10.getY() - c20.getY();
            double v4 = c12.getY() * (c10.getX() - c20.getX()) - c12.getX() * v3;
            double v5 = -c11.getY() * v2 + c12.getY() * v4;
            double v6 = v2 * v2;

            poly = new Polynomial(
                    v0 * v0,
                    2 * v0 * v1,
                    (-c22.getY() * v6 + c12.getY() * v1 * v1 + c12.getY() * v0 * v4 + v0 * v5) / c12.getY(),
                    (-c21.getY() * v6 + c12.getY() * v1 * v4 + v1 * v5) / c12.getY(),
                    (v3 * v6 + v4 * v5) / c12.getY()
            );
        }

        double[] roots = poly.getRoots();
        for (int i = 0; i < roots.length; i++) {
            double s = roots[i];

            if (0 <= s && s <= 1) {
                double[] xRoots = new Polynomial(
                        c12.getX(),
                        c11.getX(),
                        c10.getX() - c20.getX() - s * c21.getX() - s * s * c22.getX()
                ).getRoots();
                double yRoots[] = new Polynomial(
                        c12.getY(),
                        c11.getY(),
                        c10.getY() - c20.getY() - s * c21.getY() - s * s * c22.getY()
                ).getRoots();

                if (xRoots.length > 0 && yRoots.length > 0) {
                    double TOLERANCE = 1e-4;

                    checkRoots:
                    for (int j = 0; j < xRoots.length; j++) {
                        double xRoot = xRoots[j];

                        if (0 <= xRoot && xRoot <= 1) {
                            for (int k = 0; k < yRoots.length; k++) {
                                if (Math.abs(xRoot - yRoots[k]) < TOLERANCE) {
                                    result.appendPoint(c22.multiply(s * s).add(c21.multiply(s).add(c20)), xRoot);
                                    break checkRoots;
                                }
                            }
                        }
                    }
                }
            }
        }

        if (result.size() > 0) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    ;


    /**
     * Computes the intersection between quadratic bezier curve 'a' and
     * cubic bezier curve 'b'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range [0,1].
     *
     * @param a1 control point 1 of 'a'
     * @param a2 control point 2 of 'a'
     * @param a3 control point 3 of 'a'
     * @param b1 control point 1 of 'b'
     * @param b2 control point 2 of 'b'
     * @param b3 control point 3 of 'b'
     * @param b4 control point 4 of 'b'
     * @return the computed result
     */
public static Intersection intersectBezier2Bezier3(Point2D a1, Point2D a2, Point2D a3, Point2D b1, Point2D b2, Point2D b3, Point2D b4) {
        Point2D a, b, c, d;
        Point2D c12, c11, c10;
        Point2D c23, c22, c21, c20;
        Intersection result = new Intersection(Status.NO_INTERSECTION);

        a = a2.multiply(-2);
        c12 = a1.add(a.add(a3));

        a = a1.multiply(-2);
        b = a2.multiply(2);
        c11 = a.add(b);

        c10 = new Point2D(a1.getX(), a1.getY());

        a = b1.multiply(-1);
        b = b2.multiply(3);
        c = b3.multiply(-3);
        d = a.add(b.add(c.add(b4)));
        c23 = new Point2D(d.getX(), d.getY());

        a = b1.multiply(3);
        b = b2.multiply(-6);
        c = b3.multiply(3);
        d = a.add(b.add(c));
        c22 = new Point2D(d.getX(), d.getY());

        a = b1.multiply(-3);
        b = b2.multiply(3);
        c = a.add(b);
        c21 = new Point2D(c.getX(), c.getY());

        c20 = new Point2D(b1.getX(), b1.getY());

        double c10x2 = c10.getX() * c10.getX();
        double c10y2 = c10.getY() * c10.getY();
        double c11x2 = c11.getX() * c11.getX();
        double c11y2 = c11.getY() * c11.getY();
        double c12x2 = c12.getX() * c12.getX();
        double c12y2 = c12.getY() * c12.getY();
        double c20x2 = c20.getX() * c20.getX();
        double c20y2 = c20.getY() * c20.getY();
        double c21x2 = c21.getX() * c21.getX();
        double c21y2 = c21.getY() * c21.getY();
        double c22x2 = c22.getX() * c22.getX();
        double c22y2 = c22.getY() * c22.getY();
        double c23x2 = c23.getX() * c23.getX();
        double c23y2 = c23.getY() * c23.getY();

        Polynomial poly = new Polynomial(
                -2 * c12.getX() * c12.getY() * c23.getX() * c23.getY() + c12x2 * c23y2 + c12y2 * c23x2,
                -2 * c12.getX() * c12.getY() * c22.getX() * c23.getY() - 2 * c12.getX() * c12.getY() * c22.getY() * c23.getX() + 2 * c12y2 * c22.getX() * c23.getX()
                + 2 * c12x2 * c22.getY() * c23.getY(),
                -2 * c12.getX() * c21.getX() * c12.getY() * c23.getY() - 2 * c12.getX() * c12.getY() * c21.getY() * c23.getX() - 2 * c12.getX() * c12.getY() * c22.getX() * c22.getY()
                + 2 * c21.getX() * c12y2 * c23.getX() + c12y2 * c22x2 + c12x2 * (2 * c21.getY() * c23.getY() + c22y2),
                2 * c10.getX() * c12.getX() * c12.getY() * c23.getY() + 2 * c10.getY() * c12.getX() * c12.getY() * c23.getX() + c11.getX() * c11.getY() * c12.getX() * c23.getY()
                + c11.getX() * c11.getY() * c12.getY() * c23.getX() - 2 * c20.getX() * c12.getX() * c12.getY() * c23.getY() - 2 * c12.getX() * c20.getY() * c12.getY() * c23.getX()
                - 2 * c12.getX() * c21.getX() * c12.getY() * c22.getY() - 2 * c12.getX() * c12.getY() * c21.getY() * c22.getX() - 2 * c10.getX() * c12y2 * c23.getX()
                - 2 * c10.getY() * c12x2 * c23.getY() + 2 * c20.getX() * c12y2 * c23.getX() + 2 * c21.getX() * c12y2 * c22.getX()
                - c11y2 * c12.getX() * c23.getX() - c11x2 * c12.getY() * c23.getY() + c12x2 * (2 * c20.getY() * c23.getY() + 2 * c21.getY() * c22.getY()),
                2 * c10.getX() * c12.getX() * c12.getY() * c22.getY() + 2 * c10.getY() * c12.getX() * c12.getY() * c22.getX() + c11.getX() * c11.getY() * c12.getX() * c22.getY()
                + c11.getX() * c11.getY() * c12.getY() * c22.getX() - 2 * c20.getX() * c12.getX() * c12.getY() * c22.getY() - 2 * c12.getX() * c20.getY() * c12.getY() * c22.getX()
                - 2 * c12.getX() * c21.getX() * c12.getY() * c21.getY() - 2 * c10.getX() * c12y2 * c22.getX() - 2 * c10.getY() * c12x2 * c22.getY()
                + 2 * c20.getX() * c12y2 * c22.getX() - c11y2 * c12.getX() * c22.getX() - c11x2 * c12.getY() * c22.getY() + c21x2 * c12y2
                + c12x2 * (2 * c20.getY() * c22.getY() + c21y2),
                2 * c10.getX() * c12.getX() * c12.getY() * c21.getY() + 2 * c10.getY() * c12.getX() * c21.getX() * c12.getY() + c11.getX() * c11.getY() * c12.getX() * c21.getY()
                + c11.getX() * c11.getY() * c21.getX() * c12.getY() - 2 * c20.getX() * c12.getX() * c12.getY() * c21.getY() - 2 * c12.getX() * c20.getY() * c21.getX() * c12.getY()
                - 2 * c10.getX() * c21.getX() * c12y2 - 2 * c10.getY() * c12x2 * c21.getY() + 2 * c20.getX() * c21.getX() * c12y2
                - c11y2 * c12.getX() * c21.getX() - c11x2 * c12.getY() * c21.getY() + 2 * c12x2 * c20.getY() * c21.getY(),
                -2 * c10.getX() * c10.getY() * c12.getX() * c12.getY() - c10.getX() * c11.getX() * c11.getY() * c12.getY() - c10.getY() * c11.getX() * c11.getY() * c12.getX()
                + 2 * c10.getX() * c12.getX() * c20.getY() * c12.getY() + 2 * c10.getY() * c20.getX() * c12.getX() * c12.getY() + c11.getX() * c20.getX() * c11.getY() * c12.getY()
                + c11.getX() * c11.getY() * c12.getX() * c20.getY() - 2 * c20.getX() * c12.getX() * c20.getY() * c12.getY() - 2 * c10.getX() * c20.getX() * c12y2
                + c10.getX() * c11y2 * c12.getX() + c10.getY() * c11x2 * c12.getY() - 2 * c10.getY() * c12x2 * c20.getY()
                - c20.getX() * c11y2 * c12.getX() - c11x2 * c20.getY() * c12.getY() + c10x2 * c12y2 + c10y2 * c12x2
                + c20x2 * c12y2 + c12x2 * c20y2
        );
        double[] roots = poly.getRootsInInterval(0, 1);

        for (int i = 0; i < roots.length; i++) {
            double s = roots[i];
            double[] xRoots = new Polynomial(
                    c12.getX(),
                    c11.getX(),
                    c10.getX() - c20.getX() - s * c21.getX() - s * s * c22.getX() - s * s * s * c23.getX()
            ).getRoots();
            double[] yRoots = new Polynomial(
                    c12.getY(),
                    c11.getY(),
                    c10.getY() - c20.getY() - s * c21.getY() - s * s * c22.getY() - s * s * s * c23.getY()
            ).getRoots();

            if (xRoots.length > 0 && yRoots.length > 0) {
                double TOLERANCE = 1e-4;

                checkRoots:
                for (int j = 0; j < xRoots.length; j++) {
                    double xRoot = xRoots[j];

                    if (0 <= xRoot && xRoot <= 1) {
                        for (int k = 0; k < yRoots.length; k++) {
                            if (Math.abs(xRoot - yRoots[k]) < TOLERANCE) {
                                result.appendPoint(
                                        c23.multiply(s * s * s).add(c22.multiply(s * s).add(c21.multiply(s).add(c20))),
                                        xRoot
                                );
                                break checkRoots;
                            }
                        }
                    }
                }
            }
        }

        if (result.size() > 0) {
            result.status = Status.INTERSECTION;
        }

        return result;

    }

    ;


/**
     * Computes the intersection between quadratic bezier curve 'p' and
     * the given circle.
 * 
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param c the center of the circle
     * @param r the radius of the circle
     * @return the computed result
 */
public static Intersection intersectBezier2Circle(Point2D p1, Point2D p2, Point2D p3, Point2D c, double r) {
        return Intersection.intersectBezier2Ellipse(p1, p2, p3, c, r, r);
    }

    ;


/**
     * Computes the intersection between quadratic bezier curve 'p' and
     * the given ellipse.
     * <p>
     * The intersection will contain the parameters 't' of curve 'p' in range [0,1].
 * 
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @return the computed result
 */
public static Intersection intersectBezier2Ellipse(Point2D p1, Point2D p2, Point2D p3, Point2D ec, double rx, double ry) {
        Point2D a, b;       // temporary variables
        Point2D c2, c1, c0; // coefficients of quadratic
        Intersection result = new Intersection(Status.NO_INTERSECTION);

        a = p2.multiply(-2);
        c2 = p1.add(a.add(p3));

        a = p1.multiply(-2);
        b = p2.multiply(2);
        c1 = a.add(b);

        c0 = new Point2D(p1.getX(), p1.getY());

        double rxrx = rx * rx;
        double ryry = ry * ry;
        double[] roots = new Polynomial(
                ryry * c2.getX() * c2.getX() + rxrx * c2.getY() * c2.getY(),
                2 * (ryry * c2.getX() * c1.getX() + rxrx * c2.getY() * c1.getY()),
                ryry * (2 * c2.getX() * c0.getX() + c1.getX() * c1.getX()) + rxrx * (2 * c2.getY() * c0.getY() + c1.getY() * c1.getY())
                - 2 * (ryry * ec.getX() * c2.getX() + rxrx * ec.getY() * c2.getY()),
                2 * (ryry * c1.getX() * (c0.getX() - ec.getX()) + rxrx * c1.getY() * (c0.getY() - ec.getY())),
                ryry * (c0.getX() * c0.getX() + ec.getX() * ec.getX()) + rxrx * (c0.getY() * c0.getY() + ec.getY() * ec.getY())
                - 2 * (ryry * ec.getX() * c0.getX() + rxrx * ec.getY() * c0.getY()) - rxrx * ryry
        ).getRoots();

        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                result.appendPoint(c2.multiply(t * t).add(c1.multiply(t).add(c0)), t);
            }
        }

        if (result.size() > 0) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the line
     * 'a'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param a1 point 1 of 'a'
     * @param a2 point 2 of 'a'
     * @return the computed intersection
     */
    public static Intersection intersectBezier2Line(Point2D p1, Point2D p2, Point2D p3, Point2D a1, Point2D a2) {
        Point2D a, b;             // temporary variables
        Point2D c2, c1, c0;       // coefficients of quadratic
        double cl;               // c coefficient for normal form of line
        Point2D n;                // normal for normal form of line
        Point2D min = minp(a1, a2); // used to determine if point is on line segment
        Point2D max = maxp(a1, a2); // used to determine if point is on line segment
        Intersection result = new Intersection(Status.NO_INTERSECTION);

        a = p2.multiply(-2);
        c2 = p1.add(a.add(p3));

        a = p1.multiply(-2);
        b = p2.multiply(2);
        c1 = a.add(b);

        c0 = new Point2D(p1.getX(), p1.getY());

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        n = new Point2D(a1.getY() - a2.getY(), a2.getX() - a1.getX());

        // Determine new c coefficient
        cl = a1.getX() * a2.getY() - a2.getX() * a1.getY();

        // Transform cubic coefficients to line's coordinate system and find roots
        // of cubic
        double[] roots = new Polynomial(
                n.dotProduct(c2),
                n.dotProduct(c1),
                n.dotProduct(c0) + cl
        ).getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                Point2D p4 = lerp(p1, p2, t);
                Point2D p5 = lerp(p2, p3, t);

                Point2D p6 = lerp(p4, p5, t);

                // See if point is on line segment
                // Had to make special cases for vertical and horizontal lines due
                // to slight errors in calculation of p6
                if (a1.getX() == a2.getX()) {
                    if (min.getY() <= p6.getY() && p6.getY() <= max.getY()) {
                        result.status = Status.INTERSECTION;
                        result.appendPoint(p6, t);
                    }
                } else if (a1.getY() == a2.getY()) {
                    if (min.getX() <= p6.getX() && p6.getX() <= max.getX()) {
                        result.status = Status.INTERSECTION;
                        result.appendPoint(p6, t);
                    }
                } else if (gte(p6, min) && lte(p6, max)) {
                    result.status = Status.INTERSECTION;
                    result.appendPoint(p6, t);
                }
            }
        }

        return result;
    }

    /**
     * Computes the intersection between quadratic bezier curve 'p' and the
     * given closed polygon.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param points the points of the polygon
     * @return the computed intersection
     */
    public static Intersection intersectBezier2Polygon(Point2D p1, Point2D p2, Point2D p3, List<Point2D> points) {
        Intersection result = new Intersection(Status.NO_INTERSECTION);
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D a1 = points.get(i);
            Point2D a2 = points.get((i + 1) % length);
            Intersection inter = Intersection.intersectBezier2Line(p1, p2, p3, a1, a2);

            result.appendPoints(inter.getPoints());
        }

        if (result.size() > 0) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    ;



/**
     * Computes the intersection between quadratic bezier curve 'p' and
     * the provided rectangle.
     * 
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param r1 corner point 1 of the rectangle
     * @param r2 corner point 2 of the rectangle
     * @return the computed intersection
     */
public static Intersection intersectBezier2Rectangle(Point2D p1, Point2D p2, Point2D p3, Point2D r1, Point2D r2) {
        Point2D min = minp(r1, r2);
        Point2D max = maxp(r1, r2);
        Point2D topRight = new Point2D(max.getX(), min.getY());
        Point2D bottomLeft = new Point2D(min.getX(), max.getY());

        Intersection inter1 = Intersection.intersectBezier2Line(p1, p2, p3, min, topRight);
        Intersection inter2 = Intersection.intersectBezier2Line(p1, p2, p3, topRight, max);
        Intersection inter3 = Intersection.intersectBezier2Line(p1, p2, p3, max, bottomLeft);
        Intersection inter4 = Intersection.intersectBezier2Line(p1, p2, p3, bottomLeft, min);

        Intersection result = new Intersection(Status.NO_INTERSECTION);

        result.appendPoints(inter1.points);
        result.appendPoints(inter2.points);
        result.appendPoints(inter3.points);
        result.appendPoints(inter4.points);

        if (result.size() > 0) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between cubic bezier curve 'a' and cubic bezier
     * curve 'b'.
     * <p>
     * The intersection will contain the parameters 't' of curve 'a' in range
     * [0,1].
     *
     * @param a1 control point 1 of 'a'
     * @param a2 control point 2 of 'a'
     * @param a3 control point 3 of 'a'
     * @param a4 control point 4 of 'a'
     * @param b1 control point 1 of 'b'
     * @param b2 control point 2 of 'b'
     * @param b3 control point 3 of 'b'
     * @param b4 control point 4 of 'b'
     * @return the computed result
     */
    public static Intersection intersectBezier3Bezier3(Point2D a1, Point2D a2, Point2D a3, Point2D a4, Point2D b1, Point2D b2, Point2D b3, Point2D b4) {
        Point2D a, b, c, d;         // temporary variables
        Point2D c13, c12, c11, c10; // coefficients of cubic
        Point2D c23, c22, c21, c20; // coefficients of cubic
        Intersection result = new Intersection(Status.NO_INTERSECTION);

        // Calculate the coefficients of cubic polynomial
        a = a1.multiply(-1);
        b = a2.multiply(3);
        c = a3.multiply(-3);
        d = a.add(b.add(c.add(a4)));
        c13 = new Point2D(d.getX(), d.getY());

        a = a1.multiply(3);
        b = a2.multiply(-6);
        c = a3.multiply(3);
        d = a.add(b.add(c));
        c12 = new Point2D(d.getX(), d.getY());

        a = a1.multiply(-3);
        b = a2.multiply(3);
        c = a.add(b);
        c11 = new Point2D(c.getX(), c.getY());

        c10 = new Point2D(a1.getX(), a1.getY());

        a = b1.multiply(-1);
        b = b2.multiply(3);
        c = b3.multiply(-3);
        d = a.add(b.add(c.add(b4)));
        c23 = new Point2D(d.getX(), d.getY());

        a = b1.multiply(3);
        b = b2.multiply(-6);
        c = b3.multiply(3);
        d = a.add(b.add(c));
        c22 = new Point2D(d.getX(), d.getY());

        a = b1.multiply(-3);
        b = b2.multiply(3);
        c = a.add(b);
        c21 = new Point2D(c.getX(), c.getY());

        c20 = new Point2D(b1.getX(), b1.getY());
        final double c10x = c10.getX();

        double c10x2 = c10x * c10x;
        double c10x3 = c10x * c10x * c10x;
        final double c10y = c10.getY();
        double c10y2 = c10y * c10y;
        double c10y3 = c10y * c10y * c10y;
        final double c11x = c11.getX();
        double c11x2 = c11x * c11x;
        double c11x3 = c11x * c11x * c11x;
        final double c11y = c11.getY();
        double c11y2 = c11y * c11y;
        double c11y3 = c11y * c11y * c11y;
        final double c12x = c12.getX();
        double c12x2 = c12x * c12x;
        double c12x3 = c12x * c12x * c12x;
        final double c12y = c12.getY();
        double c12y2 = c12y * c12y;
        double c12y3 = c12y * c12y * c12y;
        final double c13x = c13.getX();
        double c13x2 = c13x * c13x;
        double c13x3 = c13x * c13x * c13x;
        final double c13y = c13.getY();
        double c13y2 = c13y * c13y;
        double c13y3 = c13y * c13y * c13y;
        final double c20x = c20.getX();
        double c20x2 = c20x * c20x;
        double c20x3 = c20x * c20x * c20x;
        final double c20y = c20.getY();
        double c20y2 = c20y * c20y;
        double c20y3 = c20y * c20y * c20y;
        final double c21x = c21.getX();
        double c21x2 = c21x * c21x;
        double c21x3 = c21x * c21x * c21x;
        final double c21y = c21.getY();
        double c21y2 = c21y * c21y;
        final double c22x = c22.getX();
        double c22x2 = c22x * c22x;
        double c22x3 = c22x * c22x * c22x;
        final double c22y = c22.getY();
        double c22y2 = c22y * c22y;
        final double c23x = c23.getX();
        double c23x2 = c23x * c23x;
        double c23x3 = c23x * c23x * c23x;
        final double c23y = c23.getY();
        double c23y2 = c23y * c23y;
        double c23y3 = c23y * c23y * c23y;
        Polynomial poly = new Polynomial(
                -c13x3 * c23y3 + c13y3 * c23x3 - 3 * c13x * c13y2 * c23x2 * c23y
                + 3 * c13x2 * c13y * c23x * c23y2,
                -6 * c13x * c22x * c13y2 * c23x * c23y + 6 * c13x2 * c13y * c22y * c23x * c23y + 3 * c22x * c13y3 * c23x2
                - 3 * c13x3 * c22y * c23y2 - 3 * c13x * c13y2 * c22y * c23x2 + 3 * c13x2 * c22x * c13y * c23y2,
                -6 * c21x * c13x * c13y2 * c23x * c23y - 6 * c13x * c22x * c13y2 * c22y * c23x + 6 * c13x2 * c22x * c13y * c22y * c23y
                + 3 * c21x * c13y3 * c23x2 + 3 * c22x2 * c13y3 * c23x + 3 * c21x * c13x2 * c13y * c23y2 - 3 * c13x * c21y * c13y2 * c23x2
                - 3 * c13x * c22x2 * c13y2 * c23y + c13x2 * c13y * c23x * (6 * c21y * c23y + 3 * c22y2) + c13x3 * (-c21y * c23y2
                - 2 * c22y2 * c23y - c23y * (2 * c21y * c23y + c22y2)),
                c11x * c12y * c13x * c13y * c23x * c23y - c11y * c12x * c13x * c13y * c23x * c23y + 6 * c21x * c22x * c13y3 * c23x
                + 3 * c11x * c12x * c13x * c13y * c23y2 + 6 * c10x * c13x * c13y2 * c23x * c23y - 3 * c11x * c12x * c13y2 * c23x * c23y
                - 3 * c11y * c12y * c13x * c13y * c23x2 - 6 * c10y * c13x2 * c13y * c23x * c23y - 6 * c20x * c13x * c13y2 * c23x * c23y
                + 3 * c11y * c12y * c13x2 * c23x * c23y - 2 * c12x * c12y2 * c13x * c23x * c23y - 6 * c21x * c13x * c22x * c13y2 * c23y
                - 6 * c21x * c13x * c13y2 * c22y * c23x - 6 * c13x * c21y * c22x * c13y2 * c23x + 6 * c21x * c13x2 * c13y * c22y * c23y
                + 2 * c12x2 * c12y * c13y * c23x * c23y + c22x3 * c13y3 - 3 * c10x * c13y3 * c23x2 + 3 * c10y * c13x3 * c23y2
                + 3 * c20x * c13y3 * c23x2 + c12y3 * c13x * c23x2 - c12x3 * c13y * c23y2 - 3 * c10x * c13x2 * c13y * c23y2
                + 3 * c10y * c13x * c13y2 * c23x2 - 2 * c11x * c12y * c13x2 * c23y2 + c11x * c12y * c13y2 * c23x2 - c11y * c12x * c13x2 * c23y2
                + 2 * c11y * c12x * c13y2 * c23x2 + 3 * c20x * c13x2 * c13y * c23y2 - c12x * c12y2 * c13y * c23x2
                - 3 * c20y * c13x * c13y2 * c23x2 + c12x2 * c12y * c13x * c23y2 - 3 * c13x * c22x2 * c13y2 * c22y
                + c13x2 * c13y * c23x * (6 * c20y * c23y + 6 * c21y * c22y) + c13x2 * c22x * c13y * (6 * c21y * c23y + 3 * c22y2)
                + c13x3 * (-2 * c21y * c22y * c23y - c20y * c23y2 - c22y * (2 * c21y * c23y + c22y2) - c23y * (2 * c20y * c23y + 2 * c21y * c22y)),
                6 * c11x * c12x * c13x * c13y * c22y * c23y + c11x * c12y * c13x * c22x * c13y * c23y + c11x * c12y * c13x * c13y * c22y * c23x
                - c11y * c12x * c13x * c22x * c13y * c23y - c11y * c12x * c13x * c13y * c22y * c23x - 6 * c11y * c12y * c13x * c22x * c13y * c23x
                - 6 * c10x * c22x * c13y3 * c23x + 6 * c20x * c22x * c13y3 * c23x + 6 * c10y * c13x3 * c22y * c23y + 2 * c12y3 * c13x * c22x * c23x
                - 2 * c12x3 * c13y * c22y * c23y + 6 * c10x * c13x * c22x * c13y2 * c23y + 6 * c10x * c13x * c13y2 * c22y * c23x
                + 6 * c10y * c13x * c22x * c13y2 * c23x - 3 * c11x * c12x * c22x * c13y2 * c23y - 3 * c11x * c12x * c13y2 * c22y * c23x
                + 2 * c11x * c12y * c22x * c13y2 * c23x + 4 * c11y * c12x * c22x * c13y2 * c23x - 6 * c10x * c13x2 * c13y * c22y * c23y
                - 6 * c10y * c13x2 * c22x * c13y * c23y - 6 * c10y * c13x2 * c13y * c22y * c23x - 4 * c11x * c12y * c13x2 * c22y * c23y
                - 6 * c20x * c13x * c22x * c13y2 * c23y - 6 * c20x * c13x * c13y2 * c22y * c23x - 2 * c11y * c12x * c13x2 * c22y * c23y
                + 3 * c11y * c12y * c13x2 * c22x * c23y + 3 * c11y * c12y * c13x2 * c22y * c23x - 2 * c12x * c12y2 * c13x * c22x * c23y
                - 2 * c12x * c12y2 * c13x * c22y * c23x - 2 * c12x * c12y2 * c22x * c13y * c23x - 6 * c20y * c13x * c22x * c13y2 * c23x
                - 6 * c21x * c13x * c21y * c13y2 * c23x - 6 * c21x * c13x * c22x * c13y2 * c22y + 6 * c20x * c13x2 * c13y * c22y * c23y
                + 2 * c12x2 * c12y * c13x * c22y * c23y + 2 * c12x2 * c12y * c22x * c13y * c23y + 2 * c12x2 * c12y * c13y * c22y * c23x
                + 3 * c21x * c22x2 * c13y3 + 3 * c21x2 * c13y3 * c23x - 3 * c13x * c21y * c22x2 * c13y2 - 3 * c21x2 * c13x * c13y2 * c23y
                + c13x2 * c22x * c13y * (6 * c20y * c23y + 6 * c21y * c22y) + c13x2 * c13y * c23x * (6 * c20y * c22y + 3 * c21y2)
                + c21x * c13x2 * c13y * (6 * c21y * c23y + 3 * c22y2) + c13x3 * (-2 * c20y * c22y * c23y - c23y * (2 * c20y * c22y + c21y2)
                - c21y * (2 * c21y * c23y + c22y2) - c22y * (2 * c20y * c23y + 2 * c21y * c22y)),
                c11x * c21x * c12y * c13x * c13y * c23y + c11x * c12y * c13x * c21y * c13y * c23x + c11x * c12y * c13x * c22x * c13y * c22y
                - c11y * c12x * c21x * c13x * c13y * c23y - c11y * c12x * c13x * c21y * c13y * c23x - c11y * c12x * c13x * c22x * c13y * c22y
                - 6 * c11y * c21x * c12y * c13x * c13y * c23x - 6 * c10x * c21x * c13y3 * c23x + 6 * c20x * c21x * c13y3 * c23x
                + 2 * c21x * c12y3 * c13x * c23x + 6 * c10x * c21x * c13x * c13y2 * c23y + 6 * c10x * c13x * c21y * c13y2 * c23x
                + 6 * c10x * c13x * c22x * c13y2 * c22y + 6 * c10y * c21x * c13x * c13y2 * c23x - 3 * c11x * c12x * c21x * c13y2 * c23y
                - 3 * c11x * c12x * c21y * c13y2 * c23x - 3 * c11x * c12x * c22x * c13y2 * c22y + 2 * c11x * c21x * c12y * c13y2 * c23x
                + 4 * c11y * c12x * c21x * c13y2 * c23x - 6 * c10y * c21x * c13x2 * c13y * c23y - 6 * c10y * c13x2 * c21y * c13y * c23x
                - 6 * c10y * c13x2 * c22x * c13y * c22y - 6 * c20x * c21x * c13x * c13y2 * c23y - 6 * c20x * c13x * c21y * c13y2 * c23x
                - 6 * c20x * c13x * c22x * c13y2 * c22y + 3 * c11y * c21x * c12y * c13x2 * c23y - 3 * c11y * c12y * c13x * c22x2 * c13y
                + 3 * c11y * c12y * c13x2 * c21y * c23x + 3 * c11y * c12y * c13x2 * c22x * c22y - 2 * c12x * c21x * c12y2 * c13x * c23y
                - 2 * c12x * c21x * c12y2 * c13y * c23x - 2 * c12x * c12y2 * c13x * c21y * c23x - 2 * c12x * c12y2 * c13x * c22x * c22y
                - 6 * c20y * c21x * c13x * c13y2 * c23x - 6 * c21x * c13x * c21y * c22x * c13y2 + 6 * c20y * c13x2 * c21y * c13y * c23x
                + 2 * c12x2 * c21x * c12y * c13y * c23y + 2 * c12x2 * c12y * c21y * c13y * c23x + 2 * c12x2 * c12y * c22x * c13y * c22y
                - 3 * c10x * c22x2 * c13y3 + 3 * c20x * c22x2 * c13y3 + 3 * c21x2 * c22x * c13y3 + c12y3 * c13x * c22x2
                + 3 * c10y * c13x * c22x2 * c13y2 + c11x * c12y * c22x2 * c13y2 + 2 * c11y * c12x * c22x2 * c13y2
                - c12x * c12y2 * c22x2 * c13y - 3 * c20y * c13x * c22x2 * c13y2 - 3 * c21x2 * c13x * c13y2 * c22y
                + c12x2 * c12y * c13x * (2 * c21y * c23y + c22y2) + c11x * c12x * c13x * c13y * (6 * c21y * c23y + 3 * c22y2)
                + c21x * c13x2 * c13y * (6 * c20y * c23y + 6 * c21y * c22y) + c12x3 * c13y * (-2 * c21y * c23y - c22y2)
                + c10y * c13x3 * (6 * c21y * c23y + 3 * c22y2) + c11y * c12x * c13x2 * (-2 * c21y * c23y - c22y2)
                + c11x * c12y * c13x2 * (-4 * c21y * c23y - 2 * c22y2) + c10x * c13x2 * c13y * (-6 * c21y * c23y - 3 * c22y2)
                + c13x2 * c22x * c13y * (6 * c20y * c22y + 3 * c21y2) + c20x * c13x2 * c13y * (6 * c21y * c23y + 3 * c22y2)
                + c13x3 * (-2 * c20y * c21y * c23y - c22y * (2 * c20y * c22y + c21y2) - c20y * (2 * c21y * c23y + c22y2)
                - c21y * (2 * c20y * c23y + 2 * c21y * c22y)),
                -c10x * c11x * c12y * c13x * c13y * c23y + c10x * c11y * c12x * c13x * c13y * c23y + 6 * c10x * c11y * c12y * c13x * c13y * c23x
                - 6 * c10y * c11x * c12x * c13x * c13y * c23y - c10y * c11x * c12y * c13x * c13y * c23x + c10y * c11y * c12x * c13x * c13y * c23x
                + c11x * c11y * c12x * c12y * c13x * c23y - c11x * c11y * c12x * c12y * c13y * c23x + c11x * c20x * c12y * c13x * c13y * c23y
                + c11x * c20y * c12y * c13x * c13y * c23x + c11x * c21x * c12y * c13x * c13y * c22y + c11x * c12y * c13x * c21y * c22x * c13y
                - c20x * c11y * c12x * c13x * c13y * c23y - 6 * c20x * c11y * c12y * c13x * c13y * c23x - c11y * c12x * c20y * c13x * c13y * c23x
                - c11y * c12x * c21x * c13x * c13y * c22y - c11y * c12x * c13x * c21y * c22x * c13y - 6 * c11y * c21x * c12y * c13x * c22x * c13y
                - 6 * c10x * c20x * c13y3 * c23x - 6 * c10x * c21x * c22x * c13y3 - 2 * c10x * c12y3 * c13x * c23x + 6 * c20x * c21x * c22x * c13y3
                + 2 * c20x * c12y3 * c13x * c23x + 2 * c21x * c12y3 * c13x * c22x + 2 * c10y * c12x3 * c13y * c23y - 6 * c10x * c10y * c13x * c13y2 * c23x
                + 3 * c10x * c11x * c12x * c13y2 * c23y - 2 * c10x * c11x * c12y * c13y2 * c23x - 4 * c10x * c11y * c12x * c13y2 * c23x
                + 3 * c10y * c11x * c12x * c13y2 * c23x + 6 * c10x * c10y * c13x2 * c13y * c23y + 6 * c10x * c20x * c13x * c13y2 * c23y
                - 3 * c10x * c11y * c12y * c13x2 * c23y + 2 * c10x * c12x * c12y2 * c13x * c23y + 2 * c10x * c12x * c12y2 * c13y * c23x
                + 6 * c10x * c20y * c13x * c13y2 * c23x + 6 * c10x * c21x * c13x * c13y2 * c22y + 6 * c10x * c13x * c21y * c22x * c13y2
                + 4 * c10y * c11x * c12y * c13x2 * c23y + 6 * c10y * c20x * c13x * c13y2 * c23x + 2 * c10y * c11y * c12x * c13x2 * c23y
                - 3 * c10y * c11y * c12y * c13x2 * c23x + 2 * c10y * c12x * c12y2 * c13x * c23x + 6 * c10y * c21x * c13x * c22x * c13y2
                - 3 * c11x * c20x * c12x * c13y2 * c23y + 2 * c11x * c20x * c12y * c13y2 * c23x + c11x * c11y * c12y2 * c13x * c23x
                - 3 * c11x * c12x * c20y * c13y2 * c23x - 3 * c11x * c12x * c21x * c13y2 * c22y - 3 * c11x * c12x * c21y * c22x * c13y2
                + 2 * c11x * c21x * c12y * c22x * c13y2 + 4 * c20x * c11y * c12x * c13y2 * c23x + 4 * c11y * c12x * c21x * c22x * c13y2
                - 2 * c10x * c12x2 * c12y * c13y * c23y - 6 * c10y * c20x * c13x2 * c13y * c23y - 6 * c10y * c20y * c13x2 * c13y * c23x
                - 6 * c10y * c21x * c13x2 * c13y * c22y - 2 * c10y * c12x2 * c12y * c13x * c23y - 2 * c10y * c12x2 * c12y * c13y * c23x
                - 6 * c10y * c13x2 * c21y * c22x * c13y - c11x * c11y * c12x2 * c13y * c23y - 2 * c11x * c11y2 * c13x * c13y * c23x
                + 3 * c20x * c11y * c12y * c13x2 * c23y - 2 * c20x * c12x * c12y2 * c13x * c23y - 2 * c20x * c12x * c12y2 * c13y * c23x
                - 6 * c20x * c20y * c13x * c13y2 * c23x - 6 * c20x * c21x * c13x * c13y2 * c22y - 6 * c20x * c13x * c21y * c22x * c13y2
                + 3 * c11y * c20y * c12y * c13x2 * c23x + 3 * c11y * c21x * c12y * c13x2 * c22y + 3 * c11y * c12y * c13x2 * c21y * c22x
                - 2 * c12x * c20y * c12y2 * c13x * c23x - 2 * c12x * c21x * c12y2 * c13x * c22y - 2 * c12x * c21x * c12y2 * c22x * c13y
                - 2 * c12x * c12y2 * c13x * c21y * c22x - 6 * c20y * c21x * c13x * c22x * c13y2 - c11y2 * c12x * c12y * c13x * c23x
                + 2 * c20x * c12x2 * c12y * c13y * c23y + 6 * c20y * c13x2 * c21y * c22x * c13y + 2 * c11x2 * c11y * c13x * c13y * c23y
                + c11x2 * c12x * c12y * c13y * c23y + 2 * c12x2 * c20y * c12y * c13y * c23x + 2 * c12x2 * c21x * c12y * c13y * c22y
                + 2 * c12x2 * c12y * c21y * c22x * c13y + c21x3 * c13y3 + 3 * c10x2 * c13y3 * c23x - 3 * c10y2 * c13x3 * c23y
                + 3 * c20x2 * c13y3 * c23x + c11y3 * c13x2 * c23x - c11x3 * c13y2 * c23y - c11x * c11y2 * c13x2 * c23y
                + c11x2 * c11y * c13y2 * c23x - 3 * c10x2 * c13x * c13y2 * c23y + 3 * c10y2 * c13x2 * c13y * c23x - c11x2 * c12y2 * c13x * c23y
                + c11y2 * c12x2 * c13y * c23x - 3 * c21x2 * c13x * c21y * c13y2 - 3 * c20x2 * c13x * c13y2 * c23y + 3 * c20y2 * c13x2 * c13y * c23x
                + c11x * c12x * c13x * c13y * (6 * c20y * c23y + 6 * c21y * c22y) + c12x3 * c13y * (-2 * c20y * c23y - 2 * c21y * c22y)
                + c10y * c13x3 * (6 * c20y * c23y + 6 * c21y * c22y) + c11y * c12x * c13x2 * (-2 * c20y * c23y - 2 * c21y * c22y)
                + c12x2 * c12y * c13x * (2 * c20y * c23y + 2 * c21y * c22y) + c11x * c12y * c13x2 * (-4 * c20y * c23y - 4 * c21y * c22y)
                + c10x * c13x2 * c13y * (-6 * c20y * c23y - 6 * c21y * c22y) + c20x * c13x2 * c13y * (6 * c20y * c23y + 6 * c21y * c22y)
                + c21x * c13x2 * c13y * (6 * c20y * c22y + 3 * c21y2) + c13x3 * (-2 * c20y * c21y * c22y - c20y2 * c23y
                - c21y * (2 * c20y * c22y + c21y2) - c20y * (2 * c20y * c23y + 2 * c21y * c22y)),
                -c10x * c11x * c12y * c13x * c13y * c22y + c10x * c11y * c12x * c13x * c13y * c22y + 6 * c10x * c11y * c12y * c13x * c22x * c13y
                - 6 * c10y * c11x * c12x * c13x * c13y * c22y - c10y * c11x * c12y * c13x * c22x * c13y + c10y * c11y * c12x * c13x * c22x * c13y
                + c11x * c11y * c12x * c12y * c13x * c22y - c11x * c11y * c12x * c12y * c22x * c13y + c11x * c20x * c12y * c13x * c13y * c22y
                + c11x * c20y * c12y * c13x * c22x * c13y + c11x * c21x * c12y * c13x * c21y * c13y - c20x * c11y * c12x * c13x * c13y * c22y
                - 6 * c20x * c11y * c12y * c13x * c22x * c13y - c11y * c12x * c20y * c13x * c22x * c13y - c11y * c12x * c21x * c13x * c21y * c13y
                - 6 * c10x * c20x * c22x * c13y3 - 2 * c10x * c12y3 * c13x * c22x + 2 * c20x * c12y3 * c13x * c22x + 2 * c10y * c12x3 * c13y * c22y
                - 6 * c10x * c10y * c13x * c22x * c13y2 + 3 * c10x * c11x * c12x * c13y2 * c22y - 2 * c10x * c11x * c12y * c22x * c13y2
                - 4 * c10x * c11y * c12x * c22x * c13y2 + 3 * c10y * c11x * c12x * c22x * c13y2 + 6 * c10x * c10y * c13x2 * c13y * c22y
                + 6 * c10x * c20x * c13x * c13y2 * c22y - 3 * c10x * c11y * c12y * c13x2 * c22y + 2 * c10x * c12x * c12y2 * c13x * c22y
                + 2 * c10x * c12x * c12y2 * c22x * c13y + 6 * c10x * c20y * c13x * c22x * c13y2 + 6 * c10x * c21x * c13x * c21y * c13y2
                + 4 * c10y * c11x * c12y * c13x2 * c22y + 6 * c10y * c20x * c13x * c22x * c13y2 + 2 * c10y * c11y * c12x * c13x2 * c22y
                - 3 * c10y * c11y * c12y * c13x2 * c22x + 2 * c10y * c12x * c12y2 * c13x * c22x - 3 * c11x * c20x * c12x * c13y2 * c22y
                + 2 * c11x * c20x * c12y * c22x * c13y2 + c11x * c11y * c12y2 * c13x * c22x - 3 * c11x * c12x * c20y * c22x * c13y2
                - 3 * c11x * c12x * c21x * c21y * c13y2 + 4 * c20x * c11y * c12x * c22x * c13y2 - 2 * c10x * c12x2 * c12y * c13y * c22y
                - 6 * c10y * c20x * c13x2 * c13y * c22y - 6 * c10y * c20y * c13x2 * c22x * c13y - 6 * c10y * c21x * c13x2 * c21y * c13y
                - 2 * c10y * c12x2 * c12y * c13x * c22y - 2 * c10y * c12x2 * c12y * c22x * c13y - c11x * c11y * c12x2 * c13y * c22y
                - 2 * c11x * c11y2 * c13x * c22x * c13y + 3 * c20x * c11y * c12y * c13x2 * c22y - 2 * c20x * c12x * c12y2 * c13x * c22y
                - 2 * c20x * c12x * c12y2 * c22x * c13y - 6 * c20x * c20y * c13x * c22x * c13y2 - 6 * c20x * c21x * c13x * c21y * c13y2
                + 3 * c11y * c20y * c12y * c13x2 * c22x + 3 * c11y * c21x * c12y * c13x2 * c21y - 2 * c12x * c20y * c12y2 * c13x * c22x
                - 2 * c12x * c21x * c12y2 * c13x * c21y - c11y2 * c12x * c12y * c13x * c22x + 2 * c20x * c12x2 * c12y * c13y * c22y
                - 3 * c11y * c21x2 * c12y * c13x * c13y + 6 * c20y * c21x * c13x2 * c21y * c13y + 2 * c11x2 * c11y * c13x * c13y * c22y
                + c11x2 * c12x * c12y * c13y * c22y + 2 * c12x2 * c20y * c12y * c22x * c13y + 2 * c12x2 * c21x * c12y * c21y * c13y
                - 3 * c10x * c21x2 * c13y3 + 3 * c20x * c21x2 * c13y3 + 3 * c10x2 * c22x * c13y3 - 3 * c10y2 * c13x3 * c22y + 3 * c20x2 * c22x * c13y3
                + c21x2 * c12y3 * c13x + c11y3 * c13x2 * c22x - c11x3 * c13y2 * c22y + 3 * c10y * c21x2 * c13x * c13y2
                - c11x * c11y2 * c13x2 * c22y + c11x * c21x2 * c12y * c13y2 + 2 * c11y * c12x * c21x2 * c13y2 + c11x2 * c11y * c22x * c13y2
                - c12x * c21x2 * c12y2 * c13y - 3 * c20y * c21x2 * c13x * c13y2 - 3 * c10x2 * c13x * c13y2 * c22y + 3 * c10y2 * c13x2 * c22x * c13y
                - c11x2 * c12y2 * c13x * c22y + c11y2 * c12x2 * c22x * c13y - 3 * c20x2 * c13x * c13y2 * c22y + 3 * c20y2 * c13x2 * c22x * c13y
                + c12x2 * c12y * c13x * (2 * c20y * c22y + c21y2) + c11x * c12x * c13x * c13y * (6 * c20y * c22y + 3 * c21y2)
                + c12x3 * c13y * (-2 * c20y * c22y - c21y2) + c10y * c13x3 * (6 * c20y * c22y + 3 * c21y2)
                + c11y * c12x * c13x2 * (-2 * c20y * c22y - c21y2) + c11x * c12y * c13x2 * (-4 * c20y * c22y - 2 * c21y2)
                + c10x * c13x2 * c13y * (-6 * c20y * c22y - 3 * c21y2) + c20x * c13x2 * c13y * (6 * c20y * c22y + 3 * c21y2)
                + c13x3 * (-2 * c20y * c21y2 - c20y2 * c22y - c20y * (2 * c20y * c22y + c21y2)),
                -c10x * c11x * c12y * c13x * c21y * c13y + c10x * c11y * c12x * c13x * c21y * c13y + 6 * c10x * c11y * c21x * c12y * c13x * c13y
                - 6 * c10y * c11x * c12x * c13x * c21y * c13y - c10y * c11x * c21x * c12y * c13x * c13y + c10y * c11y * c12x * c21x * c13x * c13y
                - c11x * c11y * c12x * c21x * c12y * c13y + c11x * c11y * c12x * c12y * c13x * c21y + c11x * c20x * c12y * c13x * c21y * c13y
                + 6 * c11x * c12x * c20y * c13x * c21y * c13y + c11x * c20y * c21x * c12y * c13x * c13y - c20x * c11y * c12x * c13x * c21y * c13y
                - 6 * c20x * c11y * c21x * c12y * c13x * c13y - c11y * c12x * c20y * c21x * c13x * c13y - 6 * c10x * c20x * c21x * c13y3
                - 2 * c10x * c21x * c12y3 * c13x + 6 * c10y * c20y * c13x3 * c21y + 2 * c20x * c21x * c12y3 * c13x + 2 * c10y * c12x3 * c21y * c13y
                - 2 * c12x3 * c20y * c21y * c13y - 6 * c10x * c10y * c21x * c13x * c13y2 + 3 * c10x * c11x * c12x * c21y * c13y2
                - 2 * c10x * c11x * c21x * c12y * c13y2 - 4 * c10x * c11y * c12x * c21x * c13y2 + 3 * c10y * c11x * c12x * c21x * c13y2
                + 6 * c10x * c10y * c13x2 * c21y * c13y + 6 * c10x * c20x * c13x * c21y * c13y2 - 3 * c10x * c11y * c12y * c13x2 * c21y
                + 2 * c10x * c12x * c21x * c12y2 * c13y + 2 * c10x * c12x * c12y2 * c13x * c21y + 6 * c10x * c20y * c21x * c13x * c13y2
                + 4 * c10y * c11x * c12y * c13x2 * c21y + 6 * c10y * c20x * c21x * c13x * c13y2 + 2 * c10y * c11y * c12x * c13x2 * c21y
                - 3 * c10y * c11y * c21x * c12y * c13x2 + 2 * c10y * c12x * c21x * c12y2 * c13x - 3 * c11x * c20x * c12x * c21y * c13y2
                + 2 * c11x * c20x * c21x * c12y * c13y2 + c11x * c11y * c21x * c12y2 * c13x - 3 * c11x * c12x * c20y * c21x * c13y2
                + 4 * c20x * c11y * c12x * c21x * c13y2 - 6 * c10x * c20y * c13x2 * c21y * c13y - 2 * c10x * c12x2 * c12y * c21y * c13y
                - 6 * c10y * c20x * c13x2 * c21y * c13y - 6 * c10y * c20y * c21x * c13x2 * c13y - 2 * c10y * c12x2 * c21x * c12y * c13y
                - 2 * c10y * c12x2 * c12y * c13x * c21y - c11x * c11y * c12x2 * c21y * c13y - 4 * c11x * c20y * c12y * c13x2 * c21y
                - 2 * c11x * c11y2 * c21x * c13x * c13y + 3 * c20x * c11y * c12y * c13x2 * c21y - 2 * c20x * c12x * c21x * c12y2 * c13y
                - 2 * c20x * c12x * c12y2 * c13x * c21y - 6 * c20x * c20y * c21x * c13x * c13y2 - 2 * c11y * c12x * c20y * c13x2 * c21y
                + 3 * c11y * c20y * c21x * c12y * c13x2 - 2 * c12x * c20y * c21x * c12y2 * c13x - c11y2 * c12x * c21x * c12y * c13x
                + 6 * c20x * c20y * c13x2 * c21y * c13y + 2 * c20x * c12x2 * c12y * c21y * c13y + 2 * c11x2 * c11y * c13x * c21y * c13y
                + c11x2 * c12x * c12y * c21y * c13y + 2 * c12x2 * c20y * c21x * c12y * c13y + 2 * c12x2 * c20y * c12y * c13x * c21y
                + 3 * c10x2 * c21x * c13y3 - 3 * c10y2 * c13x3 * c21y + 3 * c20x2 * c21x * c13y3 + c11y3 * c21x * c13x2 - c11x3 * c21y * c13y2
                - 3 * c20y2 * c13x3 * c21y - c11x * c11y2 * c13x2 * c21y + c11x2 * c11y * c21x * c13y2 - 3 * c10x2 * c13x * c21y * c13y2
                + 3 * c10y2 * c21x * c13x2 * c13y - c11x2 * c12y2 * c13x * c21y + c11y2 * c12x2 * c21x * c13y - 3 * c20x2 * c13x * c21y * c13y2
                + 3 * c20y2 * c21x * c13x2 * c13y,
                c10x * c10y * c11x * c12y * c13x * c13y - c10x * c10y * c11y * c12x * c13x * c13y + c10x * c11x * c11y * c12x * c12y * c13y
                - c10y * c11x * c11y * c12x * c12y * c13x - c10x * c11x * c20y * c12y * c13x * c13y + 6 * c10x * c20x * c11y * c12y * c13x * c13y
                + c10x * c11y * c12x * c20y * c13x * c13y - c10y * c11x * c20x * c12y * c13x * c13y - 6 * c10y * c11x * c12x * c20y * c13x * c13y
                + c10y * c20x * c11y * c12x * c13x * c13y - c11x * c20x * c11y * c12x * c12y * c13y + c11x * c11y * c12x * c20y * c12y * c13x
                + c11x * c20x * c20y * c12y * c13x * c13y - c20x * c11y * c12x * c20y * c13x * c13y - 2 * c10x * c20x * c12y3 * c13x
                + 2 * c10y * c12x3 * c20y * c13y - 3 * c10x * c10y * c11x * c12x * c13y2 - 6 * c10x * c10y * c20x * c13x * c13y2
                + 3 * c10x * c10y * c11y * c12y * c13x2 - 2 * c10x * c10y * c12x * c12y2 * c13x - 2 * c10x * c11x * c20x * c12y * c13y2
                - c10x * c11x * c11y * c12y2 * c13x + 3 * c10x * c11x * c12x * c20y * c13y2 - 4 * c10x * c20x * c11y * c12x * c13y2
                + 3 * c10y * c11x * c20x * c12x * c13y2 + 6 * c10x * c10y * c20y * c13x2 * c13y + 2 * c10x * c10y * c12x2 * c12y * c13y
                + 2 * c10x * c11x * c11y2 * c13x * c13y + 2 * c10x * c20x * c12x * c12y2 * c13y + 6 * c10x * c20x * c20y * c13x * c13y2
                - 3 * c10x * c11y * c20y * c12y * c13x2 + 2 * c10x * c12x * c20y * c12y2 * c13x + c10x * c11y2 * c12x * c12y * c13x
                + c10y * c11x * c11y * c12x2 * c13y + 4 * c10y * c11x * c20y * c12y * c13x2 - 3 * c10y * c20x * c11y * c12y * c13x2
                + 2 * c10y * c20x * c12x * c12y2 * c13x + 2 * c10y * c11y * c12x * c20y * c13x2 + c11x * c20x * c11y * c12y2 * c13x
                - 3 * c11x * c20x * c12x * c20y * c13y2 - 2 * c10x * c12x2 * c20y * c12y * c13y - 6 * c10y * c20x * c20y * c13x2 * c13y
                - 2 * c10y * c20x * c12x2 * c12y * c13y - 2 * c10y * c11x2 * c11y * c13x * c13y - c10y * c11x2 * c12x * c12y * c13y
                - 2 * c10y * c12x2 * c20y * c12y * c13x - 2 * c11x * c20x * c11y2 * c13x * c13y - c11x * c11y * c12x2 * c20y * c13y
                + 3 * c20x * c11y * c20y * c12y * c13x2 - 2 * c20x * c12x * c20y * c12y2 * c13x - c20x * c11y2 * c12x * c12y * c13x
                + 3 * c10y2 * c11x * c12x * c13x * c13y + 3 * c11x * c12x * c20y2 * c13x * c13y + 2 * c20x * c12x2 * c20y * c12y * c13y
                - 3 * c10x2 * c11y * c12y * c13x * c13y + 2 * c11x2 * c11y * c20y * c13x * c13y + c11x2 * c12x * c20y * c12y * c13y
                - 3 * c20x2 * c11y * c12y * c13x * c13y - c10x3 * c13y3 + c10y3 * c13x3 + c20x3 * c13y3 - c20y3 * c13x3
                - 3 * c10x * c20x2 * c13y3 - c10x * c11y3 * c13x2 + 3 * c10x2 * c20x * c13y3 + c10y * c11x3 * c13y2
                + 3 * c10y * c20y2 * c13x3 + c20x * c11y3 * c13x2 + c10x2 * c12y3 * c13x - 3 * c10y2 * c20y * c13x3 - c10y2 * c12x3 * c13y
                + c20x2 * c12y3 * c13x - c11x3 * c20y * c13y2 - c12x3 * c20y2 * c13y - c10x * c11x2 * c11y * c13y2
                + c10y * c11x * c11y2 * c13x2 - 3 * c10x * c10y2 * c13x2 * c13y - c10x * c11y2 * c12x2 * c13y + c10y * c11x2 * c12y2 * c13x
                - c11x * c11y2 * c20y * c13x2 + 3 * c10x2 * c10y * c13x * c13y2 + c10x2 * c11x * c12y * c13y2
                + 2 * c10x2 * c11y * c12x * c13y2 - 2 * c10y2 * c11x * c12y * c13x2 - c10y2 * c11y * c12x * c13x2 + c11x2 * c20x * c11y * c13y2
                - 3 * c10x * c20y2 * c13x2 * c13y + 3 * c10y * c20x2 * c13x * c13y2 + c11x * c20x2 * c12y * c13y2 - 2 * c11x * c20y2 * c12y * c13x2
                + c20x * c11y2 * c12x2 * c13y - c11y * c12x * c20y2 * c13x2 - c10x2 * c12x * c12y2 * c13y - 3 * c10x2 * c20y * c13x * c13y2
                + 3 * c10y2 * c20x * c13x2 * c13y + c10y2 * c12x2 * c12y * c13x - c11x2 * c20y * c12y2 * c13x + 2 * c20x2 * c11y * c12x * c13y2
                + 3 * c20x * c20y2 * c13x2 * c13y - c20x2 * c12x * c12y2 * c13y - 3 * c20x2 * c20y * c13x * c13y2 + c12x2 * c20y2 * c12y * c13x
        );
        double[] roots = poly.getRootsInInterval(0, 1);

        for (int i = 0; i < roots.length; i++) {
            double s = roots[i];
            double[] xRoots = new Polynomial(
                    c13x, c12x, c11x,
                    c10x - c20x - s * c21x - s * s * c22x - s * s * s * c23x
            ).getRoots();
            double[] yRoots = new Polynomial(
                    c13y, c12y, c11y,
                    c10y - c20y - s * c21y - s * s * c22y - s * s * s * c23y
            ).getRoots();

            if (xRoots.length > 0 && yRoots.length > 0) {
                double TOLERANCE = 1e-4;

                checkRoots:
                for (int j = 0; j < xRoots.length; j++) {
                    double xRoot = xRoots[j];

                    if (0 <= xRoot && xRoot <= 1) {
                        for (int k = 0; k < yRoots.length; k++) {
                            if (Math.abs(xRoot - yRoots[k]) < TOLERANCE) {
                                result.appendPoint(
                                        c23.multiply(s * s * s).add(c22.multiply(s * s).add(c21.multiply(s).add(c20))),
                                        xRoot
                                );
                                break checkRoots;
                            }
                        }
                    }
                }
            }
        }

        if (result.size() > 0) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    ;


/**
     * Computes the intersection between cubic bezier curve 'p' and
     * the given circle.
 * 
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param p4 control point 4 of 'p'
     * @param c the center of the circle
     * @param r the radius of the circle
     * @return the computed result
 */
public static Intersection intersectBezier3Circle(Point2D p1, Point2D p2, Point2D p3, Point2D p4, Point2D c, double r) {
        return Intersection.intersectBezier3Ellipse(p1, p2, p3, p4, c, r, r);
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the given
     * ellipse.
     *
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param p4 control point 4 of 'p'
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @return the computed result
     */
    public static Intersection intersectBezier3Ellipse(Point2D p1, Point2D p2, Point2D p3, Point2D p4, Point2D ec, double rx, double ry) {
        Point2D a, b, c, d;       // temporary variables
        Point2D c3, c2, c1, c0;   // coefficients of cubic
        Intersection result = new Intersection(Status.NO_INTERSECTION);

        // Calculate the coefficients of cubic polynomial
        a = p1.multiply(-1);
        b = p2.multiply(3);
        c = p3.multiply(-3);
        d = a.add(b.add(c.add(p4)));
        c3 = new Point2D(d.getX(), d.getY());

        a = p1.multiply(3);
        b = p2.multiply(-6);
        c = p3.multiply(3);
        d = a.add(b.add(c));
        c2 = new Point2D(d.getX(), d.getY());

        a = p1.multiply(-3);
        b = p2.multiply(3);
        c = a.add(b);
        c1 = new Point2D(c.getX(), c.getY());

        c0 = new Point2D(p1.getX(), p1.getY());

        double rxrx = rx * rx;
        double ryry = ry * ry;
        final double c3x = c3.getX();
        final double c3y = c3.getY();
        final double c2x = c2.getX();
        final double c1x = c1.getX();
        final double c2y = c2.getY();
        final double c1y = c1.getY();
        final double ecx = ec.getX();
        final double c0x = c0.getX();
        final double c0y = c0.getY();
        final double ecy = ec.getY();
        Polynomial poly = new Polynomial(
                c3x * c3x * ryry + c3y * c3y * rxrx,
                2 * (c3x * c2x * ryry + c3y * c2y * rxrx),
                2 * (c3x * c1x * ryry + c3y * c1y * rxrx) + c2x * c2x * ryry + c2y * c2y * rxrx,
                2 * c3x * ryry * (c0x - ecx) + 2 * c3y * rxrx * (c0y - ecy)
                + 2 * (c2x * c1x * ryry + c2y * c1y * rxrx),
                2 * c2x * ryry * (c0x - ecx) + 2 * c2y * rxrx * (c0y - ecy)
                + c1x * c1x * ryry + c1y * c1y * rxrx,
                2 * c1x * ryry * (c0x - ecx) + 2 * c1y * rxrx * (c0y - ecy),
                c0x * c0x * ryry - 2 * c0y * ecy * rxrx - 2 * c0x * ecx * ryry
                + c0y * c0y * rxrx + ecx * ecx * ryry + ecy * ecy * rxrx - rxrx * ryry
        );
        double[] roots = poly.getRootsInInterval(0, 1);

        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            result.appendPoint(
                    c3.multiply(t * t * t).add(c2.multiply(t * t).add(c1.multiply(t).add(c0))),
                    t
            );
        }

        if (result.size() > 0) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    ;


    /**
     * Computes the intersection between cubic bezier curve 'p' and
     * the line 'a'.
     * 
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param p4 control point 4 of 'p'
     * @param a1 point 1 of 'a'
     * @param a2 point 2 of 'a'
     * @return the computed intersection
     */
public static Intersection intersectBezier3Line(Point2D p1, Point2D p2, Point2D p3, Point2D p4, Point2D a1, Point2D a2) {
        Point2D a, b, c, d;       // temporary variables
        Point2D c3, c2, c1, c0;   // coefficients of cubic
        double cl;               // c coefficient for normal form of line
        Point2D n;                // normal for normal form of line
        final Point2D min = minp(a1, a2); // used to determine if point is on line segment
        final Point2D max = maxp(a1, a2); // used to determine if point is on line segment
        Intersection result = new Intersection(Status.NO_INTERSECTION);

        // Start with Bezier using Bernstein polynomials for weighting functions:
        //     (1-t^3)P1 + 3t(1-t)^2P2 + 3t^2(1-t)P3 + t^3P4
        //
        // Expand and collect terms to form linear combinations of original Bezier
        // controls.  This ends up with a vector cubic in t:
        //     (-P1+3P2-3P3+P4)t^3 + (3P1-6P2+3P3)t^2 + (-3P1+3P2)t + P1
        //             /\                  /\                /\       /\
        //             ||                  ||                ||       ||
        //             c3                  c2                c1       c0
        // Calculate the coefficients
        a = p1.multiply(-1);
        b = p2.multiply(3);
        c = p3.multiply(-3);
        d = a.add(b.add(c.add(p4)));
        c3 = new Point2D(d.getX(), d.getY());

        a = p1.multiply(3);
        b = p2.multiply(-6);
        c = p3.multiply(3);
        d = a.add(b.add(c));
        c2 = new Point2D(d.getX(), d.getY());

        a = p1.multiply(-3);
        b = p2.multiply(3);
        c = a.add(b);
        c1 = new Point2D(c.getX(), c.getY());

        c0 = new Point2D(p1.getX(), p1.getY());

        // Convert line to normal form: ax + by + c = 0
        // Find normal to line: negative inverse of original line's slope
        n = new Point2D(a1.getY() - a2.getY(), a2.getX() - a1.getX());

        // Determine new c coefficient
        cl = a1.getX() * a2.getY() - a2.getX() * a1.getY();

        // ?Rotate each cubic coefficient using line for new coordinate system?
        // Find roots of rotated cubic
        double[] roots = new Polynomial(
                n.dotProduct(c3),
                n.dotProduct(c2),
                n.dotProduct(c1),
                n.dotProduct(c0) + cl
        ).getRoots();

        // Any roots in closed interval [0,1] are intersections on Bezier, but
        // might not be on the line segment.
        // Find intersections and calculate point coordinates
        for (int i = 0; i < roots.length; i++) {
            double t = roots[i];

            if (0 <= t && t <= 1) {
                // We're within the Bezier curve
                // Find point on Bezier
                Point2D p5 = lerp(p1, p2, t);
                Point2D p6 = lerp(p2, p3, t);
                Point2D p7 = lerp(p3, p4, t);

                Point2D p8 = lerp(p5, p6, t);
                Point2D p9 = lerp(p6, p7, t);

                Point2D p10 = lerp(p8, p9, t);

                // See if point is on line segment
                // Had to make special cases for vertical and horizontal lines due
                // to slight errors in calculation of p10
                if (a1.getX() == a2.getX()) {
                    if (min.getY() <= p10.getY() && p10.getY() <= max.getY()) {
                        result.status = Status.INTERSECTION;
                        result.appendPoint(p10, t);
                    }
                } else if (a1.getY() == a2.getY()) {
                    if (min.getX() <= p10.getX() && p10.getX() <= max.getX()) {
                        result.status = Status.INTERSECTION;
                        result.appendPoint(p10, t);
                    }
                } else if (gte(p10, min) && lte(p10, max)) {
                    result.status = Status.INTERSECTION;
                    result.appendPoint(p10, t);
                }
            }
        }

        return result;
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the given
     * closed polygon.
     *
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param p4 control point 4 of 'p'
     * @param points the points of the polygon
     * @return the computed intersection
     */
    public static Intersection intersectBezier3Polygon(Point2D p1, Point2D p2, Point2D p3, Point2D p4, List<Point2D> points) {
        Intersection result = new Intersection(Status.NO_INTERSECTION);
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D a1 = points.get(i);
            Point2D a2 = points.get((i + 1) % length);
            Intersection inter = Intersection.intersectBezier3Line(p1, p2, p3, p4, a1, a2);

            result.appendPoints(inter.points);
        }

        if (result.size() > 0) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between cubic bezier curve 'p' and the provided
     * rectangle.
     *
     * @param p1 control point 1 of 'p'
     * @param p2 control point 2 of 'p'
     * @param p3 control point 3 of 'p'
     * @param p4 control point 4 of 'p'
     * @param r1 corner point 1 of the rectangle
     * @param r2 corner point 2 of the rectangle
     * @return the computed intersection
     */
    public static Intersection intersectBezier3Rectangle(Point2D p1, Point2D p2, Point2D p3, Point2D p4, Point2D r1, Point2D r2) {
        Point2D min = minp(r1, r2);
        Point2D max = maxp(r1, r2);
        Point2D topRight = new Point2D(max.getX(), min.getY());
        Point2D bottomLeft = new Point2D(min.getX(), max.getY());

        Intersection inter1 = Intersection.intersectBezier3Line(p1, p2, p3, p4, min, topRight);
        Intersection inter2 = Intersection.intersectBezier3Line(p1, p2, p3, p4, topRight, max);
        Intersection inter3 = Intersection.intersectBezier3Line(p1, p2, p3, p4, max, bottomLeft);
        Intersection inter4 = Intersection.intersectBezier3Line(p1, p2, p3, p4, bottomLeft, min);

        Intersection result = new Intersection(Status.NO_INTERSECTION);

        result.appendPoints(inter1.points);
        result.appendPoints(inter2.points);
        result.appendPoints(inter3.points);
        result.appendPoints(inter4.points);

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between circle 1 and circle 2.
     *
     * @param c1 the center of circle 1
     * @param r1 the radius of circle 1
     * @param c2 the center of circle 2
     * @param r2 the radius of circle 2
     * @return computed intersection
     */
    public static Intersection intersectCircleCircle(Point2D c1, double r1, Point2D c2, double r2) {
        Intersection result;

        // Determine minimum and maximum radii where circles can intersect
        double r_max = r1 + r2;
        double r_min = Math.abs(r1 - r2);

        // Determine actual distance between circle circles
        double c_dist = c1.distance(c2);

        if (c_dist > r_max) {
            result = new Intersection(Status.NO_INTERSECTION_OUTSIDE);
        } else if (c_dist < r_min) {
            result = new Intersection(Status.NO_INTERSECTION_INSIDE);
        } else {
            result = new Intersection(Status.INTERSECTION);

            double a = (r1 * r1 - r2 * r2 + c_dist * c_dist) / (2 * c_dist);
            double h = Math.sqrt(r1 * r1 - a * a);
            Point2D p = lerp(c1, c2, a / c_dist);
            double b = h / c_dist;

            result.appendPoint(
                    new Point2D(
                            p.getX() - b * (c2.getY() - c1.getY()),
                            p.getY() + b * (c2.getX() - c1.getX())
                    ),
                    Double.NaN
            );
            result.appendPoint(
                    new Point2D(
                            p.getX() + b * (c2.getY() - c1.getY()),
                            p.getY() - b * (c2.getX() - c1.getX())
                    ), Double.NaN
            );
        }
        result.throwAwayTs();
        return result;
    }

    /**
     * Computes the intersection between a circle and an ellipse.
     *
     * @param cc the center of the circle
     * @param r the radius of the circle
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @return computed intersection
     */
    public static Intersection intersectCircleEllipse(Point2D cc, double r, Point2D ec, double rx, double ry) {
        return Intersection.intersectEllipseEllipse(cc, r, r, ec, rx, ry);
    }

    /**
     * Computes the intersection between a circle and a line.
     *
     * @param c the center of the circle
     * @param r the radius of the circle
     * @param a1 point 1 of the line
     * @param a2 point 2 of the line
     * @return computed intersection
     */
    public static Intersection intersectCircleLine(Point2D c, double r, Point2D a1, Point2D a2) {
        Intersection inter = intersectLineCircle(a1, a2, c, r);
        inter.throwAwayTs();
        return inter;
    }

    /**
     * Computes the intersection between a circle and a polygon.
     *
     * @param c the center of the circle
     * @param r the radius of the circle
     * @param points the points of the polygon
     * @return computed intersection
     */
    public static Intersection intersectCirclePolygon(Point2D c, double r, List<Point2D> points) {
        Intersection result = new Intersection(Status.NO_INTERSECTION);
        int length = points.size();
        Intersection inter = null;

        for (int i = 0; i < length; i++) {
            Point2D a1 = points.get(i);
            Point2D a2 = points.get((i + 1) % length);

            inter = Intersection.intersectCircleLine(c, r, a1, a2);
            result.appendPoints(inter.points);
        }

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        } else {
            result.status = inter == null ? Status.NO_INTERSECTION : inter.status;
        }

        return result;
    }

    /**
     * Computes the intersection between a circle and a rectangle.
     *
     * @param c the center of the circle
     * @param r the radius of the circle
     * @param r1 corner point 1 of the rectangle
     * @param r2 corner point 2 of the rectangle
     * @return computed intersection
     */
    public static Intersection intersectCircleRectangle(Point2D c, double r, Point2D r1, Point2D r2) {
        Point2D min = minp(r1, r2);
        Point2D max = maxp(r1, r2);
        Point2D topRight = new Point2D(max.getX(), min.getY());
        Point2D bottomLeft = new Point2D(min.getX(), max.getY());

        Intersection inter1 = Intersection.intersectCircleLine(c, r, min, topRight);
        Intersection inter2 = Intersection.intersectCircleLine(c, r, topRight, max);
        Intersection inter3 = Intersection.intersectCircleLine(c, r, max, bottomLeft);
        Intersection inter4 = Intersection.intersectCircleLine(c, r, bottomLeft, min);

        Intersection result = new Intersection(Status.NO_INTERSECTION);

        result.appendPoints(inter1.points);
        result.appendPoints(inter2.points);
        result.appendPoints(inter3.points);
        result.appendPoints(inter4.points);

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        } else {
            result.status = inter1.status;
        }

        return result;
    }

    ;


    /**
     * Computes the intersection between two ellipses.
     *
     * @param c1 the center of ellipse 1
     * @param rx1 the x-radius of ellipse 1
     * @param ry1 the y-radius of ellipse 1
     * @param c2 the center of ellipse 2
     * @param rx2 the x-radius of ellipse 2
     * @param ry2 the y-radius of ellipse 2
     * @return computed intersection
     */
public static Intersection intersectEllipseEllipse(Point2D c1, double rx1, double ry1, Point2D c2, double rx2, double ry2) {
        double[] a = {
            ry1 * ry1, 0, rx1 * rx1, -2 * ry1 * ry1 * c1.getX(), -2 * rx1 * rx1 * c1.getY(),
            ry1 * ry1 * c1.getX() * c1.getX() + rx1 * rx1 * c1.getY() * c1.getY() - rx1 * rx1 * ry1 * ry1
        };
        double[] b = {
            ry2 * ry2, 0, rx2 * rx2, -2 * ry2 * ry2 * c2.getX(), -2 * rx2 * rx2 * c2.getY(),
            ry2 * ry2 * c2.getX() * c2.getX() + rx2 * rx2 * c2.getY() * c2.getY() - rx2 * rx2 * ry2 * ry2
        };

        Polynomial yPoly = Intersection.bezout(a, b);
        double[] yRoots = yPoly.getRoots();
        double epsilon = 1e-3;
        double norm0 = (a[0] * a[0] + 2 * a[1] * a[1] + a[2] * a[2]) * epsilon;
        double norm1 = (b[0] * b[0] + 2 * b[1] * b[1] + b[2] * b[2]) * epsilon;
        Intersection result = new Intersection(Status.NO_INTERSECTION);

        for (int y = 0; y < yRoots.length; y++) {
            Polynomial xPoly = new Polynomial(
                    a[0],
                    a[3] + yRoots[y] * a[1],
                    a[5] + yRoots[y] * (a[4] + yRoots[y] * a[2])
            );
            double[] xRoots = xPoly.getRoots();

            for (int x = 0; x < xRoots.length; x++) {
                double test
                        = (a[0] * xRoots[x] + a[1] * yRoots[y] + a[3]) * xRoots[x]
                        + (a[2] * yRoots[y] + a[4]) * yRoots[y] + a[5];
                if (Math.abs(test) < norm0) {
                    test
                            = (b[0] * xRoots[x] + b[1] * yRoots[y] + b[3]) * xRoots[x]
                            + (b[2] * yRoots[y] + b[4]) * yRoots[y] + b[5];
                    if (Math.abs(test) < norm1) {
                        result.appendPoint(new Point2D(xRoots[x], yRoots[y]), Double.NaN);
                    }
                }
            }
        }

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }
        result.throwAwayTs();
        return result;
    }

    /**
     * Computes the intersection between an ellipse and a line.
     *
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param a1 point 1 of the line
     * @param a2 point 2 of the line
     * @return computed intersection
     */
    public static Intersection intersectEllipseLine(Point2D ec, double rx, double ry, Point2D a1, Point2D a2) {
        Intersection result = intersectLineEllipse(a1, a2, ec, rx, ry);
        result.throwAwayTs();
        return result;
    }

    /**
     * Computes the intersection between a circle and a polygon.
     *
     * @param c the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param points the points of the polygon
     * @return computed intersection
     */
    public static Intersection intersectEllipsePolygon(Point2D c, double rx, double ry, List<Point2D> points) {
        Intersection result = new Intersection(Status.NO_INTERSECTION);
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D b1 = points.get(i);
            Point2D b2 = points.get((i + 1) % length);
            Intersection inter = Intersection.intersectEllipseLine(c, rx, ry, b1, b2);

            result.appendPoints(inter.points);
        }

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    ;

    /**
     * Computes the intersection between an ellipse and a rectangle.
     *
     * @param c the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param r1 corner point 1 of the rectangle
     * @param r2 corner point 2 of the rectangle
     * @return computed intersection
     */
public static Intersection intersectEllipseRectangle(Point2D c, double rx, double ry, Point2D r1, Point2D r2) {
        Point2D min = minp(r1, r2);
        Point2D max = maxp(r1, r2);
        Point2D topRight = new Point2D(max.getX(), min.getY());
        Point2D bottomLeft = new Point2D(min.getX(), max.getY());

        Intersection inter1 = Intersection.intersectEllipseLine(c, rx, ry, min, topRight);
        Intersection inter2 = Intersection.intersectEllipseLine(c, rx, ry, topRight, max);
        Intersection inter3 = Intersection.intersectEllipseLine(c, rx, ry, max, bottomLeft);
        Intersection inter4 = Intersection.intersectEllipseLine(c, rx, ry, bottomLeft, min);

        Intersection result = new Intersection(Status.NO_INTERSECTION);

        result.appendPoints(inter1.points);
        result.appendPoints(inter2.points);
        result.appendPoints(inter3.points);
        result.appendPoints(inter4.points);

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between a line and a circle.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     *
     * @param c the center of the circle
     * @param r the radius of the circle
     * @param a1 point 1 of the line
     * @param a2 point 2 of the line
     * @return computed intersection
     */
    public static Intersection intersectLineCircle(Point2D a1, Point2D a2, Point2D c, double r) {
        return intersectLineCircle(a1.getX(), a1.getY(), a2.getX(), a2.getY(), c.getX(), c.getY(), r);
    }

    /**
     * Computes the intersection between a line and a circle.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     *
     * @param cx the center of the circle
     * @param cy the center of the circle
     * @param r the radius of the circle
     * @param x1 point 1 of the line
     * @param y1 point 1 of the line
     * @param x2 point 2 of the line
     * @param y2 point 2 of the line
     * @return computed intersection
     */
    public static Intersection intersectLineCircle(double x1, double y1, double x2, double y2, double cx, double cy, double r) {
        Intersection result;
        double a = (x2 - x1) * (x2 - x1) + (y2 - y1) * (y2 - y1);
        double b = 2 * ((x2 - x1) * (x1 - cx) + (y2 - y1) * (y1 - cy));
        double cc = cx * cx + cy * cy + x1 * x1 + y1 * y1 - 2 * (cx * x1 + cy * y1) - r * r;
        double deter = b * b - 4 * a * cc;

        if (deter < 0) {
            result = new Intersection(Status.NO_INTERSECTION_OUTSIDE);
        } else if (deter == 0) {
            result = new Intersection(Status.NO_INTERSECTION_TANGENT);
            // NOTE: should calculate this point
        } else {
            double e = Math.sqrt(deter);
            double u1 = (-b + e) / (2 * a);
            double u2 = (-b - e) / (2 * a);

            if ((u1 < 0 || u1 > 1) && (u2 < 0 || u2 > 1)) {
                if ((u1 < 0 && u2 < 0) || (u1 > 1 && u2 > 1)) {
                    result = new Intersection(Status.NO_INTERSECTION_OUTSIDE);
                } else {
                    result = new Intersection(Status.NO_INTERSECTION_INSIDE);
                }
            } else {
                result = new Intersection(Status.INTERSECTION);

                if (0 <= u1 && u1 <= 1) {
                    result.appendPoint(lerp(x1, y1, x2, y2, u1), u1);
                }

                if (0 <= u2 && u2 <= 1) {
                    result.appendPoint(lerp(x1, y1, x2, y2, u2), u2);
                }
            }
        }

        return result;
    }

    /**
     * Computes the intersection between a line and an ellipse.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     *
     * @param e the bounds of the ellipse
     * @param a1 point 1 of the line
     * @param a2 point 2 of the line
     * @return computed intersection
     */
    public static Intersection intersectLineEllipse(Point2D a1, Point2D a2, Bounds e) {
        double rx = e.getWidth() * 0.5;
        double ry = e.getHeight() * 0.5;
        return intersectLineEllipse(a1, a2, new Point2D(e.getMinX() + rx, e.getMinY() + ry), rx, ry);
    }

    /**
     * Computes the intersection between a line and an ellipse.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     *
     * @param ec the center of the ellipse
     * @param rx the x-radius of the ellipse
     * @param ry the y-radius of the ellipse
     * @param a1 point 1 of the line
     * @param a2 point 2 of the line
     * @return computed intersection
     */
    public static Intersection intersectLineEllipse(Point2D a1, Point2D a2, Point2D ec, double rx, double ry) {
        Intersection result;
        Point2D origin = new Point2D(a1.getX(), a1.getY());
        Point2D dir = a2.subtract(a1);
        Point2D center = new Point2D(ec.getX(), ec.getY());
        Point2D diff = origin.subtract(center);
        Point2D mDir = new Point2D(dir.getX() / (rx * rx), dir.getY() / (ry * ry));
        Point2D mDiff = new Point2D(diff.getX() / (rx * rx), diff.getY() / (ry * ry));

        double a = dir.dotProduct(mDir);
        double b = dir.dotProduct(mDiff);
        double c = diff.dotProduct(mDiff) - 1.0;
        double d = b * b - a * c;

        if (d < 0) {
            result = new Intersection(Status.NO_INTERSECTION_OUTSIDE);
        } else if (d > 0) {
            double root = Math.sqrt(d);
            double t_a = (-b - root) / a;
            double t_b = (-b + root) / a;

            if ((t_a < 0 || 1 < t_a) && (t_b < 0 || 1 < t_b)) {
                if ((t_a < 0 && t_b < 0) || (t_a > 1 && t_b > 1)) {
                    result = new Intersection(Status.NO_INTERSECTION_OUTSIDE);
                } else {
                    result = new Intersection(Status.NO_INTERSECTION_INSIDE);
                }
            } else {
                result = new Intersection(Status.INTERSECTION);
                if (0 <= t_a && t_a <= 1) {
                    result.appendPoint(lerp(a1, a2, t_a), t_a);
                }
                if (0 <= t_b && t_b <= 1) {
                    result.appendPoint(lerp(a1, a2, t_b), t_b);
                }
            }
        } else {
            double t = -b / a;
            if (0 <= t && t <= 1) {
                result = new Intersection(Status.INTERSECTION);
                result.appendPoint(lerp(a1, a2, t), t);
            } else {
                result = new Intersection(Status.NO_INTERSECTION_OUTSIDE);
            }
        }

        return result;
    }

    /**
     * Computes the intersection between two lines 'a' and 'b'
     *
     * @param a1 point 1 of line 'a'
     * @param a2 point 2 of line 'a'
     * @param b1 point 1 of line 'b'
     * @param b2 point 2 of line 'b'
     * @return computed intersection
     */
    public static Intersection intersectLineLine(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        final double b2x = b2.getX();
        final double b1x = b1.getX();
        final double a1y = a1.getY();
        final double b1y = b1.getY();
        final double b2y = b2.getY();
        final double a1x = a1.getX();
        final double a2y = a2.getY();
        final double a2x = a2.getX();
        return intersectLineLine(a1x, a1y, a2x, a2y, b1x, b1y, b2x, b2y);
    }

    public static Intersection intersectLineLine(double a1x, double a1y, double a2x, double a2y, double b1x, double b1y, double b2x, double b2y) {
        Intersection result;

        double ua_t = (b2x - b1x) * (a1y - b1y) - (b2y - b1y) * (a1x - b1x);
        double ub_t = (a2x - a1x) * (a1y - b1y) - (a2y - a1y) * (a1x - b1x);
        double u_b = (b2y - b1y) * (a2x - a1x) - (b2x - b1x) * (a2y - a1y);

        if (u_b != 0) {
            double ua = ua_t / u_b;
            double ub = ub_t / u_b;

            if (0 <= ua && ua <= 1 && 0 <= ub && ub <= 1) {
                result = new Intersection(Status.INTERSECTION);
                result.appendPoint(new Point2D(
                        a1x + ua * (a2x - a1x),
                        a1y + ua * (a2y - a1y)
                ), ua
                );
            } else {
                result = new Intersection(Status.NO_INTERSECTION);
            }
        } else {
            if (ua_t == 0 || ub_t == 0) {
                result = new Intersection(Status.NO_INTERSECTION_COINCIDENT);
            } else {
                result = new Intersection(Status.NO_INTERSECTION_PARALLEL);
            }
        }

        return result;
    }

    public static Intersection intersectLinePathIterator(Point2D a, Point2D b, PathIterator pit) {
        Intersection result = new Intersection(Status.NO_INTERSECTION);
        final double ax = a.getX();
        final double ay = a.getY();
        final double bx = b.getX();
        final double by = b.getY();
        final double[] seg = new double[6];
        double firstx = 0, firsty = 0;
        double lastx = 0, lasty = 0;
        double x, y;
        for (; !pit.isDone(); pit.next()) {
            Intersection inter;
            switch (pit.currentSegment(seg)) {
                case PathIterator.SEG_CLOSE:
                    inter = Intersection.intersectLineLine(ax, ay, bx, by, lastx, lasty, firstx, firsty);
                    result.appendPoints(inter.points, inter.ts);
                    break;
                case PathIterator.SEG_CUBICTO:
                    break;
                case PathIterator.SEG_LINETO:
                    x = seg[0];
                    y = seg[1];
                    inter = Intersection.intersectLineLine(ax, ay, bx, by, lastx, lasty, x, y);
                    result.appendPoints(inter.points, inter.ts);
                    lastx = x;
                    lasty = y;
                    break;
                case PathIterator.SEG_MOVETO:
                    lastx = firstx = seg[0];
                    lasty = firsty = seg[1];
                    break;
                case PathIterator.SEG_QUADTO:
                    break;
            }
        }

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between a line and a polygon.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     *
     * @param a1 point 1 of the line
     * @param a2 point 2 of the line
     * @param points the points of the polygon
     * @return computed intersection
     */
    public static Intersection intersectLinePolygon(Point2D a1, Point2D a2, List<Point2D> points) {
        Intersection result = new Intersection(Status.NO_INTERSECTION);
        int length = points.size();

        for (int i = 0; i < length; i++) {
            Point2D b1 = points.get(i);
            Point2D b2 = points.get((i + 1) % length);
            Intersection inter = Intersection.intersectLineLine(a1, a2, b1, b2);

            result.appendPoints(inter.points, inter.ts);
        }

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between a line and a rectangle.
     * <p>
     * The intersection will contain the parameters 't' of the line in range
     * [0,1].
     *
     * @param a1 point 1 of the line
     * @param a2 point 2 of the line
     * @param r1 corner point 1 of the rectangle
     * @param r2 corner point 2 of the rectangle
     * @return computed intersection
     */
    public static Intersection intersectLineRectangle(Point2D a1, Point2D a2, Point2D r1, Point2D r2) {
        return intersectLineRectangle(a1, a2,
                Math.min(r1.getX(), r2.getX()),
                Math.min(r1.getY(), r2.getY()),
                Math.max(r1.getX(), r2.getX()),
                Math.max(r1.getY(), r2.getY()));
    }

    public static Intersection intersectLineRectangle(Point2D a1, Point2D a2, Bounds r) {
        return intersectLineRectangle(a1, a2, r.getMinX(), r.getMinY(), r.getMaxX(), r.getMaxY());
    }

    public static Intersection intersectLineRectangle(Point2D a1, Point2D a2, double rminx, double rminy, double rmaxx, double rmaxy) {

        Point2D min = new Point2D(rminx, rminy);
        Point2D max = new Point2D(rmaxx, rmaxy);
        Point2D topRight = new Point2D(rmaxx, rminy);
        Point2D bottomLeft = new Point2D(rminx, rmaxy);

        Intersection inter1 = Intersection.intersectLineLine(a1, a2, min, topRight);
        Intersection inter2 = Intersection.intersectLineLine(a1, a2, topRight, max);
        Intersection inter3 = Intersection.intersectLineLine(a1, a2, max, bottomLeft);
        Intersection inter4 = Intersection.intersectLineLine(a1, a2, bottomLeft, min);

        Intersection result = new Intersection(Status.NO_INTERSECTION);

        result.appendPoints(inter1.points, inter1.ts);
        result.appendPoints(inter2.points, inter2.ts);
        result.appendPoints(inter3.points, inter3.ts);
        result.appendPoints(inter4.points, inter4.ts);

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between two polygons.
     *
     * @param points1 the points of the first polygon
     * @param points2 the points of the second polygon
     * @return computed intersection
     */
    public static Intersection intersectPolygonPolygon(List<Point2D> points1, List<Point2D> points2) {
        Intersection result = new Intersection(Status.NO_INTERSECTION);
        int length = points1.size();

        for (int i = 0; i < length; i++) {
            Point2D a1 = points1.get(i);
            Point2D a2 = points1.get((i + 1) % length);
            Intersection inter = Intersection.intersectLinePolygon(a1, a2, points2);

            result.appendPoints(inter.points);
        }

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;

    }

    /**
     * Computes the intersection between a polygon and a rectangle.
     *
     * @param points the points of the polygon
     * @param r1 corner point 1 of the rectangle
     * @param r2 corner point 2 of the rectangle
     * @return computed intersection
     */
    public static Intersection intersectPolygonRectangle(List<Point2D> points, Point2D r1, Point2D r2) {
        Point2D min = minp(r1, r2);
        Point2D max = maxp(r1, r2);
        Point2D topRight = new Point2D(max.getX(), min.getY());
        Point2D bottomLeft = new Point2D(min.getX(), max.getY());

        Intersection inter1 = Intersection.intersectLinePolygon(min, topRight, points);
        Intersection inter2 = Intersection.intersectLinePolygon(topRight, max, points);
        Intersection inter3 = Intersection.intersectLinePolygon(max, bottomLeft, points);
        Intersection inter4 = Intersection.intersectLinePolygon(bottomLeft, min, points);

        Intersection result = new Intersection(Status.NO_INTERSECTION);

        result.appendPoints(inter1.points);
        result.appendPoints(inter2.points);
        result.appendPoints(inter3.points);
        result.appendPoints(inter4.points);

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    /**
     * Computes the intersection between two infinitely long rays 'a' and 'b'.
     * <p>
     * The intersection will contain the parameters 't' of ray 'a' in range
     * [0,MAX_VALUE].
     *
     * @param a1 point 1 of ray 'a'
     * @param a2 point 2 of ray 'a'
     * @param b1 point 1 of ray 'a'
     * @param b2 point 2 of ray 'b'
     * @return computed intersection
     */
    public static Intersection intersectRayRay(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        Intersection result;

        double ua_t = (b2.getX() - b1.getX()) * (a1.getY() - b1.getY()) - (b2.getY() - b1.getY()) * (a1.getX() - b1.getX());
        double ub_t = (a2.getX() - a1.getX()) * (a1.getY() - b1.getY()) - (a2.getY() - a1.getY()) * (a1.getX() - b1.getX());
        double u_b = (b2.getY() - b1.getY()) * (a2.getX() - a1.getX()) - (b2.getX() - b1.getX()) * (a2.getY() - a1.getY());

        if (u_b != 0) {
            double ua = ua_t / u_b;

            result = new Intersection(Status.INTERSECTION);
            result.appendPoint(
                    new Point2D(
                            a1.getX() + ua * (a2.getX() - a1.getX()),
                            a1.getY() + ua * (a2.getY() - a1.getY())
                    ), ua
            );
        } else {
            if (ua_t == 0 || ub_t == 0) {
                result = new Intersection(Status.NO_INTERSECTION_COINCIDENT);
            } else {
                result = new Intersection(Status.NO_INTERSECTION_PARALLEL);
            }
        }

        return result;
    }

    /**
     * Computes the intersection between two rectangles 'a' and 'b'.
     *
     * @param a1 corner point 1 of rectangle 'a'
     * @param a2 corner point 2 of rectangle 'a'
     * @param b1 corner point 1 of rectangle 'b'
     * @param b2 corner point 2 of rectangle 'b'
     * @return computed intersection
     */
    public static Intersection intersectRectangleRectangle(Point2D a1, Point2D a2, Point2D b1, Point2D b2) {
        Point2D min = minp(a1, a2);
        Point2D max = maxp(a1, a2);
        Point2D topRight = new Point2D(max.getX(), min.getY());
        Point2D bottomLeft = new Point2D(min.getX(), max.getY());

        Intersection inter1 = Intersection.intersectLineRectangle(min, topRight, b1, b2);
        Intersection inter2 = Intersection.intersectLineRectangle(topRight, max, b1, b2);
        Intersection inter3 = Intersection.intersectLineRectangle(max, bottomLeft, b1, b2);
        Intersection inter4 = Intersection.intersectLineRectangle(bottomLeft, min, b1, b2);

        Intersection result = new Intersection(Status.NO_INTERSECTION);

        result.appendPoints(inter1.points);
        result.appendPoints(inter2.points);
        result.appendPoints(inter3.points);
        result.appendPoints(inter4.points);

        if (!result.isEmpty()) {
            result.status = Status.INTERSECTION;
        }

        return result;
    }

    ;


    /**
     * Returns true if point 'a' is less or equal to point 'b'. Compares the
     * x-coordinates first, and if they are equal compares the y-coordinates.
     *
     * @param a point a
     * @param b point b
     * @return true if a is less or equal b
     */
    private static boolean lte(Point2D a, Point2D b) {
        return a.getX() <= b.getX() && a.getY() <= b.getY();
    }

    /**
     * Computes the coordinates of the bottom right corner of a rectangle given
     * two corner points defining the extrema of the rectangle.
     *
     * @param a corner point a
     * @param b corner point b
     * @return the bottom right corner
     */
    private static Point2D maxp(Point2D a, Point2D b) {
        return new Point2D(Math.max(a.getX(), b.getX()), Math.max(a.getY(), b.getY()));
    }

    /**
     * Computes the coordinates of the top left corner of a rectangle given two
     * corner points defining the extrema of the rectangle.
     *
     * @param a corner point a
     * @param b corner point b
     * @return the top left corner
     */
    private static Point2D minp(Point2D a, Point2D b) {
        return new Point2D(Math.min(a.getX(), b.getX()), Math.min(a.getY(), b.getY()));
    }

}
