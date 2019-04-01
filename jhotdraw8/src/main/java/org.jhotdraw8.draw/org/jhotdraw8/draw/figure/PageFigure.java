/* @(#)PageFigure.java
 * Copyright © by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

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
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.ImmutableLists;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.CssInsetsStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssPoint2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssRectangle2DStyleableMapAccessor;
import org.jhotdraw8.draw.key.CssSizeStyleableKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.PaperSizeStyleableMapAccessor;
import org.jhotdraw8.draw.key.Point2DStyleableMapAccessor;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.draw.render.RenderingIntent;
import org.jhotdraw8.geom.Geom;
import org.jhotdraw8.geom.Transforms;

import java.util.ArrayList;
import java.util.List;

import static java.lang.Double.max;

/**
 * Defines a page layout for printing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PageFigure extends AbstractCompositeFigure
        implements Page, Grouping, TransformableFigure, ResizableFigure, HideableFigure, LockableFigure, StyleableFigure,
        FillableFigure, StrokableFigure {

    public final static CssSizeStyleableKey HEIGHT = RectangleFigure.HEIGHT;
    public final static DoubleStyleableKey NUM_PAGES_X = new DoubleStyleableKey("num-pages-x", 1.0);
    public final static DoubleStyleableKey NUM_PAGES_Y = new DoubleStyleableKey("num-pages-y", 1.0);
    public final static Point2DStyleableMapAccessor NUM_PAGES_X_Y = new Point2DStyleableMapAccessor("num-pages", NUM_PAGES_X, NUM_PAGES_Y);
    public final static CssSizeStyleableKey PAGE_INSETS_BOTTOM = new CssSizeStyleableKey("page-insets-bottom", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableKey PAGE_INSETS_LEFT = new CssSizeStyleableKey("page-insets-left", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableKey PAGE_INSETS_RIGHT = new CssSizeStyleableKey("page-insets-right", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableKey PAGE_INSETS_TOP = new CssSizeStyleableKey("page-insets-top", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssInsetsStyleableMapAccessor PAGE_INSETS = new CssInsetsStyleableMapAccessor("page-insets", PAGE_INSETS_TOP, PAGE_INSETS_RIGHT, PAGE_INSETS_BOTTOM, PAGE_INSETS_LEFT);
    public final static CssSizeStyleableKey PAGE_OVERLAP_X = new CssSizeStyleableKey("page-overlap-x", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssSizeStyleableKey PAGE_OVERLAP_Y = new CssSizeStyleableKey("page-overlap-y", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), CssSize.ZERO);
    public final static CssPoint2DStyleableMapAccessor PAGE_OVERLAP = new CssPoint2DStyleableMapAccessor("page-overlap", PAGE_OVERLAP_X, PAGE_OVERLAP_Y);
    public final static CssSizeStyleableKey PAPER_HEIGHT = new CssSizeStyleableKey("paper-size-height", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssSize(297.0, "mm"));
    public final static CssSizeStyleableKey PAPER_WIDTH = new CssSizeStyleableKey("paper-size-width", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), new CssSize(210.0, "mm"));
    public final static PaperSizeStyleableMapAccessor PAPER_SIZE = new PaperSizeStyleableMapAccessor("paper-size", PAPER_WIDTH, PAPER_HEIGHT);
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Page";
    public final static CssSizeStyleableKey WIDTH = RectangleFigure.WIDTH;
    public final static CssSizeStyleableKey X = RectangleFigure.X;
    public final static CssSizeStyleableKey Y = RectangleFigure.Y;
    public final static CssRectangle2DStyleableMapAccessor BOUNDS = RectangleFigure.BOUNDS;
    private final static Object CONTENT_BOUNDS_PROPERTY = new Object();
    private final static Object PAGE_INSETS_PROPERTY = new Object();
    private final static Object PAGE_BOUNDS_PROPERTY = new Object();
    private final static Object CURRENT_PAGE_PROPERTY = new Object();

    public PageFigure() {
        set(FILL, new CssColor(Color.TRANSPARENT));
        set(STROKE_TYPE, StrokeType.CENTERED);
    }

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
     * @return
     */
    private double computeContentAreaFactor() {
        double contentWidth = getNonnull(WIDTH).getConvertedValue();
        double contentHeight = getNonnull(HEIGHT).getConvertedValue();
        Insets insets = getStyledNonnull(PAGE_INSETS).getConvertedValue();
        CssPoint2D overlap = getStyledNonnull(PAGE_OVERLAP);
        double overX = overlap.getX().getConvertedValue();
        double overY = overlap.getY().getConvertedValue();
        int numPagesX = Math.max(1, getStyledNonnull(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyledNonnull(NUM_PAGES_Y).intValue());
        double innerPageW = (getStyledNonnull(PAPER_WIDTH).getConvertedValue() - insets.getLeft() - insets.getRight());
        double innerPageH = (getStyledNonnull(PAPER_HEIGHT).getConvertedValue() - insets.getTop() - insets.getBottom());
        double totalInnerPageWidth = innerPageW * numPagesX - overX * max(0, numPagesX - 1);
        double totalInnerPageHeight = innerPageH * numPagesY - overY * max(0, numPagesY - 1);
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

    @Nonnull
    @Override
    public Node createNode(RenderContext ctx) {
        javafx.scene.Group groupNode = new javafx.scene.Group();

        Rectangle contentBoundsNode = new Rectangle();
        contentBoundsNode.setFill(null);
        contentBoundsNode.setStroke(Color.LIGHTGRAY);
        contentBoundsNode.setStrokeType(StrokeType.INSIDE);

        Path pageBoundsNode = new Path();

        Path insetsBoundsNode = new Path();
        insetsBoundsNode.setFill(null);
        insetsBoundsNode.setStroke(Color.LIGHTGRAY);
        insetsBoundsNode.setStrokeType(StrokeType.CENTERED);
        insetsBoundsNode.getStrokeDashArray().setAll(5.0);

        javafx.scene.Group currentPageNode = new javafx.scene.Group();

        groupNode.getChildren().addAll(pageBoundsNode, insetsBoundsNode, contentBoundsNode, currentPageNode);
        groupNode.getProperties().put(PAGE_BOUNDS_PROPERTY, pageBoundsNode);
        groupNode.getProperties().put(PAGE_INSETS_PROPERTY, insetsBoundsNode);
        groupNode.getProperties().put(CONTENT_BOUNDS_PROPERTY, contentBoundsNode);
        groupNode.getProperties().put(CURRENT_PAGE_PROPERTY, currentPageNode);
        return groupNode;
    }

    @Nonnull
    @Override
    public Node createPageNode(int internalPageNumber) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body ofCollection generated methods, choose Tools | Templates.
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        return getCssBoundsInLocal().getConvertedBoundsValue();
    }

    @Nonnull
    @Override
    public CssRectangle2D getCssBoundsInLocal() {
        return new CssRectangle2D(getNonnull(X),
                getNonnull(Y),
                getNonnull(WIDTH),
                getNonnull(HEIGHT));
    }

    @Override
    public int getNumberOfSubPages() {
        int numPagesX = Math.max(1, getStyledNonnull(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyledNonnull(NUM_PAGES_Y).intValue());
        return numPagesX * numPagesY;
    }

    @Nonnull
    @Override
    public Bounds getPageBounds(int internalPageNumber) {
        double contentAreaFactor = computeContentAreaFactor();
        Insets insets = getStyledNonnull(PAGE_INSETS).getConvertedValue();
        CssPoint2D overlap = getStyledNonnull(PAGE_OVERLAP);
        double overX = overlap.getX().getConvertedValue();
        double overY = overlap.getY().getConvertedValue();
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());

        double pageX = getNonnull(X).getConvertedValue() - insets.getLeft() * contentAreaFactor;
        double pageY = getNonnull(Y).getConvertedValue() - insets.getTop() * contentAreaFactor;
        ;
        double pageW = getStyledNonnull(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageH = getStyledNonnull(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
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
        Insets insets = getStyledNonnull(PAGE_INSETS).getConvertedValue();
        CssPoint2D overlap = getStyledNonnull(PAGE_OVERLAP);
        double overX = overlap.getX().getConvertedValue();
        double overY = overlap.getY().getConvertedValue();
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());

        double pageX = getNonnull(X).getConvertedValue();
        double pageY = getNonnull(Y).getConvertedValue();
        double pageW = get(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageH = get(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
        double marginH = insets.getLeft() + insets.getRight();
        double marginV = insets.getTop() + insets.getBottom();
        double pageOverX = (overX + marginH) * contentAreaFactor;
        double pageOverY = (overY + marginV) * contentAreaFactor;
        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        double x = pageX + (pageW - pageOverX) * px;
        double y = pageY + (pageH - pageOverY) * py;
        return new BoundingBox(x, y, pageW - marginH * contentAreaFactor, pageH - marginV * contentAreaFactor);
    }

    @Nonnull
    @Override
    public Shape getPageClip(int internalPageNumber) {
        double contentAreaFactor = computeContentAreaFactor();
        Insets insets = getStyled(PAGE_INSETS).getConvertedValue();
        CssPoint2D overlap = getStyled(PAGE_OVERLAP);
        double ox = overlap.getX().getConvertedValue();
        double oy = overlap.getY().getConvertedValue();
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());

        double pageX = getNonnull(X).getConvertedValue() - insets.getLeft() * contentAreaFactor;
        double pageY = getNonnull(Y).getConvertedValue() - insets.getTop() * contentAreaFactor;
        ;
        double pageWidth = get(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageHeight = get(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
        double pageOverlapX = (ox + insets.getLeft() + insets.getRight()) * contentAreaFactor;
        double pageOverlapY = (oy + insets.getTop() + insets.getBottom()) * contentAreaFactor;
        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        double x = pageX + (pageWidth - pageOverlapX) * px;
        double y = pageY + (pageHeight - pageOverlapY) * py;


        Bounds b = Geom.intersection(getBoundsInLocal(),
                new BoundingBox(x + insets.getLeft() * contentAreaFactor, y + insets.getTop() * contentAreaFactor,
                        pageWidth - (insets.getLeft() + insets.getRight()) * contentAreaFactor,
                        pageHeight - (insets.getTop() + insets.getBottom()) * contentAreaFactor));

        return new Rectangle(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public CssPoint2D getPaperSize() {
        return getStyled(PAPER_SIZE);
    }

    @Override
    public Transform getPageTransform(int internalPageNumber) {
        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());

        internalPageNumber = Math.max(0, Math.min(internalPageNumber, numPagesX * numPagesY));

        int px = internalPageNumber % numPagesX;
        int py = internalPageNumber / numPagesX;
        Insets insets = getStyled(PAGE_INSETS).getConvertedValue();
        CssPoint2D overlap = getStyled(PAGE_OVERLAP);
        double overlapX = overlap.getX().getConvertedValue();
        double overlapY = overlap.getY().getConvertedValue();
        double contentAreaFactor = computeContentAreaFactor();
        double pageX = getNonnull(X).getConvertedValue() - insets.getLeft() * contentAreaFactor;
        double pageY = getNonnull(Y).getConvertedValue() - insets.getTop() * contentAreaFactor;
        ;
        double pageWidth = getNonnull(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageHeight = getNonnull(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
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
        Insets insets = getStyled(PAGE_INSETS).getConvertedValue();
        CssPoint2D overlap = getStyled(PAGE_OVERLAP);
        double overlapX = overlap.getX().getConvertedValue();
        double overlapY = overlap.getY().getConvertedValue();
        double contentAreaFactor = computeContentAreaFactor();
        double pageX = getNonnull(X).getConvertedValue() - insets.getLeft() * contentAreaFactor;
        double pageY = getNonnull(Y).getConvertedValue() - insets.getTop() * contentAreaFactor;
        ;
        double pageWidth = getNonnull(PAPER_WIDTH).getConvertedValue() * contentAreaFactor;
        double pageHeight = getNonnull(PAPER_HEIGHT).getConvertedValue() * contentAreaFactor;
        double pageOverlapX = (overlapX + insets.getLeft() + insets.getRight()) * contentAreaFactor;
        double pageOverlapY = (overlapY + insets.getTop() + insets.getBottom()) * contentAreaFactor;
        double x = (pageWidth - pageOverlapX) * px;
        double y = (pageHeight - pageOverlapY) * py;

        return new Translate(x, y);
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public boolean isLayoutable() {
        return true;
    }

    @Override
    public void layout(@Nonnull RenderContext ctx) {
        int currentPage = 0;
        ImmutableList<Transform> transforms = ImmutableLists.of(getPageTransform(currentPage));

        for (Figure child : getChildren()) {
            child.set(TRANSFORMS, transforms);
        }
    }

    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        set(X, width.getValue() < 0 ? x.add(width) : x);
        set(Y, height.getValue() < 0 ? y.add(height) : y);
        set(WIDTH, width.abs());
        set(HEIGHT, height.abs());
    }

    @Override
    public void reshapeInLocal(@Nonnull Transform transform) {
        Bounds newBounds = transform.transform(getBoundsInLocal());
        set(X, new CssSize(newBounds.getMinX()));
        set(Y, new CssSize(newBounds.getMinY()));
        set(WIDTH, new CssSize(newBounds.getWidth()));
        set(HEIGHT, new CssSize(newBounds.getHeight()));
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        javafx.scene.Group groupNode = (javafx.scene.Group) node;
        // We can't use #applyTransformableFigureProperties(node) because
        // this will rotate around an unpredictable center!
        node.getTransforms().setAll(getLocalToParent(true));

        Rectangle contentBoundsNode = (Rectangle) groupNode.getProperties().get(CONTENT_BOUNDS_PROPERTY);
        Path pageBoundsNode = (Path) groupNode.getProperties().get(PAGE_BOUNDS_PROPERTY);
        Path pageInsetsNode = (Path) groupNode.getProperties().get(PAGE_INSETS_PROPERTY);
        javafx.scene.Group currentPageNode = (javafx.scene.Group) groupNode.getProperties().get(CURRENT_PAGE_PROPERTY);

        applyFillableFigureProperties(ctx, pageBoundsNode);
        applyStrokableFigureProperties(ctx, pageBoundsNode);

        if (ctx.get(RenderContext.RENDERING_INTENT) == RenderingIntent.EDITOR) {
            applyHideableFigureProperties(ctx, node);
            contentBoundsNode.setVisible(true);
            pageBoundsNode.setVisible(true);
        } else if (ctx.get(RenderContext.RENDER_PAGE) == this) {
            applyHideableFigureProperties(ctx, node);
            contentBoundsNode.setVisible(false);
            pageBoundsNode.setVisible(false);
            pageInsetsNode.setVisible(false);
        } else {
            node.setVisible(false);
        }

        double contentWidth = getNonnull(WIDTH).getConvertedValue();
        double contentHeight = getNonnull(HEIGHT).getConvertedValue();
        contentBoundsNode.setX(getNonnull(X).getConvertedValue());
        contentBoundsNode.setY(getNonnull(Y).getConvertedValue());
        contentBoundsNode.setWidth(contentWidth);
        contentBoundsNode.setHeight(contentHeight);

        int numPagesX = Math.max(1, getStyled(NUM_PAGES_X).intValue());
        int numPagesY = Math.max(1, getStyled(NUM_PAGES_Y).intValue());
        final int n = numPagesX * numPagesY;
        final List<PathElement> pbList = new ArrayList<>(n * 4);
        final List<PathElement> pmList = new ArrayList<>(n * 4);
        for (int i = 0; i < n; i++) {
            addBounds(pbList, getPageBounds(i));
            addBounds(pmList, getContentBounds(i));
        }
        pageBoundsNode.getElements().setAll(pbList);
        pageInsetsNode.getElements().setAll(pmList);

        int currentPage = ctx.get(RenderContext.RENDER_PAGE_INTERNAL_NUMBER);
        currentPageNode.getTransforms().setAll(getPageTranslate(currentPage));

        List<Node> currentPageChildren = new ArrayList<>(getChildren().size() + 2);
        for (Figure child : getChildren()) {
            currentPageChildren.add(ctx.getNode(child));
        }
        ObservableList<Node> group = currentPageNode.getChildren();
        if (!group.equals(currentPageChildren)) {
            group.setAll(currentPageChildren);
        }
    }
}
