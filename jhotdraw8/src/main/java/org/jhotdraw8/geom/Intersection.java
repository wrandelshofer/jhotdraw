/* @(#)Intersection.java
 * Copyright © 2017 by the authors and contributors of JHotDraw. MIT License.
 *
 * This class is a based on:
*
*  Polynomial.js by Kevin Lindsey.
 * Copyright (C) 2002, Kevin Lindsey.
 *
 * MgcPolynomial.cpp by David Eberly.
 * Copyright (c) 2000-2003 Magic Software, Inc.
 */
package org.jhotdraw8.geom;

import java.util.Collection;
import java.util.Collections;
import java.util.Map;
import java.util.NavigableMap;
import java.util.Set;
import java.util.SortedMap;
import javafx.geometry.Point2D;

/**
 * Describes the result of an intersection test.
 * <p>
 * This class is a port of Intersection.js by Kevin Lindsey. Part of
 * Intersection.js is based on MgcPolynomial.cpp written by David Eberly, Magic
 * Software. Inc.
 * <p>
 * References:
 * <p>
 * <a href="http://www.kevlindev.com/gui/index.htm">Intersection.js</a>,
 * Copyright (c) 2002, Kevin Lindsey.
 * <p>
 * <a href="http://www.magic-software.com">MgcPolynomial.cpp </a> Copyright
 * 2000-2003 (c) David Eberly. Magic Software, Inc.
 * <p>
 * <a href="http://pomax.github.io/bezierinfo/">A Primer on Bézier Curves</a>,
 * Copyright © 2011-2016 Mike "Pomax" Kamermansy.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Intersection {

    private final NavigableMap<Double, Point2D> intersections;
    private final Status status;

    public Intersection(NavigableMap<Double, Point2D> intersections) {
        this(intersections.isEmpty() ? Status.NO_INTERSECTION : Status.INTERSECTION, intersections);
    }

    public Intersection(Status status, NavigableMap<Double, Point2D> intersections) {
        if (status == Status.INTERSECTION && intersections.isEmpty()
                || status != Status.INTERSECTION && !intersections.isEmpty()) {
            throw new IllegalArgumentException("status=" + status + " intersections=" + intersections);
        }
        this.intersections = Collections.unmodifiableNavigableMap(intersections);
        this.status = status;
    }

    public SortedMap<Double, Point2D> getIntersections() {
        return intersections;
    }

    public Point2D getLastPoint() {
        return intersections.get(intersections.lastKey());
    }

    public double getLastT() {
        return intersections.lastKey();
    }

    public Collection<Point2D> getPoints() {
        return intersections.values();
    }

    public Status getStatus() {
        return status;
    }

    public Set<Double> getTs() {
        return intersections.keySet();
    }

    public boolean isEmpty() {
        return intersections.isEmpty();
    }

    public int size() {
        return intersections.size();
    }

    @Override
    public String toString() {
        StringBuilder b = new StringBuilder();
        b.append("Intersection{").append(status).append(", points=");
        boolean first = true;
        for (Point2D p : getPoints()) {
            if (first) {
                first = false;
            } else {
                b.append(' ');
            }
            b.append(p.getX()).append(',').append(p.getY());
        }
        b.append(", ts=").append(getTs()).append('}');
        return b.toString();
    }

    enum Status {
        INTERSECTION,
        NO_INTERSECTION,
        NO_INTERSECTION_INSIDE,
        NO_INTERSECTION_OUTSIDE,
        NO_INTERSECTION_TANGENT,
        NO_INTERSECTION_COINCIDENT,
        NO_INTERSECTION_PARALLEL
    }
}
