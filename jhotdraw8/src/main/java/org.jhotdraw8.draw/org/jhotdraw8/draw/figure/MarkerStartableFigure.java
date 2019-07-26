/*
 * @(#)MarkerStartableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableKey;

/**
 * A figure which supports start markers.
 *
 * @author Werner Randelshofer
 */
public interface MarkerStartableFigure extends Figure {
    /**
     * Marker start is an SVG path that points to the right, with coordinate 0,0 at the head of the path.
     */
    NullableSvgPathStyleableKey MARKER_START_SHAPE = new NullableSvgPathStyleableKey("marker-start-shape", DirtyMask.of(DirtyBits.NODE), null);
    DoubleStyleableKey MARKER_START_SCALE_FACTOR = new DoubleStyleableKey("marker-start-scale-factor", 1.0);

    @Nullable
    default String getMarkerStartShape() {
        return getStyled(MarkerStartableFigure.MARKER_START_SHAPE);
    }

    default double getMarkerStartScaleFactor() {
        return getStyledNonnull(MarkerStartableFigure.MARKER_START_SCALE_FACTOR);
    }
}
