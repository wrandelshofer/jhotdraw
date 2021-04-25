/*
 * @(#)PointAndTangentUtilTest.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.geom;

import org.junit.jupiter.api.Test;

import java.awt.geom.CubicCurve2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.QuadCurve2D;
import java.awt.geom.Rectangle2D;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PointAndTangentBuilderTest {
    @Test
    public void testPointAndTangentAtStraightLine() {
        Line2D.Double line = new Line2D.Double(0, 0, 1, 0);
        PointAndTangentBuilder instance = new PointAndTangentBuilder(line.getPathIterator(null), 0.125);

        double actualLength = instance.getLength();
        assertEquals(1.0, actualLength);

        PointAndTangent actualStartPAndT = instance.getPointAndTangentAt(0);
        assertEquals(new Point2D.Double(0, 0), actualStartPAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(1, 0), actualStartPAndT.getTangent(Point2D.Double::new));

        PointAndTangent actualEndPAndT = instance.getPointAndTangentAt(1.0);
        assertEquals(new Point2D.Double(1, 0), actualEndPAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(1, 0), actualEndPAndT.getTangent(Point2D.Double::new));

        PointAndTangent actualMiddlePAndT = instance.getPointAndTangentAt(0.5);
        assertEquals(new Point2D.Double(0.5, 0), actualMiddlePAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(1, 0), actualMiddlePAndT.getTangent(Point2D.Double::new));

    }

    @Test
    public void testPointAndTangentAtQuadCurve() {
        QuadCurve2D.Double quadCurve = new QuadCurve2D.Double(0, 0, 1, 0, 1, 1);
        PointAndTangentBuilder instance = new PointAndTangentBuilder(quadCurve.getPathIterator(null), 0.125);
        double actualLength = instance.getLength();
        assertEquals(1.612752463338847, actualLength);

        PointAndTangent actualStartPAndT = instance.getPointAndTangentAt(0);
        assertEquals(new Point2D.Double(0, 0), actualStartPAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(1, 0), actualStartPAndT.getTangent(Point2D.Double::new));

        PointAndTangent actualEndPAndT = instance.getPointAndTangentAt(1.0);
        assertEquals(new Point2D.Double(1, 1), actualEndPAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(0, 1), actualEndPAndT.getTangent(Point2D.Double::new));

        PointAndTangent actualMiddlePAndT = instance.getPointAndTangentAt(0.5);
        assertEquals(new Point2D.Double(0.75, 0.25), actualMiddlePAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(0.5, 0.5), actualMiddlePAndT.getTangent(Point2D.Double::new));

    }

    @Test
    public void testPointAndTangentAtCubicCurve() {
        CubicCurve2D.Double cubicCurve = new CubicCurve2D.Double(0, 0, 1, 0, 1, 0, 1, 1);
        PointAndTangentBuilder instance = new PointAndTangentBuilder(cubicCurve.getPathIterator(null), 0.125);
        double actualLength = instance.getLength();
        assertEquals(1.7677669529663689, actualLength);

        PointAndTangent actualStartPAndT = instance.getPointAndTangentAt(0);
        assertEquals(new Point2D.Double(0, 0), actualStartPAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(1, 0), actualStartPAndT.getTangent(Point2D.Double::new));

        PointAndTangent actualEndPAndT = instance.getPointAndTangentAt(1.0);
        assertEquals(new Point2D.Double(1, 1), actualEndPAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(0, 1), actualEndPAndT.getTangent(Point2D.Double::new));

        PointAndTangent actualMiddlePAndT = instance.getPointAndTangentAt(0.5);
        assertEquals(new Point2D.Double(0.875, 0.125), actualMiddlePAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(0.25, 0.25), actualMiddlePAndT.getTangent(Point2D.Double::new));

    }

    @Test
    public void testPointAndTangentAtSquare() {
        Rectangle2D.Double rectangle = new Rectangle2D.Double(0, 0, 1, 1);
        PointAndTangentBuilder instance = new PointAndTangentBuilder(rectangle, 0.125);
        double actualLength = instance.getLength();
        assertEquals(4.0, actualLength);

        PointAndTangent actualStartPAndT = instance.getPointAndTangentAt(0);
        assertEquals(new Point2D.Double(0, 0), actualStartPAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(1, 0), actualStartPAndT.getTangent(Point2D.Double::new));

        PointAndTangent actualEndPAndT = instance.getPointAndTangentAt(1.0);
        assertEquals(new Point2D.Double(0, 0), actualEndPAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(0, -1), actualEndPAndT.getTangent(Point2D.Double::new));

        PointAndTangent actualMiddlePAndT = instance.getPointAndTangentAt(0.5);
        assertEquals(new Point2D.Double(1, 1), actualMiddlePAndT.getPoint(Point2D.Double::new));
        assertEquals(new Point2D.Double(-1, 0), actualMiddlePAndT.getTangent(Point2D.Double::new));

    }

}