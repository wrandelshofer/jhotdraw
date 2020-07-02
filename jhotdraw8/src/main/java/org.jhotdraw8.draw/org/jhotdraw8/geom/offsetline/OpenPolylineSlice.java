package org.jhotdraw8.geom.offsetline;

/**
 * Represents an open polyline slice of the raw offset polyline.
 */

public class OpenPolylineSlice {
    int intrStartIndex;
    PolyArcPath pline;

    public OpenPolylineSlice() {
        pline = new PolyArcPath();
    }

    public OpenPolylineSlice(int sIndex, PolyArcPath slice) {
        this.intrStartIndex = sIndex;
        this.pline = slice.clone();
    }
}
