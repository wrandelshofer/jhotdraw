/* @(#)DefaultFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may not use, copy or modify this file, except in compliance with the
 * accompanying license terms.
 */

package org.jhotdraw.draw.io;

import java.util.Map;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.FigureKeys;
import org.jhotdraw.draw.RectangleFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.TextFigure;
import org.jhotdraw.text.DefaultConverter;
import org.jhotdraw.text.Point2DConverter;
import org.jhotdraw.text.Rectangle2DConverter;

/**
 * DefaultFigureFactory.
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultFigureFactory extends SimpleFigureFactory {

    public DefaultFigureFactory() {
        addFigure("Drawing", SimpleDrawing.class);
        addFigure("Rectangle", RectangleFigure.class);
        addFigure("Text", TextFigure.class);
        addFigureKeys(RectangleFigure.class, RectangleFigure.getFigureKeys().values());
        addFigureKeys(TextFigure.class, TextFigure.getFigureKeys().values());
        addKeys(RectangleFigure.getFigureKeys());
        addKeys(TextFigure.getFigureKeys());
        addConverter(Rectangle2D.class, new Rectangle2DConverter());
        addConverter(String.class, new DefaultConverter());
        addConverter(Point2D.class, new Point2DConverter());
    }
}
