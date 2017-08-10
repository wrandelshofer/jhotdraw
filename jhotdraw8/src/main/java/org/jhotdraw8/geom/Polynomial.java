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

    private final static double TOLERANCE = 1e-6;
    private final static double ACCURACY = 6;

    private int simplifiedDegree() {
        int i = this.getDegree();
        while (i > 0 && Math.abs(this.coefs[i]) <= Polynomial.TOLERANCE) {
            i--;
        }
        return i;
    }

    /**
     * "
     * Interpolate. Computes y and dy for a given x.
     *
     * @param xs
     * @param ys
     * @param n
     * @param offset
     * @param x
     * @return a tuple: y, dy. ***
     */
    private static YDY interpolate(double[] xs, double[] ys, int n, int offset, double x) {

        double y = 0;
        double dy = 0;
        double[] c = new double[n];
        double[] d = new double[n];
        int ns = 0;
        YDY result;

        double diff = Math.abs(x - xs[offset]);
        for (int i = 0; i < n; i++) {
            double dift = Math.abs(x - xs[offset + i]);

            if (dift < diff) {
                ns = i;
                diff = dift;
            }
            c[i] = d[i] = ys[offset + i];
        }
        y = ys[offset + ns];
        ns--;

        for (int m = 1; m < n; m++) {
            for (int i = 0; i < n - m; i++) {
                double ho = xs[offset + i] - x;
                double hp = xs[offset + i + m] - x;
                double w = c[i + 1] - d[i];
                double den = ho - hp;

                if (den == 0.0) {
                    result = new YDY(0, 0); //{ y: 0, dy: 0};
                    break;
                }

                den = w / den;
                d[i] = hp * den;
                c[i] = ho * den;
            }
            dy = (2 * (ns + 1) < (n - m)) ? c[ns + 1] : d[ns--];
            y += dy;
        }

        return new YDY(y, dy);// { y: y, dy: dy };
    }

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
        this.coefs = new double[coefs.length];
        for (int i = 0; i < coefs.length; i++) {
            this.coefs[i] = coefs[coefs.length - i - 1];
        }

        this.variable = "t";
    }
    private double[] coefs;
    private final String variable;

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
        return new Polynomial(newCoefs);
    }

    /**
     * ln(10)≈2.302
     */
    private final static double MathLN10 = Math.log(10);
    /**
     * ln(2)≈0.693
     */
    private final static double MathLN2 = Math.log(2);

    /**
     * Searches for a root in the given interval using the bisection method.
     *
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @return the potential root
     */
    public double bisection(double min, double max) {
        double minValue = this.eval(min);
        double maxValue = this.eval(max);
        double result = 0;

        if (Math.abs(minValue) <= Polynomial.TOLERANCE) {
            result = min;
        } else if (Math.abs(maxValue) <= Polynomial.TOLERANCE) {
            result = max;
        } else if (minValue * maxValue <= 0) {
            double tmp1 = Math.log(max - min);
            double tmp2 = MathLN10 * Polynomial.ACCURACY;
            double iters = Math.ceil((tmp1 + tmp2) / MathLN2);

            for (double i = 0; i < iters; i++) {
                result = 0.5 * (min + max);
                double value = this.eval(result);

                if (Math.abs(value) <= Polynomial.TOLERANCE) {
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
     * ***
     *
     * toString
     *
     ****
     * @return string representation
     */
    public String toString() {
        if (true) {
            return Arrays.toString(this.coefs);
        }
        ArrayList<String> coefs = new ArrayList<String>();
        ArrayList<String> signs = new ArrayList<String>();

        for (int i = this.coefs.length - 1; i >= 0; i--) {
            // double value = Math.round(this.coefs[i] * 1000) / 1000;
            double value = this.coefs[i];

            if (value != 0) {
                String sign = (value < 0) ? " - " : " + ";

                value = Math.abs(value);
                String strvalue = Double.toString(value);
                if (i > 0) {
                    if (value == 1) {
                        strvalue = this.variable;
                    } else {
                        strvalue = value + this.variable;
                    }
                }
                if (i > 1) {
                    strvalue += "^" + i;
                }

                signs.add(sign);
                coefs.add(strvalue);
            }
        }

        if (signs.size() > 0) {
            signs.set(0, (signs.get(0) == " + ") ? "" : "-");
        }

        StringBuilder result = new StringBuilder();
        for (int i = 0; i < coefs.size(); i++) {
            result.append(signs.get(i)).append(coefs.get(i));
        }

        return result.toString();
    }

    /**
     * Estimates the integral of the polynomial in the given interval using the
     * trapezoidal rule.
     *
     * trapezoid Based on trapzd in "Numerical Recipes in C", page 137
     *
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @param n the number of trapezoids
     * @return the area of the polynomial
     */
    public double trapezoid(double min, double max, int n) {

        double range = max - min;
        double _s = 0;
        if (n == 1) {
            double minValue = this.eval(min);
            double maxValue = this.eval(max);
            _s = 0.5 * range * (minValue + maxValue);
        } else {
            double it = 1 << (n - 2);
            double delta = range / it;
            double x = min + 0.5 * delta;
            double sum = 0;

            for (double i = 0; i < it; i++) {
                sum += this.eval(x);
                x += delta;
            }
            _s = 0.5 * (_s + range * sum / it);
        }

        if (isNaN(_s)) {
            throw new Error("Polynomial.trapezoid: _s is NaN");
        }

        return _s;
    }

    /**
     * Estimates the integral of the polynomial in the given interval using
     * Simpsons's rule.
     *
     * simpson Based on trapzd in "Numerical Recipes in C", page 139
     *
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @return the area under the curve
     */
    public double simpson(double min, double max) {

        double range = max - min;
        double st = 0.5 * range * (this.eval(min) + this.eval(max));
        double t = st;
        double s = 4.0 * st / 3.0;
        double os = s;
        double ost = st;
        double TOLERANCE = 1e-7;

        int it = 1;
        for (int n = 2; n <= 20; n++) {
            double delta = range / it;
            double x = min + 0.5 * delta;
            double sum = 0;

            for (double i = 1; i <= it; i++) {
                sum += this.eval(x);
                x += delta;
            }

            t = 0.5 * (t + range * sum / it);
            st = t;
            s = (4.0 * st - ost) / 3.0;

            if (Math.abs(s - os) < TOLERANCE * Math.abs(os)) {
                break;
            }

            os = s;
            ost = st;
            it <<= 1;
        }

        return s;
    }

    public static class YDY {

        final double y, dy;

        public YDY(double y, double dy) {
            this.y = y;
            this.dy = dy;
        }
    }

    /**
     * Estimates the integral of the polynomial in the given interval using
     * Romberg's method.
     *
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @return the area under the curve
     */
    public double romberg(double min, double max) {

        int MAX = 20;
        int K = 3;
        double TOLERANCE = 1e-6;
        double[] s = new double[MAX + 1];
        double[] h = new double[MAX + 1];
        YDY result = new YDY(0, 0);

        h[0] = 1.0;
        for (int j = 1; j <= MAX; j++) {
            s[j - 1] = this.trapezoid(min, max, j);
            if (j >= K) {
                result = Polynomial.interpolate(h, s, K, j - K, 0.0);
                if (Math.abs(result.dy) <= TOLERANCE * result.y) {
                    break;
                }
            }
            s[j] = s[j - 1];
            h[j] = 0.25 * h[j - 1];
        }

        return result.y;
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

        return new Polynomial(derivative);
    }

    /**
     * Attempts to find the roots of the current polynomial. This method will
     * attempt to decrease the degree of the polynomial using the simplify()
     * method. Once the degree is determined, getRoots() dispatches the
     * appropriate root-finding method for the degree of the polynomial.
     * <p>
     * NOTE Polynomials above the 4'th degree are not supported.
     *
     * @return the roots of the polynomial
     */
    public double[] getRoots() {
        double[] result;

        switch (simplifiedDegree()) {
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
                result = new double[0];
            // should try Newton's method and/or bisection
        }

        return result;
    }

    ;

private static double[] push(double[] a, double d) {
        double[] r = new double[a.length + 1];
        System.arraycopy(a, 0, r, 0, r.length);
        r[a.length] = d;
        return r;
    }

    /**
     * getRootsInInterval
     *
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @return a list of roots
     */
    public double[] getRootsInInterval(double min, double max) {
        double[] roots = new double[0];
        Double root;

        if (this.getDegree() == 1) {
            root = this.bisection(min, max);
            if (root != null) {
                roots = new double[]{root};
            }
        } else {
            // get roots of derivative
            Polynomial deriv = this.getDerivative();
            double[] droots = deriv.getRootsInInterval(min, max);

            if (droots.length > 0) {
                // find root on [min, droots[0]]
                root = this.bisection(min, droots[0]);
                if (root != null) {
                    roots = new double[]{root};
                }

                // find root on [droots[i],droots[i+1]] for 0 <= i <= count-2
                for (int i = 0; i <= droots.length - 2; i++) {
                    root = this.bisection(droots[i], droots[i + 1]);
                    if (root != null) {
                        roots = push(roots, root);
                    }
                }

                // find root on [droots[count-1],xmax]
                root = this.bisection(droots[droots.length - 1], max);
                if (root != null) {
                    roots = push(roots, root);
                }
            } else {
                // polynomial is monotone on [min,max], has at most one root
                root = this.bisection(min, max);
                if (root != null) {
                    roots = new double[]{root};
                }
            }
        }

        return roots;
    }

    ;


/**
* Returns  the root of a linear polynomial (degree equals one).
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

    ;


/**
* Returns the roots of a quadratic polynomial (degree equals two).
     * @return the roots
*
     * @return the roots
*/
private double[] getQuadraticRoots() {
        double[] results = new double[0];

        double a = this.coefs[2];
        double b = this.coefs[1] / a;
        double c = this.coefs[0] / a;
        double d = b * b - 4 * c;

        if (d > 0) {
            double e = Math.sqrt(d);

            results = new double[]{
                0.5 * (-b + e),
                0.5 * (-b - e)};
        } else if (d == 0) {
            // really two roots with same value, but we only return one
            results = new double[]{0.5 * -b};
        }

        return results;
    }

    /**
     * Returns the roots of a cubic polynomial (degree equals three).
     *
     * This code is based on MgcPolynomial.cpp written by David Eberly. His code
     * along with many other excellent examples are avaiable at his site:
     * http://www.magic-software.com
     *
     * @return the roots
     */
    private double[] getCubicRoots() {
        double[] results = new double[0];

        double c3 = this.coefs[3];
        double c2 = this.coefs[2] / c3;
        double c1 = this.coefs[1] / c3;
        double c0 = this.coefs[0] / c3;

        double a = (3 * c1 - c2 * c2) / 3;
        double b = (2 * c2 * c2 * c2 - 9 * c1 * c2 + 27 * c0) / 27;
        double offset = c2 / 3;
        double discrim = b * b / 4 + a * a * a / 27;
        double halfB = b / 2;

        if (Math.abs(discrim) <= Polynomial.TOLERANCE) {
            discrim = 0;
        }

        if (discrim > 0) {
            double e = Math.sqrt(discrim);
            double tmp;
            double root;

            tmp = -halfB + e;
            if (tmp >= 0) {
                root = Math.pow(tmp, 1.0 / 3.0);
            } else {
                root = -Math.pow(-tmp, 1.0 / 3.0);
            }

            tmp = -halfB - e;
            if (tmp >= 0) {
                root += Math.pow(tmp, 1.0 / 3.0);
            } else {
                root -= Math.pow(-tmp, 1.0 / 3.0);
            }
            results = new double[]{root - offset};
        } else if (discrim < 0) {
            double distance = Math.sqrt(-a / 3.0);
            double angle = Math.atan2(Math.sqrt(-discrim), -halfB) / 3.0;
            double cos = Math.cos(angle);
            double sin = Math.sin(angle);
            double sqrt3 = Math.sqrt(3);

            results = new double[]{2 * distance * cos - offset,
                -distance * (cos + sqrt3 * sin) - offset,
                -distance * (cos - sqrt3 * sin) - offset};
        } else {
            double tmp;

            if (halfB >= 0) {
                tmp = -Math.pow(halfB, 1.0 / 3.0);
            } else {
                tmp = Math.pow(-halfB, 1.0 / 3.0);
            }

            results = new double[]{2 * tmp - offset,
                // really should return next root twice, but we return only one
                -tmp - offset};
        }

        return results;
    }

    /**
     * Returns the roots of a quartic polynomial (degree equals four).
     *
     * This code is based on MgcPolynomial.cpp written by David Eberly. His code
     * along with many other excellent examples are available at his site:
     * http://www.magic-software.com
     *
     * @return the roots
     */
    private double[] getQuarticRoots() {

        double[] results = new double[4];
        int numResults = 0;

        double c4 = this.coefs[4];
        double c3 = this.coefs[3] / c4;
        double c2 = this.coefs[2] / c4;
        double c1 = this.coefs[1] / c4;
        double c0 = this.coefs[0] / c4;

        double[] resolveRoots = new Polynomial(
                1, -c2, c3 * c1 - 4 * c0, -c3 * c3 * c0 + 4 * c2 * c0 - c1 * c1
        ).getCubicRoots();
        double y = resolveRoots[0];
        double discrim = c3 * c3 / 4 - c2 + y;

        if (Math.abs(discrim) <= Polynomial.TOLERANCE) {
            discrim = 0;
        }

        if (discrim > 0) {
            double e = Math.sqrt(discrim);
            double t1 = 3 * c3 * c3 / 4 - e * e - 2 * c2;
            double t2 = (4 * c3 * c2 - 8 * c1 - c3 * c3 * c3) / (4 * e);
            double plus = t1 + t2;
            double minus = t1 - t2;

            if (Math.abs(plus) <= Polynomial.TOLERANCE) {
                plus = 0;
            }
            if (Math.abs(minus) <= Polynomial.TOLERANCE) {
                minus = 0;
            }

            if (plus >= 0) {
                double f = Math.sqrt(plus);

                results[numResults++] = -c3 / 4 + (e + f) / 2;
                results[numResults++] = -c3 / 4 + (e - f) / 2;
            }
            if (minus >= 0) {
                double f = Math.sqrt(minus);

                results[numResults++] = -c3 / 4 + (f - e) / 2;
                results[numResults++] = -c3 / 4 - (f + e) / 2;
            }
        } else if (discrim < 0) {
            // no roots
        } else {
            double t2 = y * y - 4 * c0;

            if (t2 >= -Polynomial.TOLERANCE) {
                if (t2 < 0) {
                    t2 = 0;
                }

                t2 = 2 * Math.sqrt(t2);
                double t1 = 3 * c3 * c3 / 4 - 2 * c2;
                if (t1 + t2 >= Polynomial.TOLERANCE) {
                    double d = Math.sqrt(t1 + t2);

                    results[numResults++] = -c3 / 4 + d / 2;
                    results[numResults++] = -c3 / 4 - d / 2;
                }
                if (t1 - t2 >= Polynomial.TOLERANCE) {
                    double d = Math.sqrt(t1 - t2);

                    results[numResults++] = -c3 / 4 + d / 2;
                    results[numResults++] = -c3 / 4 - d / 2;
                }
            }
        }

        double[] finalResults = new double[numResults];
        System.arraycopy(results, 0, finalResults, 0, numResults);

        return finalResults;
    }
}
