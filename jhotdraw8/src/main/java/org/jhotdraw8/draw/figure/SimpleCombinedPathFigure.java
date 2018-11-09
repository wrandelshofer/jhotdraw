/* @(#)SimpleCombinedPathFigure.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.Path;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.AWTPathBuilder;
import org.jhotdraw8.geom.ConcatenatedPathIterator;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

/**
 * This is lake a group, but the shapes of the child figures are presented as a
 * unified path.
 * <p>
 * Only children which are PathIterableFigures are considered.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleCombinedPathFigure extends AbstractCompositeFigure
        implements StrokeableFigure, FillableFigure, Grouping,
        ResizableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure,
        CompositableFigure,
        ConnectableFigure, PathIterableFigure {

    @Nonnull
    public final static EnumStyleableFigureKey<CagOperation> CAG_OPERATION = new EnumStyleableFigureKey<>("cag-operation", CagOperation.class, DirtyMask.of(DirtyBits.NODE), true, null);
    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "CombinedPath";

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {

        return new Path();
    }

    @Nonnull
    @Override
    public Connector findConnector(Point2D pointInLocal, Figure connectingFigure) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body ofCollection generated methods, choose Tools | Templates.
    }

    private PathIterator getStyledPathIteratorInParent(PathIterableFigure f, @Nullable AffineTransform tx) {
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
        PathIterator iter = f.getPathIterator(childTx);
        if (f instanceof StrokeableFigure) {
            Paint stroke = Paintable.getPaint(f.getStyled(STROKE));
            if (stroke != null) {
                double strokeWidth = f.getStyledNonnull(STROKE_WIDTH).getConvertedValue();
                if (strokeWidth > 0.0) {
                    BasicStroke basicStroke;
                    final ImmutableList<CssSize> dashArray = f.getStyledNonnull(STROKE_DASH_ARRAY);
                    if (!dashArray.isEmpty()) {
                        double dashOffset = f.getStyledNonnull(STROKE_DASH_OFFSET).getConvertedValue();
                        float[] dash = new float[dashArray.size()];
                        for (int i = 0, n = dashArray.size(); i < n; i++) {
                            dash[i] = (float)dashArray.get(i).getConvertedValue();
                        }
                        basicStroke = new BasicStroke((float) strokeWidth,
                                Shapes.awtCapFromFX(f.getStyledNonnull(STROKE_LINE_CAP)),
                                Shapes.awtJoinFromFX(f.getStyledNonnull(STROKE_LINE_JOIN)),
                                (float)f.getStyledNonnull(STROKE_MITER_LIMIT).getConvertedValue(), dash, (float) dashOffset);

                    } else {
                        basicStroke = new BasicStroke((float) strokeWidth,
                                Shapes.awtCapFromFX(f.getStyledNonnull(STROKE_LINE_CAP)),
                                Shapes.awtJoinFromFX(f.getStyledNonnull(STROKE_LINE_JOIN)),
                                (float)f.getStyledNonnull(STROKE_MITER_LIMIT).getConvertedValue());

                    }
                    iter = basicStroke.createStrokedShape(Shapes.buildFromPathIterator(new AWTPathBuilder(), iter).build()).getPathIterator(null);
                }
            }
        }
        return iter;
    }

    @Nonnull
    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        CagOperation op = getStyled(CAG_OPERATION);
        if (op != null) {
            return getPathIteratorCAG(tx, op);
        }
        List<PathIterator> iterators = new ArrayList<>();
        for (Figure child : getChildren()) {
            if (child instanceof PathIterableFigure) {
                final PathIterator childPathIterator = getStyledPathIteratorInParent((PathIterableFigure) child, tx);
                iterators.add(childPathIterator);
            }
        }
        return new ConcatenatedPathIterator(getStyled(FILL_RULE), iterators);

    }

    @Nonnull
    private PathIterator getPathIteratorCAG(AffineTransform tx, @Nonnull CagOperation op) {
        Area area = null;
        boolean first = true;
        for (Figure child : getChildren()) {
            if (child instanceof PathIterableFigure) {
                final PathIterator childPathIterator = getStyledPathIteratorInParent((PathIterableFigure) child, tx);
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

    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    /** Always returns true. */
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
    public void reshapeInLocal(@Nonnull CssSize x, @Nonnull CssSize y, @Nonnull CssSize width, @Nonnull CssSize height) {
        // XXX if one ofCollection the children is non-transformable, we should not reshapeInLocal at all!
        flattenTransforms();
        Transform localTransform = Transforms.createReshapeTransform(getCssBoundsInLocal(), x, y, width, height);
        for (Figure child : getChildren()) {
            child.reshapeInParent(localTransform);
        }
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, Node node) {
        Path n = (Path) node;
        applyHideableFigureProperties(n);
        applyTransformableFigureProperties(n);
        applyStyleableFigureProperties(ctx, n);
        applyFillableFigureProperties(n);
        applyStrokeableFigureProperties(n);
        applyTransformableFigureProperties(n);
        applyCompositableFigureProperties(n);

        n.getElements().setAll(Shapes.fxPathElementsFromAWT(getPathIterator(null)));
    }

    /**
     * Constructive Area Geometry Operation (CAG Operation.
     */
    public static enum CagOperation {
        ADD, SUBTRACT, INTERSECT, XOR
    }

}
