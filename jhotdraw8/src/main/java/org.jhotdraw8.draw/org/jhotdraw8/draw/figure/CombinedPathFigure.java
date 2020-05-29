/*
 * @(#)CombinedPathFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.NullableEnumStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.ConcatenatedPathIterator;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * This is lake a group, but the shapes of the child figures are presented as a
 * unified path.
 * <p>
 * Only children which are PathIterableFigures are considered.
 *
 * @author Werner Randelshofer
 */
public class CombinedPathFigure extends AbstractCompositeFigure
        implements StrokableFigure, FillableFigure, Grouping,
        ResizableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure,
        CompositableFigure, FillRulableFigure,
        ConnectableFigure, PathIterableFigure {

    @NonNull
    public final static NullableEnumStyleableKey<CagOperation> CAG_OPERATION = new NullableEnumStyleableKey<>("cag-operation", CagOperation.class, true, null);
    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "CombinedPath";

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {

        return new Path();
    }

    @NonNull
    @Override
    public Connector findConnector(Point2D pointInLocal, Figure connectingFigure) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body ofCollection generated methods, choose Tools | Templates.
    }

    private PathIterator getStyledPathIteratorInParent(RenderContext ctx, @NonNull PathIterableFigure f, @Nullable AffineTransform tx) {
        AffineTransform childTx = tx;
        final Transform localToParent = f.getLocalToParent();
        if (localToParent != null) {
            AffineTransform ltpTx = Transforms.toAWT(localToParent);
            if (tx != null) {
                childTx = (AffineTransform) tx.clone();
                childTx.concatenate(ltpTx);
            } else {
                childTx = ltpTx;
            }
        }
        PathIterator iter = f.getPathIterator(ctx, childTx);
        if (f instanceof StrokableFigure) {
            Paint stroke = Paintable.getPaint(f.getStyled(STROKE));
            if (stroke != null) {
                double strokeWidth = f.getStyledNonNull(STROKE_WIDTH).getConvertedValue();
                if (strokeWidth > 0.0) {
                    BasicStroke basicStroke;
                    final ImmutableList<CssSize> dashArray = f.getStyledNonNull(STROKE_DASH_ARRAY);
                    if (!dashArray.isEmpty()) {
                        double dashOffset = f.getStyledNonNull(STROKE_DASH_OFFSET).getConvertedValue();
                        float[] dash = new float[dashArray.size()];
                        for (int i = 0, n = dashArray.size(); i < n; i++) {
                            dash[i] = (float) dashArray.get(i).getConvertedValue();
                        }
                        basicStroke = new BasicStroke((float) strokeWidth,
                                Shapes.awtCapFromFX(f.getStyledNonNull(STROKE_LINE_CAP)),
                                Shapes.awtJoinFromFX(f.getStyledNonNull(STROKE_LINE_JOIN)),
                                (float) f.getStyledNonNull(STROKE_MITER_LIMIT).getConvertedValue(), dash, (float) dashOffset);

                    } else {
                        basicStroke = new BasicStroke((float) strokeWidth,
                                Shapes.awtCapFromFX(f.getStyledNonNull(STROKE_LINE_CAP)),
                                Shapes.awtJoinFromFX(f.getStyledNonNull(STROKE_LINE_JOIN)),
                                (float) f.getStyledNonNull(STROKE_MITER_LIMIT).getConvertedValue());

                    }
                    iter = basicStroke.createStrokedShape(Shapes.buildFromPathIterator(new AWTPathBuilder(), iter).build()).getPathIterator(null);
                }
            }
        }
        return iter;
    }

    @NonNull
    @Override
    public PathIterator getPathIterator(RenderContext ctx, AffineTransform tx) {
        CagOperation op = getStyled(CAG_OPERATION);
        if (op != null) {
            return getPathIteratorCAG(ctx, tx, op);
        }
        List<PathIterator> iterators = new ArrayList<>();
        for (Figure child : getChildren()) {
            if (child instanceof PathIterableFigure) {
                final PathIterator childPathIterator = getStyledPathIteratorInParent(ctx, (PathIterableFigure) child, tx);
                iterators.add(childPathIterator);
            }
        }
        return new ConcatenatedPathIterator(getStyled(FILL_RULE), iterators);

    }

    @NonNull
    private PathIterator getPathIteratorCAG(RenderContext ctx, AffineTransform tx, @NonNull CagOperation op) {
        Area area = null;
        boolean first = true;
        for (Figure child : getChildren()) {
            if (child instanceof PathIterableFigure) {
                final PathIterator childPathIterator = getStyledPathIteratorInParent(ctx, (PathIterableFigure) child, tx);
                if (first) {
                    first = false;
                    area = new Area(Shapes.buildFromPathIterator(new AWTPathBuilder(), childPathIterator).build());
                } else {
                    Area area1 = new Area(Shapes.buildFromPathIterator(new AWTPathBuilder(), childPathIterator).build());
                    switch (op) {
                        case ADD:
                        default:
                            area.add(area1);
                            break;
                        case INTERSECT:
                            area.intersect(area1);
                            break;
                        case SUBTRACT:
                            area.subtract(area1);
                            break;
                        case XOR:
                            area.exclusiveOr(area1);
                            break;
                    }
                }
            }
        }
        PathIterator iter = area != null ? area.getPathIterator(null) : new ConcatenatedPathIterator(getStyled(FILL_RULE), Collections.emptyList());
        return iter;
    }

    @NonNull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    /**
     * Always returns true.
     */
    @Override
    public boolean isLayoutable() {
        return true;
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        // XXX if one ofCollection the children is non-transformable, we should not reshapeInLocal at all!
        flattenTransforms();
        Transform localTransform = transform;
        //Transform localTransform = transform.createConcatenation(getParentToLocal());
        for (Figure child : getChildren()) {
            child.reshapeInParent(localTransform);
        }
    }


    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        // XXX if one ofCollection the children is non-transformable, we should not reshapeInLocal at all!
        flattenTransforms();
        Transform localTransform = Transforms.createReshapeTransform(getCssLayoutBounds(), x, y, width, height);
        for (Figure child : getChildren()) {
            child.reshapeInParent(localTransform);
        }
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node node) {
        Path n = (Path) node;
        applyHideableFigureProperties(ctx, n);
        applyTransformableFigureProperties(ctx, n);
        applyStyleableFigureProperties(ctx, n);
        applyFillableFigureProperties(ctx, n);
        applyFillRulableFigureProperties(ctx, n);
        applyStrokableFigureProperties(ctx, n);
        applyTransformableFigureProperties(ctx, n);
        applyCompositableFigureProperties(ctx, n);

        n.getElements().setAll(Shapes.fxPathElementsFromAWT(getPathIterator(ctx, null)));
    }

    /**
     * Constructive Area Geometry Operation (CAG Operation.
     */
    public enum CagOperation {
        ADD, SUBTRACT, INTERSECT, XOR
    }


    @Override
    public boolean isSuitableParent(@NonNull Figure newParent) {
        return true;
    }

    /**
     * This method returns true for instances of PathIterableFigure.
     *
     * @param newChild The new child figure.
     * @return true if instanceof PathIterableFigure.
     */
    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return newChild instanceof PathIterableFigure;
    }
}
