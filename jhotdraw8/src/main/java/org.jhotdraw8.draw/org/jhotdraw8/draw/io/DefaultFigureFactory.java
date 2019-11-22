/*
 * @(#)DefaultFigureFactory.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

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
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.CssInsets;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssPoint3D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssStroke;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssEffectConverter;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.css.text.CssFontConverter;
import org.jhotdraw8.css.text.CssInsetsConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssPaintConverter;
import org.jhotdraw8.css.text.CssPaintableConverter;
import org.jhotdraw8.css.text.CssPoint2DConverter;
import org.jhotdraw8.css.text.CssPoint3DConverter;
import org.jhotdraw8.css.text.CssRectangle2DConverter;
import org.jhotdraw8.css.text.CssRegexConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.css.text.CssStrokeStyleConverter;
import org.jhotdraw8.css.text.CssTransformConverter;
import org.jhotdraw8.css.text.CssWordListConverter;
import org.jhotdraw8.css.text.InsetsConverter;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.BezierFigure;
import org.jhotdraw8.draw.figure.ClippingFigure;
import org.jhotdraw8.draw.figure.CombinedPathFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.DrawingFigure;
import org.jhotdraw8.draw.figure.EllipseFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.GroupFigure;
import org.jhotdraw8.draw.figure.ImageFigure;
import org.jhotdraw8.draw.figure.LabelAutorotate;
import org.jhotdraw8.draw.figure.LabelFigure;
import org.jhotdraw8.draw.figure.LayerFigure;
import org.jhotdraw8.draw.figure.LineConnectionFigure;
import org.jhotdraw8.draw.figure.LineConnectionWithMarkersFigure;
import org.jhotdraw8.draw.figure.LineFigure;
import org.jhotdraw8.draw.figure.MarkerStrokableFigure;
import org.jhotdraw8.draw.figure.PageFigure;
import org.jhotdraw8.draw.figure.PageLabelFigure;
import org.jhotdraw8.draw.figure.PolygonFigure;
import org.jhotdraw8.draw.figure.PolylineFigure;
import org.jhotdraw8.draw.figure.RectangleFigure;
import org.jhotdraw8.draw.figure.RegionFigure;
import org.jhotdraw8.draw.figure.SliceFigure;
import org.jhotdraw8.draw.figure.StrokableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.figure.TextAreaFigure;
import org.jhotdraw8.draw.figure.TextFigure;
import org.jhotdraw8.draw.figure.TextStrokeableFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.text.DefaultConverter;
import org.jhotdraw8.text.RegexReplace;
import org.jhotdraw8.xml.text.XmlBezierNodeListConverter;
import org.jhotdraw8.xml.text.XmlBooleanConverter;
import org.jhotdraw8.xml.text.XmlConnectorConverter;
import org.jhotdraw8.xml.text.XmlObjectReferenceConverter;
import org.jhotdraw8.xml.text.XmlPoint2DConverter;
import org.jhotdraw8.xml.text.XmlPoint3DConverter;
import org.jhotdraw8.xml.text.XmlRectangle2DConverter;
import org.jhotdraw8.xml.text.XmlSvgPathConverter;
import org.jhotdraw8.xml.text.XmlUriConverter;
import org.jhotdraw8.xml.text.XmlUrlConverter;

import java.net.URI;
import java.net.URL;
import java.util.HashSet;
import java.util.Set;

/**
 * DefaultFigureFactory.
 *
 * @author Werner Randelshofer
 */
public class DefaultFigureFactory extends AbstractFigureFactory {

    public DefaultFigureFactory() {
        this(new SimpleFigureIdFactory());
    }

