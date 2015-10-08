/* @(#)SimpleImageFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw.draw;

import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.collections.ObservableList;
import javafx.css.Styleable;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw.draw.connector.ChopRectangleConnector;
import org.jhotdraw.draw.connector.Connector;
import org.jhotdraw.draw.key.DirtyBits;
import org.jhotdraw.draw.key.DirtyMask;
import org.jhotdraw.draw.key.SimpleFigureKey;
import org.jhotdraw.draw.shape.AbstractShapeFigure;

/**
 * SimpleImageFigure.
 *
 * @author Werner Randelshofer
 */
public class SimpleImageFigure extends AbstractLeafFigure {

    /**
     * The CSS type selector for this object is {@code "Image"}.
     */
    public final static String TYPE_SELECTOR = "Image";

    public final static SimpleFigureKey<URL> IMAGE_URL = new SimpleFigureKey<>("imageUrl", URL.class, false, DirtyMask.of(DirtyBits.NODE), null);
    public final static SimpleFigureKey<Rectangle2D> IMAGE_RECTANGLE = new SimpleFigureKey<>("imageRectangle", Rectangle2D.class, false, DirtyMask.of(DirtyBits.NODE, DirtyBits.CONNECTION_LAYOUT, DirtyBits.LAYOUT), new Rectangle2D(0, 0, 1, 1));

    private Image cachedImage;
    private URL cachedImageUrl;

    public SimpleImageFigure() {
        this(0, 0, 1, 1);
    }

    public SimpleImageFigure(double x, double y, double width, double height) {
        set(IMAGE_RECTANGLE, new Rectangle2D(x, y, width, height));
    }

    public SimpleImageFigure(Rectangle2D rect) {
        set(IMAGE_RECTANGLE, rect);
    }

    @Override
    public Bounds getBoundsInLocal() {
        Rectangle2D r = get(IMAGE_RECTANGLE);
        return new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
    }

    @Override
    public void reshape(Transform transform) {
        Rectangle2D r = get(IMAGE_RECTANGLE);
        Bounds b = new BoundingBox(r.getMinX(), r.getMinY(), r.getWidth(), r.getHeight());
        b = transform.transform(b);
        reshape(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshape(double x, double y, double width, double height) {
        set(IMAGE_RECTANGLE, new Rectangle2D(x + min(width, 0), y + min(height, 0), abs(width), abs(height)));
    }

    @Override
    public Node createNode(RenderContext drawingView) {
        ImageView imageView = new ImageView();
        imageView.setPreserveRatio(false);
        return imageView;
    }

    @Override
    public void updateNode(RenderContext drawingView, Node node) {
        ImageView imageView = (ImageView) node;
        validateImage();
        imageView.setImage(cachedImage);
        applyFigureProperties(imageView);
        Rectangle2D r = get(IMAGE_RECTANGLE);
        imageView.setX(r.getMinX());
        imageView.setY(r.getMinY());
        imageView.setFitWidth(r.getWidth());
        imageView.setFitHeight(r.getHeight());
        imageView.applyCss();
    }

    @Override
    public Connector findConnector(Point2D p, Figure prototype) {
        return new ChopRectangleConnector(this);
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

    @Override
    public boolean isLayoutable() {
        return false;
    }

    @Override
    public void layout() {
        // empty since we are not layoutable
    }

    private void validateImage() {
        URL url = get(IMAGE_URL);
        if (url == null) {
            cachedImageUrl = null;
            cachedImage = null;
            return;
        }
        Drawing drawing = getDrawing();
        URL documentHome = drawing == null ? null : drawing.get(Drawing.DOCUMENT_HOME);
        try {
            URL absoluteUrl = (documentHome == null) ? url : new URL(documentHome, url.toString());
            if (cachedImageUrl == null || !cachedImageUrl.equals(absoluteUrl)) {
                cachedImageUrl = absoluteUrl;
                cachedImage = new Image(cachedImageUrl.toString(), true);
            }
        } catch (MalformedURLException ex) {
            System.err.println("warning could not load image " + ex);
        }
    }
}
