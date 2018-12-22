/* @(#)TextFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.jhotdraw8.annotation.Nonnull;

import org.jhotdraw8.collection.Key;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * SimpleLabelFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleLabelFigure extends AbstractLabelFigure implements HideableFigure, FontableFigure, TextableFigure, StyleableFigure, LockableFigure, TransformableFigure, CompositableFigure {
    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Label";
    public SimpleLabelFigure() {
        this(0, 0, "");
    }

    public SimpleLabelFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }
    
    public SimpleLabelFigure(double x, double y, String text, Object... keyValues) {
        set(TEXT, text);
        set(ORIGIN, new CssPoint2D(x, y));
        for (int i = 0; i < keyValues.length; i += 2) {
            @SuppressWarnings("unchecked") // the set() method will perform the check for us
            Key<Object> key = (Key<Object>) keyValues[i];
            set(key, keyValues[i + 1]);
        }
    }

    @Override
    public void updateNode(@Nonnull RenderContext ctx, @Nonnull Node node) {
        super.updateNode(ctx, node);
        applyTransformableFigureProperties(ctx, node);
        applyCompositableFigureProperties(ctx, node);
        applyStyleableFigureProperties(ctx, node);
        applyHideableFigureProperties(ctx, node);
    }

    @Override
    protected String getText(RenderContext ctx) {
        return getStyled(TEXT);
    }
    
    
    @Nonnull
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
