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
 * A figure which supports markers at the nodes of a path.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface MarkerMidableFigure extends Figure {
    /**
     * "Marker Mid" is an SVG path that points to the right, with coordinate 0,0 at a node of the path.
     */
    NullableSvgPathStyleableFigureKey MARKER_MID_SHAPE = new NullableSvgPathStyleableFigureKey("marker-mid-shape", DirtyMask.of(DirtyBits.NODE), null);
    DoubleStyleableFigureKey MARKER_MID_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-mid-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);

    @Nullable
    default String getMarkerMidShape() {
        return getStyled(MARKER_MID_SHAPE);
    }

    default double getMarkerMidScaleFactor() {
        return getStyledNonnull(MARKER_MID_SCALE_FACTOR);
    }
}
