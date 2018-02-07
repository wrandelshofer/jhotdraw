/* @(#)StartAndEndMarkerableFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.SvgPathStyleableFigureKey;

/**
 * A figure which supports start and end markers.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface StartAndEndMarkerableFigure extends Figure {

    public final static SvgPathStyleableFigureKey MARKER_START_SHAPE = new SvgPathStyleableFigureKey("marker-start-shape", DirtyMask.of(DirtyBits.NODE), null);
    public final static SvgPathStyleableFigureKey MARKER_END_SHAPE = new SvgPathStyleableFigureKey("marker-end-shape", DirtyMask.of(DirtyBits.NODE), null);
    public final static DoubleStyleableFigureKey MARKER_START_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-start-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);
    public final static DoubleStyleableFigureKey MARKER_END_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-end-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);

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
