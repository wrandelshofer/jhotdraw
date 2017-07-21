/* @(#)DefaultFigureFactory.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;
import javafx.geometry.HPos;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.Point3D;
import javafx.geometry.Rectangle2D;
import javafx.geometry.VPos;
import javafx.scene.effect.BlendMode;
import javafx.scene.effect.Effect;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.FontPosture;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.SimpleClipping;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.SimpleGroupFigure;
import org.jhotdraw8.draw.figure.SimpleLineConnectionFigure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.figure.SimpleLabelFigure;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.figure.StrokeableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.SimpleBezierFigure;
import org.jhotdraw8.draw.figure.CombinedPathFigure;
import org.jhotdraw8.draw.figure.SimpleImageFigure;
import org.jhotdraw8.draw.figure.SimpleEllipseFigure;
import org.jhotdraw8.draw.figure.SimpleLineConnectionWithMarkersFigure;
import org.jhotdraw8.draw.figure.SimpleLineFigure;
import org.jhotdraw8.draw.figure.SimplePageFigure;
import org.jhotdraw8.draw.figure.SimplePageLabelFigure;
import org.jhotdraw8.draw.figure.SimplePolygonFigure;
import org.jhotdraw8.draw.figure.SimplePolylineFigure;
import org.jhotdraw8.draw.figure.SimpleTextFigure;
import org.jhotdraw8.draw.figure.SimpleRectangleFigure;
import org.jhotdraw8.draw.figure.SimpleSliceFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.text.XmlConnectorConverter;
import org.jhotdraw8.text.DefaultConverter;
import org.jhotdraw8.text.XmlPoint2DConverter;
import org.jhotdraw8.text.CssDoubleListConverter;
import org.jhotdraw8.text.CssFont;
import org.jhotdraw8.text.CssWordListConverter;
import org.jhotdraw8.text.CssPoint2DListConverter;
import org.jhotdraw8.text.CssRegexConverter;
import org.jhotdraw8.text.CssSize;
import org.jhotdraw8.text.CssSize2D;
import org.jhotdraw8.text.CssSize2DConverter;
import org.jhotdraw8.text.CssSizeConverter;
import org.jhotdraw8.text.CssSizeInsets;
import org.jhotdraw8.text.CssSizeInsetsConverter;
import org.jhotdraw8.draw.key.Paintable;
import org.jhotdraw8.text.RegexReplace;
import org.jhotdraw8.text.XmlUrlConverter;
import org.jhotdraw8.text.XmlUriConverter;
import org.jhotdraw8.text.XmlBooleanConverter;
import org.jhotdraw8.text.XmlCColorConverter;
import org.jhotdraw8.text.XmlDoubleConverter;
import org.jhotdraw8.text.XmlEffectConverter;
import org.jhotdraw8.text.XmlEnumConverter;
import org.jhotdraw8.text.XmlFFontConverter;
import org.jhotdraw8.text.XmlObjectReferenceConverter;
import org.jhotdraw8.text.XmlInsetsConverter;
import org.jhotdraw8.text.SvgPaintConverter;
import org.jhotdraw8.text.XmlBezierNodeListConverter;
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

    public DefaultFigureFactory() {
        this(new SimpleFigureIdFactory());
    }
    public DefaultFigureFactory(IdFactory idFactory){
        super(idFactory);
        addFigureKeysAndNames("Layer", SimpleLayer.class);
        addFigureKeysAndNames("Clipping", SimpleClipping.class);
        addFigureKeysAndNames("Rectangle", SimpleRectangleFigure.class);
        addFigureKeysAndNames("Slice", SimpleSliceFigure.class);
        addFigureKeysAndNames("Group", SimpleGroupFigure.class);
        addFigureKeysAndNames("Polyline", SimplePolylineFigure.class);
        addFigureKeysAndNames("Polygon", SimplePolygonFigure.class);
        addFigureKeysAndNames("Page", SimplePageFigure.class);
        addFigureKeysAndNames("CombinedPath", CombinedPathFigure.class);

        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(SimpleDrawing.class));
            keys.remove(Drawing.USER_AGENT_STYLESHEETS);
            keys.remove(Drawing.AUTHOR_STYLESHEETS);
            keys.remove(Drawing.INLINE_STYLESHEETS);
            keys.remove(Drawing.DOCUMENT_HOME);
            addFigureKeysAndNames("Drawing", SimpleDrawing.class, keys);
        }

        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(SimpleTextFigure.class));
            keys.remove(SimpleTextFigure.TEXT);
            addNodeListKey(SimpleTextFigure.class, "", SimpleTextFigure.TEXT);
            addFigureKeysAndNames("Text", SimpleTextFigure.class, keys);
        }
        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(SimpleLabelFigure.class));
            keys.remove(SimpleLabelFigure.TEXT);
            addNodeListKey(SimpleLabelFigure.class, "", SimpleLabelFigure.TEXT);
            addFigureKeysAndNames("Label", SimpleLabelFigure.class, keys);
        }
        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(SimplePageLabelFigure.class));
            keys.remove(SimplePageLabelFigure.TEXT_WITH_PLACEHOLDERS);
            addNodeListKey(SimplePageLabelFigure.class, "", SimplePageLabelFigure.TEXT_WITH_PLACEHOLDERS);
            addFigureKeysAndNames("PageLabel", SimplePageLabelFigure.class, keys);
        }

        addFigureKeysAndNames("Line", SimpleLineFigure.class);
        addFigureKeysAndNames("Ellipse", SimpleEllipseFigure.class);
        addFigureKeysAndNames("LineConnection", SimpleLineConnectionFigure.class);
        addFigureKeysAndNames("LineConnectionWithMarkers", SimpleLineConnectionWithMarkersFigure.class);
        addFigureKeysAndNames("Image", SimpleImageFigure.class);
        addFigureKeysAndNames("BezierPath", SimpleBezierFigure.class);

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
        addConverterForType(TextAlignment.class, new XmlEnumConverter<>(TextAlignment.class));
        addConverterForType(CombinedPathFigure.CagOperation.class, new XmlEnumConverter<CombinedPathFigure.CagOperation>(CombinedPathFigure.CagOperation.class));
        addConverterForType(VPos.class, new XmlEnumConverter<>(VPos.class));
        addConverterForType(HPos.class, new XmlEnumConverter<>(HPos.class));
        addConverterForType(CssFont.class, new XmlFFontConverter());
        addConverterForType(Rectangle2D.class, new XmlRectangle2DConverter());
        addConverterForType(BlendMode.class, new XmlEnumConverter<>(BlendMode.class));
        addConverterForType(Effect.class, new XmlEffectConverter());
        addConverterForType(Figure.class, new XmlObjectReferenceConverter<>(Figure.class));
        addConverterForType(CssSize.class, new CssSizeConverter());
        addConverterForType(CssSizeInsets.class, new CssSizeInsetsConverter());
        addConverterForType(CssSize2D.class, new CssSize2DConverter());
        addConverterForType(FillRule.class, new XmlEnumConverter<FillRule>(FillRule.class));
        addConverterForType(FontWeight.class, new XmlEnumConverter<>(FontWeight.class));
        addConverterForType(FontPosture.class, new XmlEnumConverter<>(FontPosture.class));
        addConverterForType(RegexReplace.class, new CssRegexConverter(true));

        addConverter(StyleableFigure.STYLE_CLASS, new CssWordListConverter());
        addConverter(StrokeableFigure.STROKE_DASH_ARRAY, new CssDoubleListConverter());
        addConverter(StrokeableFigure.STROKE_LINE_CAP, new XmlEnumConverter<StrokeLineCap>(StrokeLineCap.class));
        addConverter(StrokeableFigure.STROKE_LINE_JOIN, new XmlEnumConverter<StrokeLineJoin>(StrokeLineJoin.class));
        addConverter(StrokeableFigure.STROKE_TYPE, new XmlEnumConverter<StrokeType>(StrokeType.class));
        addConverter(TransformableFigure.TRANSFORMS, new XmlTransformListConverter());
        addConverter(SimplePolylineFigure.POINTS, new CssPoint2DListConverter());
        addConverter(SimpleBezierFigure.PATH, new XmlBezierNodeListConverter(true));

        removeKey(StyleableFigure.PSEUDO_CLASS_STATES);

        removeRedundantKeys();
        checkConverters();
    }

}
