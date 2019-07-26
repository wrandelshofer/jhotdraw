/*
 * @(#)MarkerEndableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableKey;

/**
 * A figure which supports end markers.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface MarkerEndableFigure extends Figure {

    /**
     * Marker end is an SVG path that points to the right, with coordinate 0,0 at the tail of the path.
     */
    NullableSvgPathStyleableKey MARKER_END_SHAPE = new NullableSvgPathStyleableKey("marker-end-shape", DirtyMask.of(DirtyBits.NODE), null);
    DoubleStyleableKey MARKER_END_SCALE_FACTOR = new DoubleStyleableKey("marker-end-scale-factor", 1.0);

    @Nullable
    default String getMarkerEndShape() {
        return getStyled(MARKER_END_SHAPE);
    }

    default double getMarkerEndScaleFactor() {
        return getStyledNonnull(MARKER_END_SCALE_FACTOR);
    }

}
