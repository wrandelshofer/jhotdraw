/* @(#)SvgExportOutputFormat.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import org.jhotdraw8.svg.*;
import java.io.File;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import javafx.geometry.Bounds;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.DataFormat;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import static org.jhotdraw8.draw.SimpleDrawingRenderer.toNode;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.ImageFigure;
import org.jhotdraw8.draw.figure.Page;
import org.jhotdraw8.draw.figure.Slice;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.text.CssTransformListConverter;
import org.jhotdraw8.text.SvgTransformListConverter;
import org.jhotdraw8.text.XmlNumberConverter;
import org.jhotdraw8.text.SvgPaintConverter;
import org.jhotdraw8.text.XmlSizeListConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * Exports a JavaFX scene graph to SVG.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SvgExportOutputFormat extends AbstractExportOutputFormat implements ClipboardOutputFormat, OutputFormat, XmlOutputFormatMixin {

    public final static DataFormat SVG_FORMAT = DataFormat.lookupMimeType("image/svg+xml");

    private final static String XLINK_NS = "http://www.w3.org/1999/xlink";
    private final static String XMLNS_NS = "http://www.w3.org/2000/xmlns/";
    private final static String XLINK_Q = "xlink";
    private final SvgTransformListConverter tx = new SvgTransformListConverter();
    private final SvgPaintConverter paint = new SvgPaintConverter();
    private final XmlNumberConverter nb = new XmlNumberConverter();
    private final CssTransformListConverter txc = new CssTransformListConverter();
    private final XmlSizeListConverter nbList = new XmlSizeListConverter();
    private final String SVG_NS = "http://www.w3.org/2000/svg";
    private final String namespaceQualifier = null;
    private URI internalHome;
    private URI externalHome;
    private IdFactory idFactory = new SimpleIdFactory();
    private String indent = "  ";
    private boolean skipInvisibleNodes = true;

    public Document toDocument(Drawing external) throws IOException {
        return toDocument(external, Collections.singleton(external));
    }

    public Document toDocument(Drawing external, Collection<Figure> selection) throws IOException {
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        javafx.scene.Node drawingNode = toNode(external, selection, hints);
        SvgExporter exporter = new SvgExporter(ImageFigure.IMAGE_URI, null);
        Document doc = exporter.toDocument(drawingNode);
        writeDrawingElementAttributes(doc.getDocumentElement(), external);
        return doc;
    }

    private void writeDrawingElementAttributes(Element docElement, Drawing drawing) throws IOException {
        docElement.setAttribute("width", nb.toString(drawing.get(Drawing.WIDTH)));
        docElement.setAttribute("height", nb.toString(drawing.get(Drawing.HEIGHT)));
    }

    @Override
    public void write(Map<DataFormat, Object> clipboard, Drawing drawing, Collection<Figure> selection) throws IOException {
        setExternalHome(null);
        setInternalHome(drawing.get(Drawing.DOCUMENT_HOME));
        StringWriter out = new StringWriter();
        Document doc = toDocument(drawing, selection);
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(doc);
            StreamResult result = new StreamResult(out);
            t.transform(source, result);
        } catch (TransformerException ex) {
            throw new IOException(ex);
        }
        clipboard.put(SVG_FORMAT, out.toString());
    }

    @Override
    public void setInternalHome(URI uri) {
        internalHome = uri;
    }

    public URI getInternalHome() {
        return internalHome;
    }

    public URI getExternalHome() {
        return externalHome;
    }

    public String getIndent() {
        return indent;
    }

    public void setIndent(String indent) {
        this.indent = indent;
    }

    @Override
    public void setExternalHome(URI uri) {
        externalHome = uri;
    }
    private final static String SKIP_KEY = "skip";

    @Override
    protected void writeSlice(File file, Slice slice, Node node) throws IOException {
        SvgExporter exporter = new SvgExporter(ImageFigure.IMAGE_URI, SKIP_KEY);
        markNodesOutsideBoundsWithSkip(node, slice.getBoundsInLocal());
        // FIXME transform node!
        Document doc = exporter.toDocument(node);
        writeSliceElementAttributes(doc.getDocumentElement(), slice);
        write(file, doc);
    }

    @Override
    protected void writePage(File file, Page page, Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException {
        SvgExporter exporter = new SvgExporter(ImageFigure.IMAGE_URI, SKIP_KEY);
        markNodesOutsideBoundsWithSkip(node, page.getPageBounds(internalPageNumber));
        Document doc = exporter.toDocument(node);
        writePageElementAttributes(doc.getDocumentElement(), page, internalPageNumber);
        write(file, doc);
    }

    private void writeSliceElementAttributes(Element docElement, Slice slice) throws IOException {
  Bounds b=slice.getBoundsInLocal();
        Transform tx=slice.getWorldToLocal();
        docElement.setAttribute("width", nb.toString(b.getWidth()));
        docElement.setAttribute("height", nb.toString(b.getHeight()));

        if (tx != null && !tx.isIdentity()) {
            docElement.setAttribute("transform",
                    txc.toString(ImmutableObservableList.of(new Translate(-b.getMinX(), -b.getMinY()), tx)));
            docElement.setAttribute("viewBox", nb.
                    toString(0) + " " + nb.toString(0)
                    + " " + nb.toString(b.getWidth()) + " " + nb.toString(b.getHeight()));
        } else {
            docElement.setAttribute("viewBox", nb.
                    toString(b.getMinX()) + " " + nb.toString(b.getMinY())
                    + " " + nb.toString(b.getWidth()) + " " + nb.toString(b.getHeight()));
        }    }

    private void writePageElementAttributes(Element docElement, Page page, int internalPageNumber) throws IOException {
  Bounds b=page.getBoundsInLocal();
  Bounds pb=page.getPageBounds(internalPageNumber);
        Transform tx=page.getWorldToLocal();
        docElement.setAttribute("width", nb.toString(b.getWidth()));
        docElement.setAttribute("height", nb.toString(b.getHeight()));

        if (tx != null && !tx.isIdentity()) {
            docElement.setAttribute("transform",
                    txc.toString(ImmutableObservableList.of(new Translate(b.getMinX()-pb.getMinX(), b.getMinY()-pb.getMinY()), tx)));
            docElement.setAttribute("viewBox", nb.
                    toString(pb.getMinX()) + " " + nb.toString(pb.getMinY())
                    + " " + nb.toString(pb.getWidth()) + " " + nb.toString(pb.getHeight()));
        } else {
            docElement.setAttribute("viewBox", nb.
                    toString(pb.getMinX()) + " " + nb.toString(pb.getMinY())
                    + " " + nb.toString(pb.getWidth()) + " " + nb.toString(pb.getHeight()));
        }    }

    @Override
    protected String getExtension() {
        return "svg";
    }

    private void markNodesOutsideBoundsWithSkip(Node node, Bounds sceneBounds) {
        boolean intersects = node.intersects(node.sceneToLocal(sceneBounds));
        if (intersects) {
            node.getProperties().put(SKIP_KEY, false);
            if (node instanceof Parent) {
                Parent parent = (Parent) node;
                for (Node child : parent.getChildrenUnmodifiable()) {
                    markNodesOutsideBoundsWithSkip(child, sceneBounds);
                }
            }
        } else {
            node.getProperties().put(SKIP_KEY, true);
        }
    }

}
