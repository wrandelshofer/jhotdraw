/* @(#)BoundsInLocalHandle.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */
package org.jhotdraw8.draw.handle;

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
import org.jhotdraw8.collection.ImmutableObservableList;
import org.jhotdraw8.collection.MapAccessor;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.SimpleDrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.geom.BezierNode;
import org.jhotdraw8.geom.BezierNodePath;
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

    private final MapAccessor<ImmutableObservableList<BezierNode>> key;

    private Path node;
    private String styleclass;

    public BezierOutlineHandle(Figure figure, MapAccessor<ImmutableObservableList<BezierNode>> key) {
        this(figure, key, STYLECLASS_HANDLE_MOVE_OUTLINE);
    }

    public BezierOutlineHandle(Figure figure, MapAccessor<ImmutableObservableList<BezierNode>> key, String styleclass) {
        super(figure);
        this.key = key;
        node = new Path();
        this.styleclass = styleclass;
        initNode(node);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return false;
    }

    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void handleMouseClicked(MouseEvent event, DrawingView dv) {

        // FIXME implement me
        if (key != null && event.getClickCount() == 2) {
            double px = event.getX();
            double py = event.getY();
            double tolerance = SimpleDrawingView.TOLERANCE;

            Point2D pInDrawing = dv.viewToWorld(new Point2D(px, py));
            pInDrawing = dv.getConstrainer().constrainPoint(owner, pInDrawing);
            Point2D pInLocal = owner.worldToLocal(pInDrawing);
            //dv.getModel().set(owner, key, ImmutableObservableList.add(owner.get(key), insertAt, pInLocal));
            dv.recreateHandles();
        }
    }

    protected void initNode(Path r) {
        r.setFill(null);
        r.setStroke(Color.BLUE);
        r.getStyleClass().addAll(styleclass, STYLECLASS_HANDLE);
    }

    @Override
    public boolean isSelectable() {
        return true;
    }

    @Override
    public void updateNode(DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getLocalToWorld());
        Bounds b = getOwner().getBoundsInLocal();
        final ImmutableObservableList<BezierNode> nodes = f.getStyled(key);
        final BezierNodePath bnp = new BezierNodePath(nodes);
        List<PathElement> elements = Shapes.fxPathElementsFromAWT(bnp.getPathIterator(Transforms.toAWT(t)));
        node.getElements().setAll(elements);
    }

}
