/* @(#)BoundsInLocalOutlineHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.handle;

import javafx.collections.ObservableList;
import javafx.geometry.Bounds;
import javafx.scene.Cursor;
import javafx.scene.Group;
import javafx.scene.Node;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;
import org.jhotdraw8.annotation.Nonnull;
import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssColor;
import org.jhotdraw8.css.Paintable;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;
import org.jhotdraw8.draw.figure.TransformableFigure;
import org.jhotdraw8.geom.Transforms;

import static org.jhotdraw8.draw.figure.TransformableFigure.TRANSLATE_X;
import static org.jhotdraw8.draw.figure.TransformableFigure.TRANSLATE_Y;

/**
 * Draws the {@code boundsInLocal} with applied translation of a {@code Figure},
 * but does not provide any interactions.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BoundsInTranslationOutlineHandle extends AbstractHandle {
    private Group node;
    private Polygon poly1;
    private Polygon poly2;
    private double[] points;
    private String styleclass;

    public BoundsInTranslationOutlineHandle(Figure figure) {
        this(figure, STYLECLASS_HANDLE_SELECT_OUTLINE);
    }

    public BoundsInTranslationOutlineHandle(Figure figure, String styleclass) {
        super(figure);

        node = new Group();
        points = new double[8];
        poly1 = new Polygon(points);
        poly2 = new Polygon(points);
        poly2.getStrokeDashArray().setAll(2.0);
        poly1.setFill(null);
        poly2.setFill(null);
        poly1.setStrokeWidth(3);
        poly1.setStroke(Color.WHITE);
        node.getChildren().addAll(poly1, poly2);
    }

    @Override
    public boolean contains(DrawingView dv, double x, double y, double tolerance) {
        return false;
    }

    @Nullable
    @Override
    public Cursor getCursor() {
        return null;
    }

    @Override
    public Node getNode(DrawingView view) {
        CssColor color = view.getEditor().getHandleColor();
        poly2.setStroke(Paintable.getPaint(color));
        poly2.setStrokeWidth(view.getEditor().getHandleStrokeWidth());
        return node;
    }

    @Override
    public boolean isSelectable() {
        return false;
    }

    @Override
    public void updateNode(@Nonnull DrawingView view) {
        Figure f = getOwner();
        Transform t = Transforms.concat(view.getWorldToView(), f.getParentToWorld());
        if (f instanceof TransformableFigure) {
            TransformableFigure tf = (TransformableFigure) f;
            t = Transforms.concat(t, new Translate(tf.getNonnull(TRANSLATE_X), tf.getNonnull(TRANSLATE_Y)));
        }
        t = Transforms.concat(Transform.translate(0.5, 0.5), t);
        Bounds b = f.getBoundsInLocal();
        points[0] = b.getMinX();
        points[1] = b.getMinY();
        points[2] = b.getMaxX();
        points[3] = b.getMinY();
        points[4] = b.getMaxX();
        points[5] = b.getMaxY();
        points[6] = b.getMinX();
        points[7] = b.getMaxY();
        if (t != null && t.isType2D()) {
            t.transform2DPoints(points, 0, points, 0, 4);
        }

        ObservableList<Double> pp1 = poly1.getPoints();
        ObservableList<Double> pp2 = poly2.getPoints();
        for (int i = 0; i < points.length; i++) {
            pp1.set(i, points[i]);
            pp2.set(i, points[i]);
        }

    }

}
