/* @(#)SimpleCombinedPathFigure.java
 * Copyright © 2017 by the authors and contributors ofCollection JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.transform.Transform;
import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.key.Paintable;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.ConcatenatedPathIterator;
import org.jhotdraw8.geom.AWTDoublePathBuilder;
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

    public final static EnumStyleableFigureKey<CagOperation> CAG_OPERATION = new EnumStyleableFigureKey<>("cag-operation", CagOperation.class, DirtyMask.of(DirtyBits.NODE), true, null);
    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "CombinedPath";

    @Override
    public Node createNode(RenderContext drawingView) {

        return new Path();
    }

    @Override
    public Connector findConnector(Point2D pointInLocal, Figure connectingFigure) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body ofCollection generated methods, choose Tools | Templates.
    }

    private PathIterator getStyledPathIteratorInParent(PathIterableFigure f, AffineTransform tx) {
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
                double strokeWidth = f.getStyled(STROKE_WIDTH);
                if (strokeWidth > 0.0) {
                    BasicStroke basicStroke;
                    final ImmutableList<Double> dashArray = f.getStyled(STROKE_DASH_ARRAY);
                    if (dashArray != null && !dashArray.isEmpty()) {
                        double dashOffset = f.getStyled(STROKE_DASH_OFFSET);
                        float[] dash = new float[dashArray.size()];
                        boolean allZero = false;
                        for (int i = 0, n = dashArray.size(); i < n; i++) {
                            dash[i] = dashArray.get(i).floatValue();
                            allZero = allZero && dash[i] == 0f;
                        }
                        if (allZero) {
                            dash = null;
                        }
                        basicStroke = new BasicStroke((float) strokeWidth, Shapes.awtCapFromFX(f.getStyled(STROKE_LINE_CAP)),
                                Shapes.awtJoinFromFX(f.getStyled(STROKE_LINE_JOIN)), f.getStyled(STROKE_MITER_LIMIT).floatValue(), dash, (float) dashOffset);

                    } else {
                        basicStroke = new BasicStroke((float) strokeWidth, Shapes.awtCapFromFX(f.getStyled(STROKE_LINE_CAP)),
                                Shapes.awtJoinFromFX(f.getStyled(STROKE_LINE_JOIN)), f.getStyled(STROKE_MITER_LIMIT).floatValue());

                    }
                    iter = basicStroke.createStrokedShape(Shapes.buildFromPathIterator(new AWTDoublePathBuilder(), iter).get()).getPathIterator(null);
                }
            }
        }
        return iter;
    }

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

    private PathIterator getPathIteratorCAG(AffineTransform tx, CagOperation op) {
        Area area = null;
        boolean first = true;
        for (Figure child : getChildren()) {
            if (child instanceof PathIterableFigure) {
                final PathIterator childPathIterator = getStyledPathIteratorInParent((PathIterableFigure) child, tx);
                if (first) {
                    first = false;
                    area = new Area(Shapes.buildFromPathIterator(new AWTDoublePathBuilder(), childPathIterator).get());
                } else {
                    Area area1 = new Area(Shapes.buildFromPathIterator(new AWTDoublePathBuilder(), childPathIterator).get());
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
    public void reshapeInLocal(double x, double y, double width, double height) {
        // XXX if one ofCollection the children is non-transformable, we should not reshapeInLocal at all!
        flattenTransforms();
        Transform localTransform = Transforms.createReshapeTransform(getBoundsInLocal(), x, y, width, height);
        //Transform localTransform = transform.createConcatenation(getParentToLocal());
        for (Figure child : getChildren()) {
            child.reshapeInParent(localTransform);
        }
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
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
