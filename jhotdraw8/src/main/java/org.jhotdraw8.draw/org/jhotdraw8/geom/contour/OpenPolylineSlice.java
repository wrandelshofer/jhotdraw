/*
 * @(#)OpenPolylineSlice.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

/**
 * Represents an open polyline slice of the raw offset polyline.
 * <p>
 * This code has been derived from Cavalier Contours [1].
 * <p>
 * References:
 * <dl>
 *     <dt>[1] Cavalier Contours</dt>
 *     <dd>Cavalier Contours, Copyright (c) 2019 Jedidiah Buck McCready, MIT License.
 *     <a href="https://github.com/jbuckmccready/CavalierContours">github.com</a></dd>
 * </dl>
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
