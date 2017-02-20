/* @(#)CombinedPathFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.figure;

import java.awt.BasicStroke;
import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javafx.geometry.Point2D;
import javafx.scene.Node;
import javafx.scene.shape.FillRule;
import javafx.scene.shape.Path;
import javafx.scene.shape.StrokeLineCap;
import javafx.scene.shape.StrokeLineJoin;
import javafx.scene.transform.Transform;
import org.jhotdraw8.draw.connector.Connector;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.EnumStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.geom.CombinedPathIterator;
import org.jhotdraw8.geom.Path2DDoubleBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

/**
 * This is lake a group, but the shapes of the child figures are presented as a
 * unified path.
 * <p>
 * Only children which are PathIterableFigures are considered.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public class CombinedPathFigure extends AbstractCompositeFigure
        implements StrokeableFigure, FillableFigure, Grouping,
        ResizableFigure, TransformableFigure, HideableFigure, StyleableFigure, LockableFigure,
        CompositableFigure,
        ConnectableFigure, PathIterableFigure {

    /**
     * The CSS type selector for group objects is @code("group"}.
     */
    public final static String TYPE_SELECTOR = "CombinedPath";
    public final static EnumStyleableFigureKey<FillRule> FILL_RULE = BezierFigure.FILL_RULE;
    public static DoubleStyleableFigureKey COMBINE_STROKE_WIDTH = new DoubleStyleableFigureKey("combine-stroke-width", DirtyMask.of(DirtyBits.NODE), 0.0);
    public static EnumStyleableFigureKey<StrokeLineCap> COMBINE_STROKE_LINE_CAP = new EnumStyleableFigureKey<>("combine-stroke-linecap", StrokeLineCap.class, DirtyMask.of(DirtyBits.NODE), StrokeLineCap.BUTT);
    public static EnumStyleableFigureKey<StrokeLineJoin> COMBINE_STROKE_LINE_JOIN = new EnumStyleableFigureKey<>("combine-stroke-linejoin", StrokeLineJoin.class, DirtyMask.of(DirtyBits.NODE), StrokeLineJoin.MITER);
    public static DoubleStyleableFigureKey COMBINE_STROKE_MITER_LIMIT = new DoubleStyleableFigureKey("combine-stroke-miterlimit", DirtyMask.of(DirtyBits.NODE), 4.0);

    public static enum Combination {
    APPEND,UNION,SUBTRACT,INTERSECT
}
    public final static EnumStyleableFigureKey<Combination> COMBINATION_MODE = new EnumStyleableFigureKey<>("combination-mode", Combination.class, DirtyMask.of(DirtyBits.NODE), Combination.APPEND);
    
    @Override
    public Connector findConnector(Point2D pointInLocal, Figure connectingFigure) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public PathIterator getPathIterator(AffineTransform tx) {
        List<PathIterator> iterators = new ArrayList<>();
        for (Figure child : getChildren()) {
            if (child instanceof PathIterableFigure) {
                final PathIterableFigure pathIterable = (PathIterableFigure) child;
                AffineTransform childTx =tx;
            final Transform localToParent = child.getLocalToParent();
                if (localToParent != null) {               
                    AffineTransform ltpTx = Transforms.toAWT(localToParent);             
                    if (tx != null) {
                        childTx=(AffineTransform)tx.clone();
                        childTx.concatenate(ltpTx);
                    } else {
                        childTx = ltpTx;
                    }
                }                
        
                iterators.add(pathIterable.getPathIterator(childTx));
            }
        }
        PathIterator iter= new CombinedPathIterator(getStyled(FILL_RULE), iterators);
        double combineStrokeWidth=getStyled(COMBINE_STROKE_WIDTH);
        if (combineStrokeWidth>0) {
            int cap;
            int join;
            switch (getStyled(COMBINE_STROKE_LINE_CAP)) {
                case BUTT:default:
                    cap=BasicStroke.CAP_BUTT;break;
                    case ROUND: cap=BasicStroke.CAP_ROUND;break;
                    case SQUARE:cap=BasicStroke.CAP_SQUARE;break;
            }
            switch (getStyled(COMBINE_STROKE_LINE_JOIN)) {
                default:
                case BEVEL:join=BasicStroke.JOIN_BEVEL;break;
                case MITER:join=BasicStroke.JOIN_MITER;break;
                case ROUND:join=BasicStroke.JOIN_ROUND;break;
            }
            BasicStroke stroke=new BasicStroke((float)combineStrokeWidth,cap,join,getStyled(COMBINE_STROKE_MITER_LIMIT).floatValue());
            Path2DDoubleBuilder builder=new Path2DDoubleBuilder();
            try {
                Shapes.buildFromPathIterator(builder, iter);

            } catch (IOException ex) {
                throw new InternalError(ex);
            }
          iter=  stroke.createStrokedShape(builder.get()).getPathIterator(null);
        }
        return iter;
    }

    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }

    @Override
    public Node createNode(RenderContext drawingView) {

        return new Path();
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
        n.setFillRule(getStyled(FILL_RULE));

        n.getElements().setAll(Shapes.fxPathElementsFromAWT(getPathIterator(null)));
    }

    @Override
    public void reshapeInLocal(Transform transform) {
        // XXX if one of the children is non-transformable, we should not reshapeInLocal at all!
        flattenTransforms();
        Transform localTransform = transform;
        //Transform localTransform = transform.createConcatenation(getParentToLocal());
        for (Figure child : getChildren()) {
            child.reshapeInParent(localTransform);
        }
    }

}
