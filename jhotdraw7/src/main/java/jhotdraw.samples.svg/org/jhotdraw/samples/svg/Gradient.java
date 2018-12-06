/* @(#)Gradient.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.samples.svg;

import org.jhotdraw.draw.Figure;

import java.awt.Paint;
import java.awt.geom.AffineTransform;

/**
 * Represents an SVG Gradient.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public interface Gradient extends Cloneable {
    public Paint getPaint(Figure f, double opacity);
    public boolean isRelativeToFigureBounds();
    public void transform(AffineTransform tx);
    public Object clone();
    public void makeRelativeToFigureBounds(Figure f);
}
