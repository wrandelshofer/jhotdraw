package org.jhotdraw8.geom.contour;

import java.util.ArrayDeque;
import java.util.Deque;

public class CoincidentSlicesResult {
    Deque<PolyArcPath> coincidentSlices = new ArrayDeque<>();
    Deque<PlineIntersect> sliceStartPoints = new ArrayDeque<>();
    Deque<PlineIntersect> sliceEndPoints = new ArrayDeque<>();
}
