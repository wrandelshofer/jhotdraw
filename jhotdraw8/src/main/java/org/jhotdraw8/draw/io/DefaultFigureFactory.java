/* @(#)DefaultFigureFactory.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
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
import org.jhotdraw8.css.CssInsets;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssPoint3D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.CssStroke;
import org.jhotdraw8.css.text.CssRectangle2DConverter;
import org.jhotdraw8.css.text.CssPoint3DConverter;
import org.jhotdraw8.css.text.CssStrokeConverter;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.figure.SecondStrokeableFigure;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.LabelAutorotate;
import org.jhotdraw8.draw.figure.SimpleBezierFigure;
import org.jhotdraw8.draw.figure.SimpleClipping;
import org.jhotdraw8.draw.figure.SimpleCombinedPathFigure;
import org.jhotdraw8.draw.figure.SimpleDrawing;
import org.jhotdraw8.draw.figure.SimpleEllipseFigure;
import org.jhotdraw8.draw.figure.SimpleGroupFigure;
import org.jhotdraw8.draw.figure.SimpleImageFigure;
import org.jhotdraw8.draw.figure.SimpleLabelFigure;
import org.jhotdraw8.draw.figure.SimpleLayer;
import org.jhotdraw8.draw.figure.SimpleLineConnectionFigure;
import org.jhotdraw8.draw.figure.SimpleLineConnectionWithMarkersFigure;
import org.jhotdraw8.draw.figure.SimpleLineFigure;
import org.jhotdraw8.draw.figure.SimplePageFigure;
import org.jhotdraw8.draw.figure.SimplePageLabelFigure;
import org.jhotdraw8.draw.figure.SimplePolygonFigure;
import org.jhotdraw8.draw.figure.SimplePolylineFigure;
import org.jhotdraw8.draw.figure.SimpleRectangleFigure;
import org.jhotdraw8.draw.figure.SimpleRegionFigure;
import org.jhotdraw8.draw.figure.SimpleSliceFigure;
import org.jhotdraw8.draw.figure.SimpleTextFigure;
import org.jhotdraw8.draw.figure.StrokeableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.figure.TextStrokeableFigure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.css.text.CssColorConverter;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssEffectConverter;
import org.jhotdraw8.css.text.CssEnumConverter;
import org.jhotdraw8.css.CssFont;
import org.jhotdraw8.css.text.CssInsetsConverterOLD;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssPaintConverter;
import org.jhotdraw8.css.text.CssPaintableConverter;
import org.jhotdraw8.css.text.Point2DConverter;
import org.jhotdraw8.css.text.CssRegexConverter;
import org.jhotdraw8.css.text.CssPoint2DConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.css.text.CssInsetsConverter;
import org.jhotdraw8.css.text.CssTransformConverter;
import org.jhotdraw8.css.text.CssWordListConverter;
import org.jhotdraw8.text.DefaultConverter;
import org.jhotdraw8.text.RegexReplace;
import org.jhotdraw8.xml.text.XmlBezierNodeListConverter;
import org.jhotdraw8.xml.text.XmlBooleanConverter;
import org.jhotdraw8.xml.text.XmlConnectorConverter;
import org.jhotdraw8.xml.text.XmlDoubleConverter;
import org.jhotdraw8.xml.text.XmlCssFontConverter;
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
        addFigureKeysAndNames("Region", SimpleRegionFigure.class);
        addFigureKeysAndNames("Slice", SimpleSliceFigure.class);
        addFigureKeysAndNames("Group", SimpleGroupFigure.class);
        addFigureKeysAndNames("Polyline", SimplePolylineFigure.class);
        addFigureKeysAndNames("Polygon", SimplePolygonFigure.class);
        addFigureKeysAndNames("Page", SimplePageFigure.class);
        addFigureKeysAndNames("CombinedPath", SimpleCombinedPathFigure.class);

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
        addConverterForType(Insets.class, new CssInsetsConverterOLD(false));
        addConverterForType(Double.class, new XmlDoubleConverter());
        addConverterForType(URL.class, new XmlUrlConverter());
        addConverterForType(URI.class, new XmlUriConverter());
        addConverterForType(Connector.class, new XmlConnectorConverter());
        addConverterForType(Paint.class, new CssPaintConverter(true));
        addConverterForType(Paintable.class, new CssPaintableConverter(true));
        addConverterForType(CssColor.class, new CssColorConverter());
        addConverterForType(Boolean.class, new XmlBooleanConverter());
        addConverterForType(TextAlignment.class, new CssEnumConverter<>(TextAlignment.class));
        addConverterForType(SimpleCombinedPathFigure.CagOperation.class, new CssEnumConverter<>(SimpleCombinedPathFigure.CagOperation.class));
        addConverterForType(VPos.class, new CssEnumConverter<>(VPos.class));
        addConverterForType(HPos.class, new CssEnumConverter<>(HPos.class));
        addConverterForType(CssFont.class, new XmlCssFontConverter(false));
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
        addConverterForType(RegexReplace.class, new CssRegexConverter(true));
        addConverterForType(StrokeLineJoin.class, new CssEnumConverter<>(StrokeLineJoin.class));
        addConverterForType(StrokeLineCap.class, new CssEnumConverter<>(StrokeLineCap.class));
        addConverterForType(StrokeType.class, new CssEnumConverter<>(StrokeType.class));
        addConverterForType(CssStroke.class, new CssStrokeConverter(false));


        addConverter(StyleableFigure.STYLE_CLASS, new CssWordListConverter());
        addConverter(TextStrokeableFigure.TEXT_STROKE_DASH_ARRAY, new CssListConverter<>(new CssSizeConverter(false)));
        addConverter(StrokeableFigure.STROKE_DASH_ARRAY, new CssListConverter<>(new CssSizeConverter(false)));
        addConverter(TransformableFigure.TRANSFORMS, new CssListConverter<>(new CssTransformConverter(false)));
        addConverter(SimplePolylineFigure.POINTS,new CssListConverter<>(new Point2DConverter(false)));
        addConverter(SimpleBezierFigure.PATH, new XmlBezierNodeListConverter(true));
        addConverter(SecondStrokeableFigure.SECOND_STROKE_DASH_ARRAY, new CssListConverter<>(new CssDoubleConverter(false)));

        removeKey(StyleableFigure.PSEUDO_CLASS_STATES);

        checkConverters();
    }

}
