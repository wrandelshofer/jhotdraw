/*
 * @(#)IntrPlineSegsResult.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;


import java.awt.geom.Point2D;

/**
 * Represents the result of
 * {@link ContourIntersections#intrPlineSegs(PlineVertex, PlineVertex, PlineVertex, PlineVertex)}.
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
public class IntrPlineSegsResult {
    PlineSegIntrType intrType;
    Point2D.Double point1;
    Point2D.Double point2;
}

