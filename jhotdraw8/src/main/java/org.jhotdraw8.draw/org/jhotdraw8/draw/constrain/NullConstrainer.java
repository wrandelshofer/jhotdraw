/*
 * @(#)NullConstrainer.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.constrain;

import javafx.geometry.Point2D;
import javafx.geometry.Rectangle2D;
import javafx.scene.Node;
import javafx.scene.shape.Path;
import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.css.CssPoint2D;
import org.jhotdraw8.css.CssRectangle2D;
import org.jhotdraw8.draw.DrawingView;
import org.jhotdraw8.draw.figure.Figure;

/**
 * NullConstrainer does not constrain anything.
 *
 * @author Werner Randelshofer
 */
public class NullConstrainer extends AbstractConstrainer {

    private final Path node = new Path();

    @NonNull
    @Override
    public CssPoint2D translatePoint(Figure f, @NonNull CssPoint2D cssp, @NonNull CssPoint2D cssdir) {
        Point2D p = cssp.getConvertedValue();
        Point2D dir = cssdir.getConvertedValue();
        return new CssPoint2D(p.add(dir));
    }

    @NonNull
    @Override
    public CssRectangle2D translateRectangle(Figure f, @NonNull CssRectangle2D cssr, @NonNull CssPoint2D cssdir) {
        Rectangle2D r = cssr.getConvertedValue();
        Point2D dir = cssdir.getConvertedValue();
        return new CssRectangle2D(r.getMinX() + dir.getX(), r.getMinY() + dir.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public double translateAngle(Figure f, double angle, double dir) {
        return angle + dir;
    }

    @NonNull
    @Override
    public Node getNode() {
        return node;
    }

    @Override
    public void updateNode(DrawingView drawingView) {
        // empty
    }

}
