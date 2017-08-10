/* @(#)BezierNode.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.geometry.Point2D;
import javafx.scene.transform.Transform;

/**
 * Represents a node of a bezier path. A node has up to three control points:
 * <ul>
 * <li>C0 is the point through which the curve passes.</li>
 * <li>C1 controls the tangent of the curve going towards C0.</li>
 * <li>C2 controls the tangent of the curve going away from C0.</li>
 * </ul>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierNode {

    /**
     * Constant for having control point C0 in effect
     */
    public static final int C0_MASK = 1;
    /**
     * Constant for having control point C1 in effect (in addition to C0). C1
     * controls the curve going towards C0.
     *
     */
    public static final int C1_MASK = 2;
    /**
     * Constant for having control points C0 and C1 in effect.
     */
    public static final int C0C1_MASK = C0_MASK | C1_MASK;
    /**
     * Constant for having control point C2 in effect (in addition to C0). C2
     * controls the curve going away from C0.
     */
    public static final int C2_MASK = 4;
    /**
     * Constant for having control points C1 and C2 in effect.
     */
    public static final int C1C2_MASK = C1_MASK | C2_MASK;
    /**
     * Constant for having control points C0, C1 and C2 in effect.
     */
    public static final int C0C1C2_MASK = C0_MASK | C1_MASK | C2_MASK;
    /**
     * Constant for having control points C0 and C2 in effect.
     */
    public static final int C0C2_MASK = C0_MASK | C2_MASK;
    /**
     * Constant for having control point C0 in effect, but we are only moving to
     * this bezier node.
     */
    public static final int MOVE_MASK = 8;

    /**
     * This is a hint for editing tools. If this is set to true, the editing
     * tools shall keep all control points on the same line.
     */
    private final boolean colinear;
    /**
     * This is a hint for editing tools. If this is set to true, the editing
     * tools shall keep C2 at the same distance from C0 as C1.
     */
    private final boolean equidistant;
    /**
     * This mask is used to describe which control points in addition to C0 are
     * in effect.
     */
    private final int mask;

    /**
     * Holds the y-coordinates of the control points C0, C1, C2.
     */
    private final double x0;
    /**
     * Holds the y-coordinates of the control points C0, C1, C2.
     */
    private final double x1;
    /**
     * Holds the y-coordinates of the control points C0, C1, C2.
     */
    private final double x2;
    /**
     * Holds the y-coordinates of the control points C0, C1, C2.
     */
    private final double y0;
    /**
     * Holds the y-coordinates of the control points C0, C1, C2.
     */
    private final double y1;
    /**
     * Holds the y-coordinates of the control points C0, C1, C2.
     */
    private final double y2;

    public BezierNode(Point2D c0) {
        this.mask = C0_MASK;
        this.colinear = false;
        this.equidistant = false;
        this.x0 = c0.getX();
        this.x1 = c0.getX();
        this.x2 = c0.getX();
        this.y0 = c0.getY();
        this.y1 = c0.getY();
        this.y2 = c0.getY();
    }

    public BezierNode(int mask, boolean equidistant, boolean colinear, Point2D c0, Point2D c1, Point2D c2) {
        this.mask = mask;
        this.colinear = colinear;
        this.equidistant = equidistant;
        this.x0 = c0.getX();
        this.x1 = c1.getX();
        this.x2 = c2.getX();
        this.y0 = c0.getY();
        this.y1 = c1.getY();
        this.y2 = c2.getY();
    }

    public BezierNode(int mask, boolean equidistant, boolean colinear, double x0, double y0, double x1, double y1, double x2, double y2) {
        this.mask = mask;
        this.colinear = colinear;
        this.equidistant = equidistant;
        this.x0 = x0;
        this.x1 = x1;
        this.x2 = x2;
        this.y0 = y0;
        this.y1 = y1;
        this.y2 = y2;
        if (equidistant) {
            throw new InternalError("equidistant");
        }
    }

    public boolean computeIsColinear() {
        if ((mask & MOVE_MASK) != 0 || (mask & C1C2_MASK) != C1C2_MASK) {
            return false;
        }
        Point2D c0 = getC0();
        Point2D c2 = getC2();
        Point2D c1 = getC1();
        final Point2D t1 = c1.subtract(c0);
        final Point2D t2 = c2.subtract(c0);
        return 1 - Math.abs(t1.normalize().dotProduct(t2.normalize())) < 1e-4;
    }

    public boolean computeIsEquidistant() {
        if ((mask & MOVE_MASK) != 0 || (mask & C1C2_MASK) != C1C2_MASK) {
            return false;
        }
        Point2D c0 = getC0();
        Point2D c2 = getC2();
        Point2D c1 = getC1();
        final Point2D t1 = c1.subtract(c0);
        final Point2D t2 = c2.subtract(c0);
        return Math.abs(t1.magnitude() - t2.magnitude()) < 1e-4;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final BezierNode other = (BezierNode) obj;
        if (this.mask != other.mask) {
            return false;
        }
        if (this.colinear != other.colinear) {
            return false;
        }
        if (this.equidistant != other.equidistant) {
            return false;
        }
        if (Double.doubleToLongBits(this.x0) != Double.doubleToLongBits(other.x0)) {
            return false;
        }
        if (Double.doubleToLongBits(this.x1) != Double.doubleToLongBits(other.x1)) {
            return false;
        }
        if (Double.doubleToLongBits(this.x2) != Double.doubleToLongBits(other.x2)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y0) != Double.doubleToLongBits(other.y0)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y1) != Double.doubleToLongBits(other.y1)) {
            return false;
        }
        if (Double.doubleToLongBits(this.y2) != Double.doubleToLongBits(other.y2)) {
            return false;
        }
        return true;
    }

    public Point2D getC(int mask) {
        switch (mask) {
            case C0_MASK:
                return getC0();
            case C1_MASK:
                return getC1();
            case C2_MASK:
                return getC2();
            default:
                throw new IllegalArgumentException("illegal mask:" + mask);
        }
    }

    public Point2D getC0() {
        return new Point2D(x0, y0);
    }

    public Point2D getC1() {
        return new Point2D(x1, y1);
    }

    public Point2D getC2() {
        return new Point2D(x2, y2);
    }

    /**
     * @return the mask
     */
    public int getMask() {
        return mask;
    }

    public double getMaxX() {
        double maxX = x0;
        if ((mask & MOVE_MASK) == 0) {
            if ((mask & C1_MASK) != 0 && x1 > maxX) {
                maxX = x1;
            }
            if ((mask & C2_MASK) != 0 && x2 > maxX) {
                maxX = x2;
            }
        }
        return maxX;
    }

    public double getMaxY() {
        double maxY = y0;
        if ((mask & MOVE_MASK) == 0) {
            if ((mask & C1_MASK) != 0 && y1 > maxY) {
                maxY = y1;
            }
            if ((mask & C2_MASK) != 0 && y2 > maxY) {
                maxY = y2;
            }
        }
        return maxY;
    }

    public double getMinX() {
        double minX = x0;
        if ((mask & MOVE_MASK) == 0) {
            if ((mask & C1_MASK) != 0 && x1 < minX) {
                minX = x1;
            }
            if ((mask & C2_MASK) != 0 && x2 < minX) {
                minX = x2;
            }
        }
        return minX;
    }

    public double getMinY() {
        double minY = y0;
        if ((mask & MOVE_MASK) == 0) {
            if ((mask & C1_MASK) != 0 && y1 < minY) {
                minY = y1;
            }
            if ((mask & C2_MASK) != 0 && y2 < minY) {
                minY = y2;
            }
        }
        return minY;
    }

    public double getX(int mask) {
        switch (mask) {
            case C0_MASK:
                return getX0();
            case C1_MASK:
                return getX1();
            case C2_MASK:
                return getX2();
            default:
                throw new IllegalArgumentException("illegal mask:" + mask);
        }
    }

    /**
     * @return the x0
     */
    public double getX0() {
        return x0;
    }

    /**
     * @return the x1
     */
    public double getX1() {
        return x1;
    }

    /**
     * @return the x2
     */
    public double getX2() {
        return x2;
    }

    public double getY(int mask) {
        switch (mask) {
            case C0_MASK:
                return getY0();
            case C1_MASK:
                return getY1();
            case C2_MASK:
                return getY2();
            default:
                throw new IllegalArgumentException("illegal mask:" + mask);
        }
    }

    /**
     * @return the y0
     */
    public double getY0() {
        return y0;
    }

    /**
     * @return the y1
     */
    public double getY1() {
        return y1;
    }

    /**
     * @return the y2
     */
    public double getY2() {
        return y2;
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 59 * hash + this.mask;
        hash = 59 * hash + (this.colinear ? 1 : 0);
        hash = 59 * hash + (this.equidistant ? 1 : 0);
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.x0) ^ (Double.doubleToLongBits(this.x0) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.x1) ^ (Double.doubleToLongBits(this.x1) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.x2) ^ (Double.doubleToLongBits(this.x2) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.y0) ^ (Double.doubleToLongBits(this.y0) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.y1) ^ (Double.doubleToLongBits(this.y1) >>> 32));
        hash = 59 * hash + (int) (Double.doubleToLongBits(this.y2) ^ (Double.doubleToLongBits(this.y2) >>> 32));
        return hash;
    }

    public boolean isC(int mask) {
        return (this.mask & mask) == mask;
    }

    public boolean isC1() {
        return (mask & C1_MASK) == C1_MASK;
    }

    public boolean isC2() {
        return (mask & C2_MASK) == C2_MASK;
    }

    /**
     * @return the colinear
     */
    public boolean isColinear() {
        return colinear;
    }

    public boolean isControlPoint(int mask) {
        return (mask & mask) == mask;
    }

    /**
     * @return the equidistant
     */
    public boolean isEquidistant() {
        return equidistant;
    }

    public boolean isMoveTo() {
        return (mask & MOVE_MASK) == MOVE_MASK;
    }

    /**
     * @param mask specifies which control point must be set
     * @param c the c to set
     * @return a new instance
     */
    public BezierNode setC(int mask, Point2D c) {
        double x = c.getX(), y = c.getY();
        double nx0, ny0, nx1, ny1, nx2, ny2;
        if ((mask & C0_MASK) != 0) {
            nx0 = x;
            ny0 = y;
        } else {
            nx0 = x0;
            ny0 = y0;
        }
        if ((mask & C1_MASK) != 0) {
            nx1 = x;
            ny1 = y;
        } else {
            nx1 = x1;
            ny1 = y1;
        }
        if ((mask & C2_MASK) != 0) {
            nx2 = x;
            ny2 = y;
        } else {
            nx2 = x2;
            ny2 = y2;
        }

        return new BezierNode(this.mask, equidistant, colinear, nx0, ny0, nx1, ny1, nx2, ny2);
    }

    /**
     * @param c0 the c0 to set
     * @return a new instance
     */
    public BezierNode setC0(Point2D c0) {
        return new BezierNode(mask, equidistant, colinear, c0.getX(), c0.getY(), x1, y1, x2, y2);
    }

    /**
     * @param x0 the x0 to set
     * @param y0 the y0 to set
     * @return a new instance
     */
    public BezierNode setC0(double x0, double y0) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param c0 the c0 to set
     * @return a new instance
     */
    public BezierNode setC0AndTranslateC1C2(Point2D c0) {
        double x = c0.getX();
        double y = c0.getY();
        return new BezierNode(mask, equidistant, colinear, x, y, x1 + x - x0, y1 + y - y0, x2 + x - x0, y2 + y - y0);
    }

    /**
     * @param c1 the c0 to set
     * @return a new instance
     */
    public BezierNode setC1(Point2D c1) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, c1.getX(), c1.getY(), x2, y2);
    }

    /**
     * @param x1 the x1 to set
     * @param y1 the y1to set
     * @return a new instance
     */
    public BezierNode setC1(double x1, double y1) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param c2 the c0 to set
     * @return a new instance
     */
    public BezierNode setC2(Point2D c2) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, c2.getX(), c2.getY());
    }

    /**
     * @param x2 the x2 to set
     * @param y2 the y2 to set
     * @return a new instance
     */
    public BezierNode setC2(double x2, double y2) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param colinear the colinear to set
     * @return a new instance
     */
    public BezierNode setColinear(boolean colinear) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param equidistant the equidistant to set
     * @return a new instance
     */
    public BezierNode setEquidistant(boolean equidistant) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param mask the mask to set
     * @return a new instance
     */
    public BezierNode setMask(int mask) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param x0 the x0 to set
     * @return a new instance
     */
    public BezierNode setX0(double x0) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param x1 the x1 to set
     * @return a new instance
     */
    public BezierNode setX1(double x1) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param x2 the x2 to set
     * @return a new instance
     */
    public BezierNode setX2(double x2) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param y0 the y0 to set
     * @return a new instance
     */
    public BezierNode setY0(double y0) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param y1 the y1 to set
     * @return a new instance
     */
    public BezierNode setY1(double y1) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    /**
     * @param y2 the y2 to set
     * @return a new instance
     */
    public BezierNode setY2(double y2) {
        return new BezierNode(mask, equidistant, colinear, x0, y0, x1, y1, x2, y2);
    }

    @Override
    public String toString() {
        return "BezierNode{" + "colinear=" + colinear + ", equidistant=" + equidistant + ", mask=" + mask + ", x0=" + x0 + ", x1=" + x1 + ", x2=" + x2 + ", y0=" + y0 + ", y1=" + y1 + ", y2=" + y2 + '}';
    }

    public BezierNode transform(Transform transform) {
        Point2D p0 = transform.transform(x0, y0);
        Point2D p1 = transform.transform(x1, y1);
        Point2D p2 = transform.transform(x2, y2);
        return new BezierNode(mask, equidistant, colinear, p0.getX(), p0.getY(), p1.getX(), p1.getY(), p2.getX(), p2.getY());
    }

}
