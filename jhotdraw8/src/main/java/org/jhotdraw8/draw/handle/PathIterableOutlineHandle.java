/* @(#)PathIterableOutlineHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import java.util.ArrayList;
import java.util.List;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.transform.Transform;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.PathIterableFigure;
import org.jhotdraw8.geom.FXPathBuilder;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

/**
 * Draws an outline of the path of a {@link PathIterableFigure}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class PathIterableOutlineHandle extends AbstractHandle {

    @Nonnull
    private final Path node;
    private final String styleclass;
    private final boolean selectable;

    public PathIterableOutlineHandle(PathIterableFigure figure, boolean selectable) {
        this(figure,selectable, STYLECLASS_HANDLE_MOVE_OUTLINE);
    }

    public PathIterableOutlineHandle(PathIterableFigure figure, boolean selectable, String styleclass) {
        super(figure);
        node = new Path();
        this.styleclass = styleclass;
        initNode(node);
        this.selectable=selectable;
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return node.contains(x,y);
    }

    @Nullable
    @Override
    public Cursor getCursor() {
        return null;
    }

    @Nonnull
    @Override
    public Node getNode() {
        return node;
    }

    protected void initNode(@Nonnull Path r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        r.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
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
        node.getElements().setAll(elements);
    }

}
