/* @(#)DefaultFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.net.URI;
import java.net.URL;
import java.util.Set;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.TextAlignment;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.Drawing;
import org.jhotdraw8.draw.SimpleClipping;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.GroupFigure;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import org.jhotdraw8.draw.SimpleDrawing;
import org.jhotdraw8.draw.figure.LabelFigure;
import org.jhotdraw8.draw.SimpleLayer;
import org.jhotdraw8.draw.figure.StrokeableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.ImageFigure;
import org.jhotdraw8.draw.figure.EllipseFigure;
import org.jhotdraw8.draw.figure.LineFigure;
import org.jhotdraw8.draw.figure.TextFigure;
import org.jhotdraw8.draw.figure.RectangleFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.text.CssColor;
import org.jhotdraw8.text.XmlConnectorConverter;
import org.jhotdraw8.text.DefaultConverter;
import org.jhotdraw8.text.XmlPoint2DConverter;
import org.jhotdraw8.text.CssObservableWordListConverter;
import org.jhotdraw8.text.CssSizeListConverter;
import org.jhotdraw8.text.CssFont;
import org.jhotdraw8.text.Paintable;
import org.jhotdraw8.text.XmlUrlConverter;
import org.jhotdraw8.text.XmlUriConverter;
import org.jhotdraw8.text.XmlBooleanConverter;
import org.jhotdraw8.text.XmlCColorConverter;
import org.jhotdraw8.text.XmlDoubleConverter;
import org.jhotdraw8.text.XmlEffectConverter;
import org.jhotdraw8.text.XmlEnumConverter;
import org.jhotdraw8.text.XmlFFontConverter;
import org.jhotdraw8.text.XmlFigureReferenceConverter;
import org.jhotdraw8.text.XmlInsetsConverter;
import org.jhotdraw8.text.SvgPaintConverter;
import org.jhotdraw8.text.XmlPaintableConverter;
import org.jhotdraw8.text.XmlPoint3DConverter;
import org.jhotdraw8.text.XmlRectangle2DConverter;
import org.jhotdraw8.text.XmlSvgPathConverter;
import org.jhotdraw8.text.XmlTransformListConverter;

/**
 * DefaultFigureFactory.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class DefaultFigureFactory extends SimpleFigureFactory {

    public DefaultFigureFactory(IdFactory idFactory) {
        addFigureKeysAndNames("Layer", SimpleLayer.class, Figure.getDeclaredAndInheritedKeys(SimpleLayer.class));
        addFigureKeysAndNames("Clipping", SimpleClipping.class, Figure.getDeclaredAndInheritedKeys(SimpleClipping.class));
        addFigureKeysAndNames("Rectangle", RectangleFigure.class, Figure.getDeclaredAndInheritedKeys(RectangleFigure.class));
        addFigureKeysAndNames("Group", GroupFigure.class, Figure.getDeclaredAndInheritedKeys(GroupFigure.class));

        {
            Set<MapAccessor<?>> keys = Figure.getDeclaredAndInheritedKeys(SimpleDrawing.class);
            keys.remove(Drawing.USER_AGENT_STYLESHEETS);
            keys.remove(Drawing.AUTHOR_STYLESHEETS);
            keys.remove(Drawing.INLINE_STYLESHEETS);
            keys.remove(Drawing.DOCUMENT_HOME);
            addNodeListKey(TextFigure.class, "", TextFigure.TEXT);
            addFigureKeysAndNames("Drawing", SimpleDrawing.class, keys);
        }

        {
            Set<MapAccessor<?>> keys = Figure.getDeclaredAndInheritedKeys(TextFigure.class);
            keys.remove(TextFigure.TEXT);
            addNodeListKey(TextFigure.class, "", TextFigure.TEXT);
            addFigureKeysAndNames("Text", TextFigure.class, keys);
        }
        {
            Set<MapAccessor<?>> keys = Figure.getDeclaredAndInheritedKeys(LabelFigure.class);
            keys.remove(TextFigure.TEXT);
            addNodeListKey(LabelFigure.class, "", TextFigure.TEXT);
            addFigureKeysAndNames("Label", LabelFigure.class, keys);
        }

        addFigureKeysAndNames("Line", LineFigure.class, Figure.getDeclaredAndInheritedKeys(LineFigure.class));
        addFigureKeysAndNames("Ellipse", EllipseFigure.class, Figure.getDeclaredAndInheritedKeys(EllipseFigure.class));
        addFigureKeysAndNames("LineConnection", LineConnectionFigure.class, Figure.getDeclaredAndInheritedKeys(LineConnectionFigure.class));
        addFigureKeysAndNames("Image", ImageFigure.class, Figure.getDeclaredAndInheritedKeys(ImageFigure.class));

        addConverterForType(String.class, new DefaultConverter());
        addConverterForType(Point2D.class, new XmlPoint2DConverter());
        addConverterForType(Point3D.class, new XmlPoint3DConverter());
        addConverterForType(SVGPath.class, new XmlSvgPathConverter());
        addConverterForType(Insets.class, new XmlInsetsConverter());
        addConverterForType(Double.class, new XmlDoubleConverter());
        addConverterForType(URL.class, new XmlUrlConverter());
        addConverterForType(URI.class, new XmlUriConverter());
        addConverterForType(Connector.class, new XmlConnectorConverter());
        addConverterForType(Paint.class, new SvgPaintConverter());
        addConverterForType(Paintable.class, new XmlPaintableConverter());
        addConverterForType(CssColor.class, new XmlCColorConverter(true));
        addConverterForType(Boolean.class, new XmlBooleanConverter());
        addConverterForType(TextAlignment.class, new XmlEnumConverter<TextAlignment>(TextAlignment.class));
        addConverterForType(VPos.class, new XmlEnumConverter<VPos>(VPos.class));
        addConverterForType(CssFont.class, new XmlFFontConverter());
        addConverterForType(Rectangle2D.class, new XmlRectangle2DConverter());
        addConverterForType(BlendMode.class, new XmlEnumConverter<BlendMode>(BlendMode.class));
        addConverterForType(Effect.class, new XmlEffectConverter());
        addConverterForType(Figure.class, new XmlFigureReferenceConverter());

        addConverter(StyleableFigure.STYLE_CLASS, new CssObservableWordListConverter());
        addConverter(StrokeableFigure.STROKE_DASH_ARRAY, new CssSizeListConverter());
        addConverter(StrokeableFigure.STROKE_LINE_CAP, new XmlEnumConverter<StrokeLineCap>(StrokeLineCap.class));
        addConverter(StrokeableFigure.STROKE_LINE_JOIN, new XmlEnumConverter<StrokeLineJoin>(StrokeLineJoin.class));
        addConverter(StrokeableFigure.STROKE_TYPE, new XmlEnumConverter<StrokeType>(StrokeType.class));
        addConverter(TransformableFigure.TRANSFORMS, new XmlTransformListConverter());

        removeKey(StyleableFigure.PSEUDO_CLASS_STATES);

        removeRedundantKeys();
        checkConverters();
    }

}
