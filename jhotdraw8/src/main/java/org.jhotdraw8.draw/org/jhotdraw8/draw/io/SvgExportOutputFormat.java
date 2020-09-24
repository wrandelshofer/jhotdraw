/*
 * @(#)SvgExportOutputFormat.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.input.DataFormat;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.css.CssDimension2D;
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
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;
import org.jhotdraw8.io.UriResolver;
import org.jhotdraw8.svg.TransformFlattener;
import org.jhotdraw8.svg.io.AbstractFXSvgWriter;
import org.jhotdraw8.svg.io.FXSvgFullWriter;
import org.jhotdraw8.svg.io.SvgSceneGraphWriter;
import org.jhotdraw8.svg.text.SvgPaintConverter;
import org.jhotdraw8.svg.text.SvgTransformConverter;
import org.jhotdraw8.text.Converter;
import org.jhotdraw8.xml.IndentingXMLStreamWriter;
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
import java.io.OutputStream;
import java.io.StringWriter;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;

import static org.jhotdraw8.draw.render.SimpleDrawingRenderer.toNode;

/**
 * Exports a JavaFX scene graph to SVG.
 *
 * @author Werner Randelshofer
 */
public class SvgExportOutputFormat extends AbstractExportOutputFormat
        implements ClipboardOutputFormat, OutputFormat {

    public final static DataFormat SVG_FORMAT;
    public final static String SVG_MIME_TYPE = "image/svg+xml";
    private final static String SKIP_KEY = "skip";
    private final static String XLINK_NS = "http://www.w3.org/1999/xlink";
    private final static String XLINK_Q = "xlink";
    private final static String XMLNS_NS = "http://www.w3.org/2000/xmlns/";

    static {
        DataFormat fmt = DataFormat.lookupMimeType(SVG_MIME_TYPE);
        if (fmt == null) {
            fmt = new DataFormat(SVG_MIME_TYPE);
        }
        SVG_FORMAT = fmt;
    }

    private final String SVG_NS = "http://www.w3.org/2000/svg";
    @Nullable
    private final String namespaceQualifier = null;
    private final XmlNumberConverter nb = new XmlNumberConverter();
    private final CssSizeConverter sc = new CssSizeConverter(false);
    private final Converter<ImmutableList<CssSize>> nbList = new CssListConverter<>(new CssSizeConverter(false));
    private final SvgPaintConverter paint = new SvgPaintConverter(true);
    private final Converter<CssSize> sznb = new CssSizeConverter(false);
    private final Converter<ImmutableList<Transform>> tx = new CssListConverter<>(new SvgTransformConverter(false));
    @NonNull
    private IdFactory idFactory = new SimpleIdFactory();

    private BiFunction<Object, Object, AbstractFXSvgWriter> exporterFactory = FXSvgFullWriter::new;

    public void setExporterFactory(BiFunction<Object, Object, AbstractFXSvgWriter> exporterFactory) {
        this.exporterFactory = exporterFactory;
    }

    @NonNull
    private AbstractFXSvgWriter createExporter() {
        AbstractFXSvgWriter exporter = exporterFactory.apply(ImageFigure.IMAGE_URI, SKIP_KEY);
        exporter.setUriResolver(getUriResolver());
        exporter.setExportInvisibleElements(getNonNull(SvgSceneGraphWriter.EXPORT_INVISIBLE_ELEMENTS_KEY));
        return exporter;
    }

    @NonNull
    @Override
    protected String getExtension() {
        return "svg";
    }

    @Override
    protected boolean isResolutionIndependent() {
        return true;
    }


    private void markNodesOutsideBoundsWithSkip(@NonNull Node node, Bounds sceneBounds) {
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


    public Document toDocument(URI documentHome, @NonNull Drawing external, @NonNull Collection<Figure> selection) throws IOException {
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        javafx.scene.Node drawingNode = toNode(external, selection, hints);
        final AbstractFXSvgWriter exporter = createExporter();
        exporter.setRelativizePaths(true);
        Document doc = exporter.toDocument(drawingNode,
                new CssDimension2D(
                        external.getNonNull(Drawing.WIDTH),
                        external.getNonNull(Drawing.HEIGHT)));
        writeDrawingElementAttributes(doc.getDocumentElement(), external);
        return doc;
    }

    @Override
    public void write(@NonNull Map<DataFormat, Object> clipboard, @NonNull Drawing drawing, @NonNull Collection<Figure> selection) throws IOException {
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

    public void write(@NonNull Path file, @NonNull Drawing drawing, WorkState workState) throws IOException {
        if (isExportDrawing()) {
            Map<Key<?>, Object> hints = new HashMap<>();
            RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
            try (OutputStream w = Files.newOutputStream(file)) {
                final AbstractFXSvgWriter exporter = createExporter();
                exporter.setRelativizePaths(true);
                javafx.scene.Node drawingNode = toNode(drawing, Collections.singletonList(drawing), hints);
                exporter.write(w, drawingNode,
                        new CssDimension2D(drawing.getNonNull(Drawing.WIDTH), drawing.getNonNull(Drawing.HEIGHT)));
            }
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

    @Override
    public void write(URI documentHome, OutputStream out, Drawing drawing, WorkState workState) throws IOException {
        IndentingXMLStreamWriter w = new IndentingXMLStreamWriter(out);
        write(documentHome, out, drawing, drawing.getChildren());
    }

    protected void write(URI documentHome, OutputStream out, Drawing drawing, Collection<Figure> selection) throws IOException {
        Document doc = toDocument(documentHome, drawing, selection);
        XmlUtil.write(out, doc);
    }

    private void writeDrawingElementAttributes(@NonNull Element docElement, @NonNull Drawing drawing) throws IOException {
        docElement.setAttribute("width", sc.toString(drawing.get(Drawing.WIDTH)));
        docElement.setAttribute("height", sc.toString(drawing.get(Drawing.HEIGHT)));
    }

    @Override
    protected void writePage(@NonNull Path file, @NonNull Page page, @NonNull Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException {
        CssSize pw = page.getNonNull(PageFigure.PAPER_WIDTH);
        CssSize ph = page.getNonNull(PageFigure.PAPER_HEIGHT);
        markNodesOutsideBoundsWithSkip(node, FXTransforms.transform(page.getLocalToWorld(), page.getPageBounds(internalPageNumber)));
        node.getTransforms().setAll(page.getWorldToLocal());
        final AbstractFXSvgWriter exporter = createExporter();
        final Document doc = exporter.toDocument(node, new CssDimension2D(pw, ph));
        writePageElementAttributes(doc.getDocumentElement(), page, internalPageNumber);
        node.getTransforms().clear();
        XmlUtil.write(file, doc);
    }

    private void writePageElementAttributes(@NonNull Element docElement, @NonNull Page page, int internalPageNumber) throws IOException {
        Bounds b = page.getLayoutBounds();
        Bounds pb = page.getPageBounds(internalPageNumber);
        docElement.setAttribute("width", sznb.toString(page.get(PageFigure.PAPER_WIDTH)));
        docElement.setAttribute("height", sznb.toString(page.get(PageFigure.PAPER_HEIGHT)));
        docElement.setAttribute("viewBox", nb.
                toString(pb.getMinX()) + " " + nb.toString(pb.getMinY())
                + " " + nb.toString(pb.getWidth()) + " " + nb.toString(pb.getHeight()));
    }

    @Override
    protected boolean writeSlice(@NonNull Path file, @NonNull Slice slice, @NonNull Node node, double dpi) throws IOException {
        markNodesOutsideBoundsWithSkip(node, slice.getLayoutBounds());
        Transform worldToLocal = slice.getWorldToLocal();
        Point2D sliceOrigin = slice.getSliceOrigin();
        worldToLocal = FXTransforms.concat(worldToLocal, new Translate(-sliceOrigin.getX(), -sliceOrigin.getY()));
        if (!worldToLocal.isIdentity()) {
            node.getTransforms().setAll(worldToLocal);
        }
        new TransformFlattener().flattenTranslates(node);
        final AbstractFXSvgWriter exporter = createExporter();
        Bounds bounds = slice.getBoundsInLocal();
        final Document doc = exporter.toDocument(node, new CssDimension2D(bounds.getWidth(), bounds.getHeight()));
        writeSliceElementAttributes(doc.getDocumentElement(), slice);
        node.getTransforms().clear();
        XmlUtil.write(file, doc);
        return true;
    }

    private void writeSliceElementAttributes(@NonNull Element docElement, @NonNull Slice slice) throws IOException {
        Bounds b = slice.getLayoutBounds();
        Point2D sliceOrigin = slice.getSliceOrigin();
        Transform tx = slice.getWorldToLocal();
        docElement.setAttribute("width", nb.toString(b.getWidth()));
        docElement.setAttribute("height", nb.toString(b.getHeight()));
        docElement.setAttribute("viewBox", nb.
                toString(b.getMinX() - sliceOrigin.getX()) + " " + nb.toString(b.getMinY() - sliceOrigin.getY())
                + " " + nb.toString(b.getWidth()) + " " + nb.toString(b.getHeight()));
    }
}
