/* @(#)BitmapExportOutputFormat.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.io;

import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.concurrent.WorkState;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Page;
import org.jhotdraw8.draw.figure.PageFigure;
import org.jhotdraw8.draw.figure.Slice;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.geom.Transforms;

import javax.imageio.IIOImage;
import javax.imageio.ImageIO;
import javax.imageio.ImageTypeSpecifier;
import javax.imageio.ImageWriteParam;
import javax.imageio.ImageWriter;
import javax.imageio.metadata.IIOInvalidTreeException;
import javax.imageio.metadata.IIOMetadata;
import javax.imageio.metadata.IIOMetadataNode;
import javax.imageio.stream.ImageOutputStream;
import javax.imageio.stream.MemoryCacheImageOutputStream;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

import static org.jhotdraw8.draw.SimpleDrawingRenderer.toNode;

/**
 * BitmapExportOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BitmapExportOutputFormat extends AbstractExportOutputFormat implements ClipboardOutputFormat, OutputFormat {

    private final static double INCH_2_MM = 25.4;
    public final static DataFormat JPEG_FORMAT = new DataFormat("image/jpeg");
    public final static DataFormat PNG_FORMAT = new DataFormat("image/png");

    private WritableImage doRenderImage(Figure slice, Node node, Bounds bounds, double dpi) {
        SnapshotParameters parameters = new SnapshotParameters();
        double scale = dpi / RenderContext.DPI.getDefaultValue();
        parameters.setTransform(Transforms.concat(Transform.scale(scale, scale), slice.getWorldToLocal()));
        Drawing drawing = (slice instanceof Drawing) ? (Drawing) slice : slice.getDrawing();
        parameters.setFill(drawing.get(Drawing.BACKGROUND).getColor());
        double x = bounds.getMinX() * scale;
        double y = bounds.getMinY() * scale;
        double width = bounds.getWidth() * scale;
        double height = bounds.getHeight() * scale;

        parameters.setViewport(new Rectangle2D(x, y, width, height));

        WritableImage image = node.snapshot(parameters, null);

        return image;
    }

    @Nonnull
    @Override
    protected String getExtension() {
        return "png";
    }

    @Override
    protected boolean isResolutionIndependent() {
        return false;
    }

    private WritableImage renderImage(@Nonnull Drawing drawing, @Nonnull Collection<Figure> selection, double dpi) throws IOException {
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        RenderContext.DPI.put(hints, dpi);
        Node node = toNode(drawing, selection, hints);
        Bounds bounds = Figure.visualBounds(selection);

        if (!Platform.isFxApplicationThread()) {
            CompletableFuture<WritableImage> future = CompletableFuture.supplyAsync(() -> doRenderImage(drawing, node, bounds, drawingDpi), Platform::runLater);
            try {
                return future.get();
            } catch (@Nonnull InterruptedException | ExecutionException ex) {
                throw new IOException(ex);
            }
        } else {
            return doRenderImage(drawing, node, bounds, drawingDpi);
        }
    }

    private WritableImage renderSlice(@Nonnull Figure slice, @Nonnull Bounds bounds, @Nonnull Node node, double dpi) throws IOException {
        if (!Platform.isFxApplicationThread()) {
            CompletableFuture<WritableImage> future = CompletableFuture.supplyAsync(() -> doRenderImage(slice, node, bounds, dpi), Platform::runLater);
            try {
                return future.get();
            } catch (@Nonnull InterruptedException | ExecutionException ex) {
                throw new IOException(ex);
            }
        } else {
            return doRenderImage(slice, node, bounds, dpi);
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
    public void write(@Nonnull Map<DataFormat, Object> out, @Nonnull Drawing drawing, @Nonnull Collection<Figure> selection) throws IOException {
        WritableImage image = renderImage(drawing, selection, drawingDpi);
        out.put(DataFormat.IMAGE, image);
    }

    @Override
    public void write(URI documentHome, @Nonnull OutputStream out, @Nonnull Drawing drawing, WorkState workState) throws IOException {
        WritableImage writableImage = renderImage(drawing, Collections.singleton(drawing), drawingDpi);
        //ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", out);
        writeImage(out, writableImage, drawingDpi);

    }

    public void write(@Nonnull Path file, Drawing drawing, WorkState workState) throws IOException {
        if (isExportDrawing()) {
            OutputFormat.super.write(file, drawing, workState);
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

    private void writeImage(@Nonnull OutputStream out, @Nonnull WritableImage writableImage, double dpi) throws IOException {
        BufferedImage image = SwingFXUtils.fromFXImage(writableImage, null);

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext(); ) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata, dpi);

            try (ImageOutputStream output = new MemoryCacheImageOutputStream(out)) {
                writer.setOutput(output);
                writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
            }
            break;
        }
    }

    @Override
    protected void writePage(@Nonnull Path file, @Nonnull Page page, @Nonnull Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException {
        CssSize pw = page.get(PageFigure.PAPER_WIDTH);
        double paperWidth = pw.getConvertedValue();
        final Bounds pageBounds = page.getPageBounds(internalPageNumber);
        double factor = paperWidth / pageBounds.getWidth();
        WritableImage image = renderSlice(page, pageBounds, node, pagesDpi * factor);
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file))) {
            writeImage(out, image, pagesDpi);
        }
    }

    protected boolean writeSlice(@Nonnull Path file, @Nonnull Slice slice, @Nonnull Node node, double dpi) throws IOException {
        WritableImage image = renderSlice(slice, slice.getBoundsInLocal(), node, dpi);
        try (OutputStream out = new BufferedOutputStream(Files.newOutputStream(file))) {
            writeImage(out, image, dpi);
        }
        return false;
    }

}
