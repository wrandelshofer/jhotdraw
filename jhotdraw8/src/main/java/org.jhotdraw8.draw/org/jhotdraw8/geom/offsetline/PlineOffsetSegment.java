package org.jhotdraw8.geom.offsetline;


import javafx.geometry.Point2D;

/**
 * Represents a raw polyline offset segment.
 */
class PlineOffsetSegment {
    final PlineVertex v1;
    final PlineVertex v2;
    final Point2D origV2Pos;
    final boolean collapsedArc;


    public PlineOffsetSegment(PlineVertex v1, PlineVertex v2, Point2D origV2Pos, boolean collapsedArc) {
        this.v1 = v1;
        this.v2 = v2;
        this.origV2Pos = origV2Pos;
        this.collapsedArc = collapsedArc;
    }
}
