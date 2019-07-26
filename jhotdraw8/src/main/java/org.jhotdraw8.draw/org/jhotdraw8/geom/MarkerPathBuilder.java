/*
 * @(#)MarkerPathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

import java.awt.geom.Path2D;

/**
 * MarkerPathBuilder. Places markers at the start, end and middle of the path.
 * The path itself is not included by the builder.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class MarkerPathBuilder extends AbstractPathBuilder {

    private final Path2D.Double startMarker;
    private final Path2D.Double endMarker;
    private final Path2D.Double midMarker;// FIXME support midMarker
    private PathBuilder out;
    private boolean needsStartMarker;
    private boolean needsEndMarker;
    private double tangentX, tangentY;

    public MarkerPathBuilder(PathBuilder out, Path2D.Double startMarker, Path2D.Double endMarker, Path2D.Double midMarker) {
        this.startMarker = startMarker;
        this.endMarker = endMarker;
        this.midMarker = midMarker;
        this.out = out;
    }

    @Override
    protected void doClosePath() {
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x3, double y3) {
        doStartOrMidMarker(x1, y1);
        tangentX = x2;
        tangentY = y2;
        needsEndMarker = true;
    }

    @Override
    protected void doPathDone() {
        doEndMarker();
        out.pathDone();
    }

    @Override
    protected void doLineTo(double x, double y) {
        doStartOrMidMarker(x, y);
        tangentX = getLastX();
        tangentY = getLastY();
        needsEndMarker = true;
    }

    private void doStartOrMidMarker(double x, double y) {
        final Path2D.Double marker;
        if (needsStartMarker) {
            needsStartMarker = false;
            marker = startMarker;
        } else {
            marker = midMarker;
        }

        if (marker == null) {
            return;
        }
        final double x0 = getLastX();
        final double y0 = getLastY();
        final Transform tx = Transforms.rotate(x0 - x, y0 - y, 0, 0).createConcatenation(new Translate(x, y));
        Shapes.buildFromPathIterator(out, marker.getPathIterator(Transforms.toAWT(tx)));
    }

    private void doEndMarker() {
        if (needsEndMarker) {
            needsEndMarker = false;
            if (endMarker == null) {
                return;
            }
            double x = getLastX();
            double y = getLastY();
            double x0 = tangentX;
            double y0 = tangentY;
            Transform tx = Transforms.rotate(x0 - x, y0 - y, x, y).createConcatenation(new Translate(x, y));
            Shapes.buildFromPathIterator(out, startMarker.getPathIterator(Transforms.toAWT(tx)));
        }
    }

    @Override
    protected void doMoveTo(double x, double y) {
        needsStartMarker = true;
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x2, double y2) {
        doStartOrMidMarker(x1, y1);
        tangentX = x1;
        tangentY = y1;
        needsEndMarker = true;
    }

}
