/* @(#)BoundsInLocalHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import static java.lang.Math.sqrt;
import java.util.List;
import javafx.geometry.Bounds;
import javafx.geometry.Point2D;
import javafx.scene.Cursor;
import javafx.scene.Node;
import javafx.scene.input.MouseEvent;
import javafx.scene.paint.Color;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.transform.Transform;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;

import org.jhotdraw8.collection.ImmutableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.SimpleBezierFigure;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePath;
import org.jhotdraw8.geom.Intersection;
import org.jhotdraw8.geom.Intersections;
import org.jhotdraw8.geom.Shapes;
import org.jhotdraw8.geom.Transforms;

/**
 * Draws the {@code wireframe} of a {@code PolylineFigure}.
 * <p>
 * The user can insert a new point by double clicking the line.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierOutlineHandle extends AbstractHandle {

    private final MapAccessor<ImmutableList<BezierNode>> key;

    private Path node;
    private String styleclass;

    public BezierOutlineHandle(SimpleBezierFigure figure, MapAccessor<ImmutableList<BezierNode>> key) {
        this(figure, key, STYLECLASS_HANDLE_MOVE_OUTLINE);
    }

    public BezierOutlineHandle(SimpleBezierFigure figure, MapAccessor<ImmutableList<BezierNode>> key, String styleclass) {
        super(figure);
        this.key = key;
        node = new Path();
        //this.styleclass = styleclass;
        initNode(node);
    }

    @Override
    public boolean contains(@Nonnull DrawingView dv, double x, double y, double toleranceSquared) {
        return contains(dv, x, y, sqrt(toleranceSquared), toleranceSquared);

    }

    @Override
    public boolean contains(@Nonnull DrawingView dv, double x, double y, double tolerance, double toleranceSquared) {
        final SimpleBezierFigure o = getOwner();
        Point2D localp = Transforms.concat(dv.getViewToWorld(), o.getWorldToLocal()).transform(x, y);
        Intersection isect = Intersections.intersectPathIteratorCircle(o.getPathIterator(null),
                localp.getX(), localp.getY(), tolerance);
        return !isect.isEmpty();
    }

    @Nullable
    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public Node getNode(DrawingView view) {
        CssColor color=view.getHandleColor();
        node.setStroke(color.getColor());
        return node;
    }

    @Nonnull
    @Override
    public SimpleBezierFigure getOwner() {
        return (SimpleBezierFigure) super.getOwner();
    }

    @Override
    public void handleMouseClicked(@Nonnull MouseEvent event, @Nonnull DrawingView dv) {
        if (key != null && event.getClickCount() == 2) {
            double px = event.getX();
            double py = event.getY();

            Point2D pInDrawing = dv.viewToWorld(new Point2D(px, py));
            double tolerance = dv.getViewToWorld().deltaTransform(dv.getTolerance(), dv.getTolerance()).getX();
            // pInDrawing = dv.getConstrainer().constrainPoint(owner, pInDrawing);
            Point2D localp = owner.worldToLocal(pInDrawing);
            final SimpleBezierFigure o = getOwner();

            final ImmutableList<BezierNode> nodes = o.get(key);
            BezierNodePath path = new BezierNodePath(nodes);
            if (path.split(localp.getX(), localp.getY(), tolerance)) {
                dv.getModel().set(o, key, ImmutableList.ofCollection(path.getNodes()));
            }
            dv.recreateHandles();
        }
    }

    protected void initNode(@Nonnull Path r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        //r.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = getOwner().getBoundsInLocal();
        final ImmutableList<BezierNode> nodes = f.getStyled(key);
        final BezierNodePath bnp = new BezierNodePath(nodes);
        List<PathElement> elements = Shapes.fxPathElementsFromAWT(bnp.getPathIterator(Transforms.toAWT(t)));
        node.getElements().setAll(elements);
    }

}
