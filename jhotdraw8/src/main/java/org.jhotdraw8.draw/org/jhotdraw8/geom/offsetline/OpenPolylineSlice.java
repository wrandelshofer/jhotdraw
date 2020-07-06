package org.jhotdraw8.geom.offsetline;

/**
 * Represents an open polyline slice of the raw offset polyline.
 */

public class OpenPolylineSlice {
    int intrStartIndex;
    Polyline pline;

    public OpenPolylineSlice() {
        pline = new Polyline();
    }

    public OpenPolylineSlice(int sIndex, Polyline slice) {
        this.intrStartIndex = sIndex;
        this.pline = slice.clone();
    }
}
