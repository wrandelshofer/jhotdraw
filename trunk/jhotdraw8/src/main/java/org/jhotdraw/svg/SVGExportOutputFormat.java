/* @(#)SVGExportOutputFormat.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.svg;

import java.io.IOException;
import java.io.OutputStream;
import javafx.scene.Group;
import javafx.scene.Parent;
import javafx.scene.layout.Region;
import javafx.scene.shape.Arc;
import javafx.scene.shape.ArcTo;
import javafx.scene.shape.ArcType;
import javafx.scene.shape.Circle;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.CubicCurve;
import javafx.scene.shape.CubicCurveTo;
import javafx.scene.shape.Ellipse;
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
import javafx.scene.shape.VLineTo;
import javafx.scene.text.Text;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw.draw.Drawing;
import org.jhotdraw.draw.SimpleDrawingRenderer;
import org.jhotdraw.draw.figure.Figure;
import org.jhotdraw.draw.io.OutputFormat;
import org.jhotdraw.text.XmlNumberConverter;
import org.jhotdraw.text.XmlPaintConverter;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

/**
 * SVGExportOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class SVGExportOutputFormat implements OutputFormat {

    private final XmlPaintConverter paint = new XmlPaintConverter();
    private final XmlNumberConverter nb = new XmlNumberConverter();
    private final String namespaceURI = "http://www.w3.org/2000/svg";
    private final String namespaceQualifier = null;

    @Override
    public void write(OutputStream out, Drawing drawing) throws IOException {
        Document doc = toDocument(drawing);
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
    }

    public Document toDocument(Drawing external) throws IOException {
        SimpleDrawingRenderer r = new SimpleDrawingRenderer();
        javafx.scene.Node drawingNode = r.render(external);
        try {
            DocumentBuilderFactory builderFactory = DocumentBuilderFactory.newInstance();
            Document doc;
            builderFactory.setNamespaceAware(true);
            DocumentBuilder builder = builderFactory.newDocumentBuilder();
            DOMImplementation domImpl = builder.getDOMImplementation();
            doc = domImpl.createDocument(namespaceURI, namespaceQualifier == null ? "svg" : namespaceQualifier + ":" + "svg", null);

            Element docElement = doc.getDocumentElement();

            writeProcessingInstructions(doc, external, drawingNode);
            String commentText = createFileComment();
            if (commentText != null) {
                docElement.getParentNode().insertBefore(doc.createComment(commentText), docElement);
            }

            writeDocumentElementAttributes(docElement, external, drawingNode);
            writeNodeRecursively(doc, docElement, drawingNode);
            docElement.appendChild(doc.createTextNode("\n"));

            return doc;
        } catch (ParserConfigurationException ex) {
            throw new IOException(ex);
        }
    }

    private String createFileComment() {
        return null;
    }

    private void writeDocumentElementAttributes(Element docElement, Drawing drawing, javafx.scene.Node drawingNode) throws IOException {
        docElement.setAttribute("version", "1.2");
        docElement.setAttribute("baseProfile", "tiny");
        docElement.setAttribute("width", nb.toString(drawing.get(Drawing.WIDTH)));
        docElement.setAttribute("height", nb.toString(drawing.get(Drawing.HEIGHT)));
    }

    private void writeProcessingInstructions(Document doc, Drawing drawing, javafx.scene.Node external) {
// empty
    }

    private void writeNodeRecursively(Document doc, Element parent, javafx.scene.Node node) throws IOException {
        parent.appendChild(doc.createTextNode("\n"));

        Element elem = null;
        if (node instanceof Shape) {
            elem = writeShape(doc, parent, (Shape) node);
        } else if (node instanceof Group) {
            elem = writeGroup(doc, parent, (Group) node);
        } else if (node instanceof Region) {
            elem = writeRegion(doc, parent, (Region) node);
        } else {
            throw new UnsupportedOperationException("not yet implemented for " + node);
        }

        if (elem != null && (node instanceof Parent)) {
            Parent pp = (Parent) node;
            for (javafx.scene.Node child : pp.getChildrenUnmodifiable()) {
                writeNodeRecursively(doc, elem, child);
            }
            if (!pp.getChildrenUnmodifiable().isEmpty()) {
                elem.appendChild(doc.createTextNode("\n"));
            }
        }
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
        writeFillAttributes(elem, node);
        writeStrokeAttributes(elem, node);
        return elem;
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
        int largeArc = (length > 180) ? 1 : 0;
        int sweep = (length < 0) ? 1 : 0;

        buf.append('M')
                .append(nb.toString(centerX))
                .append(' ')
                .append(nb.toString(centerY))
                .append(' ');

        if (ArcType.ROUND == node.getType()) {
            buf.append("l ")
                    .append(startX)
                    .append(' ')
                    .append(startY).append(' ');
        }

        buf.append('A')
                .append(nb.toString(radiusX))
                .append(' ')
                .append(nb.toString(radiusY))
                .append(' ')
                .append(nb.toString(xAxisRot))
                .append(' ')
                .append(nb.toString(largeArc))
                .append(' ')
                .append(nb.toString(sweep))
                .append(' ')
                .append(nb.toString(endX))
                .append(' ')
                .append(nb.toString(endY))
                .append(' ');

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

    private Element writeCubicCurve(Document doc, Element parent, CubicCurve node) {
        Element elem = doc.createElement("path");
        parent.appendChild(elem);
        final StringBuilder buf = new StringBuilder();
        buf.append('M')
                .append(nb.toString(node.getStartX()))
                .append(' ')
                .append(nb.toString(node.getStartY()))
                .append(' ')
                .append('C')
                .append(nb.toString(node.getControlX1()))
                .append(' ')
                .append(nb.toString(node.getControlY1()))
                .append(' ')
                .append(nb.toString(node.getControlX2()))
                .append(' ')
                .append(nb.toString(node.getControlY2()))
                .append(' ')
                .append(nb.toString(node.getEndX()))
                .append(' ')
                .append(nb.toString(node.getEndY()));
        elem.setAttribute("content", buf.substring(0));
        return elem;
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

    private Element writePath(Document doc, Element parent, Path node) {
        Element elem = doc.createElement("path");
        parent.appendChild(elem);
        StringBuilder buf = new StringBuilder();
        for (PathElement pe : node.getElements()) {
            if (buf.length() != 0) {
                buf.append(' ');
            }
            if (pe instanceof MoveTo) {
                MoveTo e = (MoveTo) pe;
                buf.append('M')
                        .append(nb.toString(e.getX()))
                        .append(' ')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof LineTo) {
                LineTo e = (LineTo) pe;
                buf.append('L')
                        .append(nb.toString(e.getX()))
                        .append(' ')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof CubicCurveTo) {
                CubicCurveTo e = (CubicCurveTo) pe;
                buf.append('C')
                        .append(nb.toString(e.getControlX1()))
                        .append(' ')
                        .append(nb.toString(e.getControlY1()))
                        .append(' ')
                        .append(nb.toString(e.getControlX2()))
                        .append(' ')
                        .append(nb.toString(e.getControlY2()))
                        .append(' ')
                        .append(nb.toString(e.getX()))
                        .append(' ')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof QuadCurveTo) {
                QuadCurveTo e = (QuadCurveTo) pe;
                buf.append('Q')
                        .append(nb.toString(e.getControlX()))
                        .append(' ')
                        .append(nb.toString(e.getControlY()))
                        .append(' ')
                        .append(nb.toString(e.getX()))
                        .append(' ')
                        .append(nb.toString(e.getY()));
            } else if (pe instanceof ArcTo) {
                ArcTo e = (ArcTo) pe;
                buf.append('A')
                        .append(nb.toString(e.getX()))
                        .append(' ')
                        .append(nb.toString(e.getY()))
                        .append(' ')
                        .append(nb.toString(e.getRadiusX()))
                        .append(' ')
                        .append(nb.toString(e.getRadiusY()));
            } else if (pe instanceof HLineTo) {
                HLineTo e = (HLineTo) pe;
                buf.append('H').append(nb.toString(e.getX()));
            } else if (pe instanceof VLineTo) {
                VLineTo e = (VLineTo) pe;
                buf.append('V').append(nb.toString(e.getY()));
            } else if (pe instanceof ClosePath) {
                buf.append('Z');
            }
        }
        elem.setAttribute("content", buf.toString());
        return elem;
    }

    private Element writePolygon(Document doc, Element parent, Polygon node) {
        Element elem = doc.createElement("polygon");
        StringBuilder buf = new StringBuilder();
        for (Double d : node.getPoints()) {
            if (buf.length() != 0) {
                buf.append(' ');
            }
            buf.append(nb.toString(d));
        }
        elem.setAttribute("points", buf.toString());
        parent.appendChild(elem);
        return elem;
    }

    private Element writePolyline(Document doc, Element parent, Polyline node) {
        Element elem = doc.createElement("polyline");
        StringBuilder buf = new StringBuilder();
        for (Double d : node.getPoints()) {
            if (buf.length() != 0) {
                buf.append(' ');
            }
            buf.append(nb.toString(d));
        }
        elem.setAttribute("points", buf.toString());
        parent.appendChild(elem);
        return elem;
    }

    private Element writeQuadCurve(Document doc, Element parent, QuadCurve node) {
        Element elem = doc.createElement("path");
        parent.appendChild(elem);
        final StringBuilder buf = new StringBuilder();
        buf.append('M')
                .append(nb.toString(node.getStartX()))
                .append(' ')
                .append(nb.toString(node.getStartY()))
                .append(' ')
                .append('Q')
                .append(nb.toString(node.getControlX()))
                .append(' ')
                .append(nb.toString(node.getControlY()))
                .append(' ')
                .append(nb.toString(node.getEndX()))
                .append(' ')
                .append(nb.toString(node.getEndY()));
        elem.setAttribute("content", buf.substring(0));
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

    private Element writeSVGPath(Document doc, Element parent, SVGPath node) {
        Element elem = doc.createElement("path");
        elem.setAttribute("d", node.getContent());
        parent.appendChild(elem);
        return elem;
    }

    private Element writeText(Document doc, Element parent, Text node) {
        Element elem = doc.createElement("text");
        parent.appendChild(elem);
        elem.appendChild(doc.createTextNode(node.getText()));

        elem.setAttribute("x", nb.toString(node.getX()));
        elem.setAttribute("y", nb.toString(node.getY()));

        return elem;
    }

    private Element writeGroup(Document doc, Element parent, Group node) {
        if (node.getChildren().isEmpty()) {
            return parent;
        }
        Element elem = doc.createElement("g");
        parent.appendChild(elem);
        return elem;
    }

    private Element writeRegion(Document doc, Element parent, Region node) {
        Element elem = doc.createElement("g");
        parent.appendChild(elem);
        return elem;
    }

    private void writeFillAttributes(Element elem, Shape node) {
        elem.setAttribute("fill", paint.toString(node.getFill()));
    }

    private void writeStrokeAttributes(Element elem, Shape node) {
        elem.setAttribute("stroke", paint.toString(node.getStroke()));
    }
}
