/* @(#)Intersection.java
 * Copyright © The authors and contributors of JHotDraw. MIT License.
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

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javafx.geometry.Point2D;
import org.jhotdraw8.annotation.Nonnull;

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
 * Copyright ©-2016 Mike "Pomax" Kamermansy.
 *
 * @author Werner Randelshofer
 * @version $Id$
 */
public class Intersection {

    @Nonnull
    private final List<Map.Entry<Double, Point2D>> intersections;
    private final Status status;

    public Intersection(@Nonnull List<Map.Entry<Double, Point2D>> intersections) {
        this(intersections.isEmpty() ? Status.NO_INTERSECTION : Status.INTERSECTION, intersections);
    }
    public Intersection(Status status) {
        this(status, Collections.emptyList());
    }

    public Intersection(Status status, @Nonnull List<Map.Entry<Double, Point2D>> intersections) {
        if (status == Status.INTERSECTION && intersections.isEmpty()
                || status != Status.INTERSECTION && !intersections.isEmpty()) {
            throw new IllegalArgumentException("status=" + status + " intersections=" + intersections);
        }
        intersections.sort((a,b)->Double.compare(a.getKey(),b.getKey()));
        this.intersections = Collections.unmodifiableList(intersections);
        this.status = status;
    }

    @Nonnull
    public List<Map.Entry<Double, Point2D>> getIntersections() {
        return intersections;
    }

    public Point2D getLastPoint() {
        return intersections.get(intersections.size()-1).getValue();
    }

    public double getLastT() {
        return intersections.get(intersections.size()-1).getKey();
    }
    public double getFirstT() {
        return intersections.get(0).getKey();
    }

    public List<Point2D> getPoints() {
        return intersections.stream().map(Map.Entry::getValue).collect(Collectors.toList());
    }

    public Status getStatus() {
        return status;
    }

    public List<Double> getTs() {
        return intersections.stream().map(Map.Entry::getKey).collect(Collectors.toList());
    }

    public boolean isEmpty() {
        return intersections.isEmpty();
    }

    public int size() {
        return intersections.size();
    }

    @Nonnull
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

    public enum Status {
        INTERSECTION,
        NO_INTERSECTION,
        NO_INTERSECTION_INSIDE,
        NO_INTERSECTION_OUTSIDE,
        NO_INTERSECTION_TANGENT,
        NO_INTERSECTION_COINCIDENT,
        NO_INTERSECTION_PARALLEL
    }
}
