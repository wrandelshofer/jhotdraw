/*
 * @(#)PlineCoincidentIntersect.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import java.awt.geom.Point2D;

/**
 * Represents a coincident polyline intersection (stretch).
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
