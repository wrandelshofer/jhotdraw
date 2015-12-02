/* @(#)DefaultFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw.io;

import java.net.URI;
import java.net.URL;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.TextAlignment;
import org.jhotdraw.collection.Key;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.GroupFigure;
import org.jhotdraw.draw.LineConnectionFigure;
import org.jhotdraw.draw.SimpleDrawing;
import org.jhotdraw.draw.SimpleLabelFigure;
import org.jhotdraw.draw.SimpleLayer;
import org.jhotdraw.draw.StrokeableFigure;
import org.jhotdraw.draw.StyleableFigure;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.shape.EllipseFigure;
import org.jhotdraw.draw.shape.LineFigure;
import org.jhotdraw.draw.shape.TextFigure;
import org.jhotdraw.draw.shape.RectangleFigure;
import org.jhotdraw.text.CssEnumConverter;
import org.jhotdraw.text.DefaultConnectorConverter;
import org.jhotdraw.text.DefaultConverter;
import org.jhotdraw.text.XmlPoint2DConverter;
import org.jhotdraw.text.NumberConverter;
import org.jhotdraw.text.CssObservableWordListConverter;
import org.jhotdraw.text.CssSizeListConverter;
import org.jhotdraw.text.Rectangle2DConverter;
import org.jhotdraw.text.UrlConverter;
import org.jhotdraw.text.UriConverter;
import org.jhotdraw.text.XmlBooleanConverter;
import org.jhotdraw.text.XmlEnumConverter;
import org.jhotdraw.text.XmlFontConverter;
import org.jhotdraw.text.XmlInsetsConverter;
import org.jhotdraw.text.XmlPaintConverter;
import org.jhotdraw.text.XmlPoint3DConverter;
import org.jhotdraw.text.XmlSvgPathConverter;

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
        addConverterForType(Point2D.class, new XmlPoint2DConverter());
        addConverterForType(Point3D.class, new XmlPoint3DConverter());
        addConverterForType(SVGPath.class, new XmlSvgPathConverter());
        addConverterForType(Insets.class, new XmlInsetsConverter());
        addConverterForType(Double.class, new NumberConverter());
        addConverterForType(URL.class, new UrlConverter());
        addConverterForType(URI.class, new UriConverter());
        addConverterForType(Connector.class, new DefaultConnectorConverter());
        addConverterForType(Paint.class, new XmlPaintConverter());
        addConverterForType(Boolean.class, new XmlBooleanConverter());
        addConverterForType(TextAlignment.class, new XmlEnumConverter<TextAlignment>(TextAlignment.class));
        addConverterForType(VPos.class, new XmlEnumConverter<VPos>(VPos.class));
        addConverterForType(Font.class, new XmlFontConverter());

        addConverter(StyleableFigure.STYLE_CLASS, new CssObservableWordListConverter());
        addConverter(StrokeableFigure.STROKE_DASH_ARRAY, new CssSizeListConverter());
        addConverter(StrokeableFigure.STROKE_LINE_CAP, new CssEnumConverter<StrokeLineCap>(StrokeLineCap.class));
        addConverter(StrokeableFigure.STROKE_LINE_JOIN, new CssEnumConverter<StrokeLineJoin>(StrokeLineJoin.class));
        addConverter(StrokeableFigure.STROKE_TYPE, new CssEnumConverter<StrokeType>(StrokeType.class));
        
          removeKey(StyleableFigure.PSEUDO_CLASS_STATES);

        
        checkConverters();
    }

}
