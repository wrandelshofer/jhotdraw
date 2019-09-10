/*
 * @(#)SvgExporter.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg;

import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Point2D;
import javafx.geometry.VPos;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.*;
import javafx.scene.shape.*;
import javafx.scene.text.Font;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.scene.transform.Rotate;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.text.CssDoubleConverter;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssNumberConverter;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.io.UriResolver;
import org.jhotdraw8.svg.text.SvgPaintConverter;
import org.jhotdraw8.svg.text.SvgTransformConverter;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.xml.XmlUtil;
import org.jhotdraw8.xml.text.XmlNumberConverter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.imageio.ImageIO;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.awt.font.FontRenderContext;
import java.awt.font.LineBreakMeasurer;
import java.awt.font.TextAttribute;
import java.awt.font.TextLayout;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.Writer;
import java.net.URI;
import java.text.AttributedCharacterIterator;
import java.text.AttributedString;
import java.util.*;
import java.util.function.Function;

/**
 * Exports a JavaFX scene graph to SVG.
 *
 * @author Werner Randelshofer
 */
public class SvgExporter {

    public final static String SVG_MIME_TYPE = "image/svg+xml";

    private final static String XLINK_NS = "http://www.w3.org/1999/xlink";
    private final static String XLINK_Q = "xlink";
    private final static String XMLNS_NS = "http://www.w3.org/2000/xmlns/";

    private final String SVG_NS = "http://www.w3.org/2000/svg";
    @Nonnull
    private IdFactory idFactory = new SimpleIdFactory();
    private final Object imageUriKey;
    @Nullable
    private final String namespaceQualifier = null;
    private final XmlNumberConverter nb = new XmlNumberConverter();
    private final Converter<ImmutableList<Number>> nbList = new CssListConverter<>(new CssNumberConverter(false));
    private final Converter<ImmutableList<Double>> doubleList = new CssListConverter<>(new CssDoubleConverter(false));
    private final Converter<Paint> paintConverter = new SvgPaintConverter(true);
    private boolean skipInvisibleNodes = true;
    private boolean relativizePaths = false;
    private final Object skipKey;
    private final Converter<ImmutableList<Transform>> tx = new CssListConverter<>(new SvgTransformConverter(false));
    @Nullable
    private Function<URI, URI> uriResolver = new UriResolver(null, null);

    /**
     * @param imageUriKey this property is used to retrieve an URL from an
     *                    ImageView
     * @param skipKey     this property is used to retrieve a Boolean from a Node.
     *                    If the Boolean is true, then the node is skipped.
     */
    public SvgExporter(Object imageUriKey, Object skipKey) {
        this.imageUriKey = imageUriKey;
        this.skipKey = skipKey;
    }

    private String createFileComment() {
        return null;
    }

    @Nullable
    public Function<URI, URI> getUriResolver() {
        return uriResolver;
    }

