/*
 * @(#)SvgSceneGraphReader.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import javafx.geometry.BoundingBox;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.Line;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.collection.StringKey;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssTransformConverter;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.svg.text.SvgPaintConverter;
import org.jhotdraw8.svg.text.SvgStrokeAlignmentConverter;

import javax.xml.stream.Location;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.List;
import java.util.StringTokenizer;
import java.util.logging.Logger;

/**
 * Reader for an SVG Tiny 1.2 file.
 * <p>
 * This is a minimalistic SVG reader for loading icons and other resources
 * into a Java FX scene graph.
 * <p>
 * Limitations:
 * <ul>
 *     <li>Stylesheets are not supported.</li>
 *     <li>Multi-line texts and texts on paths are not supported.</li>
 *     <li>Filter elements are not supported.</li>
 * </ul>
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

public class FXSvgTinyReader {
    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
    public static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
    private static final Key<String> TITLE_KEY = new StringKey("title");
    private static final Key<String> DESC_KEY = new StringKey("desc");
    private static final Key<String> FONT_FAMILY_KEY = new StringKey("fontFamily");
    private static final Key<String> CURSOR_KEY = new StringKey("cursor");
    private static final Key<String> ON_LOAD_KEY = new StringKey("onload");
    private static final Key<String> ON_CLICK_KEY = new StringKey("onclick");
    private static final Key<String> ON_MOUSE_OVER_KEY = new StringKey("onmouseover");
    private static final Key<String> ON_MOUSE_DOWN_KEY = new StringKey("onmousedown");
    private static final Key<String> BASE_PROFILE_KEY = new StringKey("baseProfile");
    private static final Key<BoundingBox> VIEW_BOX_KEY = new ObjectKey<BoundingBox>("baseProfile", BoundingBox.class);
    private static final Key<String> VERSION_KEY = new StringKey("version");
    private static final Key<Double> STROKE_OPACITY_KEY = new ObjectKey<>("strokeOpacity", Double.class);
    private static final Key<Double> FONT_SIZE_KEY = new ObjectKey<>("fontSize", Double.class);
    private static final Key<Double> PATH_LENGTH_KEY = new ObjectKey<>("pathLength", Double.class);
    private static final String NAMESPACE_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

    private static class Context {
        public IdResolver idFactory = new SimpleIdFactory();
    }


    /**
     * Returns a value as a String array.
     * The values are separated by whitespace or by commas with optional white
     * space.
     */
    public static String[] toWhitespaceOrCommaSeparatedArray(String str) {
        String[] result = str.split("(\\s*,\\s*|\\s+)");
        if (result.length == 1 && result[0].equals("")) {
            return new String[0];
        } else {
            return result;
        }
    }


    private XMLStreamException createException(XMLStreamReader r, String s) {
        return new XMLStreamException(s + " " + getLocation(r));
    }

    private XMLStreamException createException(XMLStreamReader r, String s, Throwable cause) {
        return new XMLStreamException(s + " " + getLocation(r), cause);
    }

    public Node read(Path path) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path))) {
            return read(in);
        }
    }

    public Node read(InputStream in) throws IOException {
        try {
            XMLInputFactory dbf = XMLInputFactory.newInstance();

            // We do not want that the reader creates a socket connection!
            dbf.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            dbf.setProperty(XMLInputFactory.IS_REPLACING_ENTITY_REFERENCES, false);
            dbf.setProperty(XMLInputFactory.SUPPORT_DTD, false);
            dbf.setXMLResolver((publicID,
                                systemID,
                                baseURI,
                                namespace) -> null
            );

            XMLStreamReader r = dbf.createXMLStreamReader(in);
            while (true) {
                switch (r.next()) {
                case XMLStreamReader.START_ELEMENT:
                    return readSvgRootElement(r);
                case XMLStreamConstants.DTD:
                case XMLStreamConstants.COMMENT:
                case XMLStreamConstants.PROCESSING_INSTRUCTION:
                    break;
                default:
                    throw new XMLStreamException("<svg> element expected. Found: " + r.getEventType());
                }
            }
        } catch (XMLStreamException e) {
            throw new IOException(e);
        }
    }


    private Node readCircleElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Circle node = new Circle();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "cx":
                    node.setCenterX(toLength(r, value, 1));
                    break;
                case "cy":
                    node.setCenterY(toLength(r, value, 1));
                    break;
                case "r":
                    node.setRadius(toLength(r, value, 1));
                    break;
                default:
                    if (!readShapeAttribute(r, node, ctx, namespace, localName, value)
                            && !readNodeAttribute(r, node, ctx, namespace, localName, value)
                    ) {
                        throw createException(r, "Unsupported attribute: " + namespace + ":" + localName + "=" + value + ".");
                    }
                    break;
                }
            } else {
                logWarning(r, "Skipping attribute: " + namespace + ":" + localName + "=" + value + ".");
            }
        }


        requireEndElement(r, "<ellipse>: end element expected.", node);
        return node;
    }

    private void logWarning(XMLStreamReader r, String message) {
        LOGGER.warning(message + " " + getLocation(r));
    }

    private Node readClipPathElement(XMLStreamReader r, Context ctx, Node parent) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx, parent);
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

    private Node readDefsElement(XMLStreamReader r, Context ctx, Node parent) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx, parent);
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


    private Node readElement(XMLStreamReader r, Context ctx, Node parent) throws XMLStreamException {
        Node f;
        if (!SVG_NAMESPACE.equals(r.getNamespaceURI())) {
            LOGGER.fine("Skipping element " + r.getName() + ". " + getLocation(r));
            skipElement(r, ctx);
            return null;
        }
        switch (r.getLocalName()) {
        case "clipPath":
            f = readClipPathElement(r, ctx, parent);
            break;
        case "circle":
            f = readCircleElement(r, ctx);
            break;
        case "defs":
            f = readDefsElement(r, ctx, parent);
            break;
        case "desc":
            f = readDescElement(r, ctx, parent);
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
        case "marker":
            f = readMarkerElement(r, ctx, parent);
            break;
        case "mask":
            f = readMaskElement(r, ctx, parent);
            break;
        case "path":
            f = readPathElement(r, ctx);
            break;
        case "pattern":
            f = readPatternElement(r, ctx, parent);
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
        case "script":
            f = readScriptElement(r, ctx);
            break;
        case "text":
            f = readTextElement(r, ctx);
            break;
        case "textArea":
            f = readTextAreaElement(r, ctx);
            break;
        case "title":
            f = readTitleElement(r, ctx, parent);
            break;
        case "use":
            f = readUseElement(r, ctx);
            break;
        case "a":
        case "switch":
        case "filter":
        case "metadata":
        default:
            LOGGER.fine("Skipping <" + r.getLocalName() + "> element. " + getLocation(r));
            skipElement(r, ctx);
            f = null;
            break;
        }
        return f;
    }

    private String getLocation(XMLStreamReader r) {
        Location location = r.getLocation();
        return location == null ? "" : "at [row,col]:[" + location.getLineNumber() + "," + location.getColumnNumber() + "]";
    }

    /**
     * Skips the current element and all its child elements.
     *
     * @param r   the reader
     * @param ctx the context
     * @throws XMLStreamException if a skipped element is in SVG namespace
     */
    private void skipElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        int depth = 1;// we are currently inside START_ELEMENT
        Loop:
        while (true) {
            switch (r.next()) {
            case XMLStreamReader.START_ELEMENT:
                if (SVG_NAMESPACE.equals(r.getNamespaceURI())) {
                    logWarning(r, "Skipping element " + r.getName() + ".");
                } else {
                    LOGGER.fine("Skipping element " + r.getName() + ".");
                }
                depth++;
                break;
            case XMLStreamReader.END_ELEMENT:
                depth--;
                if (depth == 0) {
                    break Loop;
                }
                break;
            default:
                break;
            }
        }

    }

    private Node readEllipseElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Ellipse node = new Ellipse();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {

                switch (localName) {
                case "cx":
                    node.setCenterX(toLength(r, value, 1));
                    break;
                case "cy":
                    node.setCenterY(toLength(r, value, 1));
                    break;
                case "rx":
                    node.setRadiusX(toLength(r, value, 1));
                    break;
                case "ry":
                    node.setRadiusY(toLength(r, value, 1));
                    break;
                default:
                    if (!readShapeAttribute(r, node, ctx, namespace, localName, value)
                            && !readNodeAttribute(r, node, ctx, namespace, localName, value)
                    ) {
                        throw createException(r, "Unsupported attribute: " + namespace + ":" + localName + "=" + value + ".");
                    }
                    break;
                }
            } else {
                logWarning(r, "Skipping attribute: " + namespace + ":" + localName + "=" + value + ".");
            }
        }

        requireEndElement(r, "<ellipse>: end element expected.", node);
        return node;
    }

    private Node readGElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Group node = new Group();
        node.setAutoSizeChildren(false);
        node.setManaged(false);
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                default:
                    if (!readNodeAttribute(r, node, ctx, namespace, localName, value)
                    ) {
                        throw createException(r, "Unsupported attribute: " + namespace + ":" + localName + "=" + value + ".");
                    }
                    break;
                }
            } else {
                logWarning(r, "Skipping attribute: " + namespace + ":" + localName + "=" + value + ".");
            }
        }

        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx, node);
                if (child != null) {
                    node.getChildren().add(child);
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<g>: start element or end element expected.");
            }
        }
        return node;
    }

    private Node readPatternElement(XMLStreamReader r, Context ctx, Node parent) throws XMLStreamException {

        readTransformAttributes(r, parent, ctx);
        List<Node> patternNodes = new ArrayList<>();
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx, parent);
                if (child != null) {
                    patternNodes.add(child);
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<pattern>: start element or end element expected.");
            }
        }
        return null;
    }

    private Node readMarkerElement(XMLStreamReader r, Context ctx, Node parent) throws XMLStreamException {

        readTransformAttributes(r, parent, ctx);
        List<Node> patternNodes = new ArrayList<>();
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx, parent);
                if (child != null) {
                    patternNodes.add(child);
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<marker>: start element or end element expected.");
            }
        }
        return null;
    }

    private Node readMaskElement(XMLStreamReader r, Context ctx, Node parent) throws XMLStreamException {

        readTransformAttributes(r, parent, ctx);
        List<Node> maskNodes = new ArrayList<>();
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx, parent);
                if (child != null) {
                    maskNodes.add(child);
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<mask>: start element or end element expected.");
            }
        }
        return null;
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
        Line node = new Line();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            String namespace = r.getAttributeNamespace(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "x1":
                    node.setStartX(toLength(r, value, 1));
                    break;
                case "y1":
                    node.setStartY(toLength(r, value, 1));
                    break;
                case "x2":
                    node.setEndX(toLength(r, value, 1));
                    break;
                case "y2":
                    node.setEndY(toLength(r, value, 1));
                    break;
                case "pathLength":
                    PATH_LENGTH_KEY.set(node.getProperties(), toLength(r, value, 1));
                    break;
                default:
                    if (!readShapeAttribute(r, node, ctx, namespace, localName, value)
                            && !readNodeAttribute(r, node, ctx, namespace, localName, value)
                    ) {
                        throw createException(r, "Unsupported attribute " + namespace + ":" + localName + "=\"" + value + "\".");
                    }
                    break;
                }
            } else {
                logWarning(r, "Skipping attribute " + namespace + ":" + localName + "=\"" + value + "\".");
            }
        }


        requireEndElement(r, "<line>: end element expected.", node);
        return node;
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
        javafx.scene.shape.Path node = new javafx.scene.shape.Path();
        node.setStroke(null);
        node.setFill(Color.BLACK);

        node.setManaged(false);
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            String namespace = r.getAttributeNamespace(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (name) {
                case "d":
                    FXPathBuilder builder = new FXPathBuilder();
                    try {
                        Shapes.buildFromSvgString(builder, value);
                    } catch (ParseException e) {
                        logWarning(r, "Skipping illegal path. " + e.getMessage());
                    }
                    //svgPath.setContent(r.getAttributeValue(i));
                    node.getElements().setAll(builder.getElements());
                    break;
                default:
                    if (!readShapeAttribute(r, node, ctx, namespace, name, value)
                            && !readNodeAttribute(r, node, ctx, namespace, name, value)) {
                        throw createException(r, "Unsupported attribute: " + r.getAttributeName(i));
                    }
                    break;
                }
            } else {
                logWarning(r, "Skipping attribute " + namespace + ":" + name + "=\"" + value + "\".");
            }
        }

        requireEndElement(r, "<path>: end element expected.", node);

        return node;
    }

    private Node readPolygonElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Polygon node = new Polygon();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            String namespace = r.getAttributeNamespace(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "points":
                    node.getPoints().addAll(
                            toDoubles(r, value, ctx));
                    break;
                default:
                    if (!readShapeAttribute(r, node, ctx, namespace, localName, value)
                            && !readNodeAttribute(r, node, ctx, namespace, localName, value)) {
                        throw createException(r, "Unsupported attribute: " + r.getAttributeName(i));
                    }
                    break;
                }
            } else {
                logWarning(r, "Skipping attribute: " + namespace + ":" + localName + "=" + value + ".");
            }

        }

        requireEndElement(r, "<polygon>: end element expected.", node);
        return node;
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
        Polyline node = new Polyline();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            String namespace = r.getAttributeNamespace(i);
            switch (name) {
            case "points":
                node.getPoints().addAll(toDoubles(r, value, ctx));
                break;
            default:
                if (!readShapeAttribute(r, node, ctx, namespace, name, value)
                        && !readNodeAttribute(r, node, ctx, namespace, name, value)
                ) {
                    throw createException(r, "Unsupported attribute: " + r.getAttributeName(i));
                }
                break;
            }
        }

        requireEndElement(r, "<polyline>: end element expected.", node);
        return node;
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
        Rectangle node = new Rectangle();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (name) {
                case "x":
                    node.setX(toLength(r, value, 1));
                    break;
                case "y":
                    node.setY(toLength(r, value, 1));
                    break;
                case "rx":
                    node.setArcWidth(toLength(r, value, 1));
                    break;
                case "ry":
                    node.setArcHeight(toLength(r, value, 1));
                    break;
                case "width":
                    node.setWidth(toLength(r, value, 1));
                    break;
                case "height":
                    node.setHeight(toLength(r, value, 1));
                    break;
                default:
                    if (!readNodeAttribute(r, node, ctx, namespace, name, value)
                            && !readShapeAttribute(r, node, ctx, namespace, name, value)) {
                        throw createException(r, "Unsupported attribute: " + r.getAttributeName(i));
                    }
                    break;
                }
            }
        }


        requireEndElement(r, "<rect>: end element expected.", node);
        return node;
    }

    private void requireEndElement(XMLStreamReader r, String s, Node node) throws XMLStreamException {
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
     * FIXME delete me
     */
    private void readTransformAttributes(XMLStreamReader r, Node node, Context ctx) throws XMLStreamException {
        try {
            for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
                String name = r.getAttributeLocalName(i);
                String value = r.getAttributeValue(i);
                String namespace = r.getAttributeNamespace(i);
                if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                    switch (name) {
                    case "transform":
                        ImmutableList<Transform> transforms = transformsConverter.fromString(value);
                        if (transforms != null) {
                            node.getTransforms().addAll(transforms.asList());
                        }
                        break;
                    }
                }
            }

        } catch (ParseException | IOException e) {
            throw createException(r, e.getMessage());
        }
    }

    private final SvgStrokeAlignmentConverter strokAlignmentConverter = new SvgStrokeAlignmentConverter(false);


    private boolean readNodeAttribute(XMLStreamReader r, Node node, Context ctx, String namespace, String name, String value) throws XMLStreamException {
        if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
            switch (name) {
            case "id":
                node.setId(value);
                return true;
            case "style":
                node.setStyle(value);
                return true;
            case "class":
                node.getStyleClass().addAll(toWhitespaceOrCommaSeparatedArray(value));
                return true;
            case "cursor":
                CURSOR_KEY.put(node.getProperties(), value);
                return true;
            case "onload":
                ON_LOAD_KEY.put(node.getProperties(), value);
                return true;
            case "onclick":
                ON_CLICK_KEY.put(node.getProperties(), value);
                return true;
            case "onmouseover":
                ON_MOUSE_OVER_KEY.put(node.getProperties(), value);
                return true;
            case "onmousedown":
                ON_MOUSE_DOWN_KEY.put(node.getProperties(), value);
                return true;
            case "clip-path":
            case "opacity":
                return true;
            case "transform":
                ImmutableList<Transform> transforms = null;
                try {
                    transforms = transformsConverter.fromString(value);
                } catch (ParseException | IOException e) {
                    logWarning(r, "Skipping illegal transform attribute. " + e.getMessage());
                }
                if (transforms != null) {
                    node.getTransforms().addAll(transforms.asList());
                }
                return true;
            }
        }
        return false;
    }

    private boolean readShapeAttribute(XMLStreamReader r, Shape shape, Context ctx, String namespace, String name, String value) throws XMLStreamException {
        if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
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
                return true;

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
                return true;

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
                return true;
            case "stroke-dasharray":
                //'stroke-dasharray'
                //Value:  	 none | <dasharray> | inherit
                //Initial:  	 none
                //Applies to:  	 shapes and text content elements
                //Inherited:  	 yes
                //Percentages:  	 N/A
                //Media:  	 visual
                //Animatable:  	 yes (non-additive)
                //Computed value:  	 Specified value, except inherit
                shape.getStrokeDashArray().setAll(toDoubles(r, value, ctx));
                return true;

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
                double strokeOpacity = toLength(r, readInheritAttribute(r, name, value, "1", ctx), 1);
                STROKE_OPACITY_KEY.put(shape.getProperties(), strokeOpacity);
                return true;
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
                return true;
            }
            case "stroke-linecap":
                return true;

            default:
                break;
            }
        }
        return false;
    }

    private Node readSolidColorElement(XMLStreamReader r, Context ctx) {
        return null;
    }


    /**
     * The reader must be positioned on START_ELEMENT of the root SVG element.
     *
     * @param r reader
     * @throws XMLStreamException
     */
    private Node readSvgRootElement(XMLStreamReader r) throws XMLStreamException, IOException {
        Context ctx = new Context();

        StackPane node = new StackPane();

        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "height":
                    node.setPrefHeight(toLength(r, value, 1));
                    break;
                case "width":
                    node.setPrefWidth(toLength(r, value, 1));
                    break;
                case "viewBox":
                    String[] viewBoxValues = toWhitespaceOrCommaSeparatedArray(value);
                    if (viewBoxValues.length != 4) {
                        throw createException(r, "4 values expected for viewBox: " + value + ".");
                    }
                    VIEW_BOX_KEY.set(node.getProperties(), new BoundingBox(
                            toLength(r, viewBoxValues[0], 1),
                            toLength(r, viewBoxValues[1], 1),
                            toLength(r, viewBoxValues[2], 1),
                            toLength(r, viewBoxValues[3], 1)));
                    break;
                case "baseProfile":
                    if (!"tiny".equals(value)) {
                        logWarning(r, "Unsupported baseProfile=\"" + value + "\".");
                    }
                    BASE_PROFILE_KEY.put(node.getProperties(), value);
                    break;
                case "version":
                    switch (value) {
                    case "1.1":
                    case "1.2":
                        break;
                    default:
                        logWarning(r, "Unsupported version=\"" + value + "\".");
                        break;
                    }
                    VERSION_KEY.put(node.getProperties(), value);
                    break;
                default:
                    if (!readNodeAttribute(r, node, ctx, namespace, localName, value)) {
                        throw createException(r, "Unsupported attribute: " + r.getAttributeName(i));
                    }
                }
            } else {
                logWarning(r, "Ignoring attribute: " + r.getAttributeName(i));
            }
        }


        readSubtree(r, ctx, node);

        node.setClip(new Rectangle(0, 0, node.getPrefWidth(), node.getPrefHeight()));

        return node;
    }

    private void readSubtree(XMLStreamReader r, Context ctx, StackPane node) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.next()) {
            case XMLStreamReader.START_ELEMENT:
                Node child = readElement(r, ctx, node);
                if (child != null) {
                    child.setManaged(false);
                    node.getChildren().add(child);
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            case XMLStreamConstants.SPACE:
                break;
            case XMLStreamConstants.CHARACTERS:
                if (!r.getText().trim().isEmpty()) {
                    logWarning(r, "<svg>: warning skipping characters: \"" + r.getText() + "\".");
                }
                break;
            case XMLStreamConstants.CDATA:
                logWarning(r, "<svg>: warning skipping cdata: \"" + r.getText() + "\".");
                break;
            case XMLStreamConstants.COMMENT:
                break;
            default:
                throw createException(r, "<svg>: start element or end element expected.");
            }
        }
    }

    private Node readScriptElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        skipElement(r, ctx);
        return null;
    }

    private Node readTextAreaElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private static final Logger LOGGER = Logger.getLogger(FXSvgTinyReader.class.getName());

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
                logWarning(r, "Unsupported attribute in tspan element. name=" + name + ", value=" + value + ".");
                break;
            }
        }

        shape.setText(readTextContent(r));

        // normalize text
        shape.setText(shape.getText().trim().replaceAll("\\s+", " "));

        return shape;
    }

    private Text readTextPathElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
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
                logWarning(r, "Unsupported attribute in tspan element. name=" + name + ", value=" + value + ".");
                break;
            }
        }

        shape.setText(readTextContent(r));

        // normalize text
        shape.setText(shape.getText().trim().replaceAll("\\s+", " "));

        return shape;
    }

    /**
     * Reads all text content until ELEMENT_END is encountered.
     */
    private @NonNull String readTextContent(XMLStreamReader r) throws XMLStreamException {
        StringBuilder buf = new StringBuilder();
        int depth = 1;
        Loop:
        for (int eventType = r.next(); eventType != XMLStreamConstants.END_DOCUMENT; eventType = r.next()) {
            switch (eventType) {
            case XMLStreamConstants.CHARACTERS:
            case XMLStreamConstants.CDATA:
            case XMLStreamConstants.SPACE:
                if (depth == 1) {
                    buf.append(r.getText());
                }
                break;
            case XMLStreamConstants.START_ELEMENT:
                logWarning(r, "Warning skipping element <" + r.getName() + ">.");
                depth++;
                break;
            case XMLStreamConstants.END_ELEMENT:
                depth--;
                if (depth == 0) {
                    break Loop;
                }
                break;
            default:
                break;
            }
        }
        return buf.toString();
    }

    private Node readStopElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        requireEndElement(r, "<stop>: end element expected", null);
        return null;
    }

    SvgFontFamilyConverter fontFamilyConverter = new SvgFontFamilyConverter();

    private boolean readFontAttribute(XMLStreamReader r, Text node, Context ctx, String namespace, String name, String value) throws XMLStreamException {
        if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
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
                FONT_FAMILY_KEY.set(node.getProperties(), value);
                return true;
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
                FONT_SIZE_KEY.set(node.getProperties(),
                        toLength(r, readInheritAttribute(r, name, value, "1", ctx), 1));
                return true;
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
            }
        }
        return false;


    }

    private Node readTextElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        // An SVG text element can result in one or more JavaFX text nodes.

        Text node = new Text();
        node.setManaged(false);
        boolean hasLocation = false;
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String localName = r.getAttributeLocalName(i);
            String namespace = r.getAttributeNamespace(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "x":
                    hasLocation = true;
                    node.setX(toDoubles(r, value, ctx).get(0));
                    break;
                case "y":
                    hasLocation = true;
                    node.setY(toDoubles(r, value, ctx).get(0));
                    break;
                default:
                    if (!readShapeAttribute(r, node, ctx, namespace, localName, value)
                            && !readNodeAttribute(r, node, ctx, namespace, localName, value)
                            && !readFontAttribute(r, node, ctx, namespace, localName, value)
                    ) {
                        throw createException(r, "Unknown attribute " + namespace + ":" + localName + "=\"" + value + "\".");
                    }
                    break;
                }
            } else {
                logWarning(r, "Skipping attribute " + namespace + ":" + localName + "=\"" + value + "\".");
            }
        }

        Loop:
        while (true) {
            switch (r.next()) {
            case XMLStreamReader.CHARACTERS:
                node.setText(node.getText() + r.getText());
                break;
            case XMLStreamReader.START_ELEMENT:
                if (SVG_NAMESPACE.equals(r.getNamespaceURI())) {
                    String localName = r.getLocalName();
                    switch (localName) {
                    case "tspan": {
                        Text child = readTSpanElement(r, ctx);
                        if (!hasLocation) {
                            hasLocation = true;
                            node.setX(child.getX());
                            node.setY(child.getY());
                        }
                        node.setText(node.getText() + child.getText());
                        break;
                    }
                    case "textPath": {
                        Text child = readTextPathElement(r, ctx);
                        if (!hasLocation) {
                            hasLocation = true;
                            node.setX(child.getX());
                            node.setY(child.getY());
                        }
                        node.setText(node.getText() + child.getText());
                        break;
                    }
                    default:
                        // Accept non-node generating elements, like linearGradient.
                        Node childNode = readElement(r, ctx, node);
                        if (childNode != null) {
                            throw createException(r, "<text>: Unsupported child element: " + localName);
                        }
                    }
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<text>: start element or end element expected.");
            }
        }

        // normalize text
        node.setText(node.getText().trim().replaceAll("\\s+", " "));

        return node;
    }

    private Node readTitleElement(XMLStreamReader r, Context ctx, Node parent) throws XMLStreamException {
        String title = readTextContent(r);
        TITLE_KEY.put(parent.getProperties(), title);
        return null;
    }

    private Node readDescElement(XMLStreamReader r, Context ctx, Node parent) throws XMLStreamException {
        String title = readTextContent(r);
        DESC_KEY.put(parent.getProperties(), title);
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
        if (str == null || str.length() == 0 || str.equals("none")) {
            return 0d;
        }

        final double scaleFactor;
        if (str.endsWith("%")) {
            str = str.substring(0, str.length() - 1);
            scaleFactor = percentFactor;
        } else if (str.endsWith("px")) {
            str = str.substring(0, str.length() - 2);
            scaleFactor = 1.0;
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
            logWarning(r, "Could not read paint: " + value);
            return null;
        }
    }


    /**
     * Reads an attribute that is inherited.
     */
    private @NonNull String readInheritAttribute(XMLStreamReader r, String attributeName, @NonNull String value, @Nullable String initialValue, Context ctx) {
        return value;
    }
}
