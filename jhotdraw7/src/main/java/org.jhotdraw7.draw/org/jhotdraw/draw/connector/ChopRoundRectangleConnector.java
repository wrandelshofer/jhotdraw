/* @(#)ChopRoundRectangleConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */

package org.jhotdraw.draw.connector;

import org.jhotdraw.draw.AttributeKeys;
import org.jhotdraw.draw.Figure;
import org.jhotdraw.draw.RoundRectangleFigure;
import org.jhotdraw.geom.Geom;

import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;

import static org.jhotdraw.draw.AttributeKeys.STROKE_PLACEMENT;

/**
 * A {@link Connector} which locates a connection point at the bounds
 * of a {@link RoundRectangleFigure}.
 * <p>
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class ChopRoundRectangleConnector extends ChopRectangleConnector {
    private static final long serialVersionUID = 1L;
    
    /**
     * Only used for DOMStorable input.
     */
    public ChopRoundRectangleConnector() {
    }
    
    public ChopRoundRectangleConnector(Figure owner) {
        super(owner);
    }
       
    @Override
    protected Point2D.Double chop(Figure target, Point2D.Double from) {
        target =  getConnectorTarget(target);
        RoundRectangleFigure rrf = (RoundRectangleFigure) target;
        Rectangle2D.Double outer = rrf.getBounds();

        double grow;
        switch (target.get(STROKE_PLACEMENT)) {
            case CENTER :
            default :
                grow = AttributeKeys.getStrokeTotalWidth(target) / 2d;
                break;
            case OUTSIDE :
                grow = AttributeKeys.getStrokeTotalWidth(target);
                break;
            case INSIDE :
                grow = 0;
                break;
        }
        Geom.grow(outer, grow, grow);
        
        
        
        Rectangle2D.Double inner = (Rectangle2D.Double) outer.clone();
        double gw = -(rrf.getArcWidth() + grow * 2) / 2;
        double gh = -(rrf.getArcHeight() + grow *2) / 2;
        inner.x -= gw;
        inner.y -= gh;
        inner.width += gw * 2;
        inner.height += gh * 2;
        
        Point2D.Double p = Geom.angleToPoint(outer, Geom.pointToAngle(outer, from));
        
        if (p.x == outer.x
        || p.x == outer.x + outer.width) {
            p.y = Math.min(Math.max(p.y, inner.y), inner.y + inner.height);
        } else {
            p.x = Math.min(Math.max(p.x, inner.x), inner.x + inner.width);
        }
        return p;
    }
}
