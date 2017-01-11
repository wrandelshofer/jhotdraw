/* @(#)PrinterExportFormat.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.io.File;
import java.io.IOException;
import static java.lang.Math.abs;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.print.PageLayout;
import javafx.print.PageOrientation;
import javafx.print.Paper;
import javafx.print.PrinterJob;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Page;
import org.jhotdraw8.draw.figure.PageFigure;
import org.jhotdraw8.draw.figure.Slice;
import org.jhotdraw8.io.DefaultUnitConverter;
import org.jhotdraw8.io.UnitConverter;
import org.jhotdraw8.text.CssSize;
import org.jhotdraw8.text.CssSize2D;

/**
 * PrinterExportFormat.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class PrinterExportFormat extends AbstractExportOutputFormat {

    private final static double INCH_2_MM = 25.4;

    @Override
    protected String getExtension() {
        return "png";
    }

    @Override
    protected boolean isResolutionIndependent() {
        return false;
    }

    public  Paper findPaper(CssSize2D paperSize) {
        UnitConverter uc = new DefaultUnitConverter(72.0);
        double w = uc.convert(paperSize.getX(), UnitConverter.POINTS);
        double h = uc.convert(paperSize.getY(), UnitConverter.POINTS);
        for (Paper paper : job.getPrinter().getPrinterAttributes().getSupportedPapers()) {

                    if (abs(paper.getWidth() - w) < 1 && abs(paper.getHeight() - h) < 1
                            || abs(paper.getWidth() - h) < 1 && abs(paper.getHeight() - w) < 1) {
                        return paper;
                    }
        }
        return Paper.A4;
    }

    private void printSlice(CssSize2D pageSize, Figure slice, Bounds viewportBounds, Node node, double dpi) throws IOException {
            Paper paper = findPaper(pageSize);
            Point2D psize=pageSize.getConvertedValue();
        PageLayout pl = job.getPrinter().createPageLayout(paper, psize.getX()<=psize.getY()?PageOrientation.PORTRAIT:PageOrientation.LANDSCAPE, 0, 0, 0, 0);
        job.getJobSettings().setPageLayout(pl);
        paper = pl.getPaper();
        if (paper == null) {
            paper = Paper.A4;
        }
        double pw, ph;
        if (pl.getPageOrientation() == PageOrientation.LANDSCAPE) {
            pw = paper.getHeight();
            ph = paper.getWidth();
        } else {
            pw = paper.getWidth();
            ph = paper.getHeight();
        }
        double paperAspect = paper.getWidth() / paper.getHeight();
        double pageAspect = viewportBounds.getWidth() / viewportBounds.getHeight();

        double scaleFactor;
        if (paperAspect < pageAspect) {
            scaleFactor = ph / viewportBounds.getHeight();
        } else {
            scaleFactor = pw / viewportBounds.getWidth();
        }

        Group oldParent = (node.getParent() instanceof Group) ? (Group) node.getParent() : null;
        int index = -1;
        if (oldParent != null) {
            index = oldParent.getChildren().indexOf(node);
            oldParent.getChildren().remove(index);
        }
        Group printParent = new Group();

        printParent.getChildren().add(node);

        printParent.getTransforms().addAll(
                new Translate(-pl.getLeftMargin(), -pl.getTopMargin()),
                new Scale(scaleFactor,scaleFactor),
                new Translate(-viewportBounds.getMinX(), -viewportBounds.getMinY())
        // slice.getWorldToLocal()
        );
        if (slice.getWorldToLocal() != null) {
            printParent.getTransforms().add(slice.getWorldToLocal());
        }

        Group printNode = new Group();
        printNode.getChildren().addAll(printParent);
        job.printPage(printNode);
        printParent.getChildren().clear();
        if (oldParent != null) {
            oldParent.getChildren().add(index, node);
        }

    }

    private void setDPI(IIOMetadata metadata, double dpi) throws IIOInvalidTreeException {
        double dotsPerMilli = dpi / INCH_2_MM;

        IIOMetadataNode horiz = new IIOMetadataNode("HorizontalPixelSize");
        horiz.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode vert = new IIOMetadataNode("VerticalPixelSize");
        vert.setAttribute("value", Double.toString(dotsPerMilli));

        IIOMetadataNode dim = new IIOMetadataNode("Dimension");
        dim.appendChild(horiz);
        dim.appendChild(vert);

        IIOMetadataNode root = new IIOMetadataNode("javax_imageio_1.0");
        root.appendChild(dim);

        metadata.mergeTree("javax_imageio_1.0", root);
    }

    @Override
    protected void writePage(File file, Page page, Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException {
        CssSize pw = page.get(PageFigure.PAPER_WIDTH);
        double paperWidth = pw.getConvertedValue();
        final Bounds pageBounds = page.getPageBounds(internalPageNumber);
        double factor = paperWidth / pageBounds.getWidth();

        printSlice(page.get(PageFigure.PAPER_SIZE), page, pageBounds, node, pagesDpi * factor);
    }

    protected void writeSlice(File file, Slice slice, Node node, double dpi) throws IOException {
        printSlice(null, slice, slice.getBoundsInLocal(), node, dpi);
    }
    private PrinterJob job;

    public void print(PrinterJob job, Drawing drawing) throws IOException {
        this.job = job;
        try {
            writePages(null, null, drawing);
            writeSlices(null, drawing);
        } finally {
            job = null;
        }
    }
}