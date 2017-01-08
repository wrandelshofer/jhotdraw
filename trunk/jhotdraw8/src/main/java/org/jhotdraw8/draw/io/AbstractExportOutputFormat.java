/* @(#)AbstractExportOutputFormat.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
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
public abstract class AbstractExportOutputFormat implements OutputFormat, ExportOutputFormat {

    protected double drawingDpi = 72.0;
    private boolean exportDrawing = true;
    private boolean exportPages = false;
    private boolean exportSlices = false;
    private boolean exportSlices2x = false;
    private boolean exportSlices3x = false;
    protected double pagesDpi = 72.0;
    protected double slicesDpi = 72.0;

    protected abstract String getExtension();

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

    @Override
    public void write(File file, Drawing drawing) throws IOException {
        if (exportDrawing) {
            OutputFormat.super.write(file, drawing); //To change body of generated methods, choose Tools | Templates.
        }
        if (exportSlices) {
            writeSlices(file.getParentFile(), drawing);
        }
        if (exportPages) {
            String basename = file.getName();
            int p = basename.lastIndexOf('.');
            if (p != -1) {
                basename = basename.substring(0, p);
            }
            writePages(file.getParentFile(), basename, drawing);
        }
    }

    /**
     *
     * @param file
     * @param page
     * @param node
     * @param pageCount
     * @param pageNumber
     * @param internalPageNumber
     * @throws IOException
     */
    protected abstract void writePage(File file, Page page, Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException;

    private void writePages(File dir, String basename, Drawing drawing) throws IOException {
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
     *
     * @param dir
     * @param drawing
     * @param pages
     * @param hints
     * @throws java.io.IOException
     */
    protected void writePages(File dir, String basename, Drawing drawing, List<Page> pages, Map<Key<?>, Object> hints) throws IOException {
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
                File filename = new File(dir, basename + "_" + (pageNumber + 1) + "." + getExtension());

                hints.put(RenderContext.RENDER_PAGE, page);
                hints.put(RenderContext.RENDER_NUMBER_OF_PAGES, numberOfPages);
                hints.put(RenderContext.RENDER_PAGE_NUMBER, pageNumber);
                hints.put(RenderContext.RENDER_PAGE_INTERNAL_NUMBER, internalPageNumber);
                renderer.getProperties().putAll(hints);
                renderer.render(drawing);
                final Node pageNode = renderer.getNode(page);
                final Node drawingNode = renderer.getNode(drawing);

                Shape pageClip = page.getPageClip(internalPageNumber);
                Transform localToWorld = page.getLocalToWorld();
                if (localToWorld == null) {
                    pageClip.getTransforms().clear();
                } else {
                    pageClip.getTransforms().setAll(localToWorld);
                }
                drawingNode.setClip(pageClip);

                Group oldParentOfPageNode = (Group) pageNode.getParent();
                if (oldParentOfPageNode != null) {
                    oldParentOfPageNode.getChildren().remove(pageNode);
                }
                parentOfPageNode.getChildren().setAll(pageNode);

                rootNode.getChildren().setAll(drawingNode, parentOfPageNode);

                writePage(filename, page, rootNode, numberOfPages, pageNumber, internalPageNumber);

                pageNumber++;
            }
        }
    }

    protected abstract void writeSlice(File file, Slice slice, Node node, double dpi) throws IOException;

    private void writeSlices(File dir, Drawing drawing) throws IOException {
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

    protected abstract boolean isResolutionIndependent();
}
