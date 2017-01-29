/* @(#)AbstractExportOutputFormat.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.SimpleDrawingRenderer;
import static org.jhotdraw8.draw.SimpleDrawingRenderer.toNode;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Page;
import org.jhotdraw8.draw.figure.Slice;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;

/**
 * AbstractExportOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $$Id: AbstractExportOutputFormat.java 1258 2017-01-05 18:38:14Z
 * rawcoder $$
 */
public abstract class AbstractExportOutputFormat implements ExportOutputFormat, InternalExternalUriMixin {

    protected double drawingDpi = 72.0;
    private boolean exportDrawing = true;
    private boolean exportPages = false;
    private boolean exportSlices = false;
    private boolean exportSlices2x = false;
    private boolean exportSlices3x = false;
    private URI externalHome;
    private URI internalHome;
    protected double pagesDpi = 72.0;
    protected double slicesDpi = 72.0;

    protected abstract String getExtension();

    public URI getExternalHome() {
        return externalHome;
    }

    @Override
    public void setExternalHome(URI uri) {
        externalHome = uri;
    }

    public URI getInternalHome() {
        return internalHome;
    }

    @Override
    public void setInternalHome(URI uri) {
        internalHome = uri;
    }

    @Override
    public void setOptions(Map<? super Key<?>, Object> options) {
        if (options != null) {
            exportDrawing = EXPORT_DRAWING_KEY.get(options);
            exportPages = EXPORT_PAGES_KEY.get(options);
            exportSlices = EXPORT_SLICES_KEY.get(options);
            exportSlices2x = EXPORT_SLICES_RESOLUTION_2X_KEY.get(options);
            exportSlices3x = EXPORT_SLICES_RESOLUTION_3X_KEY.get(options);
            drawingDpi = EXPORT_DRAWING_DPI_KEY.get(options);
            pagesDpi = EXPORT_PAGES_DPI_KEY.get(options);
            slicesDpi = EXPORT_SLICES_DPI_KEY.get(options);
        }
    }

    public boolean isExportDrawing() {
        return exportDrawing;
    }

    public boolean isExportPages() {
        return exportPages;
    }

    public boolean isExportSlices() {
        return exportSlices;
    }

    public boolean isExportSlices2x() {
        return exportSlices2x;
    }

    public boolean isExportSlices3x() {
        return exportSlices3x;
    }

    protected abstract boolean isResolutionIndependent();

