/* @(#)SimplePageLabelFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.geometry.Point2D;
import javafx.scene.Node;
import org.jhotdraw8.collection.Key;
import static org.jhotdraw8.draw.figure.AbstractLabelFigure.ORIGIN;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.StringStyleableFigureKey;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * SimplePageLabelFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimplePageLabelFigure extends AbstractLabelFigure implements HideableFigure, FontableFigure, StyleableFigure, LockableFigure, TransformableFigure, CompositableFigure {
 public final static String TYPE_SELECTOR = "PageLabel";
    public final static String NUM_PAGES_PLACEHOLDER = "${numPages}";
    public final static String PAGE_PLACEHOLDER = "${page}";
    /**
     * The text. Default value: {@code ""}.
     */
    public final static StringStyleableFigureKey TEXT_WITH_PLACEHOLDERS = new StringStyleableFigureKey("text", DirtyMask.of(DirtyBits.NODE, DirtyBits.LAYOUT), "",
            "Supported placeholders:  " + PAGE_PLACEHOLDER + ", " + NUM_PAGES_PLACEHOLDER);

    public SimplePageLabelFigure() {
        this(0, 0, "");
    }

    public SimplePageLabelFigure(Point2D position, String text) {
        this(position.getX(), position.getY(), text);
    }

    public SimplePageLabelFigure(double x, double y, String text, Object... keyValues) {
        set(TEXT_WITH_PLACEHOLDERS, text);
        set(ORIGIN, new Point2D(x, y));
        for (int i = 0; i < keyValues.length; i += 2) {
            @SuppressWarnings("unchecked") // the set() method will perform the check for us
            Key<Object> key = (Key<Object>) keyValues[i];
            set(key, keyValues[i + 1]);
        }
    }

    @Override
    protected String getText(RenderContext ctx) {
        String text = get(TEXT_WITH_PLACEHOLDERS);
        final Integer pageNumber = ctx==null?0:ctx.get(RenderContext.RENDER_PAGE_NUMBER);
        final Integer numPages =ctx==null?0:ctx.get(RenderContext.RENDER_NUMBER_OF_PAGES);

        if (pageNumber != null) {
            text = replaceAll(text, PAGE_PLACEHOLDER, "" + (pageNumber + 1));
        }
        if (numPages != null) {
            text = replaceAll(text, NUM_PAGES_PLACEHOLDER, "" + numPages);
        }

        return text;
    }

    private String replaceAll(String text, String placeholder, String replace) {
        for (int p = text.indexOf(placeholder); p != -1; p = text.indexOf(placeholder)) {
            text = text.substring(0, p) + replace + text.substring(p + placeholder.length());
        }
        return text;
    }

    @Override
    public void updateNode(RenderContext ctx, Node node) {
        super.updateNode(ctx, node);
        applyTransformableFigureProperties(node);
        applyCompositableFigureProperties(node);
        applyStyleableFigureProperties(ctx, node);
        applyHideableFigureProperties(node);                
    }
    @Override
    public String getTypeSelector() {
        return TYPE_SELECTOR;
    }
}
