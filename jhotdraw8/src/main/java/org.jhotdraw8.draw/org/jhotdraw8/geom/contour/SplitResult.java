package org.jhotdraw8.geom.contour;

/// Result of splitting a segment v1 to v2.
public class SplitResult {
    /// Updated starting vertex.
    PlineVertex updatedStart;
    /// Vertex at the split point.
    PlineVertex splitVertex;
};