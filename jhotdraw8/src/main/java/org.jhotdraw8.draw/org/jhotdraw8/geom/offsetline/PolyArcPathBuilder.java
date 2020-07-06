package org.jhotdraw8.geom.offsetline;

import org.jhotdraw8.geom.AbstractPathBuilder;

import java.util.ArrayList;
import java.util.List;

/**
 * Path builder for PolyArcPath.
 * <p>
 * References:
 *  <ul>
 *   <li>Bulge conversions: http://www.lee-mac.com/bulgeconversion.html</li>
 * </ul>
 *  </p>
 */
public class PolyArcPathBuilder extends AbstractPathBuilder {
    private final List<Polyline> paths = new ArrayList<>();
    private Polyline current;

    public List<Polyline> getPaths() {
        return paths;
    }

    @Override
    protected void doClosePath() {
        if (current != null) {
            current.isClosed(true);
        }
    }

    @Override
    protected void doPathDone() {
        if (current != null) {
            paths.add(current);
            current = null;
        }
    }

    @Override
    protected void doCurveTo(double x1, double y1, double x2, double y2, double x, double y) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doLineTo(double x, double y) {
        if (current == null) {
            current = new Polyline();
        }
        current.addVertex(x, y);
    }

    @Override
    protected void doMoveTo(double x, double y) {
        if (current != null) {
            paths.add(current);
        }
        current = new Polyline();
        current.addVertex(x, y);
    }

    @Override
    protected void doQuadTo(double x1, double y1, double x, double y) {
        throw new UnsupportedOperationException();
    }

    @Override
    protected void doArcTo(double radiusX, double radiusY, double xAxisRotation, double x, double y, boolean largeArcFlag, boolean sweepFlag) {
        if (radiusX == radiusY) {

        } else {
            super.doArcTo(radiusX, radiusY, xAxisRotation, x, y, largeArcFlag, sweepFlag);
        }
    }
}
