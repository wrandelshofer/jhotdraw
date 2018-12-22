/* @(#)PathIterableFigure.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;
import org.jhotdraw8.annotation.Nullable;

/**
 * PathIterableFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface PathIterableFigure extends Figure {
 PathIterator getPathIterator(@Nullable AffineTransform tx);
}
