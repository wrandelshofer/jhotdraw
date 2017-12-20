/* @(#)BezierNodePath.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 */
package org.jhotdraw8.geom;

import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.FlatteningPathIterator;
import java.awt.geom.PathIterator;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.util.ArrayList;
import java.util.List;
import javafx.scene.shape.FillRule;

/**
 * BezierNodePath.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class BezierNodePath implements Shape {

    private boolean closed;
    private List<BezierNode> nodes ;
    private int windingRule;

    public BezierNodePath() {
        this(new ArrayList<>(),false,PathIterator.WIND_EVEN_ODD);
    }

    public BezierNodePath( List<BezierNode> nodes) {
        this(nodes,false,PathIterator.WIND_EVEN_ODD);
    }
    public BezierNodePath( List<BezierNode> nodes , boolean closed, FillRule windingRule) {
        this(nodes,closed,windingRule==FillRule.EVEN_ODD?PathIterator.WIND_EVEN_ODD:PathIterator.WIND_NON_ZERO);
        
    }
    public BezierNodePath( List<BezierNode> nodes , boolean closed, int windingRule) {
        this.nodes=nodes;
        this.closed = closed;
        this.windingRule = windingRule;
    }

    
    
    @Override
    public boolean contains(double x, double y) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(Point2D p) {
        return contains(p.getX(), p.getY());
    }

    @Override
    public boolean contains(double x, double y, double w, double h) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean contains(Rectangle2D r) {
        return contains(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }

    @Override
    public Rectangle getBounds() {
       return getBounds2D().getBounds();
    }

    @Override
    public Rectangle2D getBounds2D() {
        double x1 = Double.POSITIVE_INFINITY, y1 = Double.POSITIVE_INFINITY,
                x2 = Double.NEGATIVE_INFINITY, y2 = Double.NEGATIVE_INFINITY;
        for (BezierNode n : nodes) {
            double y = n.getY0();
            double x = n.getX0();
            if (x < x1) {
                x1 = x;
            }
            if (y < y1) {
                y1 = y;
            }
            if (x > x2) {
                x2 = x;
            }
            if (y > y2) {
                y2 = y;
            }
            if (n.isC1()) {
                y = n.getY1();
                x = n.getX1();
                if (x < x1) {
                    x1 = x;
                }
                if (y < y1) {
                    y1 = y;
                }
                if (x > x2) {
                    x2 = x;
                }
                if (y > y2) {
                    y2 = y;
                }
            }
            if (n.isC2()) {
                y = n.getY2();
                x = n.getX2();
                if (x < x1) {
                    x1 = x;
                }
                if (y < y1) {
                    y1 = y;
                }
                if (x > x2) {
                    x2 = x;
                }
                if (y > y2) {
                    y2 = y;
                }
            }
        }
        return new Rectangle2D.Double(x1, y1, x2 - x1, y2 - y1);
    }

    public List<BezierNode> getNodes() {
        return nodes;
    }

    public void setNodes(List<BezierNode> nodes) {
        this.nodes = nodes;
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at) {
        return new BezierNodePathIterator(nodes, closed, windingRule, at);
    }

    @Override
    public PathIterator getPathIterator(AffineTransform at, double flatness) {
        return new FlatteningPathIterator(getPathIterator(at), flatness);
    }

    @Override
    public boolean intersects(double x, double y, double w, double h) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean intersects(Rectangle2D r) {
        return intersects(r.getX(), r.getY(), r.getWidth(), r.getHeight());
    }
    
    public boolean pathIntersects(double x, double y, double tolerance) {
        Intersection isect=Intersections.intersectPathIteratorPoint(getPathIterator(null),x, y, tolerance);
        return !isect.isEmpty();
    }
    public boolean split(double x, double y, double tolerance) {
        Intersection isect=Intersections.intersectPathIteratorPoint(getPathIterator(null),x, y, tolerance);
        System.err.println("BezierNodePath split "+isect);
        return !isect.isEmpty();
    }
}
