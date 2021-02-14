/*
 * @(#)Polynomial.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.intersect;

import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.DoubleArrayList;
import org.jhotdraw8.geom.Geom;

import java.util.function.ToDoubleFunction;

import static java.lang.Math.abs;
import static java.lang.Math.cbrt;
import static java.lang.Math.ceil;
import static java.lang.Math.cos;
import static java.lang.Math.log;
import static java.lang.Math.max;
import static java.lang.Math.sin;
import static java.lang.Math.sqrt;

public class Polynomial implements ToDoubleFunction<Double> {

    private static final double ACCURACY = 6;

    /**
     * ln(10)≈2.302
     */
    private static final double LN10 = log(10);
    /**
     * ln(2)≈0.693
     */
    private static final double LN2 = log(2);
    /**
     * Values closer to zero than epsilon are treated as zero .
     * Machine precision for double is 2^-53.
     */
    private static final double EPSILON = 1.0 / (1L << 33);

    /**
     * Holds the coefficients from lowest to highest degree, that is
     * {@literal coefs[i]*x^i}.
     */
    private double[] coefs;

    /**
     * Creates a new polynomial.
     * <p>
     * The coefficients are in order by highest degree monomial first. For
     * example, the following example initializes a Polynomial object for:
     * <code>3x^4 + 2x^2 + 5</code>.
     * <pre>
     * var poly = new Polynomial(3, 0, 2, 0, 5);
     * </pre> All coefficients from highest degree to degree 0 must be provided.
     * A zero is used for monomials that are not present in the polynomial.
     * <p>
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
     *                              sorted from lowest do highest degree.
     * @param coefs                 will be referenced
     */
    public Polynomial(boolean highestToLowestDegree, @NonNull double... coefs) {
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
     * Adds the coefficients of that polynomial to the coefficients of this
     * polynomial and returns the resulting polynomial. Does not change this
     * polynomial.
     *
     * @param that another polynomial
     * @return a new polynomial containing the sum of the coefficients
     */
    public @NonNull Polynomial add(@NonNull Polynomial that) {
        int d1 = this.getDegree();
        int d2 = that.getDegree();
        int dmax = max(d1, d2);

        double[] result = new double[dmax];
        for (int i = 0; i <= dmax; i++) {
            double v1 = (i <= d1) ? this.coefs[i] : 0;
            double v2 = (i <= d2) ? that.coefs[i] : 0;

            result[i] = v1 + v2;
        }

        return new Polynomial(false, result);
    }

    /**
     * Subtracts the coefficients of that polynomial from the coefficients of this
     * polynomial and returns the resulting polynomial. Does not change this
     * polynomial.
     *
     * @param that another polynomial
     * @return a new polynomial containing the difference of the coefficients
     */
    public @NonNull Polynomial subtract(@NonNull Polynomial that) {
        int d1 = this.getDegree();
        int d2 = that.getDegree();
        int dmax = max(d1, d2);

        double[] result = new double[dmax];
        for (int i = 0; i <= dmax; i++) {
            double v1 = (i <= d1) ? this.coefs[i] : 0;
            double v2 = (i <= d2) ? that.coefs[i] : 0;

            result[i] = v1 - v2;
        }

        return new Polynomial(false, result);
    }

    @Override
    public double applyAsDouble(Double x) {
        return eval(x);
    }

    /**
     * Searches for a root in the given interval using the bisection method.
     *
     * @param func the function
     * @param min  the lower bound of the interval
     * @param max  the upper bound of the interval
     * @return the root, null if no root could be found
     */
    public static @Nullable Double bisection(final @NonNull ToDoubleFunction<Double> func, double min, double max) {
        double minValue = func.applyAsDouble(min);
        double maxValue = func.applyAsDouble(max);
        Double result = null;

        if (abs(minValue) <= EPSILON) {
            result = min;
        } else if (abs(maxValue) <= EPSILON) {
            result = max;
        } else if (minValue * maxValue <= 0) {
            double tmp1 = log(max - min);
            double tmp2 = LN10 * Polynomial.ACCURACY;
            double iters = ceil((tmp1 + tmp2) / LN2);

            for (double i = 0; i < iters; i++) {
                result = 0.5 * (min + max);
                double value = func.applyAsDouble(result);

                if (abs(value) <= EPSILON) {
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
     * Does not change this polynomial.
     *
     * @param scalar a scalar
     * @return a new polynomial
     */
    public @NonNull Polynomial divide_scalar(double scalar) {
        double[] result = new double[this.coefs.length];
        for (int i = 0; i < this.coefs.length; i++) {
            result[i] = this.coefs[i] /= scalar;
        }
        return new Polynomial(false, result);
    }

    /**
     * Evaluates the polynomial at the specified x value.
     *
     * @param x is a number that is "plugged into" the polynomial to evaluate
     *          it.
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
    private @NonNull double[] getCubicRoots() {
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

        // Note: setting epsilon too high results in roots not being found!
        if (abs(discrim) <= EPSILON) {
            discrim = 0;
        }

        if (discrim > 0) {
            double e = sqrt(discrim);
            double tmp;
            double root;

            tmp = -halfB + e;
            if (tmp >= 0) {
                root = cbrt(tmp);
            } else {
                root = -cbrt(-tmp);
            }

            tmp = -halfB - e;
            if (tmp >= 0) {
                root += cbrt(tmp);
            } else {
                root -= cbrt(-tmp);
            }
            results[numResults++] = root - offset;
        } else if (discrim < 0) {
            double distance = sqrt(-a / 3);
            double angle = Geom.atan2(sqrt(-discrim), -halfB) / 3;
            double cos = cos(angle);
            double sin = sin(angle);
            final double sqrt3 = sqrt(3);

            results[numResults++] = 2 * distance * cos - offset;
            results[numResults++] = -distance * (cos + sqrt3 * sin) - offset;
            results[numResults++] = -distance * (cos - sqrt3 * sin) - offset;
        } else {
            double tmp;

            if (halfB >= 0) {
                tmp = -cbrt(halfB);
            } else {
                tmp = cbrt(-halfB);
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
     * @return the degree = number of coefficients.
     */
    public int getDegree() {
        return this.coefs.length - 1;
    }

    /**
     * Returns the derivative of this polynomial.
     *
     * @return returns the derivative of the current polynomial.
     */
    public @NonNull Polynomial getDerivative() {
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
    private @NonNull double[] getLinearRoot() {
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
     */
    private @NonNull double[] getQuadraticRoots() {
        double a = this.coefs[2];
        double b = this.coefs[1] / a;
        double c = this.coefs[0] / a;
        return getQuadraticRoots(a, b, c);
    }

    /**
     * Returns the roots of a quadratic polynomial (degree equals two).
     * <pre>
     *     a*t^2 + b*t + c = 0
     *
     *     d = b^2 - 4 * c
     *     t1 = ( -b + sqrt(d) ) / 2
     *     t2 = ( -b - sqrt(d) ) / 2
     * </pre>
     *
     * @return the roots
     */
    public static @NonNull double[] getQuadraticRoots(double a, double b, double c) {
        double d = b * b - 4 * c;
        if (d > 0) {
            double e = sqrt(d);
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
    private @NonNull double[] getQuarticRoots() {

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

        // Note: setting epsilon too high results in roots not being found!
        if (abs(discrim) <= EPSILON) {
            discrim = 0;
        }

        if (discrim > 0) {
            double e = sqrt(discrim);
            final double t1, t2;
            t1 = 0.75 * c3 * c3 - e * e - 2 * c2;
            t2 = (4 * c3 * c2 - 8 * c1 - c3 * c3 * c3) / (4 * e);
            double plus = t1 + t2;
            double minus = t1 - t2;

            if (abs(plus) <= EPSILON) {
                plus = 0;
            }
            if (abs(minus) <= EPSILON) {
                minus = 0;
            }

            if (plus >= 0) {
                double f = sqrt(plus);
                results[numResults++] = c3 / -4 + (e + f) / 2;
                results[numResults++] = c3 / -4 + (e - f) / 2;
            }
            if (minus >= 0) {
                double f = sqrt(minus);
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

                t2 = 2 * sqrt(t2);
                double t1 = 3 * c3 * c3 / 4 - 2 * c2;
                if (t1 + t2 >= EPSILON) {
                    double d = sqrt(t1 + t2);

                    results[numResults++] = -c3 / 4 + d / 2;
                    results[numResults++] = -c3 / 4 - d / 2;
                }
                if (t1 - t2 >= EPSILON) {
                    double d = sqrt(t1 - t2);

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
    public @NonNull DoubleArrayList getRootsInInterval(double min, double max) {
        DoubleArrayList roots = new DoubleArrayList(getDegree());
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
                    roots.add(root);
                }
            }
            break;
        }
        default: {
            // get roots of derivative
            Polynomial deriv = this.getDerivative();
            DoubleArrayList droots = deriv.getRootsInInterval(min, max);

            roots = getRootsInInterval(this, droots, min, max);
            numRoots = roots.size();
            break;
        }
        }

        roots.sort();
        return roots;
    }

    /**
     * Gets roots in the given interval. Uses the bisection method for root
     * finding. Can work with a polynomial of any degree.
     *
     * @param func   the function
     * @param droots the roots of the derivative of the function in the interval [min,max].
     * @param min    the lower bound of the interval (inclusive)
     * @param max    the upper bound of the interval (inclusive)
     * @return a list of roots. The list if empty, if no roots have been found
     */
    public static @NonNull DoubleArrayList getRootsInInterval(@NonNull ToDoubleFunction<Double> func, @NonNull DoubleArrayList droots, double min, double max) {
        final DoubleArrayList roots = new DoubleArrayList(droots.size());
        int numRoots = 0;

        if (droots.size() > 0) {
            // find root on [min, droots[0]]
            Double root = bisection(func, min, droots.get(0));
            if (root != null) {
                roots.add(root);
            }

            // find root on [droots[i],droots[i+1]] for 0 <= i <= count-2
            for (int i = 0; i <= droots.size() - 2; i++) {
                root = bisection(func, droots.get(i), droots.get(i + 1));
                if (root != null) {
                    roots.add(root);
                }
            }

            // find root on [droots[count-1],xmax]
            root = bisection(func, droots.get(droots.size() - 1), max);
            if (root != null) {
                roots.add(root);
            }
        } else {
            // polynomial is monotone on [min,max], has at most one root
            Double root = bisection(func, min, max);
            if (root != null) {
                roots.add(root);
            }
        }

        return roots;
    }

    /**
     * Multiplies the coefficients of this polynomial with the coefficients of
     * that polynomial and returns the resulting polynomial. Does not change
     * this polynomial.
     *
     * @param that another polynomial
     * @return a new polynomial containing the product of the coefficients
     */
    public @NonNull Polynomial multiply(@NonNull Polynomial that) {
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
        while (i > 0 && abs(this.coefs[i]) <= EPSILON) {
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
    public @NonNull Polynomial simplify() {
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
    public @NonNull String toString() {
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
     * @param a      the array
     * @return array of the specified length
     */
    static @NonNull double[] trim(int length, @NonNull double[] a) {
        if (length == a.length) {
            return a;
        }
        double[] finalResults = new double[length];
        System.arraycopy(a, 0, finalResults, 0, length);
        return finalResults;
    }

    /**
     * Estimates the integral of the given function in the given interval using the
     * trapezoidal rule.
     * <p>
     * trapezoid Based on trapzd in "Numerical Recipes in C", page 137
     *
     * @param func the function
     * @param min  the lower bound of the interval
     * @param max  the upper bound of the interval
     * @param n    the number of trapezoids
     * @return the area of the function
     */
    public static double trapezoid(@NonNull ToDoubleFunction<Double> func, double min, double max, int n) {

        double range = max - min;
        double _s = 0;
        if (n == 1) {
            double minValue = func.applyAsDouble(min);
            double maxValue = func.applyAsDouble(max);
            _s = 0.5 * range * (minValue + maxValue);
        } else {
            double it = 1 << (n - 2);
            double delta = range / it;
            double x = min + 0.5 * delta;
            double sum = 0;

            for (double i = 0; i < it; i++) {
                sum += func.applyAsDouble(x);
                x += delta;
            }
            _s = 0.5 * (_s + range * sum / it);
        }

        return _s;
    }

    /**
     * Interpolate. Computes y and dy for a given x.
     *
     * @param xs
     * @param ys
     * @param n
     * @param offset
     * @param x
     * @return a tuple: y, dy
     */
    private static @NonNull Point2D interpolate(double[] xs, double[] ys, int n, int offset, double x) {

        double y;
        double dy = 0;
        double[] c = new double[n];
        double[] d = new double[n];
        int ns = 0;
        Point2D result;

        double diff = abs(x - xs[offset]);
        for (int i = 0; i < n; i++) {
            double dift = abs(x - xs[offset + i]);

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
                    result = new Point2D(0, 0); //{ y: 0, dy: 0};
                    break;
                }

                den = w / den;
                d[i] = hp * den;
                c[i] = ho * den;
            }
            dy = (2 * (ns + 1) < (n - m)) ? c[ns + 1] : d[ns--];
            y += dy;
        }

        return new Point2D(y, dy);// { y: y, dy: dy };
    }

    /**
     * Estimates the arc length of the polynomial in the interval [min,max].
     * <p>
     * Computes {@literal  ∫_min_max sqrt(1 + (f'(x))^2 ) }
     *
     * @param min the lower bound of the interval
     * @param max the upper bound of the interval
     * @return the estimated arc length
     */
    public double arcLength(double min, double max) {
        final Polynomial dfdx = getDerivative();
        return simpson(x -> {
            double y = dfdx.eval(x);
            return sqrt(1 + y * y);
        }, min, max);
    }

    /**
     * Estimates the integral of the polynomial in the given interval using
     * Simpsons's rule.
     * <p>
     * simpson Based on trapzd in "Numerical Recipes in C", page 139
     *
     * @param func the function
     * @param min  the lower bound of the interval
     * @param max  the upper bound of the interval
     * @return the area under the curve
     */
    public static double simpson(@NonNull ToDoubleFunction<Double> func, double min, double max) {

        double range = max - min;
        double st = 0.5 * range * (func.applyAsDouble(min) + func.applyAsDouble(max));
        double t = st;
        double s = 4.0 * st / 3.0;
        double os = s;
        double ost = st;

        int it = 1;
        for (int n = 2; n <= 20; n++) {
            double delta = range / it;
            double x = min + 0.5 * delta;
            double sum = 0;

            for (double i = 1; i <= it; i++) {
                sum += func.applyAsDouble(x);
                x += delta;
            }

            t = 0.5 * (t + range * sum / it);
            st = t;
            s = (4.0 * st - ost) / 3.0;

            if (abs(s - os) < EPSILON * abs(os)) {
                break;
            }

            os = s;
            ost = st;
            it <<= 1;
        }

        return s;
    }

}