    public void setUriResolver(Function<URI, URI> uriResolver) {
        this.uriResolver = uriResolver;
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

    private boolean isSkipNode(@Nonnull Node node) {
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
                if (node instanceof Path) {
                    Path p = (Path) node;
                    return p.getElements().isEmpty();
                } else if (node instanceof Polyline) {
                    Polyline p = (Polyline) node;
                    return p.getPoints().isEmpty();
                } else if (node instanceof Polygon) {
                    Polygon p = (Polygon) node;
                    return p.getPoints().isEmpty();
                }
            } else if (node instanceof Group) {
                Group g = (Group) node;
                return g.getChildren().isEmpty();
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

    private Element writeCircle(Document doc, @Nonnull Element
            parent, Circle node) {
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

    private void writeClipAttributes(@Nonnull Element elem, Node node) {
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

    private void writeClipPathDefs(@Nonnull Document doc, @Nonnull Element
            defsNode, Node node) throws IOException {
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

    private void writeCompositingAttributes(@Nonnull Element elem, Node
            node) {
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

    private Element writeCubicCurve(Document doc, Element
            parent, CubicCurve node) {
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

    private void writeDefsRecursively(Document doc, Element
            defsNode, javafx.scene.Node node) throws IOException {
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

    private void writeDocumentElementAttributes(Element
                                                        docElement, javafx.scene.Node drawingNode) throws IOException {
        docElement.setAttribute("version", "1.2");
        docElement.setAttribute("baseProfile", "tiny");

    }

    private Element writeEllipse(Document doc, @Nonnull Element
            parent, Ellipse node) {
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

    private void writeFillAttributes(@Nonnull Element elem, Shape node) {
        Paint fill = node.getFill();
        String id = idFactory.getId(fill);
        if (id != null) {
            elem.setAttribute("fill", "url(#" + id + ")");
        } else {
            elem.setAttribute("fill", paintConverter.toString(fill));
            if (fill instanceof Color) {
                Color c = (Color) fill;
                if (!c.isOpaque()) {
                    elem.setAttribute("fill-opacity", nb.toString(c.getOpacity()));
                }
            }
        }


        final FillRule fillRule;
        if (node instanceof Path) {
            Path path = (Path) node;
            fillRule = path.getFillRule();
        } else if (node instanceof SVGPath) {
            SVGPath path = (SVGPath) node;
            fillRule = path.getFillRule();
        } else {
            fillRule = FillRule.NON_ZERO;
        }
        switch (fillRule) {
            case EVEN_ODD:
                elem.setAttribute("fill-rule", "evenodd");
                break;
            case NON_ZERO:
            default:
                break;
        }
    }

    private Element writeGroup(Document doc, Element parent, @Nonnull Group
            node) {
        Element elem = doc.createElement("g");
        writeClipAttributes(elem, node);
        parent.appendChild(elem);
        return elem;
    }

    private Element writeImageView(Document doc, Element parent, ImageView
            node) throws IOException {
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
            href = uriResolver.apply(uri).toString();
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

    private Element writeLine(Document doc, @Nonnull Element parent, Line
            node) {
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

    private void writeNodeRecursively(Document doc, Element
            parent, javafx.scene.Node node) throws IOException {
        if (isSkipNode(node)) {
            return;
        }
        Element elem = null;
        if (node instanceof Shape) {
            elem = writeShape(doc, parent, (Shape) node);
            if (elem != null) {
                writeFillAttributes(elem, (Shape) node);
                writeStrokeAttributes(elem, (Shape) node);
                writeClipAttributes(elem, node);
            }
        } else if (node instanceof Group) {
            // a group can be omitted if it does not have any children
            Group g = (Group) node;
            boolean omitGroup = skipInvisibleNodes && g.getChildren().isEmpty();
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

    private void writePaintDefs(@Nonnull Document doc, @Nonnull Element
            defsNode, Paint paint) throws IOException {
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
                    stopElem.setAttribute("stop-color", this.paintConverter.toString(c));
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
                    stopElem.setAttribute("stop-color", this.paintConverter.toString(c));
                    if (!c.isOpaque()) {
                        stopElem.setAttribute("stop-opacity", nb.toString(c.getOpacity()));
                    }
                    elem.appendChild(stopElem);
                }
                defsNode.appendChild(elem);
            }
        }
    }

    private Element writePath(@Nonnull Document doc, @Nonnull Element
            parent, Path node) {
        if (node.getElements().isEmpty()) {
            return null;
        }
        Element elem = doc.createElement("path");
        parent.appendChild(elem);
        String d;
        if (relativizePaths) {
            d = Shapes.doubleRelativeSvgStringFromAWT(Shapes.awtShapeFromFXPathElements(node.getElements()).getPathIterator(null));
        } else {
            d = Shapes.doubleSvgStringFromElements(node.getElements());
        }
        elem.setAttribute("d", d);
        return elem;
    }

    private Element writePolygon(Document doc, @Nonnull Element
            parent, Polygon node) {
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

    private Element writePolyline(Document doc, @Nonnull Element
            parent, Polyline node) {
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

    private void writeProcessingInstructions(Document
                                                     doc, javafx.scene.Node external) {
// empty
    }

    private Element writeQuadCurve(Document doc, Element parent, QuadCurve
            node) {
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

    private Element writeRectangle(Document doc, @Nonnull Element
            parent, Rectangle node) {
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

    private Element writeSVGPath(Document doc, @Nonnull Element
            parent, SVGPath node) {
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

    /**
     * Writes a shape if it has a visual representation.
     *
     * @param doc    the document
     * @param parent the parent element
     * @param node   the shape
     * @return the created element or null if the shape has no visual representation (e.g. a path without path elements)
     * @throws IOException
     */
    @Nullable
    private Element writeShape(@Nonnull Document doc, @Nonnull Element
            parent, Shape node) throws IOException {
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

    private void writeStrokeAttributes(@Nonnull Element elem, Shape shape) {
        Paint stroke = shape.getStroke();
        if (stroke == null) {
            return;
        }


        String id = idFactory.getId(stroke);
        if (id != null) {
            elem.setAttribute("stroke", "url(#" + id + ")");
        } else {
            elem.setAttribute("stroke", paintConverter.toString(stroke));
            if (stroke instanceof Color) {
                Color c = (Color) stroke;
                if (!c.isOpaque()) {
                    elem.setAttribute("stroke-opacity", nb.toString(c.getOpacity()));
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
            elem.setAttribute("stroke-dasharray", doubleList.toString(ImmutableLists.ofCollection(shape.getStrokeDashArray())));
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

    private void writeStrokeAttributes(@Nonnull Element elem, BorderStroke
            shape) {
        if (shape.getTopStroke() != null) {
            elem.setAttribute("stroke", paintConverter.toString(shape.getTopStroke()));
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
            elem.setAttribute("stroke-dasharray", doubleList.toString(ImmutableLists.ofCollection(style.getDashArray())));
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

    private void writeStyleAttributes(@Nonnull Element elem, Node node) {
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
        Element elem = node.getWrappingWidth() > 0 ? writeWrappedText(doc, parent, node) : writeUnwrappedText(doc, parent, node);
        writeTextAttributes(elem, node);
        return elem;
    }

    private Element writeUnwrappedText(Document doc, Element parent, Text
            node) {
        Element elem = doc.createElement("text");
        parent.appendChild(elem);

        String x = nb.toString(node.getX());

        VPos vpos = node.getTextOrigin();
        final double y = node.getLayoutBounds().getMinY() + node.getBaselineOffset();
        elem.setAttribute("x", x);
        elem.setAttribute("y", nb.toString(y));
        double lineSpacing = node.getLineSpacing();
        double fontSize = node.getFont().getSize() * 96 / 72;

        String text = node.getText();
        if (text != null) {
            String[] lines = text.split("\n");
            if (lines.length == 1) {
                elem.appendChild(doc.createTextNode(lines[0]));
            } else {
                for (int i = 0; i < lines.length; i++) {
                    Element tspan = doc.createElement("tspan");
                    tspan.appendChild(doc.createTextNode(lines[i]));
                    tspan.setAttribute("x", x);
                    if (i != 0) {
                        tspan.setAttribute("dy", Double.toString(lineSpacing + fontSize));
                    }
                    elem.appendChild(tspan);
                }
            }
        }

        return elem;
    }

    private Element writeWrappedText(Document doc, Element parent, Text
            node) {
        Element elem = doc.createElement("text");
        parent.appendChild(elem);
        drawText(doc, elem, node.getText(), node.getLayoutBounds(), node.getFont(), 8,
                node.isUnderline(), node.isStrikethrough(), node.getTextAlignment(), node.getLineSpacing());
        return elem;
    }

    private void drawText(Document doc, Element parent, String str, Bounds
            textRect,
                          Font tfont, int tabSize, boolean isUnderlined,
                          boolean isStrikethrough,
                          TextAlignment textAlignment, double lineSpacing) {
        FontRenderContext frc = new FontRenderContext(new AffineTransform(), true, true);
        java.awt.Font font = new java.awt.Font(tfont.getName(), java.awt.Font.PLAIN, (int) tfont.getSize()).deriveFont((float) tfont.getSize());
        float leftMargin = (float) textRect.getMinX();
        float rightMargin = (float) Math.max(leftMargin + 1, textRect.getMinX() + textRect.getWidth() + 1);
        float verticalPos = (float) textRect.getMinY();
        float maxVerticalPos = (float) (textRect.getMinY() + textRect.getHeight());
        if (leftMargin < rightMargin) {
            //float tabWidth = (float) (getTabSize() * g.getFontMetrics(font).charWidth('m'));
            float tabWidth = (float) (tabSize * font.getStringBounds("m", frc).getWidth());
            float[] tabStops = new float[(int) (textRect.getWidth() / tabWidth)];
            for (int i = 0; i < tabStops.length; i++) {
                tabStops[i] = (float) (textRect.getMinX() + (int) (tabWidth * (i + 1)));
            }

            if (str != null) {
                String[] paragraphs = str.split("\n");

                for (int i = 0; i < paragraphs.length; i++) {
                    if (paragraphs[i].length() == 0) {
                        paragraphs[i] = " ";
                    }
                    AttributedString as = new AttributedString(paragraphs[i]);
                    as.addAttribute(TextAttribute.FONT, font);
                    if (isUnderlined) {
                        as.addAttribute(TextAttribute.UNDERLINE, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                    }
                    if (isStrikethrough) {
                        as.addAttribute(TextAttribute.STRIKETHROUGH, TextAttribute.UNDERLINE_LOW_ONE_PIXEL);
                    }
                    int tabCount = paragraphs[i].split("\t").length - 1;
                    Rectangle2D.Double paragraphBounds = drawParagraph(doc, parent, frc,
                            paragraphs[i], as.getIterator(), verticalPos, maxVerticalPos, leftMargin, rightMargin, tabStops, tabCount, textAlignment,
                            lineSpacing);
                    verticalPos = (float) (paragraphBounds.y + paragraphBounds.height + lineSpacing);
                    if (verticalPos > maxVerticalPos) {
                        break;
                    }
                }
            }
        }
    }

    /**
     * Draws or measures a paragraph of text at the specified y location and
     * the bounds of the paragraph.
     *
     * @param styledText     the text of the paragraph.
     * @param verticalPos    the top bound of the paragraph
     * @param maxVerticalPos the bottom bound of the paragraph
     * @param leftMargin     the left bound of the paragraph
     * @param rightMargin    the right bound of the paragraph
     * @param tabStops       an array with tab stops
     * @param tabCount       the number of entries in tabStops which contain actual
     *                       values
     * @return Returns the actual bounds of the paragraph.
     */
    private Rectangle2D.Double drawParagraph(Document doc, Element parent,
                                             FontRenderContext frc, String
                                                     paragraph, AttributedCharacterIterator styledText,
                                             float verticalPos, float maxVerticalPos, float leftMargin,
                                             float rightMargin, float[] tabStops, int tabCount,
                                             TextAlignment textAlignment, double lineSpacing) {
        // This method is based on the code sample given
        // in the class comment of java.awt.font.LineBreakMeasurer,

        // assume styledText is an AttributedCharacterIterator, and the number
        // of tabs in styledText is tabCount

        Rectangle2D.Double paragraphBounds = new Rectangle2D.Double(leftMargin, verticalPos, 0, 0);

        int[] tabLocations = new int[tabCount + 1];

        int i = 0;
        for (char c = styledText.first(); c != AttributedCharacterIterator.DONE; c = styledText.next()) {
            if (c == '\t') {
                tabLocations[i++] = styledText.getIndex();
            }
        }
        tabLocations[tabCount] = styledText.getEndIndex() - 1;

        // Now tabLocations has an entry for every tab's offset in
        // the text.  For convenience, the last entry is tabLocations
        // is the offset of the last character in the text.

        LineBreakMeasurer measurer = new LineBreakMeasurer(styledText, frc);
        int currentTab = 0;

        int textIndex = 0;
        while (measurer.getPosition() < styledText.getEndIndex() && verticalPos <= maxVerticalPos) {

            // Lay out and draw each line.  All segments on a line
            // must be computed before any drawing can occur, since
            // we must know the largest ascent on the line.
            // TextLayouts are computed and stored in a List;
            // their horizontal positions are stored in a parallel
            // List.

            // lineContainsText is true after first segment is drawn
            boolean lineContainsText = false;
            boolean lineComplete = false;
            float maxAscent = 0, maxDescent = 0;
            float horizontalPos = leftMargin;
            LinkedList<TextLayout> layouts = new LinkedList<>();
            LinkedList<Float> penPositions = new LinkedList<>();

            int first = layouts.size();

            while (!lineComplete && verticalPos <= maxVerticalPos) {
                float wrappingWidth = rightMargin - horizontalPos;
                TextLayout layout = null;
                layout = measurer.nextLayout(wrappingWidth,
                        tabLocations[currentTab] + 1,
                        lineContainsText);

                // layout can be null if lineContainsText is true
                if (layout != null) {
                    layouts.add(layout);
                    penPositions.add(horizontalPos);
                    horizontalPos += layout.getAdvance();
                    maxAscent = Math.max(maxAscent, layout.getAscent());
                    maxDescent = Math.max(maxDescent,
                            layout.getDescent() + layout.getLeading());
                } else {
                    lineComplete = true;
                }

                lineContainsText = true;

                if (measurer.getPosition() == tabLocations[currentTab] + 1) {
                    currentTab++;
                }

                if (measurer.getPosition() == styledText.getEndIndex()) {
                    lineComplete = true;
                } else if (tabStops.length == 0 || horizontalPos >= tabStops[tabStops.length - 1]) {
                    lineComplete = true;
                }
                if (!lineComplete) {
                    // move to next tab stop
                    int j = 0;
                    while (horizontalPos >= tabStops[j]) {
                        j++;
                    }
                    horizontalPos = tabStops[j];
                }
            }
            // If there is only one layout element on the line, and we are
            // drawing, then honor alignment
            if (first == layouts.size() - 1) {
                switch (textAlignment) {
                    case RIGHT:
                        penPositions.set(first, rightMargin - layouts.get(first).getVisibleAdvance() - 1);
                        break;
                    case CENTER:
                        penPositions.set(first, (rightMargin - 1 - leftMargin - layouts.get(first).getVisibleAdvance()) / 2 + leftMargin);
                        break;
                    case JUSTIFY:
                        // not supported
                        break;
                    case LEFT:
                    default:
                        break;
                }
            }

            verticalPos += maxAscent;
            Iterator<Float> positionEnum = penPositions.iterator();

            // now iterate through layouts and draw them
            styledText.first();
            for (TextLayout nextLayout : layouts) {
                float nextPosition = positionEnum.next();

                Element tspan = doc.createElement("tspan");
                int characterCount = nextLayout.getCharacterCount();
                tspan.appendChild(doc.createTextNode(paragraph.substring(textIndex, textIndex + characterCount)));
                tspan.setAttribute("x", nb.toString(nextPosition));
                tspan.setAttribute("y", nb.toString(verticalPos));
                parent.appendChild(tspan);

                Rectangle2D layoutBounds = nextLayout.getBounds();
                paragraphBounds.add(new Rectangle2D.Double(layoutBounds.getX() + nextPosition,
                        layoutBounds.getY() + verticalPos,
                        layoutBounds.getWidth(),
                        layoutBounds.getHeight()));

                textIndex += characterCount;
            }

            verticalPos += maxDescent + lineSpacing;
        }

        return paragraphBounds;
    }

    private void writeTextAttributes(Element elem, Text node) {
        Font ft = node.getFont();
        elem.setAttribute("font-family", (ft.getFamily().equals(ft.getName())) ? "'" + ft.getName() + "'" : "'" + ft.getName() + "', '" + ft.getFamily() + "'");
        elem.setAttribute("font-size", nb.toString(ft.getSize()));
        final String style = ft.getStyle().contains("Italic") ? "italic" : "normal";
        if (!style.equals("normal")) {
            elem.setAttribute("font-style", style);
        }
        final String weight = ft.getStyle().contains("Bold") || ft.getName().toLowerCase().contains("bold") ? "bold" : "normal";
        if (!weight.equals("normal")) {
            elem.setAttribute("font-weight", weight);
        }
        if (node.isUnderline()) {
            elem.setAttribute("text-decoration", "underline");
        } else if (node.isStrikethrough()) {
            elem.setAttribute("text-decoration", "line-through ");
        }
    }

    private void writeTransformAttributes(@Nonnull Element elem, Node node) {

        // The transforms are applied before translateX, translateY, scaleX, 
        // scaleY and rotate transforms.
        List<Transform> txs = new ArrayList<>();
        Point2D pivot = Geom.center(node.getBoundsInLocal());
        if (node.getTranslateX() != 0.0 || node.getTranslateY() != 0.0) {
            txs.add(new Translate(node.getTranslateX(), node.getTranslateY()));
        }
        if (node.getRotate() != 0.0) {
            txs.add(new Rotate(node.getRotate(), pivot.getX(), pivot.getY()));
        }
        if (node.getScaleX() != 1.0 || node.getScaleY() != 1.0) {
            txs.add(new Scale(node.getScaleX(), node.getScaleY(), pivot.getX(), pivot.getY()));
        }
        txs.addAll(node.getTransforms());
        writeTransformAttributes(elem, txs);
    }

    private void writeTransformAttributes(@Nonnull Element
                                                  elem, List<Transform> txs) {

        if (txs.size() > 0) {
            String value = tx.toString(ImmutableLists.ofCollection(txs));
            if (!value.isEmpty()) {
                elem.setAttribute("transform", value);
            }
        }
    }

    public boolean isRelativizePaths() {
        return relativizePaths;
    }

    public void setRelativizePaths(boolean relativizePaths) {
        this.relativizePaths = relativizePaths;
    }
}
