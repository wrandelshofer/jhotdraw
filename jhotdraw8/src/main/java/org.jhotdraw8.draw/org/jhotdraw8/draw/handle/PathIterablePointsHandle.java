/*
 * @(#)PathIterablePointsHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.geom.FXPathPointsBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws points of the path of a {@link PathIterableFigure}.
 * <p>
 * Does not provide interactions. Just increases the hit area of the figure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PathIterablePointsHandle extends AbstractHandle {

    @Nonnull
    private final Path node;
    private final String styleclass;
    private final boolean selectable;

    public PathIterablePointsHandle(PathIterableFigure figure, boolean selectable) {
        this(figure, selectable, STYLECLASS_HANDLE_MOVE);
    }

    public PathIterablePointsHandle(PathIterableFigure figure, boolean selectable, String styleclass) {
        super(figure);
        node = new Path();
        this.styleclass = styleclass;
        initNode(node);
        this.selectable = selectable;
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return node.contains(x, y);
    }

    @Nullable
    @Override
    public Cursor getCursor() {
        return null;
    }

    @Nonnull
    @Override
    public Node getNode(DrawingView view) {
        CssColor color = view.getEditor().getHandleColor();
        node.setStroke(Paintable.getPaint(color));
        return node;
    }

    protected void initNode(@Nonnull Path r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        //r.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
    }

    @Override
    public boolean isSelectable() {
        return selectable;
    }

    @Nonnull
    @Override
    public PathIterableFigure getOwner() {
        return (PathIterableFigure) super.getOwner();
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        PathIterableFigure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        List<PathElement> elements = new ArrayList<>();
        FXPathPointsBuilder builder = new FXPathPointsBuilder(elements);
        Shapes.buildFromPathIterator(builder, f.getPathIterator(Shapes.awtTransformFromFX(t)));

        node.getElements().setAll(builder.getElements());
    }

}
