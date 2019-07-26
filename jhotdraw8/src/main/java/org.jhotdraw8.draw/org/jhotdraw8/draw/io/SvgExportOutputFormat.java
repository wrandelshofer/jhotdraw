/*
 * @(#)SvgExportOutputFormat.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.DataFormat;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.text.CssListConverter;
import org.jhotdraw8.css.text.CssSizeConverter;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.ImageFigure;
import org.jhotdraw8.draw.figure.Page;
import org.jhotdraw8.draw.figure.PageFigure;
import org.jhotdraw8.draw.figure.Slice;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.io.UriResolver;
import org.jhotdraw8.svg.SvgExporter;
import org.jhotdraw8.svg.TransformFlattener;
import org.jhotdraw8.svg.text.SvgPaintConverter;
import org.jhotdraw8.svg.text.SvgTransformConverter;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.xml.XmlUtil;
import org.jhotdraw8.xml.text.XmlNumberConverter;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.IOException;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.jhotdraw8.draw.SimpleDrawingRenderer.toNode;

/**
 * Exports a JavaFX scene graph to SVG.
 *
 * @author Werner Randelshofer
 */
public class SvgExportOutputFormat extends AbstractExportOutputFormat implements ClipboardOutputFormat, OutputFormat, XmlOutputFormatMixin {

    private final static String SKIP_KEY = "skip";

    public final static DataFormat SVG_FORMAT;

    private final static String XLINK_NS = "http://www.w3.org/1999/xlink";
    private final static String XLINK_Q = "xlink";
    private final static String XMLNS_NS = "http://www.w3.org/2000/xmlns/";

    static {
        DataFormat fmt = DataFormat.lookupMimeType("image/svg+xml");
        if (fmt == null) {
            fmt = new DataFormat("image/svg+xml");
        }
        SVG_FORMAT = fmt;
    }

    private final String SVG_NS = "http://www.w3.org/2000/svg";

    @Nonnull
    private IdFactory idFactory = new SimpleIdFactory();

    @Nullable
    private final String namespaceQualifier = null;
    private final XmlNumberConverter nb = new XmlNumberConverter();
    private final CssSizeConverter sc = new CssSizeConverter(false);
    private final Converter<ImmutableList<CssSize>> nbList = new CssListConverter<>(new CssSizeConverter(false));
    private final SvgPaintConverter paint = new SvgPaintConverter(true);
    private boolean skipInvisibleNodes = true;
    private final Converter<CssSize> sznb = new CssSizeConverter(false);
    private final Converter<ImmutableList<Transform>> tx = new CssListConverter<>(new SvgTransformConverter(false));

    @Nonnull
    private SvgExporter createExporter() {
        SvgExporter exporter = new SvgExporter(ImageFigure.IMAGE_URI, SKIP_KEY);
        exporter.setUriResolver(getUriResolver());
        return exporter;
    }

    @Nonnull
    @Override
    protected String getExtension() {
        return "svg";
    }

