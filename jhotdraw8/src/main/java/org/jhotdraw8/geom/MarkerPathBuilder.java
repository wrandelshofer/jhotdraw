/* @(#)PathWithMarkersBuilder.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.geom.Path2D;
import javafx.scene.transform.Transform;
import javafx.scene.transform.Translate;

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
        doStartMarker(x1, y1);
        tangentX = x2;
        tangentY = y2;
        needsEndMarker = true;
    }

    @Override
    protected void doFinish() {
        doEndMarker();
        out.finish();
    }

    @Override
    protected void doLineTo(double x, double y) {
        doStartMarker(x, y);
        tangentX = getLastX();
        tangentY = getLastY();
        needsEndMarker = true;
    }

    private void doStartMarker(double x, double y) {
        if (needsStartMarker) {
            needsStartMarker = false;
            if (startMarker == null) {
                return;
            }
            double x0 = getLastX();
            double y0 = getLastY();
            Transform tx = Transforms.rotate(x0 - x, y0 - y, 0, 0).createConcatenation(new Translate(x, y));
            Shapes.buildFromPathIterator(out, startMarker.getPathIterator(Transforms.toAWT(tx)));
        }
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
        doStartMarker(x1, y1);
        tangentX = x1;
        tangentY = y1;
        needsEndMarker = true;
    }

}
