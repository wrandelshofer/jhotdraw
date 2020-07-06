package org.jhotdraw8.geom.offsetline;

import java.util.ArrayList;
import java.util.List;

/**
 * Holds a collection of intersects found.
 */
public class PlineIntersectsResult {
    List<PlineIntersect> intersects = new ArrayList<>();
    List<PlineCoincidentIntersect> coincidentIntersects = new ArrayList<>();
}
