/* @(#)StartMarkerableFigure.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableFigureKey;

/**
 * A figure which supports start markers.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface StartMarkerableFigure extends Figure {

    NullableSvgPathStyleableFigureKey MARKER_START_SHAPE = new NullableSvgPathStyleableFigureKey("marker-start-shape", DirtyMask.of(DirtyBits.NODE), null);
    DoubleStyleableFigureKey MARKER_START_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-start-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);

    @Nullable
    default String getMarkerStartShape() {
        return getStyled(StartMarkerableFigure.MARKER_START_SHAPE);
    }

    default double getMarkerStartScaleFactor() {
        return getStyled(StartMarkerableFigure.MARKER_START_SCALE_FACTOR);
    }
}
