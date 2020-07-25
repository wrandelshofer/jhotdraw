package org.jhotdraw8.geom.contour;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a collection of intersects found.
 */
public class PlineIntersectsResult {
    List<PlineIntersect> intersects = new ArrayList<>();
    List<PlineCoincidentIntersect> coincidentIntersects = new ArrayList<>();

    @Override
    public String toString() {
        return "PlineIntersectsResult{" +
                "intersects=" + intersects +
                ", coincidentIntersects=" + coincidentIntersects +
                '}';
    }
}
