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
import org.jhotdraw8.draw.key.UriStyleableKey;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;

import java.net.URI;

/**
 * ImageFigure.
 *
 * @author Werner Randelshofer
 */
public class ImageFigure extends AbstractLeafFigure
        implements ResizableFigure, TransformableFigure, StyleableFigure, LockableFigure, CompositableFigure, ConnectableFigure,
        HideableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Image";

    /**
     * The URI of the image.
     * <p>
     * This property is also set on the ImageView node, so that
     * {@link org.jhotdraw8.draw.io.SvgExportOutputFormat} can pick it up.
     */
    @NonNull
    public final static UriStyleableKey IMAGE_URI = new UriStyleableKey("src", null);
    @NonNull
    public final static CssSizeStyleableKey X = RectangleFigure.X;
    @NonNull
    public final static CssSizeStyleableKey Y = RectangleFigure.Y;
    @NonNull
    public final static CssSizeStyleableKey WIDTH = RectangleFigure.WIDTH;
    @NonNull
    public final static CssSizeStyleableKey HEIGHT = RectangleFigure.HEIGHT;
    @NonNull
    public final static CssRectangle2DStyleableMapAccessor BOUNDS = RectangleFigure.BOUNDS;
    @Nullable
    private Image cachedImage;
    @Nullable
    private URI cachedImageUri;

    public ImageFigure() {
        this(0, 0, 1, 1);
    }

    public ImageFigure(double x, double y, double width, double height) {
        set(BOUNDS, new CssRectangle2D(x, y, width, height));
    }

    public ImageFigure(CssRectangle2D rect) {
        set(BOUNDS, rect);
    }

    @NonNull
    @Override
    public CssRectangle2D getCssLayoutBounds() {
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

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {
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

    @NonNull
    @Override
    public Connector findConnector(@NonNull Point2D p, Figure prototype) {
        return new RectangleConnector(new BoundsLocator(getLayoutBounds(), p));
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public double getPreferredAspectRatio() {
        return (cachedImage == null || cachedImage.getWidth() == 0 || cachedImage.getHeight() == 0)//
                ? super.getPreferredAspectRatio()//
                : cachedImage.getHeight() / cachedImage.getWidth();
    }

    private void validateImage() {
        URI uri = get(IMAGE_URI);
        if (uri == null) {
            cachedImageUri = null;
            cachedImage = null;
            return;
        }
        Drawing drawing = getDrawing();
        URI documentHome = drawing == null ? null : drawing.get(Drawing.DOCUMENT_HOME);
        URI absoluteUri = (documentHome == null) ? uri : documentHome.resolve(uri);
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
