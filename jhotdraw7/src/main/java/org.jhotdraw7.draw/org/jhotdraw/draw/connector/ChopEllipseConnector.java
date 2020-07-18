/* @(#)ChopEllipseConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.connector;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.Geom;

import java.awt.Color;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.jhotdraw.draw.AttributeKeys.STROKE_COLOR;
import static org.jhotdraw.draw.AttributeKeys.STROKE_PLACEMENT;
import static org.jhotdraw.draw.AttributeKeys.STROKE_WIDTH;
import static org.jhotdraw.draw.AttributeKeys.getStrokeTotalWidth;
/**
 * A {@link Connector} which locates a connection point at the bounds
 * of any figure which has an elliptic shape, such as {@link org.jhotdraw.draw.EllipseFigure}.
 * <p>
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class ChopEllipseConnector extends ChopRectangleConnector {
    private static final long serialVersionUID = 1L;
    /** 
     * Only used for DOMStorable input.
     */
    public ChopEllipseConnector() {
    }
    
    public ChopEllipseConnector(Figure owner) {
        super(owner);
    }
    
    private Color getStrokeColor(Figure f) {
        return f.get(STROKE_COLOR);
    }
    private float getStrokeWidth(Figure f) {
        Double w = f.get(STROKE_WIDTH);
        return (w == null) ? 1f : w.floatValue();
    }

    @Override
    protected Point2D.Double chop(Figure target, Point2D.Double from) {
        target =  getConnectorTarget(target);
        Rectangle2D.Double r = target.getBounds();
        if (getStrokeColor(target) != null) {
            double grow;
            switch (target.get(STROKE_PLACEMENT)) {
                case CENTER:
                 default :
                    grow = getStrokeTotalWidth(target) / 2d;
                    break;
                case OUTSIDE :
                    grow = getStrokeTotalWidth(target);
                    break;
                case INSIDE :
                    grow = 0f;
                    break;
            }
            Geom.grow(r, grow, grow);
        }
        double angle = Geom.pointToAngle(r, from);
        return Geom.ovalAngleToPoint(r, angle);
    }
}
