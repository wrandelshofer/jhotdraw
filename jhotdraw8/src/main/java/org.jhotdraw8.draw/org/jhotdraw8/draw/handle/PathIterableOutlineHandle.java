/*
 * @(#)PathIterableOutlineHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.scene.Cursor;
import javafx.scene.Group;
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
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

import java.util.ArrayList;
import java.util.List;

/**
 * Draws an outline of the path of a {@link PathIterableFigure}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PathIterableOutlineHandle extends AbstractHandle {

    private final Group node;
    private final Path path2;
    private final Path path1;
    private final String styleclass;
    private final boolean selectable;

    public PathIterableOutlineHandle(PathIterableFigure figure, boolean selectable) {
        this(figure, selectable, STYLECLASS_HANDLE_MOVE_OUTLINE);
    }

    public PathIterableOutlineHandle(PathIterableFigure figure, boolean selectable, String styleclass) {
        super(figure);
        node = new Group();
        path2 = new Path();
        path1 = new Path();
        node.getChildren().addAll(path1, path2);
        this.styleclass = styleclass;
        this.selectable = selectable;
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return path1.contains(x, y);
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
        path1.setStroke(Color.WHITE);
        path2.setStroke(Paintable.getPaint(color));
        int strokeWidth = view.getEditor().getHandleStrokeWidth();
        path1.setStrokeWidth(strokeWidth + 2);
        path2.setStrokeWidth(strokeWidth);
        return node;
    }

    @Override
    public boolean isEditable() {
        return false;
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
        FXPathBuilder builder = new FXPathBuilder(elements);
        Shapes.buildFromPathIterator(builder, f.getPathIterator(Shapes.awtTransformFromFX(t)));
        path1.getElements().setAll(elements);
        path2.getElements().setAll(elements);
    }

}
