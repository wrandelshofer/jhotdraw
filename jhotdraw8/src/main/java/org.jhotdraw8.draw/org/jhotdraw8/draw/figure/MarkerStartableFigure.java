/*
 * @(#)MarkerStartableFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.NonNull;
import org.jhotdraw8.annotation.Nullable;
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
    @NonNull NullableSvgPathStyleableKey MARKER_START_SHAPE = new NullableSvgPathStyleableKey("marker-start-shape", null);
    @NonNull DoubleStyleableKey MARKER_START_SCALE_FACTOR = new DoubleStyleableKey("marker-start-scale-factor", 1.0);

    default @Nullable String getMarkerStartShape() {
        return getStyled(MarkerStartableFigure.MARKER_START_SHAPE);
    }

    default double getMarkerStartScaleFactor() {
        return getStyledNonNull(MarkerStartableFigure.MARKER_START_SCALE_FACTOR);
    }
}