    @Override
    protected boolean isResolutionIndependent() {
        return true;
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

    public Document toDocument(URI documentHome, @Nonnull Drawing external) throws IOException {
        return toDocument(documentHome, external, Collections.singleton(external));
    }

    public Document toDocument(URI documentHome, @Nonnull Drawing external, Collection<Figure> selection) throws IOException {
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        javafx.scene.Node drawingNode = toNode(external, selection, hints);
        final SvgExporter exporter = createExporter();
        exporter.setSkipInvisibleNodes(false);
        Document doc = exporter.toDocument(drawingNode);
        writeDrawingElementAttributes(doc.getDocumentElement(), external);
        return doc;
    }

    @Override
    public void write(@Nonnull Map<DataFormat, Object> clipboard, @Nonnull Drawing drawing, Collection<Figure> selection) throws IOException {
        setUriResolver(new UriResolver(drawing.get(Drawing.DOCUMENT_HOME), null));
        StringWriter out = new StringWriter();
        Document doc = toDocument(null, drawing, selection);
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

    public void write(@Nonnull Path file, @Nonnull Drawing drawing, WorkState workState) throws IOException {
        if (isExportDrawing()) {
            XmlOutputFormatMixin.super.write(file, drawing, workState);
        }
        if (isExportSlices()) {
            writeSlices(file.getParent(), drawing);
        }
        if (isExportPages()) {
            String basename = file.getFileName().toString();
            int p = basename.lastIndexOf('.');
            if (p != -1) {
                basename = basename.substring(0, p);
            }
            writePages(file.getParent(), basename, drawing);
        }
    }

    private void writeDrawingElementAttributes(Element docElement, Drawing drawing) throws IOException {
        docElement.setAttribute("width", sc.toString(drawing.get(Drawing.WIDTH)));
        docElement.setAttribute("height", sc.toString(drawing.get(Drawing.HEIGHT)));
    }

    @Override
    protected void writePage(@Nonnull Path file, @Nonnull Page page, @Nonnull Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException {
        CssSize pw = page.get(PageFigure.PAPER_WIDTH);
        markNodesOutsideBoundsWithSkip(node, Transforms.transform(page.getLocalToWorld(), page.getPageBounds(internalPageNumber)));
        node.getTransforms().setAll(page.getWorldToLocal());
        final SvgExporter exporter = createExporter();
        final Document doc = exporter.toDocument(node);
        writePageElementAttributes(doc.getDocumentElement(), page, internalPageNumber);
        node.getTransforms().clear();
        XmlUtil.write(file, doc);
    }

    private void writePageElementAttributes(Element docElement, Page page, int internalPageNumber) throws IOException {
        Bounds b = page.getBoundsInLocal();
        Bounds pb = page.getPageBounds(internalPageNumber);
        docElement.setAttribute("width", sznb.toString(page.get(PageFigure.PAPER_WIDTH)));
        docElement.setAttribute("height", sznb.toString(page.get(PageFigure.PAPER_HEIGHT)));
        docElement.setAttribute("viewBox", nb.
                toString(pb.getMinX()) + " " + nb.toString(pb.getMinY())
                + " " + nb.toString(pb.getWidth()) + " " + nb.toString(pb.getHeight()));
    }

    @Override
    protected boolean writeSlice(@Nonnull Path file, @Nonnull Slice slice, @Nonnull Node node, double dpi) throws IOException {
        markNodesOutsideBoundsWithSkip(node, slice.getBoundsInLocal());
        Transform worldToLocal = slice.getWorldToLocal();
        Point2D sliceOrigin = slice.getSliceOrigin();
        worldToLocal = Transforms.concat(worldToLocal, new Translate(-sliceOrigin.getX(), -sliceOrigin.getY()));
        if (worldToLocal != null) {
            node.getTransforms().setAll(worldToLocal);
        }
        new TransformFlattener().flattenTranslates(node);
        final SvgExporter exporter = createExporter();
        final Document doc = exporter.toDocument(node);
        writeSliceElementAttributes(doc.getDocumentElement(), slice);
        node.getTransforms().clear();
        XmlUtil.write(file, doc);
        return true;
    }

    private void writeSliceElementAttributes(Element docElement, Slice slice) throws IOException {
        Bounds b = slice.getBoundsInLocal();
        Point2D sliceOrigin = slice.getSliceOrigin();
        Transform tx = slice.getWorldToLocal();
        docElement.setAttribute("width", nb.toString(b.getWidth()));
        docElement.setAttribute("height", nb.toString(b.getHeight()));
        docElement.setAttribute("viewBox", nb.
                toString(b.getMinX() - sliceOrigin.getX()) + " " + nb.toString(b.getMinY() - sliceOrigin.getY())
                + " " + nb.toString(b.getWidth()) + " " + nb.toString(b.getHeight()));
    }

}
