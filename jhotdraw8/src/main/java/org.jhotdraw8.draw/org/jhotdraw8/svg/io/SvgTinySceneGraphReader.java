/*
 * @(#)SvgSceneGraphReader.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssTransformConverter;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.svg.text.SvgPaintConverter;
import org.jhotdraw8.svg.text.SvgStrokeAlignmentConverter;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.IOException;
import java.io.InputStream;
import java.text.ParseException;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Reader for an SVG Tiny 1.2 file.
 *
 * <p>
 * References:<br>
 * <dl>
 *     <dt>SVG 1.2 Tiny</dt>
 *     <dd><a href="https://www.w3.org/TR/SVGTiny12/index.html">link</a></dd>
 * </dl>
 * <dl>
 *     <dt>SVG Strokes</dt>
 *     <dd><a href="https://www.w3.org/TR/svg-strokes/">link</a></dd>
 * </dl>
 */

public class SvgTinySceneGraphReader {
    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";

    private static class Context {
        public IdResolver idFactory = new SimpleIdFactory();
        Deque<Viewport> stack = new ArrayDeque<>();
    }

    /**
     * Returns a value as a String array.
     * The values are separated by whitespace or by commas with optional white
     * space.
     */
    public static String[] toWSOrCommaSeparatedArray(String str) throws IOException {
        String[] result = str.split("(\\s*,\\s*|\\s+)");
        if (result.length == 1 && result[0].equals("")) {
            return new String[0];
        } else {
            return result;
        }
    }

    private void computeViewportValues(Viewport viewport) {
        viewport.widthPercentFactor = viewport.viewBox.width / 100d;
        viewport.heightPercentFactor = viewport.viewBox.height / 100d;
        viewport.numberFactor = Math.min(
                viewport.width / viewport.viewBox.width,
                viewport.height / viewport.viewBox.height);

        AffineTransform viewBoxTransform = new AffineTransform();

        viewBoxTransform.translate(
                -viewport.viewBox.x * viewport.width / viewport.viewBox.width,
                -viewport.viewBox.y * viewport.height / viewport.viewBox.height);
        if (viewport.isPreserveAspectRatio) {
            double factor = Math.min(
                    viewport.width / viewport.viewBox.width,
                    viewport.height / viewport.viewBox.height);
            viewBoxTransform.scale(factor, factor);
        } else {
            viewBoxTransform.scale(
                    viewport.width / viewport.viewBox.width,
                    viewport.height / viewport.viewBox.height);
        }
    }

    private XMLStreamException createException(XMLStreamReader r, String s) {
        return new XMLStreamException(s + " " + r.getLocation());
    }

    private XMLStreamException createException(XMLStreamReader r, String s, Throwable cause) {
        return new XMLStreamException(s + " " + r.getLocation(), cause);
    }

    public Node read(InputStream in) throws IOException {
        try {
            XMLStreamReader r = XMLInputFactory.newInstance().createXMLStreamReader(in);
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                return readSvgElement(r);
            default:
                throw new XMLStreamException("<svg> element expected.");
            }
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }

