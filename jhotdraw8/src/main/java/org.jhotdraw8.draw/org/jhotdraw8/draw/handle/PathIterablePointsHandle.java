/*
 * @(#)PathIterablePointsHandle.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.geom.FXPathPointsBuilder;
import org.jhotdraw8.geom.FXTransforms;
import org.jhotdraw8.geom.Shapes;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws points of the path of a {@link PathIterableFigure}.
 * <p>
 * Does not provide interactions. Just increases the hit area of the figure.
 *
 * @author Werner Randelshofer
 */
public class PathIterablePointsHandle extends AbstractHandle {

    private final @NonNull Path node;
    private final boolean selectable;

    public PathIterablePointsHandle(PathIterableFigure figure, boolean selectable) {
        super(figure);
        node = new Path();
        initNode(node);
        this.selectable = selectable;
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return node.contains(x, y);
    }

    @Override
    public @Nullable Cursor getCursor() {
        return null;
    }

    @Override
    public @NonNull Node getNode(@NonNull DrawingView view) {
        CssColor color = view.getEditor().getHandleColor();
        node.setStroke(Paintable.getPaint(color));
        return node;
    }

    protected void initNode(@NonNull Path r) {
        r.setFill(null);
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Override
    public @NonNull PathIterableFigure getOwner() {
        return (PathIterableFigure) super.getOwner();
    }

    @Override
    public void updateNode(@NonNull DrawingView view) {
        PathIterableFigure f = getOwner();
        Transform t = FXTransforms.concat(view.getWorldToView(), f.getLocalToWorld());
        List<PathElement> elements = new ArrayList<>();
        FXPathPointsBuilder builder = new FXPathPointsBuilder(elements);
        Shapes.buildFromPathIterator(builder, f.getPathIterator(view, Shapes.awtTransformFromFX(t)));

        node.getElements().setAll(builder.getElements());
    }

}
