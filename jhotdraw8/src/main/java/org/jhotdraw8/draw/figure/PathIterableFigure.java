/* @(#)PathIterableFigure.java
 * Copyright (c) 2017 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */

package org.jhotdraw8.draw.figure;

import java.awt.geom.AffineTransform;
import java.awt.geom.PathIterator;

/**
 * PathIterableFigure.
 *
 * @author Werner Randelshofer
 * @version $$Id$$
 */
public interface PathIterableFigure extends Figure {
PathIterator getPathIterator(AffineTransform tx);
}
