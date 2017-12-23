/* @(#)Polynomial.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 *
 * This class is a based on:
*
*   Polynomial.js by Kevin Lindsey.
 * Copyright (C) 2002, Kevin Lindsey.
 *
 * MgcPolynomial.cpp by David Eberly. 
 * Copyright (c) 2000-2003 Magic Software, Inc.
 */
package org.jhotdraw8.geom;

import static java.lang.Double.isNaN;
import static java.lang.Math.abs;
import static java.lang.Math.sqrt;
import java.util.ArrayList;
import java.util.Arrays;

/**
 * Polynomial encapsulates root finding functions needed by curve intersection
 * methods which are based on numerical calculations.
 * <p>
 * This class is a port of Polynomial.js by Kevin Lindsey. Parts of
 * Polynomial.js are based on MgcPolynomial.cpp written by David Eberly, Magic
 * Software. Inc.
 * <p>
 * References:
 * <p>
 * <a href="http://www.kevlindev.com/gui/index.htm">Polynomial.js</a>, Copyright
 * (c) 2002, Kevin Lindsey.
 * <p>
 * <a href="http://www.magic-software.com">MgcPolynomial.cpp </a> Copyright
 * 2000-2003 (c) David Eberly. Magic Software, Inc.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Polynomial {

    private final static double ACCURACY = 6;

    /**
     * ln(10)≈2.302
     */
    private final static double MathLN10 = Math.log(10);
    /**
     * ln(2)≈0.693
     */
    private final static double MathLN2 = Math.log(2);
    /** We have 52 bits of precision in a double. We use 26 bits for EPSILON. */
    private final static double EPSILON = 1.0/(1<<26);
    private double[] coefs;

    /**
     * Creates a new polynomial.
     * <p>
     * The coefficients are in order by highest degree monomial first. For
     * example, the following example initializes a Polynomial object for:
     * <code>3x^4 + 2x^2 + 5</code>.
     * <pre>
     *var poly = new Polynomial(3, 0, 2, 0, 5);
     * </pre> All coefficients from highest degree to degree 0 must be provided.
     * A zero is used for monomials that are not present in the polynomial.
     *
     * NOTE: The polynomial coefficients are stored in an array in the reverse
     * order to how they were specified. This has the benefit that the
     * coefficient's position in the array corresponds to the degree of the
     * monomial to which it belongs. *
     *
     * @param coefs the coefficients of the polynomial
     */
    public Polynomial(double... coefs) {
        this(true, coefs);
    }

    /**
     * Alternative constructor.
     *
     * @param highestToLowestDegree true if sorted from highest to lowest degree, false if
     * sorted from lowest do highest degree.
     * @param coefs will be referenced
     */
    Polynomial(boolean highestToLowestDegree, double... coefs) {
        if (highestToLowestDegree) {
            this.coefs = new double[coefs.length];
            for (int i = 0; i < coefs.length; i++) {
                this.coefs[i] = coefs[coefs.length - i - 1];
            }
        } else {
            this.coefs = coefs;
        }
    }

    /**
     * Adds the coefficients of this polynomial to the coefficients of that
     * polynomial and returns the resulting polynomial. Does not change this
     * polynomial.
     *
     * @param that another polynomial
     * @return a new polynomial containing the sum of the coefficients
     */
    public Polynomial add(Polynomial that) {
        Polynomial result = new Polynomial();
        double d1 = this.getDegree();
        double d2 = that.getDegree();
        double dmax = Math.max(d1, d2);

        for (int i = 0; i <= dmax; i++) {
            double v1 = (i <= d1) ? this.coefs[i] : 0;
            double v2 = (i <= d2) ? that.coefs[i] : 0;

            result.coefs[i] = v1 + v2;
        }

        return result;
    }

    /**
     * Searches for a root in the given interval using the bisection method.
     *
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @return the potential root
     */
    public Double bisection(double min, double max) {
        double minValue = this.eval(min);
        double maxValue = this.eval(max);
        Double result = null;

        if (Math.abs(minValue) <= EPSILON) {
            result = min;
        } else if (Math.abs(maxValue) <= EPSILON) {
            result = max;
        } else if (minValue * maxValue <= 0) {
            double tmp1 = Math.log(max - min);
            double tmp2 = MathLN10 * Polynomial.ACCURACY;
            double iters = Math.ceil((tmp1 + tmp2) / MathLN2);

            for (double i = 0; i < iters; i++) {
                result = 0.5 * (min + max);
                double value = this.eval(result);

                if (Math.abs(value) <= EPSILON) {
                    break;
                }

                if (value * minValue < 0) {
                    max = result;
                    maxValue = value;
                } else {
                    min = result;
                    minValue = value;
                }
            }
        }

        return result;
    }

    /**
     * Divides the coefficients of this polynomial by the provided scalar.
     * Change this polynomial in-place.
     *
     * @param scalar a scalar
     */
    public void divide_scalar(double scalar) {
        for (int i = 0; i < this.coefs.length; i++) {
            this.coefs[i] /= scalar;
        }
    }

    /**
     * Evaluates the polynomial at the specified x value.
     *
     * @param x is a number that is "plugged into" the polynomial to evaluate
     * it.
     * @return the value of the polynomial at x
     */
    public double eval(double x) {
        double result = 0;
        for (int i = this.coefs.length - 1; i >= 0; i--) {
            result = result * x + this.coefs[i];
        }
        return result;
    }

    /**
     * Returns the roots of a cubic polynomial (degree equals three).
     *
     * @return the roots
     */
    private double[] getCubicRoots() {
        final double[] results = new double[4];
        int numResults = 0;

        final double c3, c2, c1, c0;
        c3 = this.coefs[3];
        c2 = this.coefs[2] / c3;
        c1 = this.coefs[1] / c3;
        c0 = this.coefs[0] / c3;
        if (c3 == 0) {
            throw new IllegalArgumentException("Not a cubic root! simplifiedDegree=" + simplifiedDegree());
        }

        final double a, b, offset, halfB;
        a = (3 * c1 - c2 * c2) / 3;
        b = (2 * c2 * c2 * c2 - 9 * c1 * c2 + 27 * c0) / 27;
        offset = c2 / 3;
        halfB = b / 2;
        double discrim = b * b / 4 + a * a * a / 27;

        // Note: must not set discrim to 0 here, because we loose too much precision!
        //if (Math.abs(discrim) <= EPSILON) {
        //    discrim = 0;
        //}

        if (discrim > 0) {
            double e = Math.sqrt(discrim);
            double tmp;
            double root;

            tmp = -halfB + e;
            if (tmp >= 0) {
                root = Math.pow(tmp, 1.0 / 3);
            } else {
                root = -Math.pow(-tmp, 1.0 / 3);
            }

            tmp = -halfB - e;
            if (tmp >= 0) {
                root += Math.pow(tmp, 1.0 / 3);
            } else {
                root -= Math.pow(-tmp, 1.0 / 3);
            }
            results[numResults++] = root - offset;
        } else if (discrim < 0) {
            double distance = Math.sqrt(-a / 3);
            double angle = Math.atan2(Math.sqrt(-discrim), -halfB) / 3;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double sqrt3 = Math.sqrt(3);

            results[numResults++] = 2 * distance * cos - offset;
            results[numResults++] = -distance * (cos + sqrt3 * sin) - offset;
            results[numResults++] = -distance * (cos - sqrt3 * sin) - offset;
        } else {
            double tmp;

            if (halfB >= 0) {
                tmp = -Math.pow(halfB, 1.0 / 3.0);
            } else {
                tmp = Math.pow(-halfB, 1.0 / 3.0);
            }

            results[numResults++] = 2 * tmp - offset;
            // really should return next root twice, but we return only one
            results[numResults++] = -tmp - offset;
        }

        return trim(numResults, results);
    }

    /**
     * Returns the degree of this polynomial.
     *
     * @return the degree
     */
    public int getDegree() {
        return this.coefs.length - 1;
    }

    /**
     * Returns the derivative of this polynomial.
     *
     * @return returns the derivative of the current polynomial.
     */
    public Polynomial getDerivative() {
        double[] derivative = new double[coefs.length - 1];

        for (int i = 1; i < this.coefs.length; i++) {
            derivative[i - 1] = (i * this.coefs[i]);
        }

        return new Polynomial(false, derivative);
    }

    /**
     * Returns the root of a linear polynomial (degree equals one).
     *
     * @return the roots
     */
    private double[] getLinearRoot() {
        double[] result = new double[0];
        double a = this.coefs[1];

        if (a != 0) {
            result = new double[]{-this.coefs[0] / a};
        }

        return result;
    }

    /**
     * Returns the roots of a quadratic polynomial (degree equals two).
     *
     * @return the roots
     *
     * @return the roots
     */
    private double[] getQuadraticRoots() {
        double a = this.coefs[2];
        double b = this.coefs[1] / a;
        double c = this.coefs[0] / a;
        double d = b * b - 4 * c;

        if (d > 0) {
            double e = Math.sqrt(d);

            return new double[]{
                0.5 * (-b + e),
                0.5 * (-b - e)};
        } else if (d == 0) {
            // really two roots with same value, but we only return one
            return new double[]{0.5 * -b};
        }

        return new double[0];
    }

    /**
     * Returns the roots of a quartic polynomial (degree equals four).
     *
     * @return the roots
     */
    private double[] getQuarticRoots() {

        double[] results = new double[4];
        int numResults = 0;

        final double c4, c3, c2, c1, c0;
        c4 = this.coefs[4];
        c3 = this.coefs[3] / c4;
        c2 = this.coefs[2] / c4;
        c1 = this.coefs[1] / c4;
        c0 = this.coefs[0] / c4;

        double[] resolveRoots = new Polynomial(
                1, -c2, c3 * c1 - 4 * c0, -c3 * c3 * c0 + 4 * c2 * c0 - c1 * c1
        ).getCubicRoots();
        double y = resolveRoots[0];
        double discrim = c3 * c3 / 4 - c2 + y;

        // Note: must not set discrim to 0 here, because we loose too much precision!
        //if (Math.abs(discrim) <= EPSILON) {
        //    discrim = 0;
        //}

        if (discrim > 0) {
            double e = Math.sqrt(discrim);
            final double t1, t2;
            t1 = 0.75 * c3 * c3 - e * e - 2 * c2;
            t2 = (4 * c3 * c2 - 8 * c1 - c3 * c3 * c3) / (4 * e);
            double plus = t1 + t2;
            double minus = t1 - t2;

            if (Math.abs(plus) <= EPSILON) {
                plus = 0;
            }
            if (Math.abs(minus) <= EPSILON) {
                minus = 0;
            }

            if (plus >= 0) {
                double f = Math.sqrt(plus);
                results[numResults++] = c3 / -4 + (e + f) / 2;
                results[numResults++] = c3 / -4 + (e - f) / 2;
            }
            if (minus >= 0) {
                double f = Math.sqrt(minus);
                results[numResults++] = c3 / -4 + (f - e) / 2;
                results[numResults++] = c3 / -4 - (f + e) / 2;
            }
        } else if (discrim < 0) {
            // no roots
        } else {
            double t2 = y * y - 4 * c0;

            if (t2 >= -EPSILON) {
                if (t2 < 0) {
                    t2 = 0;
                }

                t2 = 2 * Math.sqrt(t2);
                double t1 = 3 * c3 * c3 / 4 - 2 * c2;
                if (t1 + t2 >= EPSILON) {
                    double d = Math.sqrt(t1 + t2);

                    results[numResults++] = -c3 / 4 + d / 2;
                    results[numResults++] = -c3 / 4 - d / 2;
                }
                if (t1 - t2 >= EPSILON) {
                    double d = Math.sqrt(t1 - t2);

                    results[numResults++] = -c3 / 4 + d / 2;
                    results[numResults++] = -c3 / 4 - d / 2;
                }
            }
        }

        return trim(numResults, results);
    }

    /**
     * Attempts to find the roots of the current polynomial. This method will
     * attempt to decrease the degree of the polynomial using the
     * {@link #simplifiedDegree}. method. Once the degree is determined,
     * getRoots() dispatches the appropriate root-finding method for the degree
     * of the polynomial.
     * <p>
     * NOTE This method does not find roots for polynomials, which can not be
     * simplfied to 4th degree or less. Use {@link #getRootsInInterval} for
     * polynomials above 4th degree.
     *
     * @return the roots of the polynomial
     */
    public double[] getRoots() {
        double[] result;
        final int simplifiedDegree = simplifiedDegree();

        switch (simplifiedDegree) {
            case 0:
                result = new double[0];
                break;
            case 1:
                result = getLinearRoot();
                break;
            case 2:
                result = getQuadraticRoots();
                break;
            case 3:
                result = getCubicRoots();
                break;
            case 4:
                result = getQuarticRoots();
                break;
            default:
                throw new UnsupportedOperationException("Degree is too high. simplifiedDegree=" + simplifiedDegree);
        }

        return result;
    }

    /**
     * Gets roots in the given interval. Uses the bisection method for root
     * finding. Can work with a polynomial of any degree.
     *
     * @param min the lower bound of the interval (inclusive)
     * @param max the upper bound of the interval (inclusive)
     * @return a list of roots
     */
    public double[] getRootsInInterval(double min, double max) {
        final double[] roots = new double[getDegree()];
        int numRoots = 0;

        switch (this.simplifiedDegree()) {
            case 0:
                break;
            case 1:
            case 2:
            case 3:
            case 4: {
                double[] allroots = getRoots();
                for (int i = 0; i < allroots.length; i++) {
                    double root = allroots[i];
                    if (min <= root && root <= max) {
                        roots[numRoots++] = root;
                    }
                    Arrays.sort(roots, 0, numRoots);
                }
                break;
            }
            default: {
                // get roots of derivative
                Polynomial deriv = this.getDerivative();
                double[] droots = deriv.getRootsInInterval(min, max);

                if (droots.length > 0) {
                    // find root on [min, droots[0]]
                    Double root = this.bisection(min, droots[0]);
                    if (root != null) {
                        roots[numRoots++] = root;
                    }

                    // find root on [droots[i],droots[i+1]] for 0 <= i <= count-2
                    for (int i = 0; i <= droots.length - 2; i++) {
                        root = this.bisection(droots[i], droots[i + 1]);
                        if (root != null) {
                            roots[numRoots++] = root;
                        }
                    }

                    // find root on [droots[count-1],xmax]
                    root = this.bisection(droots[droots.length - 1], max);
                    if (root != null) {
                        roots[numRoots++] = root;
                    }
                } else {
                    // polynomial is monotone on [min,max], has at most one root
                    Double root = this.bisection(min, max);
                    if (root != null) {
                        roots[numRoots++] = root;
                    }
                }
                break;
            }
        }

        return trim(numRoots,roots);
    }

    /**
     * Multiplies the coefficients of this polynomial with the coefficients of
     * that polynomial and returns the resulting polynomial. Does not change
     * this polynomial.
     *
     * @param that another polynomial
     * @return a new polynomial containing the product of the coefficients
     */
    public Polynomial multiply(Polynomial that) {
        Polynomial result = new Polynomial(new double[this.getDegree() + that.getDegree()]);
        for (int i = 0; i <= this.getDegree(); i++) {
            for (int j = 0; j <= that.getDegree(); j++) {
                result.coefs[i + j] += this.coefs[i] * that.coefs[j];
            }
        }
        return result;
    }

    private int simplifiedDegree() {
        int i = this.getDegree();
        while (i > 0 && Math.abs(this.coefs[i]) <= EPSILON) {
            i--;
        }
        return i;
    }

    /**
     * Returns a simplified polynomial, by removing coefficients of the highest
     * degrees if they have a very small absolute value.
     *
     * @return a new polynomial
     */
    public Polynomial simplify() {
        int popAt = simplifiedDegree();
        if (popAt == this.getDegree()) {
            return this;
        }

        double[] newCoefs = new double[popAt];
        System.arraycopy(this.coefs, 0, newCoefs, 0, popAt);
        return new Polynomial(false, newCoefs);
    }

    /**
     * toString.
     *
     * @return string representation
     */
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append('[');
        for (int i = coefs.length - 1; i >= 0; i--) {
            if (coefs[i] >= 0) {
                b.append('+');
            }
            b.append(coefs[i]);
            if (i > 0) {
                b.append("*x");
                if (i > 1) {
                    b.append('^').append(i);
                }
            }
            if (i > 0) {
                b.append(' ');
            }
        }
        return b.append(']').toString();
    }

    /**
     * Trims an array to the specified length.
     * <p>
     * Returns the same array if it already has the specified length.
     * 
     * @param length the specified length
     * @param a the array
     * @return array of the specified length
     */
    private static double[] trim(int length, double[] a) {
        if (length == a.length) {
            return a;
        }
        double[] finalResults = new double[length];
        System.arraycopy(a, 0, finalResults, 0, length);
        return finalResults;
    }

}
