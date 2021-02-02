/*
 * @(#)CoincidentSlicesResult.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom.contour;

import java.util.ArrayDeque;
import java.util.Deque;

public class CoincidentSlicesResult {
    Deque<PolyArcPath> coincidentSlices = new ArrayDeque<>();
    Deque<PlineIntersect> sliceStartPoints = new ArrayDeque<>();
    Deque<PlineIntersect> sliceEndPoints = new ArrayDeque<>();
}
