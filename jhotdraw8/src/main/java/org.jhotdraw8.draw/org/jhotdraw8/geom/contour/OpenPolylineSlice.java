package org.jhotdraw8.geom.contour;

/**
 * Represents an open polyline slice of the raw offset polyline.
 */

public class OpenPolylineSlice {
    final int intrStartIndex;
    final PolyArcPath pline;

    public OpenPolylineSlice(int startIndex) {
        this.pline = new PolyArcPath();
        this.intrStartIndex = startIndex;
    }

    /**
     * Creates a new instance with a clone of the specified polyline slice.
     *
     * @param sIndex start index
     * @param slice  a polyline slice (will be cloned)
     */
    public OpenPolylineSlice(int sIndex, PolyArcPath slice) {
        this.intrStartIndex = sIndex;
        this.pline = slice.clone();
    }

    @Override
    public String toString() {
        return "OpenPolylineSlice{" +
                "startIndex=" + intrStartIndex +
                ", pline=" + pline +
                '}';
    }
}
