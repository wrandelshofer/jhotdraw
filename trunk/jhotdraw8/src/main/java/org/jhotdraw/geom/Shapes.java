/*
 * @(#)Shapes.java
 *
 * Copyright (c) 2007 The authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the 
 * accompanying license terms.
 */
package org.jhotdraw.geom;

import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.util.List;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.transform.MatrixType;

/**
 * Shapes.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Shapes {

    /**
     * Creates a new instance.
     */
    private Shapes() {
    }

    /**
     * Returns true, if the outline of this bezier path contains the specified
     * point.
     *
     * @param shape The shape.
     * @param p The point to be tested.
     * @param tolerance The tolerance for the test.
     * @return true if contained within tolerance
     */
    public static boolean outlineContains(Shape shape, Point2D.Double p, double tolerance) {
        double[] coords = new double[6];
        double prevX = 0, prevY = 0;
        double moveX = 0, moveY = 0;
        for (PathIterator i = new FlatteningPathIterator(shape.getPathIterator(new AffineTransform(), tolerance), tolerance); !i.isDone(); i.next()) {
            switch (i.currentSegment(coords)) {
            case PathIterator.SEG_CLOSE:
                if (Geom.lineContainsPoint(
                        prevX, prevY, moveX, moveY,
                        p.x, p.y, tolerance)) {
                    return true;
                }
                break;
            case PathIterator.SEG_CUBICTO:
                break;
            case PathIterator.SEG_LINETO:
                if (Geom.lineContainsPoint(
                        prevX, prevY, coords[0], coords[1],
                        p.x, p.y, tolerance)) {
                    return true;
                }
                break;
            case PathIterator.SEG_MOVETO:
                moveX = coords[0];
                moveY = coords[1];
                break;
            case PathIterator.SEG_QUADTO:
                break;
            default:
                break;
            }
            prevX = coords[0];
            prevY = coords[1];
        }
        return false;
    }

    /**
     * Converts a Java Path iterator to a JavaFX shape.
     *
     * @param fxT A JavaFX Transform.
     * @return An AWT Transform.
     */
    public static AffineTransform awtTransformFromFX(javafx.scene.transform.Transform fxT) {
        return new AffineTransform(fxT.toArray(MatrixType.MT_2D_2x3));
    }
    /**
     * Converts a Java AWT Shape iterator to a JavaFX Shape.
     *
     * @param shape AWT Shape
     * @param at Optional transformation which is applied to the shape
     * @return JavaFX Shape
     */
    public static javafx.scene.shape.Path fxShapeFromAWT(Shape shape, AffineTransform at) {
        return fxShapeFromAWT(shape.getPathIterator(at));
    }
    /**
     * Converts a Java Path iterator to a JavaFX shape.
     *
     * @param iter AWT Path Iterator
     * @return JavaFX Shape
     */
    static javafx.scene.shape.Path fxShapeFromAWT(PathIterator iter) {
        javafx.scene.shape.Path fxpath = new javafx.scene.shape.Path();

        switch (iter.getWindingRule()) {
        case PathIterator.WIND_EVEN_ODD:
            fxpath.setFillRule(javafx.scene.shape.FillRule.EVEN_ODD);
            break;
        case PathIterator.WIND_NON_ZERO:
            fxpath.setFillRule(javafx.scene.shape.FillRule.NON_ZERO);
            break;
        default:
            throw new IllegalArgumentException("illegal winding rule " + iter.getWindingRule());
        }

        List<PathElement> fxelem=fxpath.getElements();
        double[] coords = new double[6];
        for (; !iter.isDone(); iter.next()) {
            switch (iter.currentSegment(coords)) {
            case PathIterator.SEG_CLOSE:
                fxelem.add(new ClosePath());
                break;
            case PathIterator.SEG_CUBICTO:
                fxelem.add(new CubicCurveTo(coords[0],coords[1],coords[2],coords[3],coords[4],coords[5]));
                break;
            case PathIterator.SEG_LINETO:
                fxelem.add(new LineTo(coords[0],coords[1]));
                break;
            case PathIterator.SEG_MOVETO:
                fxelem.add(new MoveTo(coords[0],coords[1]));
                break;
            case PathIterator.SEG_QUADTO:
                fxelem.add(new QuadCurveTo(coords[0],coords[1],coords[2],coords[3]));
                break;
            }
        }
        return fxpath;
    }

}
