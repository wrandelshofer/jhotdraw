/*
 * @(#)MarkerEndableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableKey;

/**
 * A figure which supports end markers.
 *
 * @author Werner Randelshofer
 */
public interface MarkerEndableFigure extends Figure {

    /**
     * Marker end is an SVG path that points to the right, with coordinate 0,0 at the tail of the path.
     */
    @Nullable NullableSvgPathStyleableKey MARKER_END_SHAPE = new NullableSvgPathStyleableKey("marker-end-shape", null);
    DoubleStyleableKey MARKER_END_SCALE_FACTOR = new DoubleStyleableKey("marker-end-scale-factor", 1.0);

    @Nullable
    default String getMarkerEndShape() {
        return getStyled(MARKER_END_SHAPE);
    }

    default double getMarkerEndScaleFactor() {
        return getStyledNonNull(MARKER_END_SCALE_FACTOR);
    }

}
