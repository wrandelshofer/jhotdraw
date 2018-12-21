/* @(#)StartAndEndMarkerableFigure.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import javax.annotation.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.NullableDoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableFigureKey;

/**
 * A figure which supports start and end markers.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface StartAndEndMarkerableFigure extends Figure {

    NullableSvgPathStyleableFigureKey MARKER_START_SHAPE = new NullableSvgPathStyleableFigureKey("marker-start-shape", DirtyMask.of(DirtyBits.NODE), null);
    NullableSvgPathStyleableFigureKey MARKER_END_SHAPE = new NullableSvgPathStyleableFigureKey("marker-end-shape", DirtyMask.of(DirtyBits.NODE), null);
    NullableDoubleStyleableFigureKey MARKER_START_SCALE_FACTOR = new NullableDoubleStyleableFigureKey("marker-start-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);
    NullableDoubleStyleableFigureKey MARKER_END_SCALE_FACTOR = new NullableDoubleStyleableFigureKey("marker-end-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);

    @Nullable
    default String getMarkerStartShape() {
        return getStyled(MARKER_START_SHAPE);
    }

    default double getMarkerStartScaleFactor() {
        return getStyled(MARKER_START_SCALE_FACTOR);
    }

    @Nullable
    default String getMarkerEndShape() {
        return getStyled(MARKER_END_SHAPE);
    }

    default double getMarkerEndScaleFactor() {
        return getStyled(MARKER_END_SCALE_FACTOR);
    }
}
