/*
 * @(#)MarkerSegmentableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableKey;

/**
 * A figure which supports markers in the middle of a path segment.
 *
 * @author Werner Randelshofer
 */
public interface MarkerSegmentableFigure extends Figure {
    /**
     * "Marker Segment" is an SVG path that points to the right, with coordinate 0,0 at the center of a path segment.
     */
    @Nullable NullableSvgPathStyleableKey MARKER_SEGMENT_SHAPE = new NullableSvgPathStyleableKey("marker-segment-shape", null);
    DoubleStyleableKey MARKER_SEGMENT_SCALE_FACTOR = new DoubleStyleableKey("marker-segment-scale-factor", 1.0);

    default @Nullable String getMarkerSegmentShape() {
        return getStyled(MARKER_SEGMENT_SHAPE);
    }

    default double getMarkerSegmentScaleFactor() {
        return getStyledNonNull(MARKER_SEGMENT_SCALE_FACTOR);
    }

}
