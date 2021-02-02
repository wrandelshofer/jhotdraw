/*
 * @(#)AbstractExportOutputFormat.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Shape;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.beans.AbstractPropertyBean;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Page;
import org.jhotdraw8.draw.figure.Slice;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.draw.render.SimpleDrawingRenderer;
import org.jhotdraw8.io.IdFactory;
import org.jhotdraw8.io.SimpleIdFactory;

import java.io.IOException;
import java.nio.file.Path;
import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.jhotdraw8.draw.render.SimpleDrawingRenderer.toNode;

/**
 * AbstractExportOutputFormat.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractExportOutputFormat extends AbstractPropertyBean implements ExportOutputFormat {

    @NonNull
    protected abstract String getExtension();


    public boolean isExportDrawing() {
        return getNonNull(EXPORT_DRAWING_KEY);
    }

    public boolean isExportPages() {
        return getNonNull(EXPORT_PAGES_KEY);
    }

    public boolean isExportSlices() {
        return getNonNull(EXPORT_SLICES_KEY);
    }

    public boolean isExportSlices2x() {
        return getNonNull(EXPORT_SLICES_RESOLUTION_2X_KEY);
    }

    public boolean isExportSlices3x() {
        return getNonNull(EXPORT_SLICES_RESOLUTION_3X_KEY);
    }

    protected abstract boolean isResolutionIndependent();

    /**
     * Writes a page.
     *
     * @param file               the output file or null
     * @param page               the page figure
     * @param node               the node of the drawing
     * @param pageCount          the page count
     * @param pageNumber         the page number
     * @param internalPageNumber the internal page number of the page figure
     * @throws IOException if writing fails
     */
    protected abstract void writePage(Path file, Page page, Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException;

    protected void writePages(@Nullable Path dir, String basename, @NonNull Drawing drawing) throws IOException {
        List<Page> pages = new ArrayList<>();
        for (Figure f : drawing.preorderIterable()) {
            if (f instanceof Page) {
                pages.add((Page) f);
            }
        }
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        RenderContext.DPI.put(hints, getNonNull(EXPORT_PAGES_DPI_KEY));

        writePages(dir, basename, drawing, pages, hints);
    }

    /**
     * Writes all pages of the drawing.
     *
     * @param dir      the output directory, null for print output
     * @param basename the basename of the pages, null for print output
     * @param drawing  the drawing
     * @param pages    the pages
     * @param hints    the hints
     * @throws java.io.IOException in case of failure
     */
    protected void writePages(@Nullable Path dir, String basename, @NonNull Drawing drawing, @NonNull List<Page> pages, @NonNull Map<Key<?>, Object> hints) throws IOException {
        IdFactory idFactory = new SimpleIdFactory();
        int numberOfPages = 0;
        for (Page page : pages) {
            if (page.getId() != null) {
                idFactory.putIdAndObject(page.getId(), page);
            }
            numberOfPages += page.getNumberOfSubPages();
        }
        int pageNumber = 0;
        SimpleDrawingRenderer renderer = new SimpleDrawingRenderer();
        Group rootNode = new Group();
        Group parentOfPageNode = new Group();
        for (Page page : pages) {
            for (int internalPageNumber = 0, n = page.getNumberOfSubPages(); internalPageNumber < n; internalPageNumber++) {
                Path filename = (dir == null) ? null : dir.resolve(basename + "_" + (pageNumber + 1) + "." + getExtension());

                hints.put(RenderContext.RENDER_PAGE, page);
                hints.put(RenderContext.RENDER_NUMBER_OF_PAGES, numberOfPages);
                hints.put(RenderContext.RENDER_PAGE_NUMBER, pageNumber);
                hints.put(RenderContext.RENDER_PAGE_INTERNAL_NUMBER, internalPageNumber);
                hints.put(RenderContext.RENDER_TIMESTAMP, Instant.now());
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
                    Transform pageLocalToWorld = page.getLocalToWorld();
                    if (pageLocalToWorld == null) {
                        parentOfDrawing.getTransforms().clear();
                    } else {
                        parentOfDrawing.getTransforms().setAll(pageLocalToWorld);
                    }
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

    /**
     * Writes the node to the specified file as a Slice. May destroy the state of the node in the process!
     *
     * @param file  a file
     * @param slice the Slice
     * @param node  a node
     * @param dpi   dots per inch
     * @return returns true if the state of the node was destroyed
     * @throws IOException in case of failure
     */
    protected abstract boolean writeSlice(Path file, Slice slice, Node node, double dpi) throws IOException;

    protected void writeSlices(@Nullable Path dir, @NonNull Drawing drawing) throws IOException {
        List<Slice> slices = new ArrayList<>();
        for (Figure f : drawing.preorderIterable()) {
            if (f instanceof Slice) {
                slices.add((Slice) f);
            }
        }
        final Double slicesDpi = getNonNull(EXPORT_SLICES_DPI_KEY);
        writeSlices(dir, drawing, slices, "", slicesDpi);
        if (!isResolutionIndependent()) {
            if (getNonNull(EXPORT_SLICES_RESOLUTION_2X_KEY)) {
                writeSlices(dir, drawing, slices, "@2x", 2 * slicesDpi);
            }
            if (getNonNull(EXPORT_SLICES_RESOLUTION_3X_KEY)) {
                writeSlices(dir, drawing, slices, "@3x", 3 * slicesDpi);
            }
        }
    }

    /**
     * @param dir
     * @param drawing
     * @param slices
     * @throws java.io.IOException
     */
    private void writeSlices(@NonNull Path dir, @NonNull Drawing drawing, @NonNull List<Slice> slices, String suffix, double dpi) throws IOException {
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        RenderContext.DPI.put(hints, dpi);


        IdFactory idFactory = new SimpleIdFactory();
        for (Figure slice : slices) {
            if (slice.getId() != null) {
                idFactory.putIdAndObject(slice.getId(), slice);
            }
        }
        Node node = null;
        for (Slice slice : slices) {
            Path filename = dir.resolve(idFactory.createId(slice, "Slice") + suffix + "." + getExtension());
            if (node == null) {
                node = toNode(drawing, Collections.singleton(drawing), hints);
            }
            boolean destroyedNode = writeSlice(filename, slice, node, dpi);
            if (destroyedNode) {
                node = null;
            }
        }
    }
}
