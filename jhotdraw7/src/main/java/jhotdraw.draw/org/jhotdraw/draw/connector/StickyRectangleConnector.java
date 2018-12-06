/* @(#)StickyRectangleConnector.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
 */


package org.jhotdraw.draw.connector;

import org.jhotdraw.draw.Figure;
import org.jhotdraw.geom.Geom;
import org.jhotdraw.xml.DOMInput;
import org.jhotdraw.xml.DOMOutput;

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.geom.Point2D;
import java.io.IOException;

/**
 * A StickyRectangleConnector locates connection points by
 * choping the connection between the centers of the
 * two figures at the display box.
 * <p>
 * The location of the connection point is computed once,
 * when the user connects the figure. Moving the figure
 * around will not change the location.
 * 
 * @author Werner Randelshofer
 * @version $Id$
 */
public class StickyRectangleConnector extends ChopRectangleConnector {
    private static final long serialVersionUID = 1L;
    private float angle;
    
    /** Creates a new instance.
     * Only used for storable.
     */
    public StickyRectangleConnector() {
    }
    public StickyRectangleConnector(Figure owner) {
        super(owner);
    }
    
    @Override
    public void draw(Graphics2D g) {
        g.setColor(Color.blue);
        g.setStroke(new BasicStroke());
        g.draw(getBounds());
    }
    
    public void setAngle(float angle) {
        this.angle = angle;
    }
    public double getAngle() {
        return angle;
    }
    
    public StickyRectangleConnector(Figure owner, Point2D.Double p) {
        super(owner);
        this.angle = (float) Geom.pointToAngle(owner.getBounds(), p);
    }
    
    @Override
    public void updateAnchor(Point2D.Double p) {
        this.angle = (float) Geom.pointToAngle(getOwner().getBounds(), p);
    }    
    @Override
    public Point2D.Double getAnchor() {
        return Geom.angleToPoint(getOwner().getBounds(), angle);
    }
    @Override protected Point2D.Double chop(Figure target, Point2D.Double from) {
            return Geom.angleToPoint(target.getBounds(), angle);
    }

    public String getParameters() {
        return Float.toString((float) (angle / Math.PI * 180));
    }
    @Override
    public void read(DOMInput in) throws IOException {
        super.read(in);
        angle = (float) in.getAttribute("angle", 0.0);
    }
    @Override
    public void write(DOMOutput out) throws IOException {
        super.write(out);
        out.addAttribute("angle", angle);
    }
}