    /**
     * Writes a page.
     * @param file the output file or null
     * @param page the page figure
     * @param node the node of the drawing
     * @param pageCount the page count
     * @param pageNumber the page number
     * @param internalPageNumber the internal page number of the page figure
     * @throws IOException if writing fails
     */
    protected abstract void writePage(File file, Page page, Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException;

    protected void writePages(File dir, String basename, Drawing drawing) throws IOException {
        setExternalHome(dir == null ? null : dir.toURI());
        setInternalHome(drawing.get(Drawing.DOCUMENT_HOME));
        List<Page> pages = new ArrayList<>();
        for (Figure f : drawing.preorderIterable()) {
            if (f instanceof Page) {
                pages.add((Page) f);
            }
        }
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        RenderContext.DPI.put(hints, pagesDpi);

        writePages(dir, basename, drawing, pages, hints);
    }

    /**
     * Writes all pages of the drawing.
     * 
     * @param dir the output directory, null for print output
     * @param basename the basename of the pages, null for print output
     * @param drawing the drawing
     * @param pages the pages
     * @param hints the hints
     * @throws java.io.IOException in case of failure
     */
    protected void writePages(File dir, String basename, Drawing drawing, List<Page> pages, Map<Key<?>, Object> hints) throws IOException {
        setExternalHome(dir == null ? null : dir.toURI());
        setInternalHome(drawing.get(Drawing.DOCUMENT_HOME));
        IdFactory idFactory = new SimpleIdFactory();
        int numberOfPages = 0;
        for (Page page : pages) {
            if (page.getId() != null) {
                idFactory.putId(page, page.getId());
            }
            numberOfPages += page.getNumberOfSubPages();
        }
        int pageNumber = 0;
        SimpleDrawingRenderer renderer = new SimpleDrawingRenderer();
        Group rootNode = new Group();
        Group parentOfPageNode = new Group();
        for (Page page : pages) {
            for (int internalPageNumber = 0, n = page.getNumberOfSubPages(); internalPageNumber < n; internalPageNumber++) {
                File filename = (dir == null) ? null : new File(dir, basename + "_" + (pageNumber + 1) + "." + getExtension());

                hints.put(RenderContext.RENDER_PAGE, page);
                hints.put(RenderContext.RENDER_NUMBER_OF_PAGES, numberOfPages);
                hints.put(RenderContext.RENDER_PAGE_NUMBER, pageNumber);
                hints.put(RenderContext.RENDER_PAGE_INTERNAL_NUMBER, internalPageNumber);
                renderer.getProperties().putAll(hints);
                renderer.render(drawing);
                final Node pageNode = renderer.getNode(page);
                final Node drawingNode = renderer.getNode(drawing);

                Shape pageClip = page.getPageClip(internalPageNumber);
                Transform localToWorld = page.getWorldToLocal();
                Group parentOfDrawing = new Group();
                if (localToWorld == null) {
                    drawingNode.getTransforms().clear();
                } else {
                    drawingNode.getTransforms().setAll(localToWorld);
                    parentOfDrawing.getTransforms().setAll(page.getLocalToWorld());
                }
                parentOfDrawing.getChildren().add(drawingNode);
                parentOfDrawing.setClip(pageClip);

                Group oldParentOfPageNode = (Group) pageNode.getParent();
                if (oldParentOfPageNode != null) {
                    oldParentOfPageNode.getChildren().remove(pageNode);
                }
                parentOfPageNode.getChildren().setAll(pageNode);

                rootNode.getChildren().setAll(parentOfDrawing, parentOfPageNode);

                writePage(filename, page, rootNode, numberOfPages, pageNumber, internalPageNumber);

                pageNumber++;
            }
        }
    }

    protected abstract void writeSlice(File file, Slice slice, Node node, double dpi) throws IOException;

    protected void writeSlices(File dir, Drawing drawing) throws IOException {
        setExternalHome(dir == null ? null : dir.toURI());
        setInternalHome(drawing.get(Drawing.DOCUMENT_HOME));
        List<Slice> slices = new ArrayList<>();
        for (Figure f : drawing.preorderIterable()) {
            if (f instanceof Slice) {
                slices.add((Slice) f);
            }
        }
        writeSlices(dir, drawing, slices, "", slicesDpi);
        if (!isResolutionIndependent()) {
            if (exportSlices2x) {
                writeSlices(dir, drawing, slices, "@2x", 2 * slicesDpi);
            }
            if (exportSlices3x) {
                writeSlices(dir, drawing, slices, "@3x", 3 * slicesDpi);
            }
        }
    }

    /**
     *
     * @param dir
     * @param drawing
     * @param slices
     * @param hints
     * @throws java.io.IOException
     */
    private void writeSlices(File dir, Drawing drawing, List<Slice> slices, String suffix, double dpi) throws IOException {
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        RenderContext.DPI.put(hints, dpi);

        Node node = toNode(drawing, Collections.singleton(drawing), hints);

        IdFactory idFactory = new SimpleIdFactory();
        for (Figure slice : slices) {
            if (slice.getId() != null) {
                idFactory.putId(slice, slice.getId());
            }
        }
        for (Slice slice : slices) {
            File filename = new File(dir, idFactory.createId(slice, "Slice") + suffix + "." + getExtension());
            writeSlice(filename, slice, node, dpi);
        }
    }
}