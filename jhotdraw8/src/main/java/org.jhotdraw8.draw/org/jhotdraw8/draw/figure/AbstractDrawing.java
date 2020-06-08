/*
 * @(#)AbstractDrawing.java
 * Copyright © 2020 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javafx.collections.ObservableList;
import javafx.css.StyleOrigin;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.css.SimpleStylesheetsManager;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.draw.css.FigureSelectorModel;
import org.jhotdraw8.draw.render.RenderContext;

import java.util.ArrayList;
import java.util.List;

/**
 * DrawingFigure.
 *
 * @author Werner Randelshofer
 */
public abstract class AbstractDrawing extends AbstractCompositeFigure
        implements Drawing, StyleableFigure, LockableFigure, NonTransformableFigure {

    /**
     * The style manager is created lazily.
     */
    @Nullable
    private StylesheetsManager<Figure> styleManager = null;

    public AbstractDrawing() {
    }

    public AbstractDrawing(double width, double height) {
        this(new CssSize(width), new CssSize(height));

    }

    public AbstractDrawing(CssSize width, CssSize height) {
        set(WIDTH, width);
        set(HEIGHT, height);
    }

    @NonNull
    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setManaged(false);
        Rectangle background = new Rectangle();
        background.setId("background");
        g.getProperties().put("background", background);
        return g;
    }

    @NonNull
    protected StylesheetsManager<Figure> createStyleManager() {
        return new SimpleStylesheetsManager<>(new FigureSelectorModel());
    }

    /**
     * The bounds of this drawing is determined by its {@code WIDTH} and and
     * {@code HEIGHT}.
     * <p>
     * The bounds of its child figures does not affect the bounds of this
     * drawing.
     *
     * @return bounding box (0, 0, WIDTH, HEIGHT).
     */
    @NonNull
    @Override
    public CssRectangle2D getCssLayoutBounds() {
        return new CssRectangle2D(CssSize.ZERO, CssSize.ZERO, getNonNull(WIDTH), getNonNull(HEIGHT));
    }

    @NonNull
    @Override
    public Bounds getLayoutBounds() {
        // Note: We must override getBoundsInLocal of AbstractCompositeFigure.
        return getCssLayoutBounds().getConvertedBoundsValue();
    }

    @Nullable
    @Override
    public StylesheetsManager<Figure> getStyleManager() {
        if (styleManager == null) {
            styleManager = createStyleManager();
            updateStyleManager();
        }
        return styleManager;
    }

    @Override
    public void updateStyleManager() {
        if (styleManager != null) {
            styleManager.setStylesheets(StyleOrigin.USER_AGENT, get(DOCUMENT_HOME), get(USER_AGENT_STYLESHEETS).asList());
            styleManager.setStylesheets(StyleOrigin.AUTHOR, get(DOCUMENT_HOME), get(AUTHOR_STYLESHEETS).asList());
            styleManager.setStylesheets(StyleOrigin.INLINE, get(INLINE_STYLESHEETS).asList());
        }
    }

    @Override
    public void reshapeInLocal(@NonNull Transform transform) {
        Bounds b = getLayoutBounds();
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshapeInLocal(@NonNull CssSize x, @NonNull CssSize y, @NonNull CssSize width, @NonNull CssSize height) {
        set(WIDTH, width.abs());
        set(HEIGHT, height.abs());
    }

    @Override
    public void stylesheetNotify(@NonNull RenderContext ctx) {
        if (styleManager != null) {
            styleManager.setStylesheets(StyleOrigin.USER_AGENT, get(DOCUMENT_HOME), get(USER_AGENT_STYLESHEETS).asList());
            styleManager.setStylesheets(StyleOrigin.AUTHOR, get(DOCUMENT_HOME), get(AUTHOR_STYLESHEETS).asList());
            styleManager.setStylesheets(StyleOrigin.INLINE, get(INLINE_STYLESHEETS).asList());
        }
        super.stylesheetNotify(ctx);
    }

    @Override
    public void updateNode(@NonNull RenderContext ctx, @NonNull Node n) {
        Group g = (Group) n;
        //applyTransformableFigureProperties(n);
        applyStyleableFigureProperties(ctx, n);

        Bounds bounds = getLayoutBounds();
        Rectangle page = (Rectangle) g.getProperties().get("background");
        page.setX(bounds.getMinX());
        page.setY(bounds.getMinY());
        page.setWidth(bounds.getWidth());
        page.setHeight(bounds.getHeight());
        CssColor cclr = getStyled(BACKGROUND);
        page.setFill(Paintable.getPaint(cclr, ctx));
        if (g.getClip() == null || !g.getClip().getBoundsInLocal().equals(bounds)) {
            g.setClip(new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
        }

        List<Node> nodes = new ArrayList<>(getChildren().size());
        nodes.add(page);
        for (Figure child : getChildren()) {
            nodes.add(ctx.getNode(child));
        }
        ObservableList<Node> group = g.getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }

    @Override
    public boolean isSuitableParent(@NonNull Figure newParent) {
        return true;
    }


}
