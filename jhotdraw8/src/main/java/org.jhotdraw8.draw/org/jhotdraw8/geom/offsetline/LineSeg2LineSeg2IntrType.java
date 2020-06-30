package org.jhotdraw8.geom.offsetline;

public enum LineSeg2LineSeg2IntrType {
    // no intersect (segments are parallel and not collinear)
    None,
    // true intersect between line segments
    True,
    // segments overlap each other by some amount
    Coincident,
    // false intersect between line segments (one or both of the segments must be extended)
    False
}
