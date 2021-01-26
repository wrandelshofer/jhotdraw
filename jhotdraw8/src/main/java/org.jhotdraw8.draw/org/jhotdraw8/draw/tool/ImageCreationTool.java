/*
 * @(#)ImageCreationTool.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.tool;

import javafx.geometry.Dimension2D;
import javafx.stage.FileChooser;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.DrawingEditor;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.ImageFigure;
import org.jhotdraw8.draw.figure.Layer;
import org.jhotdraw8.gui.FileURIChooser;
import org.jhotdraw8.gui.URIChooser;
import org.jhotdraw8.util.Resources;

import javax.imageio.ImageIO;
import javax.imageio.ImageReader;
import javax.imageio.stream.ImageInputStream;
import java.io.IOException;
import java.net.URI;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.AbstractMap;
import java.util.Iterator;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import java.util.function.Supplier;

/**
 * ImageCreationTool.
 *
 * @author Werner Randelshofer
 * @design.pattern CreationTool AbstractFactory, Client.
 */
public class ImageCreationTool extends CreationTool {

    private @NonNull MapAccessor<URI> uriKey = ImageFigure.IMAGE_URI;
    private URIChooser uriChooser;
    private @Nullable URI uri;
    private Future<Dimension2D> dimensionFuture;

    public ImageCreationTool(String name, Resources rsrc, Supplier<Figure> supplier, Supplier<Layer> layerFactory) {
        super(name, rsrc, supplier, layerFactory);
    }

    @Override
    public void activate(@NonNull DrawingEditor editor) {
        super.activate(editor);
        Map.Entry<URI, Future<Dimension2D>> entry = chooseFile();
        if (entry != null) {
            this.uri = entry.getKey();
            this.dimensionFuture = entry.getValue();
        } else {
            this.uri = null;
            this.dimensionFuture = null;
        }
    }

    @Override
    protected Figure createFigure() {
        Figure f = super.createFigure();
        f.set(uriKey, uri);
        return f;
    }

    @Override
    public double getDefaultHeight() {
        Dimension2D dim = getDefaultDimensionFromImage();
        return dim != null ? dim.getHeight() : super.getDefaultHeight();
    }

    private @Nullable Dimension2D getDefaultDimensionFromImage() {
        if (dimensionFuture != null) {
            try {
                return dimensionFuture.get(1, TimeUnit.SECONDS);
            } catch (InterruptedException | ExecutionException | TimeoutException e) {
                //return null;
            }
        }
        return null;
    }

    @Override
    public double getDefaultWidth() {
        Dimension2D dim = getDefaultDimensionFromImage();
        return dim != null ? dim.getWidth() : super.getDefaultWidth();
    }

    protected @NonNull URIChooser createURIChooser() {
        FileURIChooser c = new FileURIChooser();
        c.setMode(FileURIChooser.Mode.OPEN);
        c.getFileChooser().getExtensionFilters().add(new FileChooser.ExtensionFilter("Image", "*.bmp", "*.gif", "*.jpg", "*.png"));
        return c;
    }

    protected @Nullable Map.Entry<URI, Future<Dimension2D>> chooseFile() {
        if (uriChooser == null) {
            uriChooser = createURIChooser();
        }
        URI uri = uriChooser.showDialog(node);


        return new AbstractMap.SimpleImmutableEntry<>(uri, readImageSize(uri));
    }

    private Future<Dimension2D> readImageSize(URI uri) {
        CompletableFuture<Dimension2D> future = new CompletableFuture<>();
        if (uri == null) {
            future.complete(null);
        } else {
            new Thread(() -> {
                Path path = Paths.get(uri);
                try (ImageInputStream in = ImageIO.createImageInputStream(path.toFile())) {
                    if (in != null) {
                        for (ImageReader reader : (Iterable<ImageReader>) () -> ImageIO.getImageReaders(in)) {
                            try {
                                reader.setInput(in);
                                future.complete(
                                        new Dimension2D(
                                                reader.getWidth(0),
                                                reader.getHeight(0)));
                                return;
                            } finally {
                                reader.dispose();
                            }
                        }
                    }
                    future.complete(null);
                } catch (IOException e) {
                    future.completeExceptionally(e);
                }
            }).start();
        }
        return future;
    }
}
