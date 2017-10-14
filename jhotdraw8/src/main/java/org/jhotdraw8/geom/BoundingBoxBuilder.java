/* @(#)BoundingBoxBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import static java.lang.Math.max;
import static java.lang.Math.min;
import java.util.List;
import javafx.geometry.BoundingBox;
import javafx.scene.shape.ClosePath;
import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;
import javafx.scene.shape.PathElement;
import javafx.scene.shape.Rectangle;

/**
 * Builds a bounding box path.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BoundingBoxBuilder extends AbstractPathBuilder {

    private double minx = Double.POSITIVE_INFINITY, miny = Double.POSITIVE_INFINITY, maxx = Double.NEGATIVE_INFINITY, maxy = Double.NEGATIVE_INFINITY;

    @Override
    protected void doClosePath() {
        // nothing to do
    }

    private void addToBounds(double x, double y) {
        minx = min(minx, x);
        miny = min(miny, y);
        maxx = max(maxx, x);
        maxy = max(maxy, y);
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        addToBounds(x1, y1);
        addToBounds(x2, y2);
        addToBounds(x3, y3);
    }

    @Override
    protected void doLineTo(double x, double y) {
        addToBounds(x, y);
    }

    @Override
    protected void doMoveTo(double x, double y) {
        addToBounds(x, y);
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        addToBounds(x1, y1);
        addToBounds(x2, y2);
    }

    public Rectangle getRectangle() {
        if (Double.isNaN(minx)) {
            return null;
        }
        return new Rectangle(minx, miny, maxx - minx, maxy - miny);
    }

    public BoundingBox getBoundingBox() {
        if (Double.isNaN(minx)) {
            return new BoundingBox(0, 0, 0, 0);
        }
        return new BoundingBox(minx, miny, maxx - minx, maxy - miny);
    }

    public Path getPath() {
        Path p = new Path();
        addPathElementsTo(p.getElements());
        return p;
    }

    @Override
    protected void doPathDone() {
        // empty
    }

    public void addPathElementsTo(List<PathElement> elements) {
        if (Double.isNaN(minx)) {
            return;
        }
        elements.add(new MoveTo(minx, miny));
        elements.add(new LineTo(maxx, miny));
        elements.add(new LineTo(maxx, maxy));
        elements.add(new LineTo(minx, maxy));
        elements.add(new ClosePath());
    }
}
