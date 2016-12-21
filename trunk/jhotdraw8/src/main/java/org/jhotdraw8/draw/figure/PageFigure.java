/* @(#)PageFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.awt.print.PageFormat;
import java.awt.print.Paper;
import static java.lang.Math.abs;
import static java.lang.Math.min;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;
import javafx.scene.shape.Shape;
import javafx.scene.shape.StrokeType;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.InsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.geom.Transforms;

/**
 * Defines a page layout for printing.
 *
 * @author Werner Randelshofer
 */
public class PageFigure extends AbstractCompositeFigure implements Page, Group, TransformableFigure, ResizableFigure, HideableFigure, LockableFigure, StyleableFigure {

    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Page";
    public final static DoubleStyleableFigureKey X = new DoubleStyleableFigureKey("x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey Y = new DoubleStyleableFigureKey("y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey WIDTH = new DoubleStyleableFigureKey("width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey HEIGHT = new DoubleStyleableFigureKey("height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Rectangle2DStyleableMapAccessor BOUNDS = new Rectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);
    public final static DoubleStyleableFigureKey PAPER_WIDTH = new DoubleStyleableFigureKey("paper-size-width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 297.0);
    public final static DoubleStyleableFigureKey PAPER_HEIGHT = new DoubleStyleableFigureKey("paper-size-height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 210.0);
    public final static Point2DStyleableMapAccessor PAPER_SIZE = new Point2DStyleableMapAccessor("paper-size", PAPER_WIDTH, PAPER_HEIGHT);
    public final static DoubleStyleableFigureKey PAGE_INSETS_TOP = new DoubleStyleableFigureKey("page-insets-top", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PAGE_INSETS_RIGHT = new DoubleStyleableFigureKey("page-insets-right", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PAGE_INSETS_BOTTOM = new DoubleStyleableFigureKey("page-.insets-bottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey PAGE_INSETS_LEFT = new DoubleStyleableFigureKey("page-insets-left", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static InsetsStyleableMapAccessor PAGE_INSETS = new InsetsStyleableMapAccessor("page-insets", PAGE_INSETS_TOP, PAGE_INSETS_RIGHT, PAGE_INSETS_BOTTOM, PAGE_INSETS_LEFT);
    public final static DoubleStyleableFigureKey NUM_PAGES_X = new DoubleStyleableFigureKey("num-pages-x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey NUM_PAGES_Y = new DoubleStyleableFigureKey("num-pages-y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static Point2DStyleableMapAccessor NUM_PAGES_X_Y = new Point2DStyleableMapAccessor("num-pages-x-y", NUM_PAGES_X, NUM_PAGES_Y);
    public final static DoubleStyleableFigureKey PAGE_OVERLAP = new DoubleStyleableFigureKey("page-overlap", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(get(X), get(Y), get(WIDTH), get(HEIGHT));
    }

    public void reshapeInLocal(double x, double y, double width, double height) {
        set(X, x + min(width, 0));
        set(Y, y + min(height, 0));
        set(WIDTH, abs(width));
        set(HEIGHT, abs(height));
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        Bounds newBounds = transform.transform(getBoundsInLocal());
        set(X, newBounds.getMinX());
        set(Y, newBounds.getMinY());
        set(WIDTH, newBounds.getWidth());
        set(HEIGHT, newBounds.getHeight());
    }

    @Override
    public Node createNode(RenderContext ctx) {
        javafx.scene.Group groupNode = new javafx.scene.Group();

        Rectangle contentBoundsNode = new Rectangle();
        contentBoundsNode.setFill(Color.TRANSPARENT);
        contentBoundsNode.setStroke(Color.BLUE);
        contentBoundsNode.setStrokeType(StrokeType.INSIDE);

        Path pageBoundsNode = new Path();
        pageBoundsNode.setFill(Color.TRANSPARENT);
        pageBoundsNode.setStroke(Color.LIGHTBLUE);
        pageBoundsNode.setStrokeType(StrokeType.CENTERED);

        groupNode.getChildren().addAll(pageBoundsNode, contentBoundsNode);
        groupNode.getProperties().put("pageBounds", pageBoundsNode);
        groupNode.getProperties().put("contentBounds", contentBoundsNode);
        return groupNode;
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        javafx.scene.Group groupNode = (javafx.scene.Group) node;
        // We can't use #applyTransformableFigureProperties(node) because
        // this will rotate around an unpredictable center!
        node.getTransforms().setAll(getLocalToParent(true));

        Rectangle contentBoundsNode = (Rectangle) groupNode.getProperties().get("contentBounds");
        Path pageBoundsNode = (Path) groupNode.getProperties().get("pageBounds");

        applyHideableFigureProperties(node);
        if (ctx.get(RenderContext.RENDERING_INTENT) != RenderingIntent.EDITOR) {
            contentBoundsNode.setVisible(false);
            pageBoundsNode.setVisible(false);
        }

        double contentWidth = get(WIDTH);
        double contentHeight = get(HEIGHT);
        contentBoundsNode.setX(get(X));
        contentBoundsNode.setY(get(Y));
        contentBoundsNode.setWidth(contentWidth);
        contentBoundsNode.setHeight(contentHeight);

        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());
        final ObservableList<PathElement> l = pageBoundsNode.getElements();
        l.clear();
        for (int i = 0, n = numPagesX * numPagesY; i < n; i++) {
            Bounds b = getPageBounds(i);
            double x = b.getMinX();
            double y = b.getMinY();
            double w = b.getWidth();
            double h = b.getHeight();
            l.add(new MoveTo(x, y));
            l.add(new LineTo(x + w, y));
            l.add(new LineTo(x + w, y + h));
            l.add(new LineTo(x, y + h));
            l.add(new ClosePath());
        }
        List<Node> nodes = new ArrayList<Node>(getChildren().size() + 2);
        nodes.add(pageBoundsNode);
        nodes.add(contentBoundsNode);
        for (Figure child : getChildren()) {
            nodes.add(ctx.getNode(child));
        }
        ObservableList<Node> group = groupNode.getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }

    private double computeContentAreaFactor() {
        double contentWidth = get(WIDTH);
        double contentHeight = get(HEIGHT);
        Insets insets = getStyled(PAGE_INSETS);
        double overlap = getStyled(PAGE_OVERLAP);
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());
        double contentAreaWidth = (get(PAPER_WIDTH) - insets.getLeft() - insets.getRight()) * numPagesX;
        double contentAreaHeight = (get(PAPER_HEIGHT) - insets.getTop() - insets.getBottom()) * numPagesY;
        double contentRatio = contentWidth / contentHeight;
        double contentAreaRatio = contentAreaWidth / contentAreaHeight;
        double contentAreaFactor;
        if (contentRatio > contentAreaRatio) {
            contentAreaFactor = (contentWidth + overlap * (numPagesX - 1)) / contentAreaWidth;
        } else {
            contentAreaFactor = (contentHeight + overlap * (numPagesY - 1)) / contentAreaHeight;
        }
        return contentAreaFactor;
    }

    @Override
    public boolean isLayoutable() {
        return true;
    }

    @Override
    public void layout() {
        int currentPage = 1;
        ImmutableObservableList<Transform> transforms = ImmutableObservableList.of(getPageTransform(currentPage));

        for (Figure child : getChildren()) {
            child.set(TRANSFORMS, transforms);
        }
    }

    @Override
    public int getNumberOfInternalPages() {
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());
        return numPagesX * numPagesY;
    }

    @Override
    public Node createPageNode(int internalPageNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Shape getPageClip(int internalPageNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Transform getPageTransform(int internalPageNumber) {
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());

        internalPageNumber = Math.max(0, Math.min(internalPageNumber, numPagesX * numPagesY));

        double px = internalPageNumber % numPagesX;
        double py = internalPageNumber / numPagesX;
        Insets insets = getStyled(PAGE_INSETS);
        double overlap = getStyled(PAGE_OVERLAP);
        double contentAreaFactor = computeContentAreaFactor();
        double pageX = get(X) - insets.getLeft() * contentAreaFactor;
        double pageY = get(Y) - insets.getTop() * contentAreaFactor;;
        double pageWidth = get(PAPER_WIDTH) * contentAreaFactor;
        double pageHeight = get(PAPER_HEIGHT) * contentAreaFactor;
        double pageOverlapX = (overlap + insets.getLeft() + insets.getRight()) * contentAreaFactor;
        double pageOverlapY = (overlap + insets.getTop() + insets.getBottom()) * contentAreaFactor;
        double x = pageX + (pageWidth - pageOverlapX) * px;
        double y = pageY + (pageHeight - pageOverlapY) * py;

        Transform pageTransform = Transforms.concat(new Translate(x, y),new Scale(contentAreaFactor, contentAreaFactor));
        return pageTransform;
    }

    @Override
    public PageFormat getPageFormat() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Paper createPaper(int internalPageNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bounds getPageBounds(int internalPageNumber) {
        double contentAreaFactor = computeContentAreaFactor();
        Insets insets = getStyled(PAGE_INSETS);
        double overlap = getStyled(PAGE_OVERLAP);
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());

        double pageX = get(X) - insets.getLeft() * contentAreaFactor;
        double pageY = get(Y) - insets.getTop() * contentAreaFactor;;
        double pageWidth = get(PAPER_WIDTH) * contentAreaFactor;
        double pageHeight = get(PAPER_HEIGHT) * contentAreaFactor;
        double pageOverlapX = (overlap + insets.getLeft() + insets.getRight()) * contentAreaFactor;
        double pageOverlapY = (overlap + insets.getTop() + insets.getBottom()) * contentAreaFactor;
        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        double x = pageX + (pageWidth - pageOverlapX) * px;
        double y = pageY + (pageHeight - pageOverlapY) * py;
        return new BoundingBox(x, y, pageWidth, pageHeight);
    }
}