    private Node readAElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private Node readCircleElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Circle shape = new Circle();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "cx":
                shape.setCenterX(toLength(r, value, 1));
                break;
            case "cy":
                shape.setCenterY(toLength(r, value, 1));
                break;
            case "r":
                shape.setRadius(toLength(r, value, 1));
                break;
            default:
                break;
            }
        }

        readShapeAttributes(r, shape, ctx);
        readTransformAttributes(r, shape, ctx);

        requireEndElement(r, "<ellipse>: end element expected.");
        return shape;
    }

    private Node readClipPathElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx);
                if (child != null) {
                    // FIXME handle clip path children
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<clipPath>: start element or end element expected.");
            }
        }

        return null;
    }

    private Node readDefsElement(XMLStreamReader r, Context ctx) throws XMLStreamException {

        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx);
                if (child != null) {
                    // FIXME add defs to ctx for second pass
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<defs>: start element or end element expected.");
            }
        }
        return null;
    }

    private Node readElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Node f;
        switch (r.getLocalName()) {
        case "a":
            f = readAElement(r, ctx);
            break;
        case "clipPath":
            f = readClipPathElement(r, ctx);
            break;
        case "circle":
            f = readCircleElement(r, ctx);
            break;
        case "defs":
            f = readDefsElement(r, ctx);
            break;
        case "ellipse":
            f = readEllipseElement(r, ctx);
            break;
        case "g":
            f = readGElement(r, ctx);
            break;
        case "image":
            f = readImageElement(r, ctx);
            break;
        case "line":
            f = readLineElement(r, ctx);
            break;
        case "linearGradient":
            f = readLinearGradientElement(r, ctx);
            break;
        case "path":
            f = readPathElement(r, ctx);
            break;
        case "polygon":
            f = readPolygonElement(r, ctx);
            break;
        case "polyline":
            f = readPolylineElement(r, ctx);
            break;
        case "radialGradient":
            f = readRadialGradientElement(r, ctx);
            break;
        case "rect":
            f = readRectElement(r, ctx);
            break;
        case "solidColor":
            f = readSolidColorElement(r, ctx);
            break;
        case "svg":
            f = readSvgElement(r, ctx);
            break;
        case "switch":
            f = readSwitchElement(r, ctx);
            break;
        case "text":
            f = readTextElement(r, ctx);
            break;
        case "textArea":
            f = readTextAreaElement(r, ctx);
            break;
        case "title":
            f = readTitleElement(r, ctx);
            break;
        case "use":
            f = readUseElement(r, ctx);
            break;
        case "style":
            f = readStyleElement(r, ctx);
            break;
        default:
            LOGGER.warning("Unknown element " + r.getLocalName());
            f = null;
            break;
        }
        return f;
    }

    private Node readEllipseElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Ellipse shape = new Ellipse();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "cx":
                shape.setCenterX(toLength(r, value, 1));
                break;
            case "cy":
                shape.setCenterY(toLength(r, value, 1));
                break;
            case "rx":
                shape.setRadiusX(toLength(r, value, 1));
                break;
            case "ry":
                shape.setRadiusY(toLength(r, value, 1));
                break;
            default:
                break;
            }
        }

        readShapeAttributes(r, shape, ctx);
        readTransformAttributes(r, shape, ctx);

        requireEndElement(r, "<ellipse>: end element expected.");
        return shape;
    }

    private Node readGElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Group g = new Group();
        g.setAutoSizeChildren(false);
        g.setManaged(false);

        readTransformAttributes(r, g, ctx);

        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx);
                if (child != null) {
                    g.getChildren().add(child);
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<g>: start element or end element expected.");
            }
        }
        return g;
    }

    private Node readImageElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    /**
     * Reads a color attribute that is inherited.
     * This is similar to {@code readInheritAttribute}, but takes care of the
     * "currentColor" magic attribute value.
     */
    @Nullable
    private String readInheritColorAttribute(@NonNull XMLStreamReader r, @NonNull String attributeName, @NonNull String value, @Nullable String initialValue, Context ctx) {
        return value;
    }

    private Node readLineElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Line shape = new Line();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "x1":
                shape.setStartX(toLength(r, value, 1));
                break;
            case "y1":
                shape.setStartY(toLength(r, value, 1));
                break;
            case "x2":
                shape.setEndX(toLength(r, value, 1));
                break;
            case "y2":
                shape.setEndY(toLength(r, value, 1));
                break;
            default:
                break;
            }
        }

        readShapeAttributes(r, shape, ctx);
        readTransformAttributes(r, shape, ctx);

        requireEndElement(r, "<line>: end element expected.");
        return shape;
    }

    private Node readLinearGradientElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readStopElement(r, ctx);
                if (child != null) {
                    // FIXME do something with stop element
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<linearGradient>: start element or end element expected.");
            }
        }
        return null;
    }

    private Node readPathElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SVGPath svgPath = new SVGPath();
        svgPath.setManaged(false);
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            switch (name) {
            case "d":
                svgPath.setContent(r.getAttributeValue(i));
                break;
            default:
                break;
            }
        }
        readShapeAttributes(r, svgPath, ctx);

        requireEndElement(r, "<path>: end element expected.");

        return svgPath;
    }

    private Node readPolygonElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Polygon shape = new Polygon();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "points":
                shape.getPoints().addAll(
                        toDoubles(r, value, ctx));
                break;
            default:
                break;
            }
        }

        readShapeAttributes(r, shape, ctx);
        readTransformAttributes(r, shape, ctx);

        requireEndElement(r, "<polygon>: end element expected.");
        return shape;
    }

    /**
     * Returns a value as a double list.
     * as specified in http://www.w3.org/TR/SVGMobile12/shapes.html#PointsBNF
     */
    private List<Double> toDoubles(XMLStreamReader r, String str, Context ctx) throws XMLStreamException {
        StringTokenizer tt = new StringTokenizer(str, " ,");
        List<Double> points = new ArrayList<>(tt.countTokens());
        for (int i = 0, n = tt.countTokens(); i < n; i++) {
            points.add(toLength(r, tt.nextToken(), 1.0));
        }
        return points;
    }

    private Node readPolylineElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Polyline shape = new Polyline();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "points":
                shape.getPoints().addAll(toDoubles(r, value, ctx));
                break;
            default:
                break;
            }
        }

        readShapeAttributes(r, shape, ctx);
        readTransformAttributes(r, shape, ctx);

        requireEndElement(r, "<polyline>: end element expected.");
        return shape;
    }

    private Node readRadialGradientElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readStopElement(r, ctx);
                if (child != null) {
                    // FIXME do something with stop element
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<radialGradient>: start element or end element expected.");
            }
        }
        return null;
    }

    private Node readRectElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Rectangle shape = new Rectangle();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "x":
                shape.setX(toLength(r, value, 1));
                break;
            case "y":
                shape.setY(toLength(r, value, 1));
                break;
            case "width":
                shape.setWidth(toLength(r, value, 1));
                break;
            case "height":
                shape.setHeight(toLength(r, value, 1));
                break;
            default:
                break;
            }
        }

        readShapeAttributes(r, shape, ctx);
        readTransformAttributes(r, shape, ctx);

        requireEndElement(r, "<rect>: end element expected.");
        return shape;
    }

    private void requireEndElement(XMLStreamReader r, String s) throws XMLStreamException {
        switch (r.nextTag()) {
        case XMLStreamReader.END_ELEMENT:
            break;
        default:
            throw createException(r, s);
        }
    }

    private Node readSvgElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    final CssListConverter<Transform> transformsConverter = new CssListConverter<>(new CssTransformConverter(false));

    /**
     * Reads shape attributes.
     */
    private void readTransformAttributes(XMLStreamReader r, Node node, Context ctx) throws XMLStreamException {
        try {
            for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
                String name = r.getAttributeLocalName(i);
                String value = r.getAttributeValue(i);
                switch (name) {
                case "transform":
                    ImmutableList<Transform> transforms = transformsConverter.fromString(value);
                    if (transforms != null) {
                        node.getTransforms().addAll(transforms.asList());
                    }
                    break;
                }
            }

        } catch (ParseException | IOException e) {
            throw createException(r, e.getMessage());
        }
    }

    private final SvgStrokeAlignmentConverter strokAlignmentConverter = new SvgStrokeAlignmentConverter(false);

    /**
     * Reads shape attributes.
     */
    private void readShapeAttributes(XMLStreamReader r, Shape shape, Context ctx)
            throws XMLStreamException {

        double strokeOpacity = 1.0;

        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {

            //'color'
            // Value:  	<color> | inherit
            // Initial:  	 depends on user agent
            // Applies to:  	None. Indirectly affects other properties via currentColor
            // Inherited:  	 yes
            // Percentages:  	 N/A
            // Media:  	 visual
            // Animatable:  	 yes
            // Computed value:  	 Specified <color> value, except inherit
            //
            // value = readInheritAttribute(elem, "color", "black");
            // if (DEBUG) System.out.println("color="+value);

            //'color-rendering'
            // Value:  	 auto | optimizeSpeed | optimizeQuality | inherit
            // Initial:  	 auto
            // Applies to:  	 container elements , graphics elements and 'animateColor'
            // Inherited:  	 yes
            // Percentages:  	 N/A
            // Media:  	 visual
            // Animatable:  	 yes
            // Computed value:  	 Specified value, except inherit
            //
            // value = readInheritAttribute(elem, "color-rendering", "auto");
            // if (DEBUG) System.out.println("color-rendering="+value);

            case "fill":
                // 'fill'
                // Value:  	<paint> | inherit (See Specifying paint)
                // Initial:  	 black
                // Applies to:  	 shapes and text content elements
                // Inherited:  	 yes
                // Percentages:  	 N/A
                // Media:  	 visual
                // Animatable:  	 yes
                // Computed value:  	 "none", system paint, specified <color> value or absolute IRI
                Paint fill = toPaint(r, readInheritColorAttribute(r, name, value, "black", ctx), ctx);
                shape.setFill(fill);
                break;

            //'fill-opacity'
            //Value:  	 <opacity-value> | inherit
            //Initial:  	 1
            //Applies to:  	 shapes and text content elements
            //Inherited:  	 yes
            //Percentages:  	 N/A
            //Media:  	 visual
            //Animatable:  	 yes
            //Computed value:  	 Specified value, except inherit

            // 'fill-rule'
            // Value:	 nonzero | evenodd | inherit
            // Initial: 	 nonzero
            // Applies to:  	 shapes and text content elements
            // Inherited:  	 yes
            // Percentages:  	 N/A
            // Media:  	 visual
            // Animatable:  	 yes
            // Computed value:  	 Specified value, except inherit

            case "stroke":
                //'stroke'
                //Value:  	<paint> | inherit (See Specifying paint)
                //Initial:  	 none
                //Applies to:  	 shapes and text content elements
                //Inherited:  	 yes
                //Percentages:  	 N/A
                //Media:  	 visual
                //Animatable:  	 yes
                //Computed value:  	 "none", system paint, specified <color> value
                // or absolute IRI

                // FIXME we can resolve this only in the second pass
                // FIXME must be combined with stroke-opacity
                Paint stroke = toPaint(r, readInheritColorAttribute(r, name, value, "none", ctx), ctx);
                shape.setStroke(stroke);
                break;

            case "stroke-alignment":
                //Name: 	stroke-alignment
                //Value: 	center | inner | outer
                //Initial: 	center
                //Applies to: 	shapes and text content elements
                //Inherited: 	yes
                //Percentages: 	N/A
                //Media: 	visual
                //Computed value: 	as specified
                //Animatable: 	yes
                try {
                    StrokeType type = strokAlignmentConverter.fromString(
                            readInheritAttribute(r, name, value, "center", ctx));
                    shape.setStrokeType(type);
                } catch (IOException | ParseException e) {
                    throw createException(r, e.getMessage(), e);
                }
                break;

            //'stroke-dasharray'
            //Value:  	 none | <dasharray> | inherit
            //Initial:  	 none
            //Applies to:  	 shapes and text content elements
            //Inherited:  	 yes
            //Percentages:  	 N/A
            //Media:  	 visual
            //Animatable:  	 yes (non-additive)
            //Computed value:  	 Specified value, except inherit

            //'stroke-dashoffset'
            //Value:  	<length> | inherit
            //Initial:  	 0
            //Applies to:  	 shapes and text content elements
            //Inherited:  	 yes
            //Percentages:  	 N/A
            //Media:  	 visual
            //Animatable:  	 yes
            //Computed value:  	 Specified value, except inherit

            //'stroke-linecap'
            //Value:  	 butt | round | square | inherit
            //Initial:  	 butt
            //Applies to:  	 shapes and text content elements
            //Inherited:  	 yes
            //Percentages:  	 N/A
            //Media:  	 visual
            //Animatable:  	 yes
            //Computed value:  	 Specified value, except inherit


            //'stroke-linejoin'
            //Value:  	 miter | round | bevel | inherit
            //Initial:  	 miter
            //Applies to:  	 shapes and text content elements
            //Inherited:  	 yes
            //Percentages:  	 N/A
            //Media:  	 visual
            //Animatable:  	 yes
            //Computed value:  	 Specified value, except inherit

            //'stroke-miterlimit'
            //Value:  	 <miterlimit> | inherit
            //Initial:  	 4
            //Applies to:  	 shapes and text content elements
            //Inherited:  	 yes
            //Percentages:  	 N/A
            //Media:  	 visual
            //Animatable:  	 yes
            //Computed value:  	 Specified value, except inherit

            case "stroke-opacity": {
                //'stroke-opacity'
                //Value:  	 <opacity-value> | inherit
                //Initial:  	 1
                //Applies to:  	 shapes and text content elements
                //Inherited:  	 yes
                //Percentages:  	 N/A
                //Media:  	 visual
                //Animatable:  	 yes
                //Computed value:  	 Specified value, except inherit
                strokeOpacity = toLength(r, readInheritAttribute(r, name, value, "1", ctx), 1);

                break;
            }

            case "stroke-width": {
                //'stroke-width'
                //Value:  	<length> | inherit
                //Initial:  	 1
                //Applies to:  	 shapes and text content elements
                //Inherited:  	 yes
                //Percentages:  	 N/A
                //Media:  	 visual
                //Animatable:  	 yes
                //Computed value:  	 Specified value, except inherit
                double doubleValue = toLength(r, readInheritAttribute(r, name, value, "1", ctx), 1);
                shape.setStrokeWidth(doubleValue);
                break;
            }
            default:
                break;
            }
        }
    }

    private Node readSolidColorElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private Node readStyleElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    /**
     * The reader must be positioned on START_ELEMENT of an svg element.
     *
     * @param r reader
     * @throws XMLStreamException
     */
    private Node readSvgElement(XMLStreamReader r) throws XMLStreamException, IOException {
        Context ctx = new Context();
        Viewport rootViewport = new Viewport();
        Viewport viewport = new Viewport();
        ctx.stack.add(rootViewport);
        ctx.stack.add(viewport);
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "height":
                viewport.height = toLength(r, value, rootViewport.heightPercentFactor);
                break;
            case "width":
                viewport.width = toLength(r, value, rootViewport.widthPercentFactor);
                break;
            case "viewBox":
                String[] viewBoxValues = toWSOrCommaSeparatedArray(value);
                if (viewBoxValues.length != 4) {
                    throw createException(r, "4 values expected for viewBox: " + value + ".");
                }
                viewport.viewBox.x = toLength(r, viewBoxValues[0], rootViewport.widthPercentFactor);
                viewport.viewBox.y = toLength(r, viewBoxValues[1], rootViewport.heightPercentFactor);
                viewport.viewBox.width = toLength(r, viewBoxValues[2], rootViewport.widthPercentFactor);
                viewport.viewBox.height = toLength(r, viewBoxValues[3], rootViewport.heightPercentFactor);
                break;
            case "baseProfile":
            case "version":
                // ignore
                break;
            default:
                LOGGER.warning("Unsupported attribute: " + name);
                break;
            }
        }

        computeViewportValues(viewport);

        StackPane region = new StackPane();
        region.setPrefWidth(viewport.width);
        region.setPrefHeight(viewport.height);
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx);
                if (child != null) {
                    child.setManaged(false);
                    region.getChildren().add(child);
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw new XMLStreamException("<svg>: start element or end element expected.");
            }
        }

        region.setClip(new Rectangle(0, 0, viewport.width, viewport.height));

        return region;
    }

    private Node readSwitchElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private Node readTextAreaElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private final static Logger LOGGER = Logger.getLogger(SvgTinySceneGraphReader.class.getName());

    private Text readTSpanElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Text shape = new Text();

        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "x":
                shape.setX(toLength(r, value, 1));
                break;
            case "y":
                shape.setY(toLength(r, value, 1));
                break;
            default:
                LOGGER.warning("Unsupported attribute in tspan element. name=" + name + ", value=" + value + ".");
                break;
            }
        }

        int eventType = r.next();
        while ((eventType == XMLStreamConstants.CHARACTERS)
                || (eventType == XMLStreamConstants.CDATA)
                || eventType == XMLStreamConstants.SPACE
                || eventType == XMLStreamConstants.PROCESSING_INSTRUCTION
                || eventType == XMLStreamConstants.COMMENT
        ) {
            switch (eventType) {
            case XMLStreamConstants.CHARACTERS:
            case XMLStreamConstants.CDATA:
            case XMLStreamConstants.SPACE:
                shape.setText(shape.getText() + r.getText());
                break;
            }
            eventType = r.next();
        }
        if (eventType != XMLStreamConstants.END_ELEMENT) {
            throw createException(r, "<tspan>: end element expected.");
        }

        // normalize text
        shape.setText(shape.getText().trim().replaceAll("\\s+", " "));

        return shape;
    }

    private Node readStopElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        requireEndElement(r, "<stop>: end element expected");
        return null;
    }

    SvgFontFamilyConverter fontFamilyConverter = new SvgFontFamilyConverter();

    private void readFontAttributes(XMLStreamReader r, Text shape, Context ctx) throws XMLStreamException {
        double fontSize = -1;
        ImmutableList<String> fontFamily = ImmutableLists.of("Arial");
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "font-family": {
                // 'font-family'
                // Value:  	[[ <family-name> |
                // <generic-family> ],]* [<family-name> |
                // <generic-family>] | inherit
                // Initial:  	depends on user agent
                // Applies to:  	text content elements
                // Inherited:  	yes
                // Percentages:  	N/A
                // Media:  	visual
                // Animatable:  	yes
                // Computed value:  	 Specified value, except inherit
                try {
                    fontFamily = fontFamilyConverter.fromString(value);
                } catch (ParseException | IOException e) {
                    LOGGER.warning(e.getMessage());
                }
                break;
            }
            case "font-size": {
                // 'font-size'
                // Value:  	<absolute-size> | <relative-gsize |
                // <length> | inherit
                // Initial:  	medium
                // Applies to:  	text content elements
                // Inherited:  	yes, the computed value is inherited
                // Percentages:  	N/A
                // Media:  	visual
                // Animatable:  	yes
                // Computed value:  	 Absolute length

                fontSize = toLength(r, readInheritAttribute(r, name, value, "1", ctx), 1);
                break;
            }
            // 'font-style'
            // Value:  	normal | italic | oblique | inherit
            // Initial:  	normal
            // Applies to:  	text content elements
            // Inherited:  	yes
            // Percentages:  	N/A
            // Media:  	visual
            // Animatable:  	yes
            // Computed value:  	 Specified value, except inherit


            //'font-variant'
            //Value:  	normal | small-caps | inherit
            //Initial:  	normal
            //Applies to:  	text content elements
            //Inherited:  	yes
            //Percentages:  	N/A
            //Media:  	visual
            //Animatable:  	no
            //Computed value:  	 Specified value, except inherit

            // 'font-weight'
            // Value:  	normal | bold | bolder | lighter | 100 | 200 | 300
            // | 400 | 500 | 600 | 700 | 800 | 900 | inherit
            // Initial:  	normal
            // Applies to:  	text content elements
            // Inherited:  	yes
            // Percentages:  	N/A
            // Media:  	visual
            // Animatable:  	yes
            // Computed value:  	 one of the legal numeric values, non-numeric
            // values shall be converted to numeric values according to the rules
            // defined below.

            // Note: text-decoration is an SVG 1.1 feature
            //'text-decoration'
            //Value:  	none | [ underline || overline || line-through || blink ] | inherit
            //Initial:  	none
            //Applies to:  	text content elements
            //Inherited:  	no (see prose)
            //Percentages:  	N/A
            //Media:  	visual
            //Animatable:  	yes
            default:
                LOGGER.warning("Unknown attribute in text element. name=" + name + ", value=" + value + ".");
                break;
            }
        }

        if (fontFamily != null && !fontFamily.isEmpty()) {
            shape.setFont(new Font(fontFamily.get(0), fontSize <= 0 ? 13 : fontSize));
        } else if (fontSize > 0) {
            shape.setFont(Font.font(null, fontSize));
        }

    }

    private Node readTextElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        // An SVG text element can result in one or more JavaFX text nodes.

        Text shape = new Text();
        shape.setManaged(false);
        boolean hasLocation = false;
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "x":
                hasLocation = true;
                shape.setX(toLength(r, value, 1));
                break;
            case "y":
                hasLocation = true;
                shape.setY(toLength(r, value, 1));
                break;
            default:
                break;
            }
        }
        readFontAttributes(r, shape, ctx);
        readShapeAttributes(r, shape, ctx);
        readTransformAttributes(r, shape, ctx);

        Loop:
        while (true) {
            switch (r.next()) {
            case XMLStreamReader.CHARACTERS:
                shape.setText(shape.getText() + r.getText());
                break;
            case XMLStreamReader.START_ELEMENT:
                Text child = readTSpanElement(r, ctx);
                if (!hasLocation) {
                    hasLocation = true;
                    shape.setX(child.getX());
                    shape.setY(child.getY());
                }
                shape.setText(shape.getText() + child.getText());
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<g>: start element or end element expected.");
            }
        }

        // normalize text
        shape.setText(shape.getText().trim().replaceAll("\\s+", " "));

        return shape;
    }

    private Node readTitleElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private Node readUseElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    /**
     * Returns a value as a length.
     * http://www.w3.org/TR/SVGMobile12/types.html#DataTypeLength
     */
    private double toLength(XMLStreamReader reader, String str, double percentFactor) throws XMLStreamException {
        double scaleFactor = 1d;
        if (str == null || str.length() == 0 || str.equals("none")) {
            return 0d;
        }

        if (str.endsWith("%")) {
            str = str.substring(0, str.length() - 1);
            scaleFactor = percentFactor;
        } else if (str.endsWith("px")) {
            str = str.substring(0, str.length() - 2);
        } else if (str.endsWith("pt")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 1.25;
        } else if (str.endsWith("pc")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 15;
        } else if (str.endsWith("mm")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 3.543307;
        } else if (str.endsWith("cm")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 35.43307;
        } else if (str.endsWith("in")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 90;
        } else if (str.endsWith("em")) {
            str = str.substring(0, str.length() - 2);
            // XXX - This doesn't work
            scaleFactor = 13;
        } else {
            scaleFactor = 1d;
        }

        return Double.parseDouble(str) * scaleFactor;
    }

    private final SvgPaintConverter paintConverter = new SvgPaintConverter(true);

    private Paint toPaint(XMLStreamReader r, String value, Context ctx) throws XMLStreamException {
        try {
            return paintConverter.fromString(value, ctx.idFactory);
        } catch (ParseException | IOException e) {
            LOGGER.warning("Could not read paint: " + value);
            return null;
        }
    }

    /**
     * Each SVG element establishes a new Viewport.
     */
    private static class Viewport {

        /**
         * The width of the Viewport.
         */
        public double width = 640d;
        /**
         * The height of the Viewport.
         */
        public double height = 480d;
        /**
         * The viewBox specifies the coordinate system within the Viewport.
         */
        public Rectangle2D.Double viewBox = new Rectangle2D.Double(0d, 0d, 640d, 480d);
        /**
         * Factor for percent values relative to Viewport width.
         */
        public double widthPercentFactor = 640d / 100d;
        /**
         * Factor for percent values relative to Viewport height.
         */
        public double heightPercentFactor = 480d / 100d;
        /**
         * Factor for number values in the user coordinate system.
         * This is the smaller value of width / viewBox.width and height / viewBox.height.
         */
        public double numberFactor;
        /**
         * http://www.w3.org/TR/SVGMobile12/coords.html#PreserveAspectRatioAttribute
         * XXX - use a more sophisticated variable here
         */
        public boolean isPreserveAspectRatio = true;
        private HashMap<Key<?>, Object> attributes = new HashMap<Key<?>, Object>();

        @Override
        public String toString() {
            return "widthPercentFactor:" + widthPercentFactor + ";"
                    + "heightPercentFactor:" + heightPercentFactor + ";"
                    + "numberFactor:" + numberFactor + ";"
                    + attributes;
        }
    }


    /**
     * Reads an attribute that is inherited.
     */
    @NonNull
    private String readInheritAttribute(XMLStreamReader r, String attributeName, @NonNull String value, @Nullable String initialValue, Context ctx) {
        return value;
    }
}
