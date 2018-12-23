/* @(#)RotateHandle.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.handle;

import org.jhotdraw.draw.Figure;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

/**
 * A Handle to rotate a Figure.
 *
 * @author Werner Randelshofer.
 * @version $Id$
 */
public class RotateHandle extends AbstractRotateHandle {
	
    /** Creates a new instance. */
    public RotateHandle(Figure owner) {
        super(owner);
    }
    
    @Override
    protected Point2D.Double getCenter() {
        Rectangle2D.Double bounds = getTransformedBounds();
    	return new Point2D.Double(bounds.getCenterX(), bounds.getCenterY());
    }

    @Override
    protected Point2D.Double getOrigin() {
        // This handle is placed above the figure.
        // We move it up by a handlesizes, so that it won't overlap with
        // the handles from TransformHandleKit.
        Rectangle2D.Double bounds = getTransformedBounds();
        Point2D.Double origin = new Point2D.Double(bounds.getCenterX(),
                bounds.y - getHandlesize() / view.getScaleFactor());
        return origin;
    }
}
