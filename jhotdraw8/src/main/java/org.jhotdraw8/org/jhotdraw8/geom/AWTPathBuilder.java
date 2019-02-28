/* @(#)AWTDoublePathBuilder.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.geom.Path2D;

/**
 * Builds an AWT {@code Path2D}.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class AWTPathBuilder extends AbstractPathBuilder {

    private Path2D path;

    public AWTPathBuilder() {
        this(new Path2D.Double());
    }

    public AWTPathBuilder(Path2D path) {
        this.path = path;
    }

    @Override
    protected void doClosePath() {
        path.closePath();
    }

    @Override
    protected void doCurveTo(double x, double y, double x0, double y0, double x1, double y1) {
        path.curveTo(x, y, x0, y0, x1, y1);
    }

    @Override
    protected void doLineTo(double x, double y) {
        path.lineTo(x, y);
    }

    @Override
    protected void doMoveTo(double x, double y) {
        path.moveTo(x, y);
    }

    @Override
    protected void doQuadTo(double x, double y, double x0, double y0) {
        path.quadTo(x, y, x0, y0);
    }

    public Path2D build() {
        pathDone();
        return path;
    }

    @Override
    protected void doPathDone() {
        // empty
    }

}
