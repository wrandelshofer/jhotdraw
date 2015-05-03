/* @(#)NullConstrainer.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */
package org.jhotdraw.draw.constrain;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;

/**
 * NullConstrainer does not constrain anything.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class NullConstrainer implements Constrainer {

    @Override
    public Point2D translatePoint(Point2D p, Point2D dir) {
        return p.add(dir);
    }

    @Override
    public Rectangle2D translateRectangle(Rectangle2D r, Point2D dir) {
        return new Rectangle2D(r.getMinX()+dir.getX(),r.getMinY()+dir.getY(),r.getWidth(),r.getHeight());
    }

    @Override
    public double translateAngle(double angle, double dir) {
        return angle+dir;
    }

}