    public DefaultFigureFactory(IdFactory idFactory) {
        super(idFactory);

        addFigureKeysAndNames("Layer", LayerFigure.class);
        addFigureKeysAndNames("Clipping", ClippingFigure.class);
        addFigureKeysAndNames("Rectangle", RectangleFigure.class);
        addFigureKeysAndNames("Region", RegionFigure.class);
        addFigureKeysAndNames("Slice", SliceFigure.class);
        addFigureKeysAndNames("Group", GroupFigure.class);
        addFigureKeysAndNames("Polyline", PolylineFigure.class);
        addFigureKeysAndNames("Polygon", PolygonFigure.class);
        addFigureKeysAndNames("Page", PageFigure.class);
        addFigureKeysAndNames("CombinedPath", CombinedPathFigure.class);

        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(DrawingFigure.class));
            keys.remove(Drawing.USER_AGENT_STYLESHEETS);
            keys.remove(Drawing.AUTHOR_STYLESHEETS);
            keys.remove(Drawing.INLINE_STYLESHEETS);
            keys.remove(Drawing.DOCUMENT_HOME);
            addFigureKeysAndNames("Drawing", DrawingFigure.class, keys);
        }

        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(TextFigure.class));
            keys.remove(TextFigure.TEXT);
            addNodeListKey(TextFigure.class, "", TextFigure.TEXT);
            addFigureKeysAndNames("Text", TextFigure.class, keys);
        }
        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(TextAreaFigure.class));
            keys.remove(TextAreaFigure.TEXT);
            addNodeListKey(TextAreaFigure.class, "", TextAreaFigure.TEXT);
            addFigureKeysAndNames("TextArea", TextAreaFigure.class, keys);
        }
        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(LabelFigure.class));
            keys.remove(LabelFigure.TEXT);
            addNodeListKey(LabelFigure.class, "", LabelFigure.TEXT);
            addFigureKeysAndNames("Label", LabelFigure.class, keys);
        }
        {
            Set<MapAccessor<?>> keys = new HashSet<>(Figure.getDeclaredAndInheritedMapAccessors(PageLabelFigure.class));
            keys.remove(PageLabelFigure.TEXT_WITH_PLACEHOLDERS);
            addNodeListKey(PageLabelFigure.class, "", PageLabelFigure.TEXT_WITH_PLACEHOLDERS);
            addFigureKeysAndNames("PageLabel", PageLabelFigure.class, keys);
        }

        addFigureKeysAndNames("Line", LineFigure.class);
        addFigureKeysAndNames("Ellipse", EllipseFigure.class);
        addFigureKeysAndNames("LineConnection", LineConnectionFigure.class);
        addFigureKeysAndNames("LineConnectionWithMarkers", LineConnectionWithMarkersFigure.class);
        addFigureKeysAndNames("Image", ImageFigure.class);
        addFigureKeysAndNames("BezierPath", BezierFigure.class);

        addConverterForType(String.class, new DefaultConverter());
        addConverterForType(Point2D.class, new XmlPoint2DConverter());
        addConverterForType(Point3D.class, new XmlPoint3DConverter());
        addConverterForType(SVGPath.class, new XmlSvgPathConverter());
        addConverterForType(Insets.class, new InsetsConverter(false));
        addConverterForType(Double.class, new CssDoubleConverter(false));
        addConverterForType(URL.class, new XmlUrlConverter());
        addConverterForType(URI.class, new XmlUriConverter());
        addConverterForType(Connector.class, new XmlConnectorConverter());
        addConverterForType(Paint.class, new CssPaintConverter(true));
        addConverterForType(Paintable.class, new CssPaintableConverter(true));
        addConverterForType(CssColor.class, new CssColorConverter());
        addConverterForType(Boolean.class, new XmlBooleanConverter());
        addConverterForType(TextAlignment.class, new CssEnumConverter<>(TextAlignment.class));
        addConverterForType(CombinedPathFigure.CagOperation.class, new CssEnumConverter<>(CombinedPathFigure.CagOperation.class));
        addConverterForType(VPos.class, new CssEnumConverter<>(VPos.class));
        addConverterForType(HPos.class, new CssEnumConverter<>(HPos.class));
        addConverterForType(CssFont.class, new CssFontConverter(false));
        addConverterForType(Rectangle2D.class, new XmlRectangle2DConverter());
        addConverterForType(BlendMode.class, new CssEnumConverter<>(BlendMode.class));
        addConverterForType(Effect.class, new CssEffectConverter());
        addConverterForType(Figure.class, new XmlObjectReferenceConverter<>(Figure.class));
        addConverterForType(CssSize.class, new CssSizeConverter(false));
        addConverterForType(CssInsets.class, new CssInsetsConverter(false));
        addConverterForType(CssRectangle2D.class, new CssRectangle2DConverter(false));
        addConverterForType(CssPoint2D.class, new CssPoint2DConverter(false));
        addConverterForType(CssPoint3D.class, new CssPoint3DConverter(false));
        addConverterForType(FillRule.class, new CssEnumConverter<>(FillRule.class));
        addConverterForType(FontWeight.class, new CssEnumConverter<>(FontWeight.class));
        addConverterForType(FontPosture.class, new CssEnumConverter<>(FontPosture.class));
        addConverterForType(LabelAutorotate.class, new CssEnumConverter<>(LabelAutorotate.class));
        addConverterForType(RegexReplace.class, new CssRegexConverter(true));// FIXME remove from JHotDraw
        addConverterForType(StrokeLineJoin.class, new CssEnumConverter<>(StrokeLineJoin.class));
        addConverterForType(StrokeLineCap.class, new CssEnumConverter<>(StrokeLineCap.class));
        addConverterForType(StrokeType.class, new CssEnumConverter<>(StrokeType.class));
        addConverterForType(CssStroke.class, new CssStrokeStyleConverter(false));


        addConverter(StyleableFigure.STYLE_CLASS, new CssWordListConverter());
        addConverter(TextStrokeableFigure.TEXT_STROKE_DASH_ARRAY, new CssListConverter<>(new CssSizeConverter(false)));
        addConverter(StrokableFigure.STROKE_DASH_ARRAY, new CssListConverter<>(new CssSizeConverter(false)));
        addConverter(TransformableFigure.TRANSFORMS, new CssListConverter<>(new CssTransformConverter(false)));
        addConverter(PolylineFigure.POINTS, new CssListConverter<>(new Point2DConverter(false)));
        addConverter(BezierFigure.PATH, new XmlBezierNodeListConverter(true));
        addConverter(StrokableFigure.STROKE_DASH_ARRAY, new CssListConverter<>(new CssSizeConverter(false)));
        addConverter(MarkerStrokableFigure.MARKER_STROKE_DASH_ARRAY, new CssListConverter<>(new CssSizeConverter(false)));

        removeKey(StyleableFigure.PSEUDO_CLASS_STATES);

        checkConverters();
    }

}
