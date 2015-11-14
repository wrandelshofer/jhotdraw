/* @(#)DefaultFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.net.URI;
import java.net.URL;
import java.util.Set;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.paint.Paint;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleLabelFigure;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.shape.EllipseFigure;
import org.jhotdraw.draw.shape.LineFigure;
import org.jhotdraw.draw.shape.TextFigure;
import org.jhotdraw.draw.shape.RectangleFigure;
import org.jhotdraw.text.DefaultConnectorConverter;
import org.jhotdraw.text.DefaultConverter;
import org.jhotdraw.text.Point2DConverter;
import org.jhotdraw.text.NumberConverter;
import org.jhotdraw.text.CssObservableWordListConverter;
import org.jhotdraw.text.Rectangle2DConverter;
import org.jhotdraw.text.UrlConverter;
import org.jhotdraw.text.UriConverter;
import org.jhotdraw.text.XmlBooleanConverter;
import org.jhotdraw.text.XmlPaintConverter;

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

        {
            Set<Key<?>> keys = Figure.getDeclaredAndInheritedKeys(SimpleDrawing.class);
            keys.remove(Drawing.USER_AGENT_STYLESHEETS);
            keys.remove(Drawing.AUTHOR_STYLESHEETS);
            keys.remove(Drawing.INLINE_STYLESHEETS);
            keys.remove(Drawing.DOCUMENT_HOME);
            addNodeListKey(TextFigure.class, "", TextFigure.TEXT);
            addFigureKeysAndNames("Drawing", SimpleDrawing.class, keys);
        }

        {
            Set<Key<?>> keys = Figure.getDeclaredAndInheritedKeys(TextFigure.class);
            keys.remove(TextFigure.TEXT);
            addNodeListKey(TextFigure.class, "", TextFigure.TEXT);
            addFigureKeysAndNames("Text", TextFigure.class, keys);
        }
        {
            Set<Key<?>> keys = Figure.getDeclaredAndInheritedKeys(SimpleLabelFigure.class);
            keys.remove(TextFigure.TEXT);
            addNodeListKey(SimpleLabelFigure.class, "", TextFigure.TEXT);
            addFigureKeysAndNames("Label", SimpleLabelFigure.class, keys);
        }

        addFigureKeysAndNames("Line", LineFigure.class, Figure.getDeclaredAndInheritedKeys(LineFigure.class));
        addFigureKeysAndNames("Ellipse", EllipseFigure.class, Figure.getDeclaredAndInheritedKeys(EllipseFigure.class));
        addFigureKeysAndNames("LineConnection", LineConnectionFigure.class, Figure.getDeclaredAndInheritedKeys(LineConnectionFigure.class));
        addConverterForType(Rectangle2D.class, new Rectangle2DConverter());
        addConverterForType(String.class, new DefaultConverter());
        addConverterForType(Point2D.class, new Point2DConverter());
        addConverterForType(Double.class, new NumberConverter());
        addConverterForType(URL.class, new UrlConverter());
        addConverterForType(URI.class, new UriConverter());
        addConverterForType(Connector.class, new DefaultConnectorConverter());
        addConverterForType(Paint.class, new XmlPaintConverter());
        addConverterForType(Boolean.class, new XmlBooleanConverter());

        addConverterForType(Figure.STYLE_CLASS.getFullValueType(), new CssObservableWordListConverter());
        
          removeKey(Drawing.PSEUDO_CLASS_STATES);

        
        checkConverters();
    }

}
