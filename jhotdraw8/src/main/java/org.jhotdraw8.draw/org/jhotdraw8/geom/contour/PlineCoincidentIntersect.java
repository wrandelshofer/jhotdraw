/*
 * @(#)PlineCoincidentIntersect.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import java.awt.geom.Point2D;

/**
 * Represents a coincident polyline intersection (stretch).
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
public class PlineCoincidentIntersect {
    /// Index of the start vertex of the first segment
    int sIndex1;
    /// Index of the start vertex of the second segment
    int sIndex2;
    /// One end point of the coincident path
    Point2D.Double point1;
    /// Other end point of the coincident path
    Point2D.Double point2;

    PlineCoincidentIntersect() {
    }

    PlineCoincidentIntersect(int si1, int si2, Point2D.Double point1,
                             Point2D.Double point2) {
        this.sIndex1 = si1;
        this.sIndex2 = si2;
        this.point1 = point1;
        this.point2 = point2;
    }
}
