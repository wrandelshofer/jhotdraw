/* @(#)DefaultFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.net.URL;
import java.util.List;
import java.util.Set;
import javafx.collections.ObservableList;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.shape.RectangleFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleLabelFigure;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.TextHolderFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.shape.EllipseFigure;
import org.jhotdraw.draw.shape.LineFigure;
import org.jhotdraw.draw.shape.TextFigure;
import org.jhotdraw.text.DefaultConnectorConverter;
import org.jhotdraw.text.DefaultConverter;
import org.jhotdraw.text.Point2DConverter;
import org.jhotdraw.text.NumberConverter;
import org.jhotdraw.text.CssObservableWordListConverter;
import org.jhotdraw.text.Rectangle2DConverter;
import org.jhotdraw.text.URLConverter;
import org.jhotdraw.text.CssWordListConverter;

/**
 * DefaultFigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultFigureFactory extends SimpleFigureFactory {

    public DefaultFigureFactory(IdFactory idFactory) {
        addFigureKeysAndNames("Layer", SimpleLayer.class, Figure.getDeclaredAndInheritedKeys(SimpleLayer.class));
        addFigureKeysAndNames("Rectangle", RectangleFigure.class, Figure.getDeclaredAndInheritedKeys(RectangleFigure.class));
        addFigureKeysAndNames("Group", GroupFigure.class, Figure.getDeclaredAndInheritedKeys(GroupFigure.class));
        addFigureKeysAndNames("Drawing", SimpleDrawing.class, Figure.getDeclaredAndInheritedKeys(SimpleDrawing.class));

        Set<Key<?>> keys = Figure.getDeclaredAndInheritedKeys(TextFigure.class);
        keys.remove(TextFigure.TEXT);
        addNodeListKey(TextFigure.class, "", TextFigure.TEXT);
        addFigureKeysAndNames("Text", TextFigure.class, keys);
        keys = Figure.getDeclaredAndInheritedKeys(SimpleLabelFigure.class);
        keys.remove(TextFigure.TEXT);
        addNodeListKey(SimpleLabelFigure.class, "", TextFigure.TEXT);
        addFigureKeysAndNames("Label", SimpleLabelFigure.class, keys);
        
        addFigureKeysAndNames("Line", LineFigure.class, Figure.getDeclaredAndInheritedKeys(LineFigure.class));
        addFigureKeysAndNames("Ellipse", EllipseFigure.class, Figure.getDeclaredAndInheritedKeys(EllipseFigure.class));
        addFigureKeysAndNames("LineConnection", LineConnectionFigure.class, Figure.getDeclaredAndInheritedKeys(LineConnectionFigure.class));
        addConverter(Rectangle2D.class, new Rectangle2DConverter());
        addConverter(String.class, new DefaultConverter());
        addConverter(Point2D.class, new Point2DConverter());
        addConverter(Double.class, new NumberConverter());
        addConverter(URL.class, new URLConverter());
        addConverter(Connector.class, new DefaultConnectorConverter());
        addConverter(Figure.STYLE_CLASS.getFullValueType(), new CssObservableWordListConverter());
    }
}
