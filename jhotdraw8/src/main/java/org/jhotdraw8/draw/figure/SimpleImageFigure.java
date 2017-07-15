/* @(#)SimpleImageFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.net.URI;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.UriStyleableFigureKey;
import org.jhotdraw8.draw.locator.RelativeLocator;

/**
 * SimpleImageFigure.
 *
 * @author Werner Randelshofer
 */
public class SimpleImageFigure extends AbstractLeafFigure implements ResizableFigure, TransformableFigure, StyleableFigure, LockableFigure, CompositableFigure,ConnectableFigure {

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
    public final static UriStyleableFigureKey IMAGE_URI = new UriStyleableFigureKey("src", null);

    public final static DoubleStyleableFigureKey X = SimpleRectangleFigure.X;
    public final static DoubleStyleableFigureKey Y = SimpleRectangleFigure.Y;
    public final static DoubleStyleableFigureKey WIDTH = SimpleRectangleFigure.WIDTH;
    public final static DoubleStyleableFigureKey HEIGHT = SimpleRectangleFigure.HEIGHT;
    public final static Rectangle2DStyleableMapAccessor BOUNDS = SimpleRectangleFigure.BOUNDS;
    private Image cachedImage;
    private URI cachedImageUri;

    public SimpleImageFigure() {
        this(0, 0, 1, 1);
    }

    public SimpleImageFigure(double x, double y, double width, double height) {
        set(BOUNDS, new Rectangle2D(x, y, width, height));
    }

    public SimpleImageFigure(Rectangle2D rect) {
        set(BOUNDS, rect);
    }

    @Override
    public Bounds getBoundsInLocal() {
        Rectangle2D r = get(BOUNDS);
        return new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        Rectangle2D r = get(BOUNDS);
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        set(BOUNDS, new Rectangle2D(x + Math.min(width, 0), y + Math.min(height, 0), Math.abs(width), Math.abs(height)));
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(false);
        return imageView;
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        ImageView imageView = (ImageView) node;
        validateImage();
        imageView.setImage(cachedImage);
        applyTransformableFigureProperties(imageView);
        applyCompositableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
        Rectangle2D r = get(BOUNDS);
        imageView.setX(r.getMinX());
        imageView.setY(r.getMinY());
        imageView.setFitWidth(r.getWidth());
        imageView.setFitHeight(r.getHeight());
        imageView.applyCss();
        imageView.getProperties().put(IMAGE_URI, get(IMAGE_URI));
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
            return new RectangleConnector(new RelativeLocator(getBoundsInLocal(), p));
    }

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
            cachedImage = new Image(cachedImageUri.toString(), true);
        }
    }
}
