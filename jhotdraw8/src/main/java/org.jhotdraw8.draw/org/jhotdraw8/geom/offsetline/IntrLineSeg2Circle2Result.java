package org.jhotdraw8.geom.offsetline;

/**
 * This is a struct.
 */
public class IntrLineSeg2Circle2Result {
    /**
     * Number of interescts found (0, 1, or 2).
     */
    int numIntersects;
    /**
     * Parametric value for first intersect (if numIntersects > 0) otherwise undefined.
     */
    double t0;
    /**
     * Parametric value for second intersect (if numintersects > 1) otherwise undefined.
     */
    double t1;
}
