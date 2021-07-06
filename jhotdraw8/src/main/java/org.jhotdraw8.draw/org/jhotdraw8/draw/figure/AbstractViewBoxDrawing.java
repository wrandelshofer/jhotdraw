/*
 * @(#)AbstractViewBoxDrawing.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.layout.Pane;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.UnitConverter;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * Abstract drawing that supports {@link ViewBoxableDrawing}.
 */
public abstract class AbstractViewBoxDrawing extends AbstractDrawing implements ViewBoxableDrawing {
    public AbstractViewBoxDrawing() {
    }

    public AbstractViewBoxDrawing(double width, double height) {
        super(width, height);
    }

    public AbstractViewBoxDrawing(CssSize width, CssSize height) {
        super(width, height);
    }

    @Override
    public @NonNull Transform getLocalToParent() {
        return Transform.translate(-getStyledNonNull(VIEW_BOX_X).getConvertedValue(),
                -getStyledNonNull(VIEW_BOX_Y).getConvertedValue());
    }

    @Override
    public @NonNull Transform getParentToLocal() {
        return Transform.translate(getStyledNonNull(VIEW_BOX_X).getConvertedValue(),
                getStyledNonNull(VIEW_BOX_Y).getConvertedValue());
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        super.updateNode(ctx, n);
        final UnitConverter unitConverter = ctx.getNonNull(RenderContext.UNIT_CONVERTER_KEY);
        final double x = getStyledNonNull(VIEW_BOX_X).getConvertedValue(unitConverter);
        final double y = getStyledNonNull(VIEW_BOX_Y).getConvertedValue(unitConverter);

        Group gg = (Group) ((Pane) n).getChildrenUnmodifiable().get(0);
        gg.setTranslateX(-x);
        gg.setTranslateY(-y);
    }

}
