/* @(#)EndMarkerableFigure.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableFigureKey;

/**
 * A figure which supports end markers.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface EndMarkerableFigure extends Figure {

    NullableSvgPathStyleableFigureKey MARKER_END_SHAPE = new NullableSvgPathStyleableFigureKey("marker-end-shape", DirtyMask.of(DirtyBits.NODE), null);
    DoubleStyleableFigureKey MARKER_END_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-end-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);

    @Nullable
    default String getMarkerEndShape() {
        return getStyled(EndMarkerableFigure.MARKER_END_SHAPE);
    }

    default double getMarkerEndScaleFactor() {
        return getStyled(EndMarkerableFigure.MARKER_END_SCALE_FACTOR);
    }

}
