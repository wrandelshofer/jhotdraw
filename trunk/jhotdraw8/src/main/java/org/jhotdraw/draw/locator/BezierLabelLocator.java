/* @(#)BezierLabelLocator.java
 * Copyright (c) 2015 by the authors and contributors of JHotDraw.
 * You may only use this file in compliance with the accompanying license terms.
 */


package org.jhotdraw.draw.locator;

import javafx.geometry.Dimension2D;
import javafx.geometry.Point2D;
import org.jhotdraw.draw.BezierFigure;
import org.jhotdraw.draw.Figure;

/**
 * A {@link Locator} which can be used to place a label on the path of
 * a {@link BezierFigure}.
 * <p>
 * The point is located at a distance and an angle relative to the total length
 * of the bezier path.
 * <p>
 * XXX - The angle should be perpendicular to the path.
 *
 * @author  Werner Randelshofer
 * @version $Id$
 */
public class BezierLabelLocator implements Locator {
    private double relativePosition;
    private double angle;
    private double distance;
    
    /**
     * Creates a new instance.
     * This constructor is for use by DOMStorable only.
     */
    public BezierLabelLocator() {
    }
    /** Creates a new locator.
     *
     * @param relativePosition The relative position of the label on the polyline.
     * 0.0 specifies the start of the bezier path, 1.0 the
     * end of the polyline. Values between 0.0 and 1.0 are relative positions
     * on the bezier path.
     * @param angle The angle of the distance vector.
     * @param distance The length of the distance vector.
     */
    public BezierLabelLocator(double relativePosition, double angle, double distance) {
        this.relativePosition = relativePosition;
        this.angle = angle;
        this.distance = distance;
    }
    
    @Override
    public Point2D locate(Figure owner) {
        return getRelativePoint((BezierFigure) owner);
    }
    @Override
    public Point2D locate(Figure owner, Figure label) {
       Point2D relativePoint = getRelativeLabelPoint((BezierFigure) owner, label);
        return relativePoint;
    }
    
    /**
     * Returns the coordinates of the relative point on the path
     * of the specified bezier figure.
     */
    public Point2D getRelativePoint(BezierFigure owner) {
       Point2D point = owner.getPointOnPath((float) relativePosition, 3);
       Point2D nextPoint = owner.getPointOnPath(
                (relativePosition < 0.5) ? (float) relativePosition + 0.1f : (float) relativePosition - 0.1f,
                3);
        
        double dir = Math.atan2(nextPoint.getY() - point.getY(), nextPoint.getX() - point.getX());
        if (relativePosition >= 0.5) {
            dir += Math.PI;
        }
        double alpha = dir + angle;
        
       Point2D p = new Point2D(
                point.getX() + distance * Math.cos(alpha),
                point.getY() + distance * Math.sin(alpha)
                );
        
        if (Double.isNaN(p.getX())) p = point;
        
        return p;
    }
    
    
    /**
     * Returns aPoint2D on the polyline that is at the provided relative position.
     * XXX - Implement this and move it to BezierPath
     */
    public Point2D getRelativeLabelPoint(BezierFigure owner, Figure label) {
        // Get a point on the path an the next point on the path
       Point2D point = owner.getPointOnPath((float) relativePosition, 3);
        if (point == null) {
            return new Point2D(0,0);
        }
       Point2D nextPoint = owner.getPointOnPath(
                (relativePosition < 0.5) ? (float) relativePosition + 0.1f : (float) relativePosition - 0.1f,
                3);
        
        double dir = Math.atan2(nextPoint.getY() - point.getY(), nextPoint.getX() - point.getX());
        if (relativePosition >= 0.5) {
            dir += Math.PI;
        }
        double alpha = dir + angle;
        
       Point2D p = new Point2D(
                point.getX() + distance * Math.cos(alpha),
                point.getY() + distance * Math.sin(alpha)
                );
        if (Double.isNaN(p.getX())) p = point;
        
        if (true)throw new UnsupportedOperationException("implement me");
        Dimension2D labelDim = null;//FIXME label.getPreferredSize();
        if (relativePosition == 0.5 && 
                p.getX() >= point.getX() - distance / 2 && 
                p.getX() <= point.getX() + distance / 2) {
            if (p.getY() >= point.getY()) {
                // South East
                return new Point2D(p.getX() - labelDim.getWidth() / 2, p.getY());
            } else {
                // North East
                return new Point2D(p.getX() - labelDim.getWidth() / 2, p.getY() - labelDim.getHeight());
            }
        } else {
            if (p.getX() >= point.getX()) {
                if (p.getY() >= point.getY()) {
                    // South East
                    return new Point2D(p.getX(), p.getY());
                } else {
                    // North East
                    return new Point2D(p.getX(), p.getY() - labelDim.getHeight());
                }
            } else {
                if (p.getY() >= point.getY()) {
                    // South West
                    return new Point2D(p.getX() - labelDim.getWidth(),  p.getY());
                } else {
                    // North West
                    return new Point2D(p.getX() - labelDim.getWidth(), p.getY() - labelDim.getHeight());
                }
            }
        }
    }
}
