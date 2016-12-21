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
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import javafx.scene.Node;
import org.jhotdraw8.collection.Key;
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
 * @version $$Id$$
 */
public abstract class AbstractExportOutputFormat implements OutputFormat {

    private boolean exportSlices = true;
    private boolean exportPages = true;
    protected double dpi = 72.0;

    @Override
    public void write(File file, Drawing drawing) throws IOException {
        OutputFormat.super.write(file, drawing); //To change body of generated methods, choose Tools | Templates.
        if (exportSlices) {
            writeSlices(file.getParentFile(), drawing);
        }
        if (exportPages) {
            writePages(file.getParentFile(), drawing);
        }
    }

    private void writeSlices(File dir, Drawing drawing) throws IOException {
        List<Slice> slices = new ArrayList<>();
        for (Figure f : drawing.preorderIterable()) {
            if (f instanceof Slice) {
                slices.add((Slice) f);
            }
        }
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        RenderContext.DPI.put(hints, dpi);

        writeSlices(dir, drawing, slices, hints);
    }

    private void writePages(File dir, Drawing drawing) throws IOException {
        List<Page> pages = new ArrayList<>();
        for (Figure f : drawing.preorderIterable()) {
            if (f instanceof Page) {
                pages.add((Page) f);
            }
        }
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        RenderContext.DPI.put(hints, dpi);

        writePages(dir, drawing, pages, hints);
    }

    protected abstract String getExtension();

    /**
     *
     * @param dir
     * @param drawing
     * @param slices
     * @param hints
     * @throws java.io.IOException
     */
    protected void writeSlices(File dir, Drawing drawing, List<Slice> slices, Map<Key<?>, Object> hints) throws IOException {
        Node node = toNode(drawing, Collections.singleton(drawing), hints);

        IdFactory idFactory = new SimpleIdFactory();
        for (Figure slice : slices) {
            if (slice.getId() != null) {
                idFactory.putId(slice, slice.getId());
            }
        }
        for (Slice slice : slices) {
            File filename = new File(dir, idFactory.createId(slice, "Slice") + "." + getExtension());
            writeSlice(filename, slice, node);

        }
    }

    /**
     *
     * @param dir
     * @param drawing
     * @param pages
     * @param hints
     * @throws java.io.IOException
     */
    protected void writePages(File dir, Drawing drawing, List<Page> pages, Map<Key<?>, Object> hints) throws IOException {
        Node node = toNode(drawing, Collections.singleton(drawing), hints);
        IdFactory idFactory = new SimpleIdFactory();
        int pageCount = 0;
        for (Page page : pages) {
            if (page.getId() != null) {
                idFactory.putId(page, page.getId());
            }
            pageCount += page.getNumberOfInternalPages();
        }
        int pageNumber = 0;

        for (Page page : pages) {
            for (int internalPageNumber = 0, n = page.getNumberOfInternalPages(); internalPageNumber < n; internalPageNumber++) {
                File filename = new File(dir, idFactory.createId(page, "Page") +"_"+(pageNumber+1)+ "." + getExtension());
                writePage(filename, page, node, pageCount, pageNumber, internalPageNumber);

                pageNumber++;
            }
        }
    }

    protected abstract void writeSlice(File file, Slice slice, Node node) throws IOException;

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
}
