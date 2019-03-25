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
 * LabelFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class LabelFigure extends AbstractLabelFigure
        implements HideableFigure, TextFontableFigure, TextLayoutableFigure, TextableFigure, StyleableFigure, LockableFigure, TransformableFigure,
        CompositableFigure, TextEditableFigure {
    /**
     * The CSS type selector for a label object is {@value #TYPE_SELECTOR}.
     */
    public final static String TYPE_SELECTOR = "Label";

    public LabelFigure() {
        this(0, 0, "");
    }

    public LabelFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public LabelFigure(double x, double y, String text, Object... keyValues) {
        set(TEXT, text);
        set(ORIGIN, new CssPoint2D(x, y));
        for (int i = 0; i < keyValues.length; i += 2) {
            @SuppressWarnings("unchecked") // the set() method will perform the check for us
                    Key<Object> key = (Key<Object>) keyValues[i];
            set(key, keyValues[i + 1]);
        }
    }

    @Override
    public TextEditorData getTextEditorDataFor(Point2D pointInLocal, Node node) {
        return new TextEditorData(this, getBoundsInLocal(), TEXT);
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
