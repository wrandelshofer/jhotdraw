/*
 * @(#)ImageFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.io.SimpleUriResolver;

import java.net.URI;

/**
 * ImageFigure presents a bitmap image on a drawing.
 *
 * @author Werner Randelshofer
 */
public class ImageFigure extends AbstractLeafFigure
        implements ResizableFigure, TransformableFigure, StyleableFigure, LockableFigure, CompositableFigure, ConnectableFigure,
        HideableFigure, ImageableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public static final String TYPE_SELECTOR = "Image";


    public static final @NonNull CssSizeStyleableKey X = RectangleFigure.X;
    public static final @NonNull CssSizeStyleableKey Y = RectangleFigure.Y;
    public static final @NonNull CssSizeStyleableKey WIDTH = RectangleFigure.WIDTH;
    public static final @NonNull CssSizeStyleableKey HEIGHT = RectangleFigure.HEIGHT;
    public static final @NonNull CssRectangle2DStyleableMapAccessor BOUNDS = RectangleFigure.BOUNDS;
    private @Nullable Image cachedImage;
    private @Nullable URI cachedImageUri;

    public ImageFigure() {
        this(0, 0, 1, 1);
    }

    public ImageFigure(double x, double y, double width, double height) {
        set(BOUNDS, new CssRectangle2D(x, y, width, height));
    }

    public ImageFigure(CssRectangle2D rect) {
        set(BOUNDS, rect);
    }

    @Override
    public @NonNull CssRectangle2D getCssLayoutBounds() {
        return getNonNull(BOUNDS);
    }

    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        Rectangle2D r = getNonNull(BOUNDS).getConvertedValue();
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        set(BOUNDS, new CssRectangle2D(
                width.getValue() < 0 ? x.add(width) : x,
                height.getValue() < 0 ? y.add(height) : y,
                width.abs(), height.abs()));
    }

    @Override
    public @NonNull Node createNode(@NonNull RenderContext drawingView) {
        ImageView n = new ImageView();
        n.setPreserveRatio(false);
        n.setManaged(false);
        return n;
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        ImageView imageView = (ImageView) node;
        validateImage();
        imageView.setImage(cachedImage);
        applyTransformableFigureProperties(ctx, imageView);
        applyCompositableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyHideableFigureProperties(ctx, node);
        Rectangle2D r = getNonNull(BOUNDS).getConvertedValue();
        imageView.setX(r.getMinX());
        imageView.setY(r.getMinY());
        imageView.setFitWidth(r.getWidth());
        imageView.setFitHeight(r.getHeight());
        imageView.applyCss();
        imageView.getProperties().put(IMAGE_URI, get(IMAGE_URI));
    }

    @Override
    public @NonNull Connector findConnector(@NonNull Point2D p, Figure prototype, double tolerance) {
        return new RectangleConnector(new BoundsLocator(getLayoutBounds(), p));
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public double getPreferredAspectRatio() {
        return (cachedImage == null || cachedImage.getWidth() == 0 || cachedImage.getHeight() == 0)//
                ? super.getPreferredAspectRatio()//
                : cachedImage.getHeight() / cachedImage.getWidth();
    }

    private void validateImage() {
        URI uri = getStyled(IMAGE_URI);
        if (uri == null) {
            cachedImageUri = null;
            cachedImage = null;
            return;
        }
        Drawing drawing = getDrawing();
        URI documentHome = drawing == null ? null : drawing.get(Drawing.DOCUMENT_HOME);
        URI absoluteUri = new SimpleUriResolver().absolutize(documentHome, uri);
        if (cachedImageUri == null || !cachedImageUri.equals(absoluteUri)) {
            cachedImageUri = absoluteUri;
            try {
                cachedImage = new Image(cachedImageUri.toString(), true);
            } catch (IllegalArgumentException e) {
                System.err.println("could not load image from uri: " + absoluteUri);
                e.printStackTrace();
            }
        }
    }

    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getLayoutBounds();
    }
}
