/*
 * @(#)ImageFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.RectangleConnector;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.locator.BoundsLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.io.SimpleUriResolver;
import org.jhotdraw8.svg.io.FXSvgTinyReader;

import javax.xml.transform.Source;
import javax.xml.transform.stream.StreamSource;
import java.io.IOException;
import java.net.URI;
import java.util.Objects;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * SvgImageFigure presents an SVG image or a bitmap image on a drawing.
 *
 * @author Werner Randelshofer
 */
public class SvgImageFigure extends AbstractLeafFigure
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

    public SvgImageFigure() {
        this(0, 0, 1, 1);
    }

    public SvgImageFigure(double x, double y, double width, double height) {
        set(BOUNDS, new CssRectangle2D(x, y, width, height));
    }

    public SvgImageFigure(CssRectangle2D rect) {
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
        Group g = new Group();
        return g;
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Group g = (Group) node;
        final Object renderedUri = g.getProperties().get("renderedUri");
        final URI imageUri = getStyled(IMAGE_URI);
        final Drawing drawing = getDrawing();
        final URI documentHome = drawing == null ? null : drawing.get(Drawing.DOCUMENT_HOME);
        final URI absoluteImageUri = documentHome == null ? imageUri : new SimpleUriResolver().absolutize(documentHome, imageUri);
        Node imageNode = g.getChildren().isEmpty() ? null : g.getChildren().get(0);
        if (imageNode == null || !Objects.equals(renderedUri, absoluteImageUri)) {
            imageNode = loadImage();
            g.getChildren().setAll(imageNode);
            g.getProperties().put("renderedUri", absoluteImageUri);
        }
        final UnitConverter converter = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        final Transform reshapeTransform = FXTransforms.createReshapeTransform(imageNode.getBoundsInLocal(),
                getLayoutBounds());
        imageNode.getTransforms().setAll(reshapeTransform);

        applyTransformableFigureProperties(ctx, g);
        applyCompositableFigureProperties(ctx, g);
        applyStyleableFigureProperties(ctx, g);
        applyHideableFigureProperties(ctx, g);
    }

    @Override
    public @NonNull Connector findConnector(@NonNull Point2D p, Figure prototype, double tolerance) {
        return new RectangleConnector(new BoundsLocator(getLayoutBounds(), p));
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    private Node loadImage() {
        URI uri = getStyled(IMAGE_URI);
        if (uri == null) {
            return new Group();// maybe we should show a broken-link icon here
        }
        Drawing drawing = getDrawing();
        URI documentHome = drawing == null ? null : drawing.get(Drawing.DOCUMENT_HOME);
        URI absoluteUri = (documentHome == null) ? uri : documentHome.resolve(uri);

        Source source = new StreamSource(absoluteUri.toString());
        try {
            final String path = absoluteUri.getPath() == null ? absoluteUri.toString() : absoluteUri.getPath();
            if (path != null && path.toLowerCase().endsWith(".svg")) {
                // must be wrapped in a group, because the node returned
                // by the reader might have transformations associated to it
                Group g = new Group();
                g.getChildren().addAll(new FXSvgTinyReader().read(source));
                return g;
            } else {
                final Image image = new Image(absoluteUri.toString(), true);
                return new ImageView(image);
            }
        } catch (IOException e) {
            Logger logger = Logger.getLogger(getClass().getName());
            logger.log(Level.INFO, "Could not load SVG image from " + absoluteUri, e);
            return new Group();// maybe we should show a broken-link icon here
        }
    }

    @Override
    public @NonNull Bounds getBoundsInLocal() {
        return getLayoutBounds();
    }
}
