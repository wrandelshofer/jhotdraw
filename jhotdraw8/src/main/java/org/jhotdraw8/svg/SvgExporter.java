/* @(#)SvgExporter.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.svg;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Objects;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.effect.BlendMode;
import javafx.scene.image.ImageView;
import javafx.scene.input.DataFormat;
import javafx.scene.layout.BackgroundFill;
import javafx.scene.layout.BackgroundImage;
import javafx.scene.layout.BorderImage;
import javafx.scene.layout.BorderStroke;
import javafx.scene.layout.BorderStrokeStyle;
import javafx.scene.layout.CornerRadii;
import javafx.scene.layout.Region;
import javafx.scene.paint.Color;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Paint;
import javafx.scene.paint.RadialGradient;
import javafx.scene.paint.Stop;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.HLineTo;
import javafx.scene.shape.Line;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.Polyline;
import javafx.scene.shape.QuadCurve;
import javafx.scene.shape.QuadCurveTo;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.SVGPath;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.shape.StrokeType;
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw8.draw.io.InternalExternalUriMixin;

import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.text.SvgTransformListConverter;
import org.jhotdraw8.text.XmlNumberConverter;
import org.jhotdraw8.text.SvgPaintConverter;
import org.jhotdraw8.text.XmlSizeListConverter;
import org.jhotdraw8.util.ReversedList;
import org.jhotdraw8.xml.XmlUtil;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Exports a JavaFX scene graph to SVG.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SvgExporter implements InternalExternalUriMixin {

    public final static DataFormat SVG_FORMAT;

    static {
        DataFormat fmt = DataFormat.lookupMimeType("image/svg+xml");
        if (fmt == null) {
            fmt = new DataFormat("image/svg+xml");
        }
        SVG_FORMAT = fmt;
    }

    private final static String XLINK_NS = "http://www.w3.org/1999/xlink";
    private final static String XLINK_Q = "xlink";
    private final static String XMLNS_NS = "http://www.w3.org/2000/xmlns/";
    private final String SVG_NS = "http://www.w3.org/2000/svg";
    private URI externalHome;
    private IdFactory idFactory = new SimpleIdFactory();
    private final Object imageUriKey;
    private URI internalHome;
    private final String namespaceQualifier = null;
    private final XmlNumberConverter nb = new XmlNumberConverter();
    private final XmlSizeListConverter nbList = new XmlSizeListConverter();
    private final SvgPaintConverter paint = new SvgPaintConverter();
    private boolean skipInvisibleNodes = true;
    private final Object skipKey;
    private final SvgTransformListConverter tx = new SvgTransformListConverter();

    /**
     *
     * @param imageUriKey this property is used to retrieve an URL from an
     * ImageView
     * @param skipKey this property is used to retrieve a Boolean from a Node.
     * If the Boolean is true, then the node is skipped.
     */
    public SvgExporter(Object imageUriKey, Object skipKey) {
        this.imageUriKey = imageUriKey;
        this.skipKey = skipKey;
    }

    private String createFileComment() {
        return null;
    }

    public URI getExternalHome() {
        return externalHome;
    }

    /**
     * Must be a directory and not a file.
     */
    public void setExternalHome(URI uri) {
        externalHome = uri;
    }

    public URI getInternalHome() {
        return internalHome;
    }

    /**
     * Must be a directory and not a file.
     *
     * @param uri the uri
     */
    public void setInternalHome(URI uri) {
        internalHome = uri;
    }

    private void initIdFactoryRecursively(javafx.scene.Node node) throws IOException {
        String id = node.getId();
        if (id != null && idFactory.getObject(id) == null) {
            idFactory.putId(id, node);
        } else {
            idFactory.createId(node, node.getTypeSelector().toLowerCase());
        }

        if (node instanceof Parent) {
            Parent pp = (Parent) node;
            for (javafx.scene.Node child : pp.getChildrenUnmodifiable()) {
                initIdFactoryRecursively(child);
            }
        }
    }

    public boolean isSkipInvisibleNodes() {
        return skipInvisibleNodes;
    }

    public void setSkipInvisibleNodes(boolean skipInvisibleNodes) {
        this.skipInvisibleNodes = skipInvisibleNodes;
    }

    private boolean isSkipNode(Node node) {
        if (skipKey != null && Objects.equals(Boolean.TRUE, node.getProperties().get(skipKey))) {
            return true;
        }
        if (skipInvisibleNodes) {
            if (!node.isVisible()) {
                return true;
            }
            if (node instanceof Shape) {
                Shape s = (Shape) node;
                if ((s.getFill() == null || Objects.equals(s.getFill(), Color.TRANSPARENT))
                        && (s.getStroke() == null || Objects.equals(s.getStroke(), Color.TRANSPARENT))) {
                    return true;
                }
            }
        }
        return false;
    }

    public Document toDocument(javafx.scene.Node drawingNode) throws IOException {
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            Document doc;
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            DOMImplementation domImpl = builder.getDOMImplementation();
            doc = domImpl.createDocument(SVG_NS, namespaceQualifier == null ? "svg" : namespaceQualifier + ":" + "svg", null);

            Element docElement = doc.getDocumentElement();
            docElement.setAttributeNS(XMLNS_NS, "xmlns:" + XLINK_Q, XLINK_NS);

            writeProcessingInstructions(doc, drawingNode);
            String commentText = createFileComment();
            if (commentText != null) {
                docElement.getParentNode().insertBefore(doc.createComment(commentText), docElement);
            }

            idFactory.reset();
            initIdFactoryRecursively(drawingNode);
            Element defsElement = doc.createElement("defs");
            writeDefsRecursively(doc, defsElement, drawingNode);
            if (defsElement.getChildNodes().getLength() > 0) {
                docElement.appendChild(defsElement);
            }
            writeDocumentElementAttributes(docElement, drawingNode);
            writeNodeRecursively(doc, docElement, drawingNode);

            return doc;
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    public void write(OutputStream out, javafx.scene.Node drawing) throws IOException {
        Document doc = toDocument(drawing);
        XmlUtil.write(out, doc);
    }

    public void write(Writer out, javafx.scene.Node drawing) throws IOException {
        Document doc = toDocument(drawing);
        XmlUtil.write(out, doc);
    }

    private Element writeArc(Document doc, Element parent, Arc node) {
        Element elem = doc.createElement("arc");
        parent.appendChild(elem);
        StringBuilder buf = new StringBuilder();
        double centerX = node.getCenterX();
        double centerY = node.getCenterY();
        double radiusX = node.getRadiusX();
        double radiusY = node.getRadiusY();
        double startAngle = Math.toRadians(-node.getStartAngle());
        double endAngle = Math.toRadians(-node.getStartAngle() - node.getLength());
        double length = node.getLength();

        double startX = radiusX * Math.cos(startAngle);
        double startY = radiusY * Math.sin(startAngle);

        double endX = centerX + radiusX * Math.cos(endAngle);
        double endY = centerY + radiusY * Math.sin(endAngle);

        int xAxisRot = 0;
        boolean largeArc = (length > 180);
        boolean sweep = (length < 0);

        buf.append('M')
                .append(nb.toString(centerX))
                .append(',')
                .append(nb.toString(centerY))
                .append(' ');

        if (ArcType.ROUND == node.getType()) {
            buf.append('l')
                    .append(startX)
                    .append(',')
                    .append(startY).append(' ');
        }

        buf.append('A')
                .append(nb.toString(radiusX))
                .append(',')
                .append(nb.toString(radiusY))
                .append(',')
                .append(nb.toString(xAxisRot))
                .append(',')
                .append(largeArc ? '1' : '0')
                .append(',')
                .append(sweep ? '1' : '0')
                .append(',')
                .append(nb.toString(endX))
                .append(',')
                .append(nb.toString(endY))
                .append(',');

        if (ArcType.CHORD == node.getType()
                || ArcType.ROUND == node.getType()) {
            buf.append('Z');
        }
        return elem;
    }

    private Element writeCircle(Document doc, Element parent, Circle node) {
        Element elem = doc.createElement("circle");
        if (node.getCenterX() != 0.0) {
            elem.setAttribute("cx", nb.toString(node.getCenterX()));
        }
        if (node.getCenterY() != 0.0) {
            elem.setAttribute("cy", nb.toString(node.getCenterY()));
        }
        if (node.getRadius() != 0.0) {
            elem.setAttribute("r", nb.toString(node.getRadius()));
        }
        parent.appendChild(elem);
        return elem;
    }

    private void writeClipAttributes(Element elem, Node node) {
        Node clip = node.getClip();
        if (clip == null) {
            return;
        }

        String id = idFactory.getId(clip);
        if (id != null) {
            elem.setAttribute("clip-path", "url(#" + id + ")");
        } else {
            System.err.println("WARNING SvgExporter does not supported recursive clips!");
        }
    }

    private void writeClipPathDefs(Document doc, Element defsNode, Node node) throws IOException {
        // FIXME clip nodes can in turn have clips - we need to support recursive calls to defsNode!!!
        Node clip = node.getClip();
        if (clip == null) {
            return;
        }
        if (idFactory.getId(clip) == null) {
            String id = idFactory.createId(clip, "clipPath");
            Element elem = doc.createElement("clipPath");
            writeNodeRecursively(doc, elem, clip);
            elem.setAttribute("id", id);
            defsNode.appendChild(elem);
        }
    }

    private void writeCompositingAttributes(Element elem, Node node) {
        if (node.getOpacity() != 1.0) {
            elem.setAttribute("opacity", nb.toString(node.getOpacity()));
        }
        /*
        if (node.getBlendMode() != null && node.getBlendMode() != BlendMode.SRC_OVER) {
        switch (node.getBlendMode()) {
        case MULTIPLY:
        case SCREEN:
        case DARKEN:
        case LIGHTEN:
        elem.setAttribute("mode", node.getBlendMode().toString().toLowerCase());
        break;
        default:
        // ignore
        }
        }*/
    }

    private Element writeCubicCurve(Document doc, Element parent, CubicCurve node) {
        Element elem = doc.createElement("path");
        parent.appendChild(elem);
        final StringBuilder buf = new StringBuilder();
        buf.append('M')
                .append(nb.toString(node.getStartX()))
                .append(',')
                .append(nb.toString(node.getStartY()))
                .append(' ')
                .append('C')
                .append(nb.toString(node.getControlX1()))
                .append(',')
                .append(nb.toString(node.getControlY1()))
                .append(',')
                .append(nb.toString(node.getControlX2()))
                .append(',')
                .append(nb.toString(node.getControlY2()))
                .append(',')
                .append(nb.toString(node.getEndX()))
                .append(',')
                .append(nb.toString(node.getEndY()));
        elem.setAttribute("d", buf.substring(0));
        return elem;
    }

    private void writeDefsRecursively(Document doc, Element defsNode, javafx.scene.Node node) throws IOException {
        if (isSkipNode(node)) {
            return;
        }

        writeClipPathDefs(doc, defsNode, node);

        if (node instanceof Shape) {
            Shape shape = (Shape) node;
            writePaintDefs(doc, defsNode, shape.getFill());
            writePaintDefs(doc, defsNode, shape.getStroke());
        }

        if (node instanceof Parent) {
            Parent pp = (Parent) node;
            for (javafx.scene.Node child : pp.getChildrenUnmodifiable()) {
                writeDefsRecursively(doc, defsNode, child);
            }
        }
    }

    private void writeDocumentElementAttributes(Element docElement, javafx.scene.Node drawingNode) throws IOException {
        docElement.setAttribute("version", "1.2");
        docElement.setAttribute("baseProfile", "tiny");

    }

    private Element writeEllipse(Document doc, Element parent, Ellipse node) {
        Element elem = doc.createElement("ellipse");
        if (node.getCenterX() != 0.0) {
            elem.setAttribute("cx", nb.toString(node.getCenterX()));
        }
        if (node.getCenterY() != 0.0) {
            elem.setAttribute("cy", nb.toString(node.getCenterY()));
        }
        if (node.getRadiusX() != 0.0) {
            elem.setAttribute("rx", nb.toString(node.getRadiusX()));
        }
        if (node.getRadiusY() != 0.0) {
            elem.setAttribute("ry", nb.toString(node.getRadiusY()));
        }
        parent.appendChild(elem);
        return elem;
    }

    private void writeFillAttributes(Element elem, Shape node) {
        Paint fill = node.getFill();
        String id = idFactory.getId(fill);
        if (id != null) {
            elem.setAttribute("fill", "url(#" + id + ")");
        } else {
            elem.setAttribute("fill", paint.toString(fill));
            if (fill instanceof Color) {
                Color c = (Color) fill;
                if (!c.isOpaque()) {
                    elem.setAttribute("fill-opacity", nb.toString(c.getOpacity()));
                }
            }
        }
    }

    private Element writeGroup(Document doc, Element parent, Group node) {
        Element elem = doc.createElement("g");
        writeClipAttributes(elem, node);
        parent.appendChild(elem);
        return elem;
    }

    private Element writeImageView(Document doc, Element parent, ImageView node) throws IOException {
        Element elem = doc.createElement("image");
        parent.appendChild(elem);

        elem.setAttribute("x", nb.toString(node.getX()));
        elem.setAttribute("y", nb.toString(node.getY()));
        elem.setAttribute("width", nb.toString(node.getFitWidth()));
        elem.setAttribute("height", nb.toString(node.getFitHeight()));
        elem.setAttribute("preserveAspectRatio", node.isPreserveRatio() ? "xMidYMid" : "none");

        URI uri = (URI) node.getProperties().get(imageUriKey);
        String href = null;
        if (uri != null) {
            href = toExternal(uri).toString();
        } else {
            if (node.getImage() != null) {
                ByteArrayOutputStream bout = new ByteArrayOutputStream();
                ImageIO.write(SwingFXUtils.fromFXImage(node.getImage(), null), "PNG", bout);
                bout.close();
                byte[] imageData = bout.toByteArray();

                href = "data:image;base64," + Base64.getEncoder().encodeToString(imageData);
            }
        }
        if (href != null) {
            elem.setAttributeNS(XLINK_NS, XLINK_Q + ":href", href);
        }
        return elem;
    }

    private Element writeLine(Document doc, Element parent, Line node) {
        Element elem = doc.createElement("line");
        if (node.getStartX() != 0.0) {
            elem.setAttribute("x1", nb.toString(node.getStartX()));
        }
        if (node.getStartY() != 0.0) {
            elem.setAttribute("y1", nb.toString(node.getStartY()));
        }
        if (node.getEndX() != 0.0) {
            elem.setAttribute("x2", nb.toString(node.getEndX()));
        }
        if (node.getEndY() != 0.0) {
            elem.setAttribute("y2", nb.toString(node.getEndY()));
        }
        parent.appendChild(elem);
        return elem;
    }

    private void writeNodeRecursively(Document doc, Element parent, javafx.scene.Node node) throws IOException {
        if (isSkipNode(node)) {
            return;
        }
        Element elem = null;
        if (node instanceof Shape) {
            elem = writeShape(doc, parent, (Shape) node);
            writeFillAttributes(elem, (Shape) node);
            writeStrokeAttributes(elem, (Shape) node);
            writeClipAttributes(elem, node);
        } else if (node instanceof Group) {
            // a group can be omitted if it does not perform a transformation, an effect or a clip
            boolean omitGroup = false;
            if (skipInvisibleNodes) {
                Group g = (Group) node;
                if ((g.getBlendMode() == null || g.getBlendMode() == BlendMode.SRC_OVER)
                        && g.getLocalToParentTransform().isIdentity()
                        && g.getEffect() == null
                        && g.getClip() == null) {
                    omitGroup = true;
                }
            }
            if (!omitGroup) {
                elem = writeGroup(doc, parent, (Group) node);
            }
        } else if (node instanceof Region) {
            elem = writeRegion(doc, parent, (Region) node);
        } else if (node instanceof ImageView) {
            elem = writeImageView(doc, parent, (ImageView) node);
        } else {
            throw new UnsupportedOperationException("not yet implemented for " + node);
        }

        if (elem != null) {
            writeStyleAttributes(elem, node);
            writeTransformAttributes(elem, node);
            writeCompositingAttributes(elem, node);
        }

        if (node instanceof Parent) {
            final Parent pp = (Parent) node;
            final Element parentElement = elem == null ? parent : elem;
            for (javafx.scene.Node child : pp.getChildrenUnmodifiable()) {
                writeNodeRecursively(doc, parentElement, child);
            }
        }

    }

    private void writePaintDefs(Document doc, Element defsNode, Paint paint) throws IOException {
        if (idFactory.getId(paint) == null) {
            if (paint instanceof LinearGradient) {
                LinearGradient g = (LinearGradient) paint;
                String id = idFactory.createId(paint, "linearGradient");
                Element elem = doc.createElement("linearGradient");
                defsNode.appendChild(doc.createTextNode("\n"));
                elem.setAttribute("id", id);
                if (g.isProportional()) {
                    elem.setAttribute("x1", nb.toString(g.getStartX() * 100) + "%");
                    elem.setAttribute("y1", nb.toString(g.getStartY() * 100) + "%");
                    elem.setAttribute("x2", nb.toString(g.getEndX() * 100) + "%");
                    elem.setAttribute("y2", nb.toString(g.getEndY() * 100) + "%");
                    elem.setAttribute("gradientUnits", "objectBoundingBox");

                } else {
                    elem.setAttribute("x1", nb.toString(g.getStartX()));
                    elem.setAttribute("y1", nb.toString(g.getStartY()));
                    elem.setAttribute("x2", nb.toString(g.getEndX()));
                    elem.setAttribute("y2", nb.toString(g.getEndY()));
                    elem.setAttribute("gradientUnits", "userSpaceOnUse");
                }
                switch (g.getCycleMethod()) {
                    case NO_CYCLE:
                        elem.setAttribute("spreadMethod", "pad");
                        break;
                    case REFLECT:
                        elem.setAttribute("spreadMethod", "reflect");
                        break;
                    case REPEAT:
                        elem.setAttribute("spreadMethod", "repeat");
                        break;
                    default:
                        throw new IOException("unsupported cycle method:" + g.getCycleMethod());
                }
                for (Stop s : g.getStops()) {
                    Element stopElem = doc.createElement("stop");
                    stopElem.setAttribute("offset", nb.toString(s.getOffset() * 100) + "%");
                    Color c = s.getColor();
                    stopElem.setAttribute("stop-color", this.paint.toString(c));
                    if (!c.isOpaque()) {
                        stopElem.setAttribute("stop-opacity", nb.toString(c.getOpacity()));
                    }
                    elem.appendChild(stopElem);
                }
                defsNode.appendChild(elem);
            } else if (paint instanceof RadialGradient) {
                RadialGradient g = (RadialGradient) paint;
                String id = idFactory.createId(paint, "radialGradient");
                Element elem = doc.createElement("radialGradient");
                defsNode.appendChild(doc.createTextNode("\n"));
                elem.setAttribute("id", id);
                if (g.isProportional()) {
                    elem.setAttribute("cx", nb.toString(g.getCenterX() * 100) + "%");
                    elem.setAttribute("cy", nb.toString(g.getCenterY() * 100) + "%");
                    elem.setAttribute("r", nb.toString(g.getRadius() * 100) + "%");
                    elem.setAttribute("fx", nb.toString((g.getCenterX() + Math.cos(g.getFocusAngle() / 180 * Math.PI) * g.getFocusDistance() * g.getRadius()) * 100) + "%");
                    elem.setAttribute("fy", nb.toString((g.getCenterY() + Math.sin(g.getFocusAngle() / 180 * Math.PI) * g.getFocusDistance() * g.getRadius()) * 100) + "%");
                    elem.setAttribute("gradientUnits", "objectBoundingBox");

                } else {
                    elem.setAttribute("cx", nb.toString(g.getCenterX()));
                    elem.setAttribute("cy", nb.toString(g.getCenterY()));
                    elem.setAttribute("r", nb.toString(g.getRadius()));
                    elem.setAttribute("fx", nb.toString(g.getCenterX() + Math.cos(g.getFocusAngle() / 180 * Math.PI) * g.getFocusDistance() * g.getRadius()));
                    elem.setAttribute("fy", nb.toString(g.getCenterY() + Math.sin(g.getFocusAngle() / 180 * Math.PI) * g.getFocusDistance() * g.getRadius()));
                    elem.setAttribute("gradientUnits", "userSpaceOnUse");
                }
                switch (g.getCycleMethod()) {
                    case NO_CYCLE:
                        elem.setAttribute("spreadMethod", "pad");
                        break;
                    case REFLECT:
                        elem.setAttribute("spreadMethod", "reflect");
                        break;
                    case REPEAT:
                        elem.setAttribute("spreadMethod", "repeat");
                        break;
                    default:
                        throw new IOException("unsupported cycle method:" + g.getCycleMethod());
                }
                for (Stop s : g.getStops()) {
                    Element stopElem = doc.createElement("stop");
                    stopElem.setAttribute("offset", nb.toString(s.getOffset() * 100) + "%");
                    Color c = s.getColor();
                    stopElem.setAttribute("stop-color", this.paint.toString(c));
                    if (!c.isOpaque()) {
                        stopElem.setAttribute("stop-opacity", nb.toString(c.getOpacity()));
                    }
                    elem.appendChild(stopElem);
                }
                defsNode.appendChild(elem);
            }
        }
    }

    private Element writePath(Document doc, Element parent, Path node) {
        Element elem = doc.createElement("path");
        parent.appendChild(elem);
        StringBuilder buf = new StringBuilder();
        char prev = '\0'; // previous command
        for (PathElement pe : node.getElements()) {
            if (buf.length() != 0) {
                buf.append(' ');
            }
            if (pe instanceof MoveTo) {
                MoveTo e = (MoveTo) pe;
                if (prev != 'M') {
                    buf.append('M');
                    prev = 'L'; // move implies line
                }
                buf.append(nb.toString(e.getX()))
                        .append(',')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof LineTo) {
                LineTo e = (LineTo) pe;
                if (prev != 'L') {
                    buf.append(prev = 'L');
                }
                buf.append(nb.toString(e.getX()))
                        .append(',')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof CubicCurveTo) {
                CubicCurveTo e = (CubicCurveTo) pe;
                if (prev != 'C') {
                    buf.append(prev = 'C');
                }
                buf.append(nb.toString(e.getControlX1()))
                        .append(',')
                        .append(nb.toString(e.getControlY1()))
                        .append(',')
                        .append(nb.toString(e.getControlX2()))
                        .append(',')
                        .append(nb.toString(e.getControlY2()))
                        .append(',')
                        .append(nb.toString(e.getX()))
                        .append(',')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof QuadCurveTo) {
                QuadCurveTo e = (QuadCurveTo) pe;
                if (prev != 'Q') {
                    buf.append(prev = 'Q');
                }
                buf.append(nb.toString(e.getControlX()))
                        .append(',')
                        .append(nb.toString(e.getControlY()))
                        .append(',')
                        .append(nb.toString(e.getX()))
                        .append(',')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof ArcTo) {
                ArcTo e = (ArcTo) pe;
                if (prev != 'A') {
                    buf.append(prev = 'A');
                }
                buf.append(nb.toString(e.getRadiusX()))
                        .append(',')
                        .append(nb.toString(e.getRadiusY()))
                        .append(',')
                        .append(nb.toString(e.getXAxisRotation()))
                        .append(',')
                        .append(e.isLargeArcFlag() ? '1' : '0')
                        .append(',')
                        .append(e.isSweepFlag() ? '1' : '0')
                        .append(',')
                        .append(nb.toString(e.getX()))
                        .append(',')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof HLineTo) {
                HLineTo e = (HLineTo) pe;
                if (prev != 'H') {
                    buf.append(prev = 'H');
                }
                buf.append(nb.toString(e.getX()));
            } else if (pe instanceof VLineTo) {
                VLineTo e = (VLineTo) pe;
                if (prev != 'V') {
                    buf.append(prev = 'V');
                }
                buf.append(nb.toString(e.getY()));
            } else if (pe instanceof ClosePath) {
                if (prev != 'Z') {
                    buf.append(prev = 'Z');
                }
            }
        }
        elem.setAttribute("d", buf.toString());
        return elem;
    }

    private Element writePolygon(Document doc, Element parent, Polygon node) {
        Element elem = doc.createElement("polygon");
        StringBuilder buf = new StringBuilder();
        List<Double> ps = node.getPoints();
        for (int i = 0, n = ps.size(); i < n; i += 2) {
            if (i != 0) {
                buf.append(' ');
            }
            buf.append(nb.toString(ps.get(i)))
                    .append(',')
                    .append(nb.toString(ps.get(i + 1)));
        }
        elem.setAttribute("points", buf.toString());
        parent.appendChild(elem);
        return elem;
    }

    private Element writePolyline(Document doc, Element parent, Polyline node) {
        Element elem = doc.createElement("polyline");
        StringBuilder buf = new StringBuilder();
        List<Double> ps = node.getPoints();
        for (int i = 0, n = ps.size(); i < n; i += 2) {
            if (i != 0) {
                buf.append(' ');
            }
            buf.append(nb.toString(ps.get(i)))
                    .append(',')
                    .append(nb.toString(ps.get(i + 1)));
        }
        elem.setAttribute("points", buf.toString());
        parent.appendChild(elem);
        return elem;
    }

    private void writeProcessingInstructions(Document doc, javafx.scene.Node external) {
// empty
    }

    private Element writeQuadCurve(Document doc, Element parent, QuadCurve node) {
        Element elem = doc.createElement("path");
        parent.appendChild(elem);
        final StringBuilder buf = new StringBuilder();
        buf.append('M')
                .append(nb.toString(node.getStartX()))
                .append(',')
                .append(nb.toString(node.getStartY()))
                .append(' ')
                .append('Q')
                .append(nb.toString(node.getControlX()))
                .append(',')
                .append(nb.toString(node.getControlY()))
                .append(',')
                .append(nb.toString(node.getEndX()))
                .append(',')
                .append(nb.toString(node.getEndY()));
        elem.setAttribute("d", buf.substring(0));
        return elem;
    }

    private Element writeRectangle(Document doc, Element parent, Rectangle node) {
        Element elem = doc.createElement("rect");
        if (node.getX() != 0.0) {
            elem.setAttribute("x", nb.toString(node.getX()));
        }
        if (node.getY() != 0.0) {
            elem.setAttribute("y", nb.toString(node.getY()));
        }
        if (node.getWidth() != 0.0) {
            elem.setAttribute("width", nb.toString(node.getWidth()));
        }
        if (node.getHeight() != 0.0) {
            elem.setAttribute("height", nb.toString(node.getHeight()));
        }
        if (node.getArcWidth() != 0.0) {
            elem.setAttribute("rx", nb.toString(node.getArcWidth()));
        }
        if (node.getArcHeight() != 0.0) {
            elem.setAttribute("ry", nb.toString(node.getArcHeight()));
        }
        parent.appendChild(elem);
        return elem;
    }

    private Element writeRegion(Document doc, Element parent, Region region) throws IOException {
        Element elem = doc.createElement("g");
        parent.appendChild(elem);

        double x = region.getLayoutX();
        double y = region.getLayoutY();
        double width = region.getWidth();
        double height = region.getHeight();

        if ((region.getBackground() != null && !region.getBackground().isEmpty())
                || (region.getBorder() != null && !region.getBorder().isEmpty())) {
            // compute the shape 's' of the region
            Shape s = (region.getShape() == null) ? null : region.getShape();
            Bounds sb = (s != null) ? s.getLayoutBounds() : null;

            // All BackgroundFills are drawn first, followed by
            // BackgroundImages, BorderStrokes, and finally BorderImages
            if (region.getBackground() != null) {
                for (BackgroundFill bgf : region.getBackground().getFills()) {
                    Paint fill = bgf.getFill() == null ? Color.TRANSPARENT : bgf.getFill();
                    CornerRadii radii = bgf.getRadii() == null ? CornerRadii.EMPTY : bgf.getRadii();
                    Insets insets = bgf.getInsets() == null ? Insets.EMPTY : bgf.getInsets();

                    Shape bgs = null;
                    if (s != null) {
                        if (region.isScaleShape()) {

                            java.awt.Shape awtShape = Shapes.awtShapeFromFX(s);
                            Transform tx = Transform.translate(-sb.getMinX(), -sb.getMinY());
                            tx = Transforms.concat(tx, Transform.translate(x + insets.getLeft(), y + insets.getTop()));
                            tx = Transforms.concat(tx, Transform.scale((width - insets.getLeft() - insets.getRight()) / sb.getWidth(), (height - insets.getTop() - insets.getBottom()) / sb.getHeight()));
                            bgs = Shapes.fxShapeFromAWT(awtShape, tx);
                        } else {
                            bgs = s;
                        }
                    } else if (radii != CornerRadii.EMPTY) {
                        throw new UnsupportedOperationException("radii not yet implemented");
                    } else {
                        bgs = new Rectangle(x + insets.getLeft(), y + insets.getTop(), width - insets.getLeft() - insets.getRight(), height - insets.getTop() - insets.getBottom());
                    }
                    bgs.setFill(fill);
                    Element bgsElement = writeShape(doc, elem, bgs);
                    writeFillAttributes(bgsElement, bgs);
                    bgsElement.setAttribute("stroke", "none");
                    elem.appendChild(bgsElement);
                }
                for (BackgroundImage bgi : region.getBackground().getImages()) {
                    throw new UnsupportedOperationException("background image not yet implemented");
                }
            }
            if (region.getBorder() != null) {
                if (region.getBorder().getImages().isEmpty() || s == null) {
                    for (BorderStroke bs : region.getBorder().getStrokes()) {
                        Insets insets = bs.getInsets();
                        CornerRadii radii = bs.getRadii() == null ? CornerRadii.EMPTY : bs.getRadii();

                        Shape bgs = null;
                        if (s != null) {
                            if (region.isScaleShape()) {
                                java.awt.Shape awtShape = Shapes.awtShapeFromFX(s);

                                Transform tx = Transform.translate(-sb.getMinX(), -sb.getMinY());
                                tx = Transforms.concat(tx, Transform.translate(x + insets.getLeft(), y + insets.getTop()));
                                tx = Transforms.concat(tx, Transform.scale((width - insets.getLeft() - insets.getRight()) / sb.getWidth(), (height - insets.getTop() - insets.getBottom()) / sb.getHeight()));
                                bgs = Shapes.fxShapeFromAWT(awtShape, tx);
                            } else {
                                bgs = s;
                            }
                        } else if (radii != CornerRadii.EMPTY) {
                            throw new UnsupportedOperationException("radii not yet implemented");
                        } else {
                            bgs = new Rectangle(x + insets.getLeft(), y + insets.getTop(), width - insets.getLeft() - insets.getRight(), height - insets.getTop() - insets.getBottom());
                        }

                        Element bgsElement = writeShape(doc, elem, bgs);
                        writeStrokeAttributes(bgsElement, bs);
                        bgsElement.setAttribute("fill", "none");
                        elem.appendChild(bgsElement);
                    }
                }
                if (s != null) {
                    for (BorderImage bi : region.getBorder().getImages()) {
                        throw new UnsupportedOperationException("border image not yet implemented");
                    }
                }
            }
        }
        return elem;
    }

    private Element writeSVGPath(Document doc, Element parent, SVGPath node) {
        Element elem = doc.createElement("path");
        elem.setAttribute("d", node.getContent());
        switch (node.getFillRule()) {
            case NON_ZERO:
                //    elem.setAttribute("fill-rule","nonzero");// default
                break;
            case EVEN_ODD:
                elem.setAttribute("fill-rule", "evenodd");
                break;
        }
        parent.appendChild(elem);
        return elem;
    }

    private Element writeShape(Document doc, Element parent, Shape node) throws IOException {
        Element elem = null;
        if (node instanceof Arc) {
            elem = writeArc(doc, parent, (Arc) node);
        } else if (node instanceof Circle) {
            elem = writeCircle(doc, parent, (Circle) node);
        } else if (node instanceof CubicCurve) {
            elem = writeCubicCurve(doc, parent, (CubicCurve) node);
        } else if (node instanceof Ellipse) {
            elem = writeEllipse(doc, parent, (Ellipse) node);
        } else if (node instanceof Line) {
            elem = writeLine(doc, parent, (Line) node);
        } else if (node instanceof Path) {
            elem = writePath(doc, parent, (Path) node);
        } else if (node instanceof Polygon) {
            elem = writePolygon(doc, parent, (Polygon) node);
        } else if (node instanceof Polyline) {
            elem = writePolyline(doc, parent, (Polyline) node);
        } else if (node instanceof QuadCurve) {
            elem = writeQuadCurve(doc, parent, (QuadCurve) node);
        } else if (node instanceof Rectangle) {
            elem = writeRectangle(doc, parent, (Rectangle) node);
        } else if (node instanceof SVGPath) {
            elem = writeSVGPath(doc, parent, (SVGPath) node);
        } else if (node instanceof Text) {
            elem = writeText(doc, parent, (Text) node);
        } else {
            throw new IOException("unknown shape type " + node);
        }
        return elem;
    }

    private void writeStrokeAttributes(Element elem, Shape shape) {
        Paint stroke = shape.getStroke();
        if (stroke != null) {
            String id = idFactory.getId(stroke);
            if (id != null) {
                elem.setAttribute("stroke", "url(#" + id + ")");
            } else {
                elem.setAttribute("stroke", paint.toString(stroke));
                if (stroke instanceof Color) {
                    Color c = (Color) stroke;
                    if (!c.isOpaque()) {
                        elem.setAttribute("stroke-opacity", nb.toString(c.getOpacity()));
                    }
                }
            }
        }
        if (shape.getStrokeWidth() != 1) {
            elem.setAttribute("stroke-width", nb.toString(shape.getStrokeWidth()));
        }
        if (shape.getStrokeLineCap() != StrokeLineCap.BUTT) {
            elem.setAttribute("stroke-linecap", shape.getStrokeLineCap().toString().toLowerCase());
        }
        if (shape.getStrokeLineJoin() != StrokeLineJoin.MITER) {
            elem.setAttribute("stroke-linecap", shape.getStrokeLineJoin().toString().toLowerCase());
        }
        if (shape.getStrokeMiterLimit() != 4) {
            elem.setAttribute("stroke-miterlimit", nb.toString(shape.getStrokeMiterLimit()));
        }
        if (!shape.getStrokeDashArray().isEmpty()) {
            elem.setAttribute("stroke-dasharray", nbList.toStringFromCollection(shape.getStrokeDashArray()));
        }
        if (shape.getStrokeDashOffset() != 0) {
            elem.setAttribute("stroke-dashoffset", nb.toString(shape.getStrokeDashOffset()));
        }
        if (shape.getStrokeType() != StrokeType.CENTERED) {
            // XXX this is currentl only a proposal for SVG 2 
            //       https://svgwg.org/specs/strokes/#SpecifyingStrokeAlignment
            switch (shape.getStrokeType()) {
                case INSIDE:
                    elem.setAttribute("stroke-alignment", "inner");
                    break;
                case CENTERED:
                    elem.setAttribute("stroke-alignment", "center");
                    break;
                case OUTSIDE:
                    elem.setAttribute("stroke-alignment", "outer");
                    break;
                default:
                    throw new InternalError("Unsupported stroke type " + shape.getStrokeType());
            }
        }
    }

    private void writeStrokeAttributes(Element elem, BorderStroke shape) {
        if (shape.getTopStroke() != null) {
            elem.setAttribute("stroke", paint.toString(shape.getTopStroke()));
        }
        if (shape.getWidths().getTop() != 1) {
            elem.setAttribute("stroke-width", nb.toString(shape.getWidths().getTop()));
        }
        BorderStrokeStyle style = shape.getTopStyle();
        // FIXME support top/right/bottom/left style!!
        if (style.getLineCap() != StrokeLineCap.BUTT) {
            elem.setAttribute("stroke-linecap", style.getLineCap().toString().toLowerCase());
        }
        if (style.getLineJoin() != StrokeLineJoin.MITER) {
            elem.setAttribute("stroke-linecap", style.getLineJoin().toString().toLowerCase());
        }
        if (style.getMiterLimit() != 4) {
            elem.setAttribute("stroke-miterlimit", nb.toString(style.getMiterLimit()));
        }
        if (!style.getDashArray().isEmpty()) {
            elem.setAttribute("stroke-dasharray", nbList.toStringFromCollection(style.getDashArray()));
        }
        if (style.getDashOffset() != 0) {
            elem.setAttribute("stroke-dashoffset", nb.toString(style.getDashOffset()));
        }
        if (style.getType() != StrokeType.CENTERED) {
            // XXX this is currently only a proposal for SVG 2 
            //       https://svgwg.org/specs/strokes/#SpecifyingStrokeAlignment
            switch (style.getType()) {
                case INSIDE:
                    elem.setAttribute("stroke-alignment", "inner");
                    break;
                case CENTERED:
                    elem.setAttribute("stroke-alignment", "center");
                    break;
                case OUTSIDE:
                    elem.setAttribute("stroke-alignment", "outer");
                    break;
                default:
                    throw new InternalError("Unsupported stroke type " + style.getType());
            }
        }
    }

    private void writeStyleAttributes(Element elem, Node node) {
        String id = node.getId();
        if (id != null && !id.isEmpty()) {
            elem.setAttribute("id", id);
        }
        List<String> styleClass = node.getStyleClass();
        if (!styleClass.isEmpty()) {
            StringBuffer buf = new StringBuffer();
            for (String clazz : styleClass) {
                if (buf.length() != 0) {
                    buf.append(' ');
                }
                buf.append(clazz);
            }
            elem.setAttribute("class", buf.toString());
        }

        if (!node.isVisible()) {
            elem.setAttribute("visibility", "hidden");
        }
    }

    private Element writeText(Document doc, Element parent, Text node) {
        Element elem = doc.createElement("text");
        parent.appendChild(elem);
        elem.appendChild(doc.createTextNode(node.getText()));

        elem.setAttribute("x", nb.toString(node.getX()));
        elem.setAttribute("y", nb.toString(node.getY()));

        writeTextAttributes(elem, node);

        return elem;
    }

    private void writeTextAttributes(Element elem, Text node) {
        Font ft = node.getFont();
        elem.setAttribute("font-family", (ft.getFamily().equals(ft.getName())) ? "'" + ft.getName() + "'" : "'" + ft.getName() + "', '" + ft.getFamily() + "'");
        elem.setAttribute("font-size", nb.toString(ft.getSize()));
        elem.setAttribute("font-style", ft.getStyle().contains("italic") ? "italic" : "normal");
        elem.setAttribute("font-weight", ft.getStyle().contains("bold") || ft.getName().toLowerCase().contains("bold") ? "bold" : "normal");
    }

    private void writeTransformAttributes(Element elem, Node node) {

        // The transforms are applied before translateX, translateY, scaleX, 
        // scaleY and rotate transforms.
        List< Transform> txs = new ArrayList<>();
        Point2D pivot = Geom.center(node.getBoundsInLocal());
        txs.add(new Translate(node.getTranslateX(), node.getTranslateY()));
        txs.add(new Rotate(node.getRotate(), pivot.getX(), pivot.getY()));
        txs.add(new Scale(node.getScaleX(), node.getScaleY(), pivot.getX(), pivot.getY()));
        txs.addAll(node.getTransforms());
        writeTransformAttributes(elem, txs);
    }

    private void writeTransformAttributes(Element elem, List< Transform> txs) {

        if (txs.size() > 0) {
            String value = tx.toString(txs);
            if (!value.isEmpty()) {
                elem.setAttribute("transform", value);
            }
        }
    }
}
