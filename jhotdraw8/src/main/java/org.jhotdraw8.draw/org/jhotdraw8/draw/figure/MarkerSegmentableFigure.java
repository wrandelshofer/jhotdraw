/*
 * @(#)MarkerSegmentableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
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
    NullableSvgPathStyleableKey MARKER_SEGMENT_SHAPE = new NullableSvgPathStyleableKey("marker-segment-shape", DirtyMask.of(DirtyBits.NODE), null);
    DoubleStyleableKey MARKER_SEGMENT_SCALE_FACTOR = new DoubleStyleableKey("marker-segment-scale-factor", 1.0);

    @Nullable
    default String getMarkerSegmentShape() {
        return getStyled(MARKER_SEGMENT_SHAPE);
    }

    default double getMarkerSegmentScaleFactor() {
        return getStyledNonnull(MARKER_SEGMENT_SCALE_FACTOR);
    }

}
