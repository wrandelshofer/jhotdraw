/* @(#)MarkerMidableFigure.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableFigureKey;

/**
 * A figure which supports markers in the middle of a path segment.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface MarkerSegmentableFigure extends Figure {
    /**
     * "Marker Segment" is an SVG path that points to the right, with coordinate 0,0 at the center of a path segment.
     */
    NullableSvgPathStyleableFigureKey MARKER_SEGMENT_SHAPE = new NullableSvgPathStyleableFigureKey("marker-segment-shape", DirtyMask.of(DirtyBits.NODE), null);
    DoubleStyleableFigureKey MARKER_SEGMENT_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-segment-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);

    @Nullable
    default String getMarkerSegmentShape() {
        return getStyled(MARKER_SEGMENT_SHAPE);
    }

    default double getMarkerSegmentScaleFactor() {
        return getStyledNonnull(MARKER_SEGMENT_SCALE_FACTOR);
    }

}
