/*
 * @(#)SvgSceneGraphReader.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.svg.io;

import javafx.scene.paint.Paint;
import javafx.scene.text.Text;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.collection.ObjectKey;
import org.jhotdraw8.collection.StringKey;
import org.jhotdraw8.concurrent.CheckedRunnable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.css.text.CssTransformConverter;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.io.IdResolver;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.svg.figure.SvgCircleFigure;
import org.jhotdraw8.svg.figure.SvgDrawing;
import org.jhotdraw8.svg.figure.SvgEllipseFigure;
import org.jhotdraw8.svg.figure.SvgGFigure;
import org.jhotdraw8.svg.figure.SvgInheritableFigure;
import org.jhotdraw8.svg.figure.SvgLineFigure;
import org.jhotdraw8.svg.figure.SvgPathFigure;
import org.jhotdraw8.svg.figure.SvgPathLengthFigure;
import org.jhotdraw8.svg.figure.SvgPolygonFigure;
import org.jhotdraw8.svg.figure.SvgPolylineFigure;
import org.jhotdraw8.svg.figure.SvgRectFigure;
import org.jhotdraw8.svg.figure.SvgTransformableFigure;
import org.jhotdraw8.svg.text.SvgPaintConverter;
import org.jhotdraw8.svg.text.SvgStrokeAlignmentConverter;
import org.jhotdraw8.xml.text.XmlNumberConverter;

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
import java.util.Arrays;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.StringTokenizer;
import java.util.logging.Logger;

import static org.jhotdraw8.svg.figure.SvgElementFigure.DESC_KEY;
import static org.jhotdraw8.svg.figure.SvgElementFigure.TITLE_KEY;

/**
 * Reader for an SVG Tiny 1.2 file.
 * <p>
 * <p>
 * FIXME this design does not work - the feature disparity between
 * SVG and JavaFX is too bit. Must reimplement this with
 * Figures in-between.
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

public class FigureSvgReader {
    public static final String SVG_NAMESPACE = "http://www.w3.org/2000/svg";
    public static final String XLINK_NAMESPACE = "http://www.w3.org/1999/xlink";
    private static final Key<String> FONT_FAMILY_KEY = new StringKey("fontFamily");
    private static final Key<String> CURSOR_KEY = new StringKey("cursor");
    private static final Key<String> ON_LOAD_KEY = new StringKey("onload");
    private static final Key<String> ON_CLICK_KEY = new StringKey("onclick");
    private static final Key<String> ON_MOUSE_OVER_KEY = new StringKey("onmouseover");
    private static final Key<String> ON_MOUSE_DOWN_KEY = new StringKey("onmousedown");
    private static final Key<Double> STROKE_OPACITY_KEY = new ObjectKey<>("strokeOpacity", Double.class);
    private static final Key<Double> FONT_SIZE_KEY = new ObjectKey<>("fontSize", Double.class);
    private static final Key<Double> PATH_LENGTH_KEY = new ObjectKey<>("pathLength", Double.class);
    private static final String NAMESPACE_NAMESPACE = "http://www.w3.org/XML/1998/namespace";

    private static class Context {
        public IdResolver idFactory = new SimpleIdFactory();
        List<String> internalStylesheets = new ArrayList<>();

        List<CheckedRunnable> secondPass = new ArrayList<>();

        public void addInternalStylesheet(String stylesheet) {
            internalStylesheets.add(stylesheet);
        }
    }

    /**
     * Set of inheritable properties from:<br>
     * <a href="https://www.w3.org/TR/SVGTiny12/attributeTable.html#PropertyTable">L.1 Property Table</a>
     */
    private static final Set<String> inheritablePropertys = new LinkedHashSet<>();

    static {
        inheritablePropertys.addAll(Arrays.asList("color",
                "color-rendering",
                "direction",
                "display-align",
                "fill",
                "fill-opacity",
                "fill-rule",
                "font-family",
                "font-size",
                "font-style",
                "font-variant",
                "font-weight",
                "image-rendering",
                "line-increment",
                "pointer-events",
                "shape-rendering",
                "stroke",
                "stroke-dasharray",
                "stroke-dashoffset",
                "stroke-linecap",
                "stroke-linejoin",
                "stroke-miterlimit",
                "stroke-opacity",
                "stroke-width",
                "text-align",
                "text-anchor",
                "text-rendering",
                "visibility"));
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

    public Figure read(Path path) throws IOException {
        try (BufferedInputStream in = new BufferedInputStream(Files.newInputStream(path))) {
            return read(in);
        }
    }

    public Figure read(InputStream in) throws IOException {
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

    private Figure readAElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private Figure readCircleElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SvgCircleFigure node = new SvgCircleFigure();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "cx":
                    node.set(SvgCircleFigure.CX, toLength(r, value));
                    break;
                case "cy":
                    node.set(SvgCircleFigure.CY, toLength(r, value));
                    break;
                case "r":
                    node.set(SvgCircleFigure.R, toLength(r, value));
                    break;
                default:
                    if (!readPathLengthAttribute(r, node, ctx, namespace, localName, value)
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

    private Figure readClipPathElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Figure child = readElement(r, ctx, parent);
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

    private Figure readDefsElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Figure child = readElement(r, ctx, parent);
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

    private Figure readFilterElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                if (SVG_NAMESPACE.equals(r.getNamespaceURI())) {
                    String localName = r.getLocalName();
                    switch (localName) {
                    case "feBlend":
                        readFeBlendElement(r, ctx, parent);
                        break;
                    case "feColorMatrix":
                        readFeColorMatrixElement(r, ctx, parent);
                        break;
                    case "feComposite":
                        readFeCompositeElement(r, ctx, parent);
                        break;
                    case "feComponentTransfer":
                        readFeComponentTransferElement(r, ctx, parent);
                        break;
                    case "feConvolveMatrix":
                        readFeConvolveMatrixElement(r, ctx, parent);
                        break;
                    case "feFlood":
                        readFeFloodElement(r, ctx, parent);
                        break;
                    case "feGaussianBlur":
                        readFeGaussianBlurElement(r, ctx, parent);
                        break;
                    case "feImage":
                        readFeImageElement(r, ctx, parent);
                        break;
                    case "feMerge":
                        readFeMergeElement(r, ctx, parent);
                        break;
                    case "feMorphology":
                        readFeMorphologyElement(r, ctx, parent);
                        break;
                    case "feOffset":
                        readFeOffsetElement(r, ctx, parent);
                        break;
                    case "feSpecularLighting":
                        readFeSpecularLightingElement(r, ctx, parent);
                        break;
                    case "feTile":
                        readFeTileElement(r, ctx, parent);
                        break;
                    case "feTurbulence":
                        readFeTurbulenceElement(r, ctx, parent);
                        break;
                    default:
                        // Accept non-node generating elements, like linearGradient.
                        Figure node = readElement(r, ctx, parent);
                        if (node != null) {
                            throw createException(r, "<filter>: Unsupported child element: " + localName);
                        }
                    }
                }
                break;
            case XMLStreamReader.END_ELEMENT:
                break Loop;
            default:
                throw createException(r, "<filter>: start element or end element expected.");
            }
        }
        return null;
    }

    private void readFeOffsetElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feOffset not implemented.");
        skipElement(r, ctx);
    }

    private void readFeMergeElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feMerge not implemented.");
        skipElement(r, ctx);
    }

    private void readFeMorphologyElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feMorphology not implemented.");
        skipElement(r, ctx);
    }

    private void readFeColorMatrixElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feColorMatrix not implemented.");
        skipElement(r, ctx);
    }

    private void readFeGaussianBlurElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feGaussianBlur not implemented.");
        skipElement(r, ctx);
    }

    private void readFeSpecularLightingElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feSpecularLighting not implemented.");
        skipElement(r, ctx);
    }

    private void readFeBlendElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feBlend not implemented.");
        skipElement(r, ctx);
    }

    private void readFeTurbulenceElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feTurbulence not implemented.");
        skipElement(r, ctx);
    }

    private void readFeTileElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feTile not implemented.");
        skipElement(r, ctx);
    }

    private void readFeFloodElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feFlood not implemented.");
        skipElement(r, ctx);
    }

    private void readFeCompositeElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feComposite not implemented.");
        skipElement(r, ctx);
    }

    private void readFeComponentTransferElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feComponentTransfer not implemented.");
        skipElement(r, ctx);
    }

    private void readFeConvolveMatrixElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feConvolveMatrix not implemented.");
        skipElement(r, ctx);
    }

    private void readFeImageElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        logWarning(r, "Read feImage not implemented.");
        skipElement(r, ctx);
    }

    private Figure readElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        Figure f;
        if (!SVG_NAMESPACE.equals(r.getNamespaceURI())) {
            LOGGER.fine("Skipping element " + r.getName() + ". " + getLocation(r));
            skipElement(r, ctx);
            return null;
        }
        switch (r.getLocalName()) {
        case "a":
            f = readAElement(r, ctx);
            break;
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
        case "filter":
            f = readFilterElement(r, ctx, parent);
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
            f = readTitleElement(r, ctx, parent);
            break;
        case "use":
            f = readUseElement(r, ctx);
            break;
        case "style":
            f = readStyleElement(r, ctx);
            break;
        case "metadata":
            f = readMetadataElement(r, ctx);
            break;
        default:
            throw createException(r, "Unknown element " + r.getLocalName());
        }
        return f;
    }

    private Figure readMetadataElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        LOGGER.fine("Skipping <metadata> element. " + getLocation(r));
        skipElement(r, ctx);
        return null;
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

    private Figure readEllipseElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SvgEllipseFigure node = new SvgEllipseFigure();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {

                switch (localName) {
                case "cx":
                    node.set(SvgEllipseFigure.CX, toLength(r, value));
                    break;
                case "cy":
                    node.set(SvgEllipseFigure.CY, toLength(r, value));
                    break;
                case "rx":
                    node.set(SvgEllipseFigure.RX, toLength(r, value));
                    break;
                case "ry":
                    node.set(SvgEllipseFigure.RY, toLength(r, value));
                    break;
                default:
                    if (!readPathLengthAttribute(r, node, ctx, namespace, localName, value)
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

    private Figure readGElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SvgGFigure node = new SvgGFigure();
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
                Figure child = readElement(r, ctx, node);
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

    private Figure readPatternElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {

        readTransformAttributes(r, parent, ctx);
        List<Figure> patternNodes = new ArrayList<>();
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Figure child = readElement(r, ctx, parent);
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

    private Figure readMarkerElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {

        readTransformAttributes(r, parent, ctx);
        List<Figure> patternNodes = new ArrayList<>();
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Figure child = readElement(r, ctx, parent);
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

    private Figure readMaskElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {

        readTransformAttributes(r, parent, ctx);
        List<Figure> maskNodes = new ArrayList<>();
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Figure child = readElement(r, ctx, parent);
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

    private Figure readImageElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    /**
     * Reads a color attribute that is inherited.
     * This is similar to {@code readInheritAttribute}, but takes care of the
     * "currentColor" magic attribute value.
     */
    private @Nullable String readInheritColorAttribute(@NonNull XMLStreamReader r, @NonNull String attributeName, @NonNull String value, @Nullable String initialValue, Context ctx) {
        return value;
    }

    private Figure readLineElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SvgLineFigure node = new SvgLineFigure();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            String namespace = r.getAttributeNamespace(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "x1":
                    node.set(SvgLineFigure.X1, toLength(r, value));
                    break;
                case "y1":
                    node.set(SvgLineFigure.Y1, toLength(r, value));
                    break;
                case "x2":
                    node.set(SvgLineFigure.X2, toLength(r, value));
                    break;
                case "y2":
                    node.set(SvgLineFigure.Y2, toLength(r, value));
                    break;
                default:
                    if (!readPathLengthAttribute(r, node, ctx, namespace, localName, value)
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

    private Figure readLinearGradientElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Figure child = readStopElement(r, ctx);
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

    private Figure readPathElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SvgPathFigure node = new SvgPathFigure();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            String namespace = r.getAttributeNamespace(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (name) {
                case "d":
                    node.set(SvgPathFigure.D, value);
                    break;
                default:
                    if (!readPathLengthAttribute(r, node, ctx, namespace, name, value)
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

    private Figure readPolygonElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SvgPolygonFigure node = new SvgPolygonFigure();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            String namespace = r.getAttributeNamespace(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "points":
                    node.set(SvgPolygonFigure.POINTS,
                            ImmutableLists.ofCollection(toDoubles(r, value, ctx)));
                    break;
                default:
                    if (!readPathLengthAttribute(r, node, ctx, namespace, localName, value)
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
            points.add(toNumber(r, tt.nextToken()));
        }
        return points;
    }

    private Double toNumber(XMLStreamReader r, String nextToken) throws XMLStreamException {
        try {
            return numberConverter.fromString(nextToken).doubleValue();
        } catch (ParseException | IOException e) {
            throw createException(r, e.getMessage());
        }
    }

    private Figure readPolylineElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SvgPolylineFigure node = new SvgPolylineFigure();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            String namespace = r.getAttributeNamespace(i);
            switch (name) {
            case "points":
                node.set(SvgPolylineFigure.POINTS, ImmutableLists.ofCollection(toDoubles(r, value, ctx)));
                break;
            default:
                if (!readPathLengthAttribute(r, node, ctx, namespace, name, value)
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

    private Figure readRadialGradientElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Loop:
        while (true) {
            switch (r.nextTag()) {
            case XMLStreamReader.START_ELEMENT:
                Figure child = readStopElement(r, ctx);
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

    private Figure readRectElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        SvgRectFigure node = new SvgRectFigure();
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (name) {
                case "x":
                    node.set(SvgRectFigure.X, toLength(r, value));
                    break;
                case "y":
                    node.set(SvgRectFigure.Y, toLength(r, value));
                    break;
                case "rx":
                    node.set(SvgRectFigure.RX, toLength(r, value));
                    break;
                case "ry":
                    node.set(SvgRectFigure.RY, toLength(r, value));
                    break;
                case "width":
                    node.set(SvgRectFigure.WIDTH, toLength(r, value));
                    break;
                case "height":
                    node.set(SvgRectFigure.HEIGHT, toLength(r, value));
                    break;
                default:
                    if (!readNodeAttribute(r, node, ctx, namespace, name, value)
                            && !readPathLengthAttribute(r, node, ctx, namespace, name, value)) {
                        throw createException(r, "Unsupported attribute: " + r.getAttributeName(i));
                    }
                    break;
                }
            }
        }


        requireEndElement(r, "<rect>: end element expected.", node);
        return node;
    }

    private void requireEndElement(XMLStreamReader r, String s, Figure node) throws XMLStreamException {
        switch (r.nextTag()) {
        case XMLStreamReader.END_ELEMENT:
            break;
        default:
            throw createException(r, s);
        }
    }

    private Figure readSvgElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    final CssListConverter<Transform> transformsConverter = new CssListConverter<>(new CssTransformConverter(false));

    /**
     * FIXME delete me
     */
    private void readTransformAttributes(XMLStreamReader r, Figure node, Context ctx) throws XMLStreamException {
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
                            node.set(SvgTransformableFigure.TRANSFORMS, transforms);
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


    private boolean readNodeAttribute(XMLStreamReader r, Figure node, Context ctx, String namespace, String name, String value) throws XMLStreamException {
        if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
            try {
                switch (name) {
                case "id":
                    node.set(StyleableFigure.ID, value);
                    break;
                case "style":
                    node.set(StyleableFigure.STYLE, value);
                    break;
                case "class":
                    node.set(StyleableFigure.STYLE_CLASS,
                            ImmutableLists.of(toWhitespaceOrCommaSeparatedArray(value)));
                    break;
                case "cursor":
                    CURSOR_KEY.put(node.getProperties(), value);
                    break;
                case "onload":
                    ON_LOAD_KEY.put(node.getProperties(), value);
                    break;
                case "onclick":
                    ON_CLICK_KEY.put(node.getProperties(), value);
                    break;
                case "onmouseover":
                    ON_MOUSE_OVER_KEY.put(node.getProperties(), value);
                    break;
                case "onmousedown":
                    ON_MOUSE_DOWN_KEY.put(node.getProperties(), value);
                    break;
                case "transform":
                    node.set(SvgTransformableFigure.TRANSFORMS, SvgTransformableFigure.TRANSFORMS.getCssConverter().fromString(value));
                    break;
                case "stroke-alignment":
                    node.set(SvgInheritableFigure.STROKE_ALIGNMENT_KEY, SvgInheritableFigure.STROKE_ALIGNMENT_KEY.getCssConverter().fromString(value));
                    break;
                case "fill":
                    ctx.secondPass.add(() ->
                            node.set(SvgInheritableFigure.FILL_KEY, SvgInheritableFigure.FILL_KEY.getCssConverter().fromString(value, ctx.idFactory))
                    );
                    break;
                case "stroke":
                    ctx.secondPass.add(() ->
                            node.set(SvgInheritableFigure.STROKE_KEY, SvgInheritableFigure.STROKE_KEY.getCssConverter().fromString(value, ctx.idFactory))
                    );
                    break;
                default:
                    return false;
                }
            } catch (ParseException | IOException e) {
                logWarning(r, "Error parsing attribute " + namespace + ":" + name + "=\"" + value + "\". " + e.getMessage());
            }
            return true;
        }
        return false;
    }

    private boolean readPathLengthAttribute(XMLStreamReader r, Figure shape, Context ctx, String namespace, String name, String value) throws XMLStreamException {
        if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
            switch (name) {

            case "pathLength": {
                //https://www.w3.org/TR/2018/CR-SVG2-20181004/paths.html#PathLengthAttribute
                double doubleValue = toNumber(r, value);
                shape.set(SvgPathLengthFigure.PATH_LENGTH, doubleValue);
                return true;
            }
            default:
                break;
            }
        }
        return false;
    }

    private Figure readSolidColorElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private Figure readStyleElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            logWarning(r, "Ignoring attribute of style element. " + name + "=\"" + value + "\".");
        }

        String stylesheet = readTextContent(r);
        ctx.addInternalStylesheet(stylesheet);
        return null;
    }

    /**
     * The reader must be positioned on START_ELEMENT of the root SVG element.
     *
     * @param r reader
     * @throws XMLStreamException
     */
    private Figure readSvgRootElement(XMLStreamReader r) throws XMLStreamException, IOException {
        Context ctx = new Context();

        SvgDrawing node = new SvgDrawing();

        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String namespace = r.getAttributeNamespace(i);
            String localName = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            if (SVG_NAMESPACE.equals(namespace) || namespace == null) {
                switch (localName) {
                case "height":
                    break;
                case "width":
                    break;
                case "viewBox":
                    String[] viewBoxValues = toWhitespaceOrCommaSeparatedArray(value);
                    if (viewBoxValues.length != 4) {
                        throw createException(r, "4 values expected for viewBox: " + value + ".");
                    }
                    break;
                case "baseProfile":
                    node.set(SvgDrawing.BASE_PROFILE, value);
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
                    node.set(SvgDrawing.VERSION, value);
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


        Loop:
        while (true) {
            switch (r.next()) {
            case XMLStreamReader.START_ELEMENT:
                Figure child = readElement(r, ctx, node);
                if (child != null) {
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


        return node;
    }

    private Figure readSwitchElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        requireEndElement(r, "<switch>: end element expected", null);
        return null;
    }

    private Figure readScriptElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        skipElement(r, ctx);
        return null;
    }

    private Figure readTextAreaElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private static final Logger LOGGER = Logger.getLogger(FigureSvgReader.class.getName());

    private Text readTSpanElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        Text shape = new Text();

        for (int i = 0, n = r.getAttributeCount(); i < n; i++) {
            String name = r.getAttributeLocalName(i);
            String value = r.getAttributeValue(i);
            switch (name) {
            case "x":
                //shape.setX(toLength(r, value));
                break;
            case "y":
                //shape.setY(toLength(r, value));
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
        return null;
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

    private Figure readStopElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        requireEndElement(r, "<stop>: end element expected", null);
        return null;
    }


    private Figure readTextElement(XMLStreamReader r, Context ctx) throws XMLStreamException {
        return null;
    }

    private Figure readTitleElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        String title = readTextContent(r);
        TITLE_KEY.put(parent.getProperties(), title);
        return null;
    }

    private Figure readDescElement(XMLStreamReader r, Context ctx, Figure parent) throws XMLStreamException {
        String title = readTextContent(r);
        DESC_KEY.put(parent.getProperties(), title);
        return null;
    }

    private Figure readUseElement(XMLStreamReader r, Context ctx) {
        return null;
    }

    private CssSize toLength(XMLStreamReader reader, String str) throws XMLStreamException {
        try {
            return sizeConverter.fromString(str);
        } catch (ParseException | IOException e) {
            logWarning(reader, e.getMessage());
            return CssSize.ZERO;
        }

    }

    final CssSizeConverter sizeConverter = new CssSizeConverter(false);
    final XmlNumberConverter numberConverter = new XmlNumberConverter();

    private final SvgPaintConverter paintConverter = new SvgPaintConverter(true);

    private Paint toPaint(XMLStreamReader r, String value, Context ctx) throws XMLStreamException {
        try {
            return paintConverter.fromString(value, ctx.idFactory);
        } catch (ParseException | IOException e) {
            logWarning(r, "Could not read paint: " + value);
            return null;
        }
    }


}
