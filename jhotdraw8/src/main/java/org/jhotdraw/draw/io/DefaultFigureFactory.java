/* @(#)DefaultFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw.draw.io;

import java.net.URL;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.shape.RectangleFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.TextHolderFigure;
import org.jhotdraw.draw.shape.CircleFigure;
import org.jhotdraw.draw.shape.EllipseFigure;
import org.jhotdraw.draw.shape.LineFigure;
import org.jhotdraw.draw.shape.TextFigure;
import org.jhotdraw.text.DefaultConverter;
import org.jhotdraw.text.Point2DConverter;
import org.jhotdraw.text.NumberConverter;
import org.jhotdraw.text.Rectangle2DConverter;
import org.jhotdraw.text.URLConverter;

/**
 * DefaultFigureFactory.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultFigureFactory extends SimpleFigureFactory {

    public DefaultFigureFactory() {
        addFigure("Layer", SimpleLayer.class);
        addFigure("Group", GroupFigure.class);
        addFigure("Drawing", SimpleDrawing.class);
        addFigure("Rectangle", RectangleFigure.class);
        addFigure("Text", TextFigure.class);
        addFigure("Circle", CircleFigure.class);
        addFigure("Line", LineFigure.class);
        addFigure("Ellipse", EllipseFigure.class);
        addFigureKeysAndNames(RectangleFigure.class, Figure.getDeclaredAndInheritedKeys(RectangleFigure.class));
        addFigureKeysAndNames(GroupFigure.class, Figure.getDeclaredAndInheritedKeys(GroupFigure.class));
        addFigureKeysAndNames(SimpleDrawing.class, Figure.getDeclaredAndInheritedKeys(SimpleDrawing.class));
        addFigureKeysAndNames(TextFigure.class, Figure.getDeclaredAndInheritedKeys(TextFigure.class));
        addFigureKeysAndNames(CircleFigure.class, Figure.getDeclaredAndInheritedKeys(CircleFigure.class));
        addFigureKeysAndNames(LineFigure.class, Figure.getDeclaredAndInheritedKeys(LineFigure.class));
        addFigureKeysAndNames(EllipseFigure.class, Figure.getDeclaredAndInheritedKeys(EllipseFigure.class));
        addConverter(Rectangle2D.class, new Rectangle2DConverter());
        addConverter(String.class, new DefaultConverter());
        addConverter(Point2D.class, new Point2DConverter());
        addConverter(Double.class, new NumberConverter());
        addConverter(URL.class, new URLConverter());
    }
}
