/* @(#)PageFigure.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import static java.lang.Double.max;
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
import org.jhotdraw8.draw.key.PaperSizeStyleableMapAccessor;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.Rectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.Size2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.SizeInsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.SizeStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;
import org.jhotdraw8.text.CssSize;
import org.jhotdraw8.text.CssSize2D;

/**
 * Defines a page layout for printing.
 *
 * @author Werner Randelshofer
 */
public class PageFigure extends AbstractCompositeFigure implements Page, Group, TransformableFigure, ResizableFigure, HideableFigure, LockableFigure, StyleableFigure {

    public final static DoubleStyleableFigureKey HEIGHT = new DoubleStyleableFigureKey("height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey NUM_PAGES_X = new DoubleStyleableFigureKey("num-pages-x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static DoubleStyleableFigureKey NUM_PAGES_Y = new DoubleStyleableFigureKey("num-pages-y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 1.0);
    public final static Point2DStyleableMapAccessor NUM_PAGES_X_Y = new Point2DStyleableMapAccessor("num-pages", NUM_PAGES_X, NUM_PAGES_Y);
    public final static SizeStyleableFigureKey PAGE_INSETS_BOTTOM = new SizeStyleableFigureKey("page-insets-bottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static SizeStyleableFigureKey PAGE_INSETS_LEFT = new SizeStyleableFigureKey("page-insets-left", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static SizeStyleableFigureKey PAGE_INSETS_RIGHT = new SizeStyleableFigureKey("page-insets-right", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static SizeStyleableFigureKey PAGE_INSETS_TOP = new SizeStyleableFigureKey("page-insets-top", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static SizeInsetsStyleableMapAccessor PAGE_INSETS = new SizeInsetsStyleableMapAccessor("page-insets", PAGE_INSETS_TOP, PAGE_INSETS_RIGHT, PAGE_INSETS_BOTTOM, PAGE_INSETS_LEFT);
    public final static SizeStyleableFigureKey PAGE_OVERLAP_X = new SizeStyleableFigureKey("page-overlap-x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static SizeStyleableFigureKey PAGE_OVERLAP_Y = new SizeStyleableFigureKey("page-overlap-y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static Size2DStyleableMapAccessor PAGE_OVERLAP = new Size2DStyleableMapAccessor("page-overlap", PAGE_OVERLAP_X, PAGE_OVERLAP_Y);
    public final static SizeStyleableFigureKey PAPER_HEIGHT = new SizeStyleableFigureKey("paper-size-height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssSize(297.0, "mm"));
    public final static SizeStyleableFigureKey PAPER_WIDTH = new SizeStyleableFigureKey("paper-size-width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssSize(210.0, "mm"));
    public final static PaperSizeStyleableMapAccessor PAPER_SIZE = new PaperSizeStyleableMapAccessor("paper-size", PAPER_WIDTH, PAPER_HEIGHT);
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Page";
    public final static DoubleStyleableFigureKey WIDTH = new DoubleStyleableFigureKey("width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey X = new DoubleStyleableFigureKey("x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static DoubleStyleableFigureKey Y = new DoubleStyleableFigureKey("y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), 0.0);
    public final static Rectangle2DStyleableMapAccessor BOUNDS = new Rectangle2DStyleableMapAccessor("bounds", X, Y, WIDTH, HEIGHT);
    private final static Object CONTENT_BOUNDS_PROPERTY =new Object();
    private final static Object PAGE_INSETS_PROPERTY = new Object();
    private final static Object PAGE_BOUNDS_PROPERTY = new Object();
private final static Object CURRENT_PAGE_PROPERTY = new Object();

    private void addBounds(final List<PathElement> pbList, Bounds b) {
        double x = b.getMinX();
        double y = b.getMinY();
        double w = b.getWidth();
        double h = b.getHeight();
        pbList.add(new MoveTo(x, y));
        pbList.add(new LineTo(x + w, y));
        pbList.add(new LineTo(x + w, y + h));
        pbList.add(new LineTo(x, y + h));
        pbList.add(new ClosePath());
    }

    /**
     *
     * @return
     */
    private double computeContentAreaFactor() {
        double contentWidth = get(WIDTH);
        double contentHeight = get(HEIGHT);
        Insets insets = getStyled(PAGE_INSETS).getDefaultConvertedValue();
        CssSize2D overlap = getStyled(PAGE_OVERLAP);
        double overX = overlap.getX().getConvertedValue();
        double overY = overlap.getY().getConvertedValue();
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());
        double innerPageW = (get(PAPER_WIDTH).getConvertedValue() - insets.getLeft() - insets.getRight());
        double innerPageH = (get(PAPER_HEIGHT).getConvertedValue() - insets.getTop() - insets.getBottom());
        double totalInnerPageWidth = innerPageW * numPagesX - overX * max(0,numPagesX - 1);
        double totalInnerPageHeight = innerPageH * numPagesY - overY * max(0,numPagesY - 1);
        double contentRatio = contentWidth / contentHeight;
        double innerPageRatio = totalInnerPageWidth / totalInnerPageHeight;
        double contentAreaFactor;
        if (contentRatio > innerPageRatio) {
            contentAreaFactor = (contentWidth) / totalInnerPageWidth;
        } else {
            contentAreaFactor = (contentHeight) / totalInnerPageHeight;
        }
        return contentAreaFactor;
    }

    @Override
    public Node createNode(RenderContext ctx) {
        javafx.scene.Group groupNode = new javafx.scene.Group();

        Rectangle contentBoundsNode = new Rectangle();
        contentBoundsNode.setFill(null);
        contentBoundsNode.setStroke(Color.MEDIUMBLUE);
        contentBoundsNode.setStrokeType(StrokeType.INSIDE);

        Path pageBoundsNode = new Path();
        pageBoundsNode.setFill(Color.TRANSPARENT);
        pageBoundsNode.setStroke(Color.LIGHTBLUE);
        pageBoundsNode.setStrokeType(StrokeType.CENTERED);

        Path insetsBoundsNode = new Path();
        insetsBoundsNode.setFill(null);
        insetsBoundsNode.setStroke(Color.LIGHTBLUE);
        insetsBoundsNode.setStrokeType(StrokeType.CENTERED);
        insetsBoundsNode.getStrokeDashArray().setAll(5.0);
        
        javafx.scene.Group currentPageNode = new javafx.scene.Group();

        groupNode.getChildren().addAll(pageBoundsNode, insetsBoundsNode,contentBoundsNode, currentPageNode);
        groupNode.getProperties().put(PAGE_BOUNDS_PROPERTY, pageBoundsNode);
        groupNode.getProperties().put(PAGE_INSETS_PROPERTY, insetsBoundsNode);
        groupNode.getProperties().put(CONTENT_BOUNDS_PROPERTY, contentBoundsNode);
        groupNode.getProperties().put(CURRENT_PAGE_PROPERTY                , currentPageNode);
        return groupNode;
    }

    @Override
    public Node createPageNode(int internalPageNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(get(X), get(Y), get(WIDTH), get(HEIGHT));
    }

    @Override
    public int getNumberOfSubPages() {
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());
        return numPagesX * numPagesY;
    }

    @Override
    public Bounds getPageBounds(int internalPageNumber) {
        double contentAreaFactor = computeContentAreaFactor();
        Insets insets = getStyled(PAGE_INSETS).getDefaultConvertedValue();
        CssSize2D overlap = getStyled(PAGE_OVERLAP);
        double overX = overlap.getX().getConvertedValue();
        double overY = overlap.getY().getConvertedValue();
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());

        double pageX = get(X) - insets.getLeft() * contentAreaFactor;
        double pageY = get(Y) - insets.getTop() * contentAreaFactor;;
        double pageW = get(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageH = get(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
        double pageOverX = (overX + insets.getLeft() + insets.getRight()) * contentAreaFactor;
        double pageOverY = (overY + insets.getTop() + insets.getBottom()) * contentAreaFactor;
        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        double x = pageX + (pageW - pageOverX) * px;
        double y = pageY + (pageH - pageOverY) * py;
        return new BoundingBox(x, y, pageW, pageH);
    }

private Bounds getContentBounds(int internalPageNumber) {
        double contentAreaFactor = computeContentAreaFactor();
        Insets insets = getStyled(PAGE_INSETS).getDefaultConvertedValue();
        CssSize2D overlap = getStyled(PAGE_OVERLAP);
        double overX = overlap.getX().getConvertedValue();
        double overY = overlap.getY().getConvertedValue();
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());

        double pageX = get(X) ;
        double pageY = get(Y) ;
        double pageW = get(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageH = get(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
        double marginH=insets.getLeft() + insets.getRight();
        double marginV=insets.getTop() + insets.getBottom();
        double pageOverX = (overX + marginH) * contentAreaFactor;
        double pageOverY = (overY + marginV) * contentAreaFactor;
        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        double x = pageX + (pageW - pageOverX) * px;
        double y = pageY + (pageH - pageOverY) * py;
        return new BoundingBox(x, y, pageW-marginH*contentAreaFactor, pageH-marginV*contentAreaFactor);
    }
    @Override
    public Shape getPageClip(int internalPageNumber) {
        double contentAreaFactor = computeContentAreaFactor();
        Insets insets = getStyled(PAGE_INSETS).getDefaultConvertedValue();
        CssSize2D overlap = getStyled(PAGE_OVERLAP);
        double ox = overlap.getX().getConvertedValue();
        double oy = overlap.getY().getConvertedValue();
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());

        double pageX = get(X) - insets.getLeft() * contentAreaFactor;
        double pageY = get(Y) - insets.getTop() * contentAreaFactor;;
        double pageWidth = get(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageHeight = get(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
        double pageOverlapX = (ox + insets.getLeft() + insets.getRight()) * contentAreaFactor;
        double pageOverlapY = (oy + insets.getTop() + insets.getBottom()) * contentAreaFactor;
        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        double x = pageX + (pageWidth - pageOverlapX) * px;
        double y = pageY + (pageHeight - pageOverlapY) * py;
        
  
      Bounds  b = Geom.intersection(getBoundsInLocal(),  
              new BoundingBox(x + insets.getLeft() * contentAreaFactor, y + insets.getTop() * contentAreaFactor,
                pageWidth - (insets.getLeft() + insets.getRight()) * contentAreaFactor,
                pageHeight - (insets.getTop() + insets.getBottom()) * contentAreaFactor));
        
        return new Rectangle(b.getMinX(),b.getMinY(),b.getWidth(),b.getHeight());
    }

    @Override
    public CssSize2D getPaperSize() {
        return getStyled(PAPER_SIZE);
    }

    @Override
    public Transform getPageTransform(int internalPageNumber) {
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());

        internalPageNumber = Math.max(0, Math.min(internalPageNumber, numPagesX * numPagesY));

        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        Insets insets = getStyled(PAGE_INSETS).getDefaultConvertedValue();
        CssSize2D overlap = getStyled(PAGE_OVERLAP);
        double overlapX = overlap.getX().getConvertedValue();
        double overlapY = overlap.getY().getConvertedValue();
        double contentAreaFactor = computeContentAreaFactor();
        double pageX = get(X) - insets.getLeft() * contentAreaFactor;
        double pageY = get(Y) - insets.getTop() * contentAreaFactor;;
        double pageWidth = get(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageHeight = get(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
        double pageOverlapX = (overlapX + insets.getLeft() + insets.getRight()) * contentAreaFactor;
        double pageOverlapY = (overlapY + insets.getTop() + insets.getBottom()) * contentAreaFactor;
        double x = pageX + (pageWidth - pageOverlapX) * px;
        double y = pageY + (pageHeight - pageOverlapY) * py;

        Transform pageTransform = Transforms.concat(new Translate(x, y), new Scale(contentAreaFactor, contentAreaFactor));
        return pageTransform;
    }

    private Translate getPageTranslate(int internalPageNumber) {
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());

        internalPageNumber = Math.max(0, Math.min(internalPageNumber, numPagesX * numPagesY));

        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        Insets insets = getStyled(PAGE_INSETS).getDefaultConvertedValue();
        CssSize2D overlap = getStyled(PAGE_OVERLAP);
        double overlapX = overlap.getX().getConvertedValue();
        double overlapY = overlap.getY().getConvertedValue();
        double contentAreaFactor = computeContentAreaFactor();
        double pageX = get(X) - insets.getLeft() * contentAreaFactor;
        double pageY = get(Y) - insets.getTop() * contentAreaFactor;;
        double pageWidth = get(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageHeight = get(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
        double pageOverlapX = (overlapX + insets.getLeft() + insets.getRight()) * contentAreaFactor;
        double pageOverlapY = (overlapY + insets.getTop() + insets.getBottom()) * contentAreaFactor;
        double x = (pageWidth - pageOverlapX) * px;
        double y = (pageHeight - pageOverlapY) * py;

        return new Translate(x, y);
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public boolean isLayoutable() {
        return true;
    }

    @Override
    public void layout() {
        int currentPage = 0;
        ImmutableObservableList<Transform> transforms = ImmutableObservableList.of(getPageTransform(currentPage));

        for (Figure child : getChildren()) {
            child.set(TRANSFORMS, transforms);
        }
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
    public void updateNode(RenderContext ctx, Node node) {
        javafx.scene.Group groupNode = (javafx.scene.Group) node;
        // We can't use #applyTransformableFigureProperties(node) because
        // this will rotate around an unpredictable center!
        node.getTransforms().setAll(getLocalToParent(true));

        Rectangle contentBoundsNode = (Rectangle) groupNode.getProperties().get(CONTENT_BOUNDS_PROPERTY);
        Path pageBoundsNode = (Path) groupNode.getProperties().get(PAGE_BOUNDS_PROPERTY);
        Path pageInsetsNode = (Path) groupNode.getProperties().get(PAGE_INSETS_PROPERTY);
        javafx.scene.Group currentPageNode = (javafx.scene.Group) groupNode.getProperties().get(CURRENT_PAGE_PROPERTY);

        if (ctx.get(RenderContext.RENDERING_INTENT) == RenderingIntent.EDITOR) {
            applyHideableFigureProperties(node);
            contentBoundsNode.setVisible(true);
            pageBoundsNode.setVisible(true);
        } else if (ctx.get(RenderContext.RENDER_PAGE) == this) {
            applyHideableFigureProperties(node);
            contentBoundsNode.setVisible(false);
            pageBoundsNode.setVisible(false);
            pageInsetsNode.setVisible(false);
        } else {
            node.setVisible(false);
        }

        double contentWidth = get(WIDTH);
        double contentHeight = get(HEIGHT);
        contentBoundsNode.setX(get(X));
        contentBoundsNode.setY(get(Y));
        contentBoundsNode.setWidth(contentWidth);
        contentBoundsNode.setHeight(contentHeight);

        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());
        final int n = numPagesX * numPagesY;
        final List<PathElement> pbList = new ArrayList<>(n*4);
        final List<PathElement> pmList = new ArrayList<>(n*4);
        for (int i = 0; i < n; i++) {
            addBounds(pbList,getPageBounds(i));
            addBounds(pmList,getContentBounds(i));
        }
       pageBoundsNode.getElements().setAll(pbList);
       pageInsetsNode.getElements().setAll(pmList);

        int currentPage = ctx.get(RenderContext.RENDER_PAGE_INTERNAL_NUMBER);
        currentPageNode.getTransforms().setAll(getPageTranslate(currentPage));

        List<Node> currentPageChildren = new ArrayList<Node>(getChildren().size() + 2);
        for (Figure child : getChildren()) {
            currentPageChildren.add(ctx.getNode(child));
        }
        ObservableList<Node> group = currentPageNode.getChildren();
        if (!group.equals(currentPageChildren)) {
            group.setAll(currentPageChildren);
        }    
    }
}
