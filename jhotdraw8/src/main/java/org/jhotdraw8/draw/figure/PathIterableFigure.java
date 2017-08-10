/* @(#)PathIterableFigure.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * PathIterableFigure.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface PathIterableFigure extends Figure {
PathIterator getPathIterator(AffineTransform tx);
}
