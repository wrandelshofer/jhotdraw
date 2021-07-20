/*
 * @(#)CoincidentSlicesResult.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import java.util.ArrayDeque;
import java.util.Deque;
import java.util.List;

/**
 * Represents the result of
 * {@link ContourIntersections#sortAndJoinCoincidentSlices(List, PolyArcPath, PolyArcPath)}.
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
public class CoincidentSlicesResult {
    Deque<PolyArcPath> coincidentSlices = new ArrayDeque<>();
    Deque<PlineIntersect> sliceStartPoints = new ArrayDeque<>();
    Deque<PlineIntersect> sliceEndPoints = new ArrayDeque<>();
}
