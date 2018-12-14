/* @(#)SimpleBezierFigure.java
 * Copyright © by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.List;

import javafx.css.StyleOrigin;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.transform.Transform;

import javax.annotation.Nonnull;

import javafx.scene.transform.Translate;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.connector.PathConnector;
import org.jhotdraw8.draw.handle.BezierControlPointEditHandle;
import org.jhotdraw8.draw.handle.BezierNodeEditHandle;
import org.jhotdraw8.draw.handle.BezierNodeTangentHandle;
import org.jhotdraw8.draw.handle.BezierOutlineHandle;
import org.jhotdraw8.draw.handle.Handle;
import org.jhotdraw8.draw.handle.HandleType;
import org.jhotdraw8.draw.handle.PathIterableOutlineHandle;
import org.jhotdraw8.draw.key.BezierNodeListStyleableFigureKey;
import org.jhotdraw8.draw.key.BooleanStyleableFigureKey;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.locator.RelativeLocator;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePath;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

/**
 * A {@link Figure} which draws a {@link BezierNodePath}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleBezierFigure extends AbstractLeafFigure
        implements StrokableFigure, FillableFigure, TransformableFigure, HideableFigure,
        StyleableFigure, LockableFigure, CompositableFigure, ResizableFigure, ConnectableFigure,
        PathIterableFigure {

    public final static BooleanStyleableFigureKey CLOSED = new BooleanStyleableFigureKey("closed", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT_OBSERVERS), false);
    public final static BezierNodeListStyleableFigureKey PATH = new BezierNodeListStyleableFigureKey("path", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT, DirtyBits.LAYOUT_OBSERVERS), ImmutableList.emptyList());
    /**
     * The CSS type selector for this object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Bezier";

    public SimpleBezierFigure() {
        setStyled(StyleOrigin.USER_AGENT, FILL, null);
    }

    @Override
    public void createHandles(HandleType handleType, @Nonnull List<Handle> list) {
        if (handleType == HandleType.SELECT) {
            list.add(new PathIterableOutlineHandle(this, true, Handle.STYLECLASS_HANDLE_SELECT_OUTLINE));
        } else if (handleType == HandleType.POINT) {
            list.add(new BezierOutlineHandle(this, PATH, Handle.STYLECLASS_HANDLE_POINT_OUTLINE));
            ImmutableList<BezierNode> nodes = get(PATH);
            for (int i = 0, n = nodes.size(); i < n; i++) {
                list.add(new BezierNodeTangentHandle(this, PATH, i, Handle.STYLECLASS_HANDLE_CONTROL_POINT_OUTLINE));
                list.add(new BezierNodeEditHandle(this, PATH, i, Handle.STYLECLASS_HANDLE_POINT));
                if (nodes.get(i).isC1()) {
                    list.add(new BezierControlPointEditHandle(this, PATH, i, BezierNode.C1_MASK, Handle.STYLECLASS_HANDLE_CONTROL_POINT));
                }
                if (nodes.get(i).isC2()) {
                    list.add(new BezierControlPointEditHandle(this, PATH, i, BezierNode.C2_MASK, Handle.STYLECLASS_HANDLE_CONTROL_POINT));
                }
            }
        } else {
            super.createHandles(handleType, list);
        }
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext ctx) {
        return new Path();
    }

    @Nonnull
    @Override
    public Connector findConnector(@Nonnull Point2D p, Figure prototype) {
        return new PathConnector(new RelativeLocator(getBoundsInLocal(), p));
    }

    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        // XXX should be cached
        double minX = Double.POSITIVE_INFINITY;
        double minY = Double.POSITIVE_INFINITY;
        double maxX = Double.NEGATIVE_INFINITY;
        double maxY = Double.NEGATIVE_INFINITY;
        for (BezierNode p : get(PATH)) {
            minX = Math.min(minX, p.getMinX());
            minY = Math.min(minY, p.getMinY());
            maxX = Math.max(maxX, p.getMaxX());
            maxY = Math.max(maxY, p.getMaxY());
        }
        return new BoundingBox(minX, minY, maxX - minX, maxY - minY);
    }

    public CssRectangle2D getCssBoundsInLocal() {
        return new CssRectangle2D(getBoundsInLocal());
    }

    public int getNodeCount() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body ofCollection generated methods, choose Tools | Templates.
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        return new BezierNodePath(getStyled(PATH), getStyled(CLOSED), getStyled(FILL_RULE)).getPathIterator(tx);
    }

    @Nonnull
    public Point2D getPoint(int index, int coord) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body ofCollection generated methods, choose Tools | Templates.
    }

    @Nonnull
    public Point2D getPointOnPath(float f, int i) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body ofCollection generated methods, choose Tools | Templates.
    }

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        reshapeInLocal(x.getConvertedValue(), y.getConvertedValue(), width.getConvertedValue(), height.getConvertedValue());
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {
        reshapeInLocal(Transforms.createReshapeTransform(getBoundsInLocal(), x, y, width, height));
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        ArrayList<BezierNode> newP = getNonnull(PATH).toArrayList();
        for (int i = 0, n = newP.size(); i < n; i++) {
            newP.set(i, newP.get(i).transform(transform));
        }
        set(PATH, ImmutableList.ofCollection(newP));
    }

    @Override
    public void translateInLocal(CssPoint2D t) {
        Transform transform = new Translate(t.getX().getConvertedValue(), t.getY().getConvertedValue());
        reshapeInLocal(transform);
    }


    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        Path pathNode = (Path) node;

        applyHideableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
        applyStrokableFigureProperties(ctx, pathNode);
        applyFillableFigureProperties(pathNode);
        applyTransformableFigureProperties(ctx, node);
        applyCompositableFigureProperties(pathNode);
        pathNode.setFillRule(getStyled(FILL_RULE));
        final List<PathElement> elements = Shapes.fxPathElementsFromAWT(new BezierNodePath(getStyled(PATH), getStyled(CLOSED), getStyled(FILL_RULE)).getPathIterator(null));
        /*        if (getStyled(CLOSED)) {
            elements.addChild(new ClosePath());
        }*/
        if (!pathNode.getElements().equals(elements)) {
            pathNode.getElements().setAll(elements);
        }

    }

}
