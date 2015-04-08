/* @(#)DefaultFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw.io;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.shape.RectangleFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.TextHolderFigure;
import org.jhotdraw.draw.shape.CircleFigure;
import org.jhotdraw.draw.shape.EllipseFigure;
import org.jhotdraw.draw.shape.LineFigure;
import org.jhotdraw.draw.shape.TextFigure;
import org.jhotdraw.text.DefaultConverter;
import org.jhotdraw.text.Point2DConverter;
import org.jhotdraw.text.RealNumberConverter;
import org.jhotdraw.text.Rectangle2DConverter;

/**
 * DefaultFigureFactory.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultFigureFactory extends SimpleFigureFactory {

    public DefaultFigureFactory() {
        addFigure("Group", GroupFigure.class);
        addFigure("Drawing", SimpleDrawing.class);
        addFigure("Rectangle", RectangleFigure.class);
        addFigure("Text", TextFigure.class);
        addFigure("Circle", CircleFigure.class);
        addFigure("Line", LineFigure.class);
        addFigure("Ellipse", EllipseFigure.class);
        addFigureKeysAndNames(RectangleFigure.class, RectangleFigure.getFigureKeys().values());
        addFigureKeysAndNames(GroupFigure.class, GroupFigure.getFigureKeys().values());
        addFigureKeysAndNames(SimpleDrawing.class, SimpleDrawing.getFigureKeys().values());
        addFigureKeysAndNames(TextFigure.class, TextFigure.getFigureKeys().values());
        addFigureKeysAndNames(CircleFigure.class, CircleFigure.getFigureKeys().values());
        addFigureKeysAndNames(LineFigure.class, LineFigure.getFigureKeys().values());
        addFigureKeysAndNames(EllipseFigure.class, EllipseFigure.getFigureKeys().values());
        addConverter(Rectangle2D.class, new Rectangle2DConverter());
        addConverter(String.class, new DefaultConverter());
        addConverter(Point2D.class, new Point2DConverter());
        addConverter(Double.class, new RealNumberConverter());
    }
}
