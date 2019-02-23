/* @(#)MidMarkerableFigure.java
 * Copyright (c) 2017 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.draw.key.DirtyBits;
import org.jhotdraw8.draw.key.DirtyMask;
import org.jhotdraw8.draw.key.DoubleStyleableFigureKey;
import org.jhotdraw8.draw.key.NullableSvgPathStyleableFigureKey;

/**
 * A figure which supports markers in the middle of a line.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface MidMarkerableFigure extends Figure {

    NullableSvgPathStyleableFigureKey MARKER_MID_SHAPE = new NullableSvgPathStyleableFigureKey("marker-mid-shape", DirtyMask.of(DirtyBits.NODE), null);
    DoubleStyleableFigureKey MARKER_MID_SCALE_FACTOR = new DoubleStyleableFigureKey("marker-mid-scale-factor", DirtyMask.of(DirtyBits.NODE), 1.0);

}
