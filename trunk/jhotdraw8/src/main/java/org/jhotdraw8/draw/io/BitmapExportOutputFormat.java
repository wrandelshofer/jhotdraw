/* @(#)BitmapExportOutputFormat.java
 * Copyright (c) 2016 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.io;

import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import javafx.application.Platform;
import javafx.embed.swing.SwingFXUtils;
import javafx.geometry.Bounds;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.SnapshotParameters;
import javafx.scene.image.WritableImage;
import javafx.scene.input.DataFormat;
import javafx.scene.paint.Color;
import javafx.scene.transform.Transform;
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
import org.jhotdraw8.collection.Key;
import org.jhotdraw8.draw.figure.Drawing;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import static org.jhotdraw8.draw.SimpleDrawingRenderer.toNode;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Page;
import org.jhotdraw8.draw.figure.Slice;
import org.jhotdraw8.draw.input.ClipboardOutputFormat;
import org.jhotdraw8.geom.Transforms;

/**
 * BitmapExportOutputFormat.
 *
 * @author Werner Randelshofer
 * @version $$Id: BitmapExportOutputFormat.java 1237 2016-12-20 08:57:59Z
 * rawcoder $$
 */
public class BitmapExportOutputFormat extends AbstractExportOutputFormat implements ClipboardOutputFormat, OutputFormat {

    public final static DataFormat PNG_FORMAT = new DataFormat("image/png");

    private final static double INCH_2_MM = 25.4;

    @Override
    public void write(Map<DataFormat, Object> out, Drawing drawing, Collection<Figure> selection) throws IOException {
        WritableImage image = renderImage(drawing, selection);
        out.put(DataFormat.IMAGE, image);
    }

    public double getDpi() {
        return dpi;
    }

    public void setDpi(double dpi) {
        this.dpi = dpi;
    }

    private void writeImage(OutputStream out, WritableImage writableImage) throws IOException {
        BufferedImage image = SwingFXUtils.fromFXImage(writableImage, null);

        for (Iterator<ImageWriter> iw = ImageIO.getImageWritersByFormatName("png"); iw.hasNext();) {
            ImageWriter writer = iw.next();
            ImageWriteParam writeParam = writer.getDefaultWriteParam();
            ImageTypeSpecifier typeSpecifier = ImageTypeSpecifier.createFromBufferedImageType(BufferedImage.TYPE_INT_RGB);
            IIOMetadata metadata = writer.getDefaultImageMetadata(typeSpecifier, writeParam);
            if (metadata.isReadOnly() || !metadata.isStandardMetadataFormatSupported()) {
                continue;
            }

            setDPI(metadata);

            ImageOutputStream output = new MemoryCacheImageOutputStream(out);

            writer.setOutput(output);
            writer.write(metadata, new IIOImage(image, null, metadata), writeParam);
            output.flush();
            break;
        }
    }

    @Override
    public void write(OutputStream out, Drawing drawing) throws IOException {
        WritableImage writableImage = renderImage(drawing, Collections.singleton(drawing));
        //ImageIO.write(SwingFXUtils.fromFXImage(image, null), "png", out);
        writeImage(out, writableImage);

    }

    private void setDPI(IIOMetadata metadata) throws IIOInvalidTreeException {
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

    private WritableImage renderImage(Drawing drawing, Collection<Figure> selection) throws IOException {
        Map<Key<?>, Object> hints = new HashMap<>();
        RenderContext.RENDERING_INTENT.put(hints, RenderingIntent.EXPORT);
        RenderContext.DPI.put(hints, dpi);
        Node node = toNode(drawing, selection, hints);
        Bounds bounds = Figure.visualBounds(selection);

        if (!Platform.isFxApplicationThread()) {
            CompletableFuture<WritableImage> future = CompletableFuture.supplyAsync(() -> doRenderImage(drawing, node, bounds), Platform::runLater);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new IOException(ex);
            }
        } else {
            return doRenderImage(drawing, node, bounds);
        }
    }

    protected void writeSlice(File file, Slice slice, Node node) throws IOException {
        WritableImage image = renderSlice(slice,slice.getBoundsInLocal(), node);
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            writeImage(out, image);
        }
    }

    @Override
    protected void writePage(File file, Page page, Node node, int pageCount, int pageNumber, int internalPageNumber) throws IOException {
        WritableImage image = renderSlice(page,page.getPageBounds(internalPageNumber), node);
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(file))) {
            writeImage(out, image);
        }
    }

    private WritableImage renderSlice(Figure slice, Bounds bounds, Node node) throws IOException {
        if (!Platform.isFxApplicationThread()) {
            CompletableFuture<WritableImage> future = CompletableFuture.supplyAsync(() -> doRenderImage(slice, node, bounds), Platform::runLater);
            try {
                return future.get();
            } catch (InterruptedException | ExecutionException ex) {
                throw new IOException(ex);
            }
        } else {
            return doRenderImage(slice, node, bounds);
        }
    }

    private WritableImage doRenderImage(Figure slice, Node node, Bounds bounds) {
        SnapshotParameters parameters = new SnapshotParameters();
        double scale = dpi / RenderContext.DPI.getDefaultValue();
        parameters.setTransform(Transforms.concat(Transform.scale(scale, scale),slice.getWorldToLocal()));
        parameters.setFill(Color.TRANSPARENT);
        double x = bounds.getMinX() * scale;
        double y = bounds.getMinY() * scale;
        double width = bounds.getWidth() * scale;
        double height = bounds.getHeight() * scale;

        parameters.setViewport(new Rectangle2D(x, y, width, height));

        WritableImage image = node.snapshot(parameters, null);

        return image;
    }

    @Override
    protected String getExtension() {
        return "png";
    }
}
