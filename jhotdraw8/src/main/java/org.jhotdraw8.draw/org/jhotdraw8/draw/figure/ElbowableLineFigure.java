/*
 * @(#)ElbowableLineFigure.java
 * Copyright © 2021 The authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.draw.figure;

import org.jhotdraw8.annotation.Nullable;
import org.jhotdraw8.css.CssSize;
import org.jhotdraw8.draw.key.NullableCssSizeStyleableKey;

/**
 * A figure which supports drawing an elbow.
 *
 * @author Werner Randelshofer
 */
public interface ElbowableLineFigure extends Figure {

    /**
     * The offset of the elbow with respect of the end of the line.
     */
    @Nullable NullableCssSizeStyleableKey ELBOW_OFFSET = new NullableCssSizeStyleableKey("elbowOffset", CssSize.ZERO);

    /**
     * The offset of the elbow from the end of the line.
     * <p>
     * If the value is null, or less or equal 0, then a straight line is drawn instead of an elbow.
     *
     * @return an offset
     */
    @Nullable
    default CssSize getElbowOffset() {
        return getStyled(ELBOW_OFFSET);
    }

}
