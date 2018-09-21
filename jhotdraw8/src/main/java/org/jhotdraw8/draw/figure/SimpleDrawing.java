/* @(#)SimpleDrawing.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import static java.lang.Math.abs;
import java.util.ArrayList;
import java.util.List;
import javafx.collections.ObservableList;
import javafx.css.StyleOrigin;
import javafx.geometry.BoundingBox;
import javafx.geometry.Bounds;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.shape.Rectangle;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.css.SimpleStylesheetsManager;
import org.jhotdraw8.css.StylesheetsManager;
import org.jhotdraw8.draw.css.FigureSelectorModel;
import org.jhotdraw8.draw.key.CssColor;
import org.jhotdraw8.draw.key.Paintable;
import org.jhotdraw8.draw.render.RenderContext;

/**
 * SimpleDrawing.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class SimpleDrawing extends AbstractCompositeFigure
        implements Drawing, StyleableFigure, LockableFigure, NonTransformableFigure {

    /**
     * The style manager is created lazily.
     */
    @Nullable
    private StylesheetsManager<Figure> styleManager = null;

    public SimpleDrawing() {
    }

    public SimpleDrawing(double width, double height) {
        set(WIDTH, width);
        set(HEIGHT, height);
    }

    @Nonnull
    @Override
    public Node createNode(RenderContext drawingView) {
        Group g = new Group();
        g.setManaged(false);
        Rectangle background = new Rectangle();
        background.setId("background");
        g.getProperties().put("background", background);
        return g;
    }

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
    @Nonnull
    @Override
    public Bounds getBoundsInLocal() {
        return new BoundingBox(0.0, 0.0, get(WIDTH), get(HEIGHT));

    }

    @Nullable
    @Override
    public StylesheetsManager<Figure> getStyleManager() {
        if (styleManager == null) {
            styleManager = createStyleManager();
            styleManager.setStylesheets(StyleOrigin.USER_AGENT, get(DOCUMENT_HOME), get(USER_AGENT_STYLESHEETS));
            styleManager.setStylesheets(StyleOrigin.AUTHOR, get(DOCUMENT_HOME), get(AUTHOR_STYLESHEETS));
            styleManager.setStylesheets(StyleOrigin.INLINE, get(INLINE_STYLESHEETS));
        }
        return styleManager;
    }

    @Override
    public void reshapeInLocal(@Nonnull Transform transform) {
        Bounds b = getBoundsInLocal();
        b = transform.transform(b);
        reshapeInLocal(b.getMinX(), b.getMinY(), b.getWidth(), b.getHeight());
    }

    @Override
    public void reshapeInLocal(double x, double y, double width, double height) {

        set(WIDTH, abs(width));
        set(HEIGHT, abs(height));
    }

    @Override
    public void stylesheetNotify() {
        if (styleManager != null) {
            styleManager.setStylesheets(StyleOrigin.USER_AGENT, get(DOCUMENT_HOME), get(USER_AGENT_STYLESHEETS));
            styleManager.setStylesheets(StyleOrigin.AUTHOR, get(DOCUMENT_HOME), get(AUTHOR_STYLESHEETS));
            styleManager.setStylesheets(StyleOrigin.INLINE, get(INLINE_STYLESHEETS));
        }
        super.stylesheetNotify();
    }

    @Override
    public void updateNode(@Nonnull RenderContext v, Node n) {
        Group g = (Group) n;
        //applyTransformableFigureProperties(n);
        applyStyleableFigureProperties(v, n);

        Bounds bounds = getBoundsInLocal();
        Rectangle page = (Rectangle) g.getProperties().get("background");
        page.setX(bounds.getMinX());
        page.setY(bounds.getMinY());
        page.setWidth(bounds.getWidth());
        page.setHeight(bounds.getHeight());
        CssColor cclr = getStyled(BACKGROUND);
        page.setFill(Paintable.getPaint(cclr));
        if (g.getClip() == null || !g.getClip().getBoundsInLocal().equals(bounds)) {
            g.setClip(new Rectangle(bounds.getMinX(), bounds.getMinY(), bounds.getWidth(), bounds.getHeight()));
        }

        List<Node> nodes = new ArrayList<Node>(getChildren().size());
        nodes.add(page);
        for (Figure child : getChildren()) {
            nodes.add(v.getNode(child));
        }
        ObservableList<Node> group = g.getChildren();
        if (!group.equals(nodes)) {
            group.setAll(nodes);
        }
    }

}
