/*
 * @(#)SvgLinearGradientFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.svg.figure;

import javafx.collections.ObservableList;
import javafx.scene.Node;
import javafx.scene.paint.Paint;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.collection.NonNullKey;
import org.jhotdraw8.collection.NonNullObjectKey;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.figure.AbstractCompositeFigure;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.Grouping;
import org.jhotdraw8.draw.figure.HideableFigure;
import org.jhotdraw8.draw.figure.LockableFigure;
import org.jhotdraw8.draw.figure.NonTransformableFigure;
import org.jhotdraw8.draw.figure.ResizableFigure;
import org.jhotdraw8.draw.figure.StyleableFigure;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.render.RenderContext;
import org.jhotdraw8.svg.text.SvgGradientUnits;

import java.util.ArrayList;
import java.util.List;

/**
 * Represents an SVG 'linearGradient' element.
 *
 * @author Werner Randelshofer
 */
public class SvgLinearGradientFigure extends AbstractCompositeFigure
        implements Grouping, ResizableFigure, NonTransformableFigure, HideableFigure, StyleableFigure, LockableFigure,
        SvgDefaultableFigure,
        SvgElementFigure, Paintable {

    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public static final String TYPE_SELECTOR = "linearGradient";
    public static final @NonNull DoubleStyleableKey X1 = new DoubleStyleableKey("x1", 0);
    public static final @NonNull DoubleStyleableKey Y1 = new DoubleStyleableKey("y1", 0);
    public static final @NonNull DoubleStyleableKey X2 = new DoubleStyleableKey("x2", 1);
    public static final @NonNull DoubleStyleableKey Y2 = new DoubleStyleableKey("y1", 0);
    public static final @NonNull NonNullKey<SvgGradientUnits> GRADIENT_UNITS =
            new NonNullObjectKey<>("gradientUnits", SvgGradientUnits.class,

                    SvgGradientUnits.OBJECT_BOUNDING_BOX);

    public SvgLinearGradientFigure() {
        set(VISIBLE, false);
    }

    @Override
    public @NonNull Node createNode(RenderContext drawingView) {
        javafx.scene.Group g = new javafx.scene.Group();
        g.setAutoSizeChildren(false);
        g.setManaged(false);
        return g;
    }

    @Override
    public @Nullable Paint getPaint() {
        return null;
    }

    @Override
    public @NonNull String getTypeSelector() {
        return TYPE_SELECTOR;
    }


    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        applyHideableFigureProperties(ctx, n);
        applyStyleableFigureProperties(ctx, n);
        applySvgDefaultableCompositingProperties(ctx, n);

        List<Node> nodes = new ArrayList<>(getChildren().size());
        for (Figure child : getChildren()) {
            nodes.add(ctx.getNode(child));
        }
        ObservableList<Node> group = ((javafx.scene.Group) n).getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }

    @Override
    public boolean isSuitableParent(@NonNull Figure newParent) {
        return true;
    }


    @Override
    public boolean isSuitableChild(@NonNull Figure newChild) {
        return true;
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        // does nothing
    }
}
